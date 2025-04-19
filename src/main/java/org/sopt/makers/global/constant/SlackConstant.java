package org.sopt.makers.global.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SlackConstant {
	// 날짜 포맷
	public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";

	// 버튼 텍스트
	public static final String SENTRY_BUTTON_TEXT = "상세 보기";

	// HTTP 관련
	public static final String HEADER_CONTENT_TYPE = "Content-Type";
	public static final String CONTENT_TYPE_JSON = "application/json";

	// 블록 타입 상수
	public static final String BLOCK_TYPE_HEADER = "header";
	public static final String BLOCK_TYPE_SECTION = "section";
	public static final String BLOCK_TYPE_ACTIONS = "actions";

	// 텍스트 타입 상수
	public static final String TEXT_TYPE_PLAIN = "plain_text";
	public static final String TEXT_TYPE_MARKDOWN = "mrkdwn";

	// 요소 타입 상수
	public static final String ELEMENT_TYPE_BUTTON = "button";

	// 스타일 상수
	public static final String STYLE_PRIMARY = "primary";

	// 줄바꿈 상수
	public static final String NEW_LINE = "\n";
}
