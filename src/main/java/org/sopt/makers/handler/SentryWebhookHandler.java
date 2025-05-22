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
import org.sopt.makers.global.exception.unchecked.SentryUncheckedException;
import org.sopt.makers.service.NotificationProcessorService;
import org.sopt.makers.service.SentryEventExtractorService;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SentryWebhookHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
	private final ObjectMapper objectMapper = ObjectMapperConfig.getInstance();
	private final SentryEventExtractorService eventExtractorService = new SentryEventExtractorService(objectMapper);
	private final NotificationProcessorService notificationProcessorService = new NotificationProcessorService(objectMapper);

	@Override
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayEvent, Context context) {
		try {
			WebhookRequest webhookRequest = WebhookRequest.from(apiGatewayEvent);
			logWebhookReceived(webhookRequest);

			SentryEventDetail sentryEvent = eventExtractorService.extractEvent(apiGatewayEvent.getBody());

			notificationProcessorService.processNotification(webhookRequest, sentryEvent);

			return createApiGatewayResponse(
				HttpURLConnection.HTTP_OK,
				createSuccessResponseBody()
			);

		} catch (SentryCheckedException e) {
			return handleSentryCheckedException(e);
		} catch (SentryUncheckedException e) {
			return handleSentryUncheckedException(e);
		} catch (Exception e) {
			return handleUnexpectedException(e);
		}
	}

	private void logWebhookReceived(WebhookRequest webhookRequest) {
		log.info("[웹훅 수신] stage={}, team={}, type={}, service={}",
			webhookRequest.stage(),
			webhookRequest.team(),
			webhookRequest.type(),
			webhookRequest.serviceType()
		);
	}

	private APIGatewayProxyResponseEvent handleSentryCheckedException(SentryCheckedException e) {
		log.error("[처리 실패] code={}, message={}", e.getBaseErrorCode().getCode(), e.getMessage(), e);
		return createApiGatewayErrorResponse(
			e.getBaseErrorCode().getStatus(),
			e.getMessage(),
			e.getBaseErrorCode().getCode()
		);
	}

	private APIGatewayProxyResponseEvent handleSentryUncheckedException(SentryUncheckedException e) {
		log.error("[심각한 오류] code={}, message={}", e.getBaseErrorCode().getCode(), e.getMessage(), e);
		return createApiGatewayErrorResponse(
			e.getBaseErrorCode().getStatus(),
			e.getMessage(),
			e.getBaseErrorCode().getCode()
		);
	}

	private APIGatewayProxyResponseEvent handleUnexpectedException(Exception e) {
		log.error("[예상치 못한 오류] error={}", e.getMessage(), e);
		ErrorMessage error = ErrorMessage.UNEXPECTED_SERVER_ERROR;
		return createApiGatewayErrorResponse(
			error.getStatus(),
			error.getMessage(),
			error.getCode()
		);
	}

	private String createSuccessResponseBody() {
		try {
			Map<String, String> responseBody = new HashMap<>();
			responseBody.put(MESSAGE, SUCCESS_MESSAGE);
			return objectMapper.writeValueAsString(responseBody);
		} catch (Exception e) {
			log.error("[응답 본문 생성 실패] error={}", e.getMessage(), e);
			return String.format("{\"%s\":\"%s\"}", MESSAGE, FALLBACK_MESSAGE);
		}
	}

	private APIGatewayProxyResponseEvent createApiGatewayResponse(int statusCode, String body) {
		APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
		response.setStatusCode(statusCode);
		response.setHeaders(createJsonContentTypeHeaders());
		response.setBody(body);
		return response;
	}

	private APIGatewayProxyResponseEvent createApiGatewayErrorResponse(int statusCode, String errorMessage,
		String errorCode) {
		try {
			Map<String, Object> responseBody = new HashMap<>();
			responseBody.put(ERROR, errorMessage);
			responseBody.put(CODE, errorCode);

			return createApiGatewayResponse(statusCode, objectMapper.writeValueAsString(responseBody));
		} catch (Exception e) {
			log.error("[오류 응답 생성 실패] error={}", e.getMessage(), e);
			return createApiFallbackResponse(statusCode, errorMessage);
		}
	}

	private APIGatewayProxyResponseEvent createApiFallbackResponse(int statusCode, String message) {
		return createApiGatewayResponse(
			statusCode,
			String.format("{\"%s\":\"%s\"}", MESSAGE, message)
		);
	}

	private Map<String, String> createJsonContentTypeHeaders() {
		Map<String, String> headers = new HashMap<>();
		headers.put(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON);
		return headers;
	}
}
