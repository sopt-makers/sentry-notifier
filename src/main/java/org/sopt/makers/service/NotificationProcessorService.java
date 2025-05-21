package org.sopt.makers.service;

import org.sopt.makers.dto.SentryEventDetail;
import org.sopt.makers.dto.WebhookRequest;
import org.sopt.makers.global.exception.checked.SentryCheckedException;
import org.sopt.makers.global.util.EnvUtil;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NotificationProcessorService {
	private final ObjectMapper objectMapper;

	public void processNotification(WebhookRequest request, SentryEventDetail event) throws SentryCheckedException {
		String serviceType = request.serviceType();
		String team = request.team();
		String stage = request.stage();
		String type = request.type();

		String webhookUrl = EnvUtil.getWebhookUrl(serviceType, team, stage, type);
		NotificationContext notificationContext = new NotificationContext(
			NotificationServiceFactory.createService(serviceType, objectMapper));
		notificationContext.executeNotification(team, type, stage, event, webhookUrl);
	}
}
