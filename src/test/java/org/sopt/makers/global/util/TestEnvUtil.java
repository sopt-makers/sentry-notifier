package org.sopt.makers.global.util;

import org.sopt.makers.global.exception.message.ErrorMessage;
import org.sopt.makers.global.exception.unchecked.InvalidEnvParameterException;
import org.sopt.makers.global.exception.unchecked.UnsupportedServiceTypeException;
import org.sopt.makers.global.exception.unchecked.WebhookUrlNotFoundException;

import io.github.cdimascio.dotenv.Dotenv;

class TestEnvUtil {
	private static final String SLACK_WEBHOOK_PREFIX = "SLACK_WEBHOOK_";
	private static final String DISCORD_WEBHOOK_PREFIX = "DISCORD_WEBHOOK_";
	private static final Dotenv dotenv = Dotenv.configure()
		.directory("src/test/resources")
		.load();

	/**
	 * 서비스 유형에 맞는 웹훅 URL 반환
	 *
	 * @param service 서비스 유형 (slack, discord 등)
	 * @param team 팀 이름 (crew, app 등)
	 * @param stage 환경 (dev, prod)
	 * @param type 서버 유형 (be, fe)
	 * @return 웹훅 URL
	 * @throws UnsupportedServiceTypeException 지원하지 않는 서비스 유형인 경우
	 * @throws WebhookUrlNotFoundException 환경 변수를 찾을 수 없는 경우
	 */
	public static String getWebhookUrl(String service, String team, String stage, String type) {
		if (service == null || team == null || stage == null || type == null) {
			throw InvalidEnvParameterException.from(ErrorMessage.INVALID_ENV_PARAMETER);
		}

		String prefix = resolvePrefix(service.toLowerCase());
		String envKey = String.format("%s%s_%s_%s",
			prefix,
			team.toUpperCase(),
			stage.toUpperCase(),
			type.toUpperCase());

		String webhookUrl = dotenv.get(envKey);

		if (webhookUrl == null || webhookUrl.isBlank()) {
			throw new WebhookUrlNotFoundException(ErrorMessage.WEBHOOK_URL_NOT_FOUND);
		}

		return webhookUrl;
	}

	private static String resolvePrefix(String service) {
		return switch (service) {
			case "slack" -> SLACK_WEBHOOK_PREFIX;
			case "discord" -> DISCORD_WEBHOOK_PREFIX;
			default -> throw new UnsupportedServiceTypeException(ErrorMessage.UNSUPPORTED_SERVICE_TYPE);
		};
	}
}
