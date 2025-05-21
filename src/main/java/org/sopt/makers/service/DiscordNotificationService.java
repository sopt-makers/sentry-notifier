package org.sopt.makers.service;

import static org.sopt.makers.global.constant.DiscordConstant.*;

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
import org.sopt.makers.global.exception.checked.MessageBuildException;
import org.sopt.makers.global.exception.checked.SendException;
import org.sopt.makers.global.exception.checked.SentryCheckedException;
import org.sopt.makers.global.exception.message.ErrorMessage;
import org.sopt.makers.global.exception.unchecked.HttpRequestException;
import org.sopt.makers.global.util.HttpClientUtil;
import org.sopt.makers.vo.discord.embed.DiscordEmbed;
import org.sopt.makers.vo.discord.embed.DiscordEmbed.DiscordEmbedField;
import org.sopt.makers.vo.discord.message.DiscordMessage;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DiscordNotificationService implements NotificationService {
	private final ObjectMapper objectMapper;

	public DiscordNotificationService() {
		this.objectMapper = ObjectMapperConfig.getInstance();
	}

	@Override
	public void sendNotification(String team, String type, String stage, SentryEventDetail sentryEventDetail,
		String webhookUrl) throws SentryCheckedException {
		DiscordMessage discordMessage = createDiscordMessage(team, type, stage, sentryEventDetail);
		sendDiscordMessage(discordMessage, webhookUrl, team, type, stage, sentryEventDetail);
	}

	/**
	 * Discord 메시지 생성
	 */
	private DiscordMessage createDiscordMessage(String team, String type, String stage,
		SentryEventDetail sentryEventDetail) throws SentryCheckedException {
		try {
			return buildDiscordMessage(team, type, stage, sentryEventDetail);
		} catch (DateTimeException e) {
			log.error("Discord 메시지 생성 실패: team={}, type={}, stage={}, id={}, error={}", team, type, stage,
				sentryEventDetail.issueId(), e.getMessage(), e);
			throw MessageBuildException.from(ErrorMessage.DISCORD_MESSAGE_BUILD_FAILED);
		}
	}

	/**
	 * Discord 메시지를 실제로 전송
	 */
	private void sendDiscordMessage(DiscordMessage discordMessage, String webhookUrl, String team, String type,
		String stage, SentryEventDetail sentryEventDetail) throws SentryCheckedException {
		try {
			String jsonPayload = objectMapper.writeValueAsString(discordMessage);
			log.info("Discord 메시지: {}", jsonPayload);
			HttpResponse<String> response = HttpClientUtil.sendPost(webhookUrl, CONTENT_TYPE_JSON, jsonPayload);
			handleDiscordResponse(response, team, type, stage, sentryEventDetail);
		} catch (HttpRequestException e) {
			throw SendException.from(e.getBaseErrorCode());
		} catch (IOException e) {
			throw SendException.from(ErrorMessage.DISCORD_SERIALIZATION_FAILED);
		}
	}

	/**
	 * Discord API 응답 처리
	 */
	private void handleDiscordResponse(HttpResponse<String> response, String team, String type, String stage,
		SentryEventDetail sentryEventDetail) throws SendException {
		// Discord는 204 No Content 응답 코드를 반환
		if (response.statusCode() != 204) {
			String errorMsg = String.format("Discord API 응답 오류, status: %d, body: %s", response.statusCode(),
				response.body());
			log.error("{}", errorMsg);
			throw SendException.from(ErrorMessage.DISCORD_SEND_FAILED);
		}

		log.info("[Discord 전송 완료] team={}, type={}, stage={}, id={}, statusCode={}", team, type, stage,
			sentryEventDetail.issueId(), response.statusCode());
	}

	/**
	 * Discord 메시지 구성 - Slack과 동일한 메시지 내용을 Discord 메시지 형식으로 변환
	 */
	private DiscordMessage buildDiscordMessage(String team, String type, String stage,
		SentryEventDetail sentryEventDetail) {
		String formattedDate = formatDateTime(sentryEventDetail.datetime());
		String colorHex = Color.getColorByLevel(sentryEventDetail.level());
		int colorInt = convertHexColorToInt(colorHex);

		String title = buildTitle(sentryEventDetail.message());
		List<DiscordEmbedField> fields = buildFields(team, type, stage, formattedDate, sentryEventDetail.issueId(),
			sentryEventDetail.level());
		String description = buildDescription(sentryEventDetail.title());

		// Discord 임베드 생성
		DiscordEmbed embed = DiscordEmbed.newInstance(
			title,
			description,
			sentryEventDetail.webUrl(),
			colorInt,
			fields);

		// Discord 메시지 생성
		return DiscordMessage.newInstance(
			"Sentry Monitor",
			SENTRY_ICON_URL,
			List.of(embed));
	}

	/**
	 * 메시지 제목 구성
	 */
	private String buildTitle(String message) {
		String title = "%s %s".formatted(EMOJI_PREFIX, message);

		if (title.length() > MAX_TITLE_LENGTH) {
			int maxContentLength = MAX_TITLE_LENGTH - TRUNCATION_SUFFIX.length();
			title = "%s %s%s".formatted(
				EMOJI_PREFIX,
				message.substring(0, maxContentLength - EMOJI_PREFIX.length() - 1),
				TRUNCATION_SUFFIX
			);
		}

		return title;
	}

	/**
	 * 메시지 상세 필드 구성
	 */
	private List<DiscordEmbedField> buildFields(String team, String type, String stage, String date, String issueId,
		String level) {
		List<DiscordEmbedField> fields = new ArrayList<>();
		fields.add(DiscordEmbedField.newInstance("Environment", stage, true));
		fields.add(DiscordEmbedField.newInstance("Team", team, true));
		fields.add(DiscordEmbedField.newInstance("Server Type", type, true));
		fields.add(DiscordEmbedField.newInstance("Issue Id", issueId, true));
		fields.add(DiscordEmbedField.newInstance("Happen", date, true));
		fields.add(DiscordEmbedField.newInstance("Level", level, true));
		return fields;
	}

	/**
	 * 메시지 오류 세부 설명 구성
	 */
	private String buildDescription(String title) {
		String description = """
        **Error Details:**
        ```
        %s
        ```""".formatted(title.trim());

		if (description.length() > MAX_DESCRIPTION_LENGTH) {
			description = description.substring(0, MAX_DESCRIPTION_LENGTH - TRUNCATION_SUFFIX.length())
				+ TRUNCATION_SUFFIX;
		}

		return description;
	}

	/**
	 * HEX 색상 코드를 Discord에서 사용하는 정수로 변환
	 */
	private int convertHexColorToInt(String hexColor) {
		String cleanHex = hexColor.startsWith("#") ? hexColor.substring(1) : hexColor;
		return Integer.parseInt(cleanHex, 16);
	}

	/**
	 * ISO 날짜 포맷팅
	 */
	private String formatDateTime(String isoDatetime) {
		OffsetDateTime utcTime = OffsetDateTime.parse(isoDatetime, DateTimeFormatter.ISO_DATE_TIME);
		LocalDateTime koreaTime = utcTime.atZoneSameInstant(ZoneId.of(TIMEZONE_SEOUL)).toLocalDateTime();
		return koreaTime.format(DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN));
	}
}
