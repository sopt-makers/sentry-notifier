package org.sopt.makers.global.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DiscordConstant {
	// 컨텐츠 타입
	public static final String CONTENT_TYPE_JSON = "application/json";

	// 날짜 형식
	public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";
	public static final String TIMEZONE_SEOUL = "Asia/Seoul";

	// 메시지 형식
	public static final String EMOJI_PREFIX = "🚨";
	public static final String TRUNCATION_SUFFIX = "...";

	// Discord 리소스
	public static final String SENTRY_ICON_URL = "https://sentry.io/favicon.ico";

	// Discord 임베드 제한
	public static final int MAX_TITLE_LENGTH = 256;
	public static final int MAX_DESCRIPTION_LENGTH = 4096;
}
