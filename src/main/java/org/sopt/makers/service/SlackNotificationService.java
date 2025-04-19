package org.sopt.makers.service;

import static org.sopt.makers.global.constant.SlackConstant.*;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.sopt.makers.dto.SentryEventDetail;
import org.sopt.makers.global.config.ObjectMapperConfig;
import org.sopt.makers.global.constant.Color;
import org.sopt.makers.global.exception.checked.SentryCheckedException;
import org.sopt.makers.global.exception.checked.SlackMessageBuildException;
import org.sopt.makers.global.exception.checked.SlackSendException;
import org.sopt.makers.global.exception.message.ErrorMessage;
import org.sopt.makers.global.util.HttpClientUtil;
import org.sopt.makers.vo.slack.SlackMessage;
import org.sopt.makers.vo.slack.block.ActionsBlock;
import org.sopt.makers.vo.slack.block.Block;
import org.sopt.makers.vo.slack.block.HeaderBlock;
import org.sopt.makers.vo.slack.block.SectionBlock;
import org.sopt.makers.vo.slack.element.Button;
import org.sopt.makers.vo.slack.text.Text;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SlackNotificationService implements NotificationService {

	private final ObjectMapper objectMapper;

	public SlackNotificationService() {
		this.objectMapper = ObjectMapperConfig.getInstance();
	}

	@Override
	public void sendNotification(String team, String type, String stage, SentryEventDetail sentryEventDetail,
		String webhookUrl) throws SentryCheckedException {
		SlackMessage slackMessage = createSlackMessage(team, type, stage, sentryEventDetail);
		sendSlackMessage(slackMessage, webhookUrl, team, type, stage, sentryEventDetail);
	}

	private SlackMessage createSlackMessage(String team, String type, String stage,
		SentryEventDetail sentryEventDetail) throws SentryCheckedException {
		try {
			return buildSlackMessage(team, type, stage, sentryEventDetail);
		} catch (DateTimeException e) {
			log.error("Slack 메시지 생성 실패: team={}, type={}, stage={}, id={}, error={}",
				team, type, stage, sentryEventDetail.issueId(), e.getMessage(), e);
			throw SlackMessageBuildException.from(ErrorMessage.SLACK_MESSAGE_BUILD_FAILED);
		}
	}

	private void sendSlackMessage(SlackMessage slackMessage, String webhookUrl, String team, String type, String stage,
		SentryEventDetail sentryEventDetail) throws SentryCheckedException {
		try {
			String jsonPayload = objectMapper.writeValueAsString(slackMessage);
			HttpResponse<String> response = HttpClientUtil.sendPost(webhookUrl, CONTENT_TYPE_JSON, jsonPayload);
			handleSlackResponse(response, team, type, stage, sentryEventDetail);

		} catch (IOException e) {
			log.error("Slack 네트워크 오류: {}", e.getMessage(), e);
			throw SlackSendException.from(ErrorMessage.SLACK_NETWORK_ERROR);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			log.error("Slack 전송 중단: {}", e.getMessage(), e);
			throw SlackSendException.from(ErrorMessage.SLACK_SEND_INTERRUPTED);
		} catch (Exception e) {
			log.error("Slack 알림 전송 실패: {}", e.getMessage(), e);
			throw SlackSendException.from(ErrorMessage.SLACK_SEND_FAILED);
		}
	}

	private void handleSlackResponse(HttpResponse<String> response, String team, String type,
		String stage, SentryEventDetail sentryEventDetail) throws SlackSendException {
		if (response.statusCode() != 200 || !"ok".equalsIgnoreCase(response.body())) {
			String errorMsg = String.format("Slack API 응답 오류, status: %d, body: %s",
				response.statusCode(), response.body());
			log.error("{}", errorMsg);
			throw SlackSendException.from(ErrorMessage.SLACK_SEND_FAILED);
		}

		log.info("[Slack 전송 완료] team={}, type={}, stage={}, id={}, statusCode={}",
			team, type, stage, sentryEventDetail.issueId(), response.statusCode());
	}

	/**
	 * Slack 메시지 구성
	 */
	private SlackMessage buildSlackMessage(String team, String type, String stage,
		SentryEventDetail sentryEventDetail) {
		String formattedDate = formatDateTime(sentryEventDetail.datetime());
		String color = Color.getColorByLevel(sentryEventDetail.level());

		List<Block> blocks = new ArrayList<>();
		blocks.add(buildHeaderBlock(sentryEventDetail.level()));
		blocks.add(buildDetailsBlock(team, type, stage, formattedDate));
		blocks.add(buildMessageBlock(sentryEventDetail.message()));
		blocks.add(buildActionsBlock(sentryEventDetail.webUrl()));

		return SlackMessage.newInstance(blocks, color);
	}

	/**
	 * 헤더 블록 구성
	 */
	private Block buildHeaderBlock(String level) {
		return HeaderBlock.newInstance(String.format("[%s] 오류 발생", level.toUpperCase()));
	}

	/**
	 * 상세 정보 블록 구성
	 */
	private SectionBlock buildDetailsBlock(String team, String type, String stage, String date) {
		List<Text> fields = List.of(
			Text.newFieldInstance(String.format("*환경:*%s%s", NEW_LINE, stage)),
			Text.newFieldInstance(String.format("*팀:*%s%s", NEW_LINE, team)),
			Text.newFieldInstance(String.format("*유형:*%s%s", NEW_LINE, type)),
			Text.newFieldInstance(String.format("*발생 시간:*%s%s", NEW_LINE, date))
		);
		return SectionBlock.newInstanceWithFields(fields);
	}

	/**
	 * 메시지 블록 구성
	 */
	private Block buildMessageBlock(String message) {
		return SectionBlock.newInstanceWithText(
			Text.newFieldInstance(String.format("*오류 메시지:*%s%s", NEW_LINE, message))
		);
	}

	/**
	 * 액션 블록 구성 (버튼)
	 */
	private Block buildActionsBlock(String webUrl) {
		return ActionsBlock.newInstance(List.of(Button.newInstance(SENTRY_BUTTON_TEXT, webUrl)));
	}

	/**
	 * ISO 날짜 포맷팅
	 */
	private String formatDateTime(String isoDatetime) {
		OffsetDateTime utcTime = OffsetDateTime.parse(isoDatetime, DateTimeFormatter.ISO_DATE_TIME);
		LocalDateTime koreaTime = utcTime.atZoneSameInstant(ZoneId.of(TIMEZONE_SEOUL))
			.toLocalDateTime();
		return koreaTime.format(DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN));
	}
}
