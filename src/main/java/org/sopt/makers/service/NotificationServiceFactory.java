package org.sopt.makers.service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.sopt.makers.global.exception.message.ErrorMessage;
import org.sopt.makers.global.exception.unchecked.UnsupportedServiceTypeException;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationServiceFactory {
	private static final Map<String, Supplier<NotificationService>> services = new HashMap<>();

	static {
		registerService("slack", SlackNotificationService::new);
		registerService("discord", DiscordNotificationService::new);
	}

	public static NotificationService createService(String serviceType) {
		Supplier<NotificationService> supplier = services.get(serviceType.toLowerCase());
		if (supplier == null) {
			throw new UnsupportedServiceTypeException(ErrorMessage.UNSUPPORTED_SERVICE_TYPE);
		}
		return supplier.get();
	}

	private static void registerService(String type, Supplier<NotificationService> supplier) {
		services.put(type.toLowerCase(), supplier);
	}
}
