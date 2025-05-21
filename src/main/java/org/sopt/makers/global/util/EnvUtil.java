package org.sopt.makers.global.util;

import org.sopt.makers.global.exception.message.ErrorMessage;
import org.sopt.makers.global.exception.unchecked.InvalidEnvParameterException;
import org.sopt.makers.global.exception.unchecked.UnsupportedServiceTypeException;
import org.sopt.makers.global.exception.unchecked.WebhookUrlNotFoundException;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EnvUtil {
	private static final String SLACK_WEBHOOK_PREFIX = "SLACK_WEBHOOK_";
	private static final String DISCORD_WEBHOOK_PREFIX = "DISCORD_WEBHOOK_";
	private static final Dotenv dotenv = Dotenv.configure().load();

	/**
	 * 서비스 유형에 맞는 웹훅 URL 반환
	 *
	 * @param serviceType 서비스 유형 (slack, discord 등)
	 * @param team 팀 이름 (crew, app 등)
	 * @param stage 환경 (dev, prod)
	 * @param type 서버 유형 (be, fe)
	 * @return 웹훅 URL
	 * @throws UnsupportedServiceTypeException 지원하지 않는 서비스 유형인 경우
	 * @throws WebhookUrlNotFoundException 환경 변수를 찾을 수 없는 경우
	 */
	public static String getWebhookUrl(String serviceType, String team, String stage, String type) {
		if (serviceType == null || team == null || stage == null || type == null) {
			log.error("환경 변수 입력값이 null입니다: serviceType={}, team={}, stage={}, type={}", serviceType, team, stage, type);
			throw InvalidEnvParameterException.from(ErrorMessage.INVALID_ENV_PARAMETER);
		}

		String prefix = resolvePrefix(serviceType.toLowerCase());
		String envKey = String.format("%s%s_%s_%s",
			prefix,
			team.toUpperCase(),
			stage.toUpperCase(),
			type.toUpperCase());

		String webhookUrl = dotenv.get(envKey);

		if (webhookUrl == null || webhookUrl.isBlank()) {
			log.error("Webhook URL을 찾을 수 없습니다: {}", envKey);
			throw new WebhookUrlNotFoundException(ErrorMessage.WEBHOOK_URL_NOT_FOUND);
		}

		return webhookUrl;
	}

	private static String resolvePrefix(String serviceTypeLowerCase) {
		return switch (serviceTypeLowerCase) {
			case "slack" -> SLACK_WEBHOOK_PREFIX;
			case "discord" -> DISCORD_WEBHOOK_PREFIX;
			default -> throw new UnsupportedServiceTypeException(ErrorMessage.UNSUPPORTED_SERVICE_TYPE);
		};
	}
}
