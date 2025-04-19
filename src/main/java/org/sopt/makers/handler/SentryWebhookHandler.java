package org.sopt.makers.handler;

import static org.sopt.makers.global.constant.SlackConstant.*;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import org.sopt.makers.dto.SentryEventDetail;
import org.sopt.makers.dto.WebhookRequest;
import org.sopt.makers.global.config.ObjectMapperConfig;
import org.sopt.makers.global.exception.checked.SentryCheckedException;
import org.sopt.makers.global.exception.message.ErrorMessage;
import org.sopt.makers.global.exception.unchecked.InvalidSlackPayloadException;
import org.sopt.makers.global.exception.unchecked.SentryUncheckedException;
import org.sopt.makers.global.util.EnvUtil;
import org.sopt.makers.service.NotificationService;
import org.sopt.makers.service.NotificationServiceFactory;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SentryWebhookHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	private final ObjectMapper objectMapper;

	public SentryWebhookHandler() {
		this.objectMapper = ObjectMapperConfig.getInstance();
	}

	@Override
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
		try {
			// 1. 경로 파라미터 추출
			WebhookRequest webhookRequest = WebhookRequest.from(input);
			String stage = webhookRequest.stage();
			String team = webhookRequest.team();
			String type = webhookRequest.type();
			String serviceType = webhookRequest.serviceType();

			log.info("[웹훅 수신] stage={}, team={}, type={}, service={}", webhookRequest.stage(), webhookRequest.team(),
				webhookRequest.type(), webhookRequest.serviceType());

			// 2. Sentry 웹훅 데이터 파싱
			String requestBody = input.getBody();
			JsonNode rootNode = parseRequestBody(requestBody);

			// 3. 필요한 필드만 추출하여 DTO 생성
			JsonNode eventNode = rootNode.path("data").path("event");
			if (eventNode.isMissingNode() || eventNode.isEmpty()) {
				log.error("[이벤트 데이터 누락] 요청 본문에 필수 이벤트 정보가 없습니다");
				throw InvalidSlackPayloadException.from(ErrorMessage.INVALID_SLACK_PAYLOAD);
			}

			SentryEventDetail sentryEventDetail = SentryEventDetail.from(eventNode);
			log.info("[이벤트 추출] issueId={}, level={}", sentryEventDetail.issueId(), sentryEventDetail.level());

			// 4. 알림 서비스 결정 및 알림 전송
			NotificationService notificationService = NotificationServiceFactory.createNotificationService(serviceType);
			String webhookUrl = EnvUtil.getWebhookUrl(serviceType, team, stage, type);
			notificationService.sendNotification(team, type, stage, sentryEventDetail, webhookUrl);

			// 5. 응답 반환
			return createSuccessResponse();

		} catch (SentryCheckedException e) {
			log.error("[처리 실패] code={}, message={}", e.getBaseErrorCode().getCode(), e.getMessage(), e);
			return createErrorResponse(e.getBaseErrorCode().getStatus(), e.getMessage(),
				e.getBaseErrorCode().getCode());
		} catch (SentryUncheckedException e) {
			log.error("[심각한 오류] code={}, message={}", e.getBaseErrorCode().getCode(), e.getMessage(), e);
			return createErrorResponse(e.getBaseErrorCode().getStatus(), e.getMessage(),
				e.getBaseErrorCode().getCode());
		} catch (Exception e) {
			log.error("[예상치 못한 오류] error={}", e.getMessage(), e);
			return createErrorResponse(HttpURLConnection.HTTP_INTERNAL_ERROR, "내부 서버 오류가 발생했습니다", "E999");
		}
	}

	private JsonNode parseRequestBody(String requestBody) {
		try {
			return objectMapper.readTree(requestBody);
		} catch (Exception e) {
			log.error("[요청 본문 파싱 실패] error={}", e.getMessage(), e);
			throw InvalidSlackPayloadException.from(ErrorMessage.INVALID_SLACK_PAYLOAD);
		}
	}

	/**
	 * 성공 응답 생성
	 */
	private APIGatewayProxyResponseEvent createSuccessResponse() {
		try {
			APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
			response.setStatusCode(HttpURLConnection.HTTP_OK);

			Map<String, String> headers = new HashMap<>();
			headers.put(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON);
			response.setHeaders(headers);

			Map<String, String> responseBody = new HashMap<>();
			responseBody.put("message", "알림이 성공적으로 전송되었습니다");
			response.setBody(objectMapper.writeValueAsString(responseBody));

			return response;
		} catch (Exception e) {
			log.error("[응답 생성 실패] error={}", e.getMessage(), e);
			return fallbackResponse(HttpURLConnection.HTTP_INTERNAL_ERROR, "내부 서버 오류");
		}
	}

	/**
	 * 오류 응답 생성
	 * @param statusCode HTTP 상태 코드
	 * @param errorMessage 오류 메시지
	 * @param errorCode 오류 코드
	 */
	private APIGatewayProxyResponseEvent createErrorResponse(int statusCode, String errorMessage, String errorCode) {
		try {
			APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
			response.setStatusCode(statusCode);

			Map<String, String> headers = new HashMap<>();
			headers.put(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON);
			response.setHeaders(headers);

			Map<String, Object> responseBody = new HashMap<>();
			responseBody.put("error", errorMessage != null ? errorMessage : "알 수 없는 오류");
			responseBody.put("code", errorCode);
			response.setBody(objectMapper.writeValueAsString(responseBody));

			return response;
		} catch (Exception e) {
			log.error("[오류 응답 생성 실패] error={}", e.getMessage(), e);
			return fallbackResponse(statusCode, errorMessage);
		}
	}

	/**
	 * 폴백 응답 (JSON 직렬화 실패 시)
	 */
	private APIGatewayProxyResponseEvent fallbackResponse(int statusCode, String message) {
		APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
		response.setStatusCode(statusCode);

		Map<String, String> headers = new HashMap<>();
		headers.put(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON);
		response.setHeaders(headers);

		response.setBody(String.format("{\"message\":\"%s\"}", message));
		return response;
	}
}
