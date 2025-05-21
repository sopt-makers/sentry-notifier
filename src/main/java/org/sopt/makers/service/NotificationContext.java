package org.sopt.makers.service;

import org.sopt.makers.dto.SentryEventDetail;
import org.sopt.makers.global.exception.checked.SentryCheckedException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NotificationContext {
	private final NotificationService strategy;

	public void executeNotification(String team, String type, String stage, SentryEventDetail event, String webhookUrl)
		throws SentryCheckedException {
		strategy.sendNotification(team, type, stage, event, webhookUrl);
	}
}
