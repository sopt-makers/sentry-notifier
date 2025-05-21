package org.sopt.makers.service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.sopt.makers.global.exception.message.ErrorMessage;
import org.sopt.makers.global.exception.unchecked.UnsupportedServiceTypeException;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationServiceFactory {
	private static final Map<String, Function<ObjectMapper, NotificationService>> services = new HashMap<>();

	static {
		registerService("slack", SlackNotificationService::new);
		registerService("discord", DiscordNotificationService::new);
	}

	public static NotificationService createService(String serviceType, ObjectMapper objectMapper) {
		Function<ObjectMapper, NotificationService> supplier = services.get(serviceType.toLowerCase());
		if (supplier == null) {
			throw new UnsupportedServiceTypeException(ErrorMessage.UNSUPPORTED_SERVICE_TYPE);
		}
		return supplier.apply(objectMapper);
	}

	private static void registerService(String type, Function<ObjectMapper, NotificationService> supplier) {
		services.put(type.toLowerCase(), supplier);
	}
}
