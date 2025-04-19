package org.sopt.makers.global.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SlackConstant {
	// 날짜 포맷 형식
	public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";

	// 버튼 텍스트
	public static final String SENTRY_BUTTON_TEXT = "상세 보기";

	// HTTP 관련 상수
	public static final String HEADER_CONTENT_TYPE = "Content-Type";
	public static final String CONTENT_TYPE_JSON = "application/json";

	// Slack 블록 타입 상수
	public static final String BLOCK_TYPE_HEADER = "header";
	public static final String BLOCK_TYPE_SECTION = "section";
	public static final String BLOCK_TYPE_ACTIONS = "actions";

	// 텍스트 타입 상수
	public static final String TEXT_TYPE_PLAIN = "plain_text";
	public static final String TEXT_TYPE_MARKDOWN = "mrkdwn";

	// 요소 타입 상수 (버튼)
	public static final String ELEMENT_TYPE_BUTTON = "button";

	// 스타일 상수
	public static final String STYLE_PRIMARY = "primary";

	// 줄바꿈 상수
	public static final String NEW_LINE = "\n";

	// JSON 키 상수
	public static final String MESSAGE = "message";
	public static final String ERROR = "error";
	public static final String CODE = "code";

	// 성공 메시지
	public static final String SUCCESS_MESSAGE = "알림이 성공적으로 전송되었습니다";

	// 폴백 메시지 (기본 오류 메시지)
	public static final String FALLBACK_MESSAGE = "알림이 전송되었습니다";
}
