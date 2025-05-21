package org.sopt.makers.service;

import org.sopt.makers.dto.SentryEventDetail;
import org.sopt.makers.global.exception.checked.SentryCheckedException;
import org.sopt.makers.global.exception.checked.MessageBuildException;
import org.sopt.makers.global.exception.checked.SendException;

public interface NotificationService {
	/**
	 * 알림 전송
	 *
	 * @param team 팀 이름
	 * @param type 유형
	 * @param stage 환경
	 * @param eventDetail 이벤트 상세 정보
	 * @param webhookUrl 웹훅 URL
	 * @throws MessageBuildException 메시지 생성 실패 시
	 * @throws SendException 전송 실패 시
	 */
	void sendNotification(String team, String type, String stage, SentryEventDetail eventDetail, String webhookUrl)
		throws SentryCheckedException;
}
