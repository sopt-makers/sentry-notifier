package org.sopt.makers.global.exception.message;

import org.sopt.makers.global.exception.base.BaseErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorMessage implements BaseErrorCode {
	SLACK_MESSAGE_BUILD_FAILED(500, "Slack 메시지 생성에 실패했습니다.", "S001"),
	SLACK_SEND_FAILED(502, "Slack 전송 요청에 실패했습니다.", "S002"),
	INVALID_SLACK_PAYLOAD(400, "Slack 페이로드 형식이 잘못되었습니다.", "S003"),
	SLACK_NETWORK_ERROR(503, "Slack 서버 연결에 실패했습니다.", "S004"),
	SLACK_SEND_INTERRUPTED(500, "Slack 알림 전송이 중단되었습니다.", "S005"),
	WEBHOOK_URL_NOT_FOUND(500, "Webhook URL을 찾을 수 없습니다.", "W001"),
	UNSUPPORTED_SERVICE_TYPE(400, "지원하지 않는 서비스 유형입니다.", "W002");

	private final int status;
	private final String message;
	private final String code;
}
