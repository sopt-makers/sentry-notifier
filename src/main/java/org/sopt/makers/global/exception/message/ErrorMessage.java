package org.sopt.makers.global.exception.message;

import org.sopt.makers.global.exception.base.BaseErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorMessage implements BaseErrorCode {
	// ===== Webhook 관련 오류 =====
	WEBHOOK_URL_NOT_FOUND(500, "Webhook URL을 찾을 수 없습니다.", "W5001"),

	// ===== Slack 및 일반 서비스 오류 =====
	INVALID_SLACK_PAYLOAD(400, "Slack 페이로드 형식이 잘못되었습니다.", "S4001"),
	UNSUPPORTED_SERVICE_TYPE(400, "지원하지 않는 서비스 유형입니다.", "S4002"),
	SLACK_MESSAGE_BUILD_FAILED(500, "Slack 메시지 생성에 실패했습니다.", "S5001"),
	SLACK_SEND_INTERRUPTED(500, "Slack 알림 전송이 중단되었습니다.", "S5002"),
	SLACK_SERIALIZATION_FAILED(500, "Slack 메시지를 JSON으로 변환하는 중 오류가 발생했습니다.", "S5003"),
	SLACK_SEND_FAILED(502, "Slack 전송 요청에 실패했습니다.", "S5021"),
	SLACK_NETWORK_ERROR(503, "Slack 서버 연결에 실패했습니다.", "S5031"),

	// ===== 공통 오류 =====
	UNEXPECTED_SERVER_ERROR(500, "내부 서버 오류가 발생했습니다.", "C5001");

	private final int status;
	private final String message;
	private final String code;
}
