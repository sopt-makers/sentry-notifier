package org.sopt.makers.service;

import java.util.HashMap;
import java.util.Map;

import org.sopt.makers.global.exception.message.ErrorMessage;
import org.sopt.makers.global.exception.unchecked.UnsupportedServiceTypeException;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationServiceFactory {

	private static final Map<String, NotificationService> serviceMap = new HashMap<>();

	static {
		serviceMap.put("slack", new SlackNotificationService());
		// serviceMap.put("discord", new DiscordNotificationService());
	}

	/**
	 * 서비스 유형에 맞는 알림 서비스 구현체 반환
	 *
	 * @param serviceType 서비스 유형 (slack, discord)
	 * @return 알림 서비스 구현체
	 * @throws UnsupportedServiceTypeException 지원하지 않는 서비스 유형인 경우
	 */
	public static NotificationService createNotificationService(String serviceType) {
		NotificationService notificationService = serviceMap.get(serviceType.toLowerCase());
		if (notificationService == null) {
			throw new UnsupportedServiceTypeException(ErrorMessage.UNSUPPORTED_SERVICE_TYPE);
		}
		return notificationService;
	}
}
