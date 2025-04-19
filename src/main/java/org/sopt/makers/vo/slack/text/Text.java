package org.sopt.makers.vo.slack.text;

import static org.sopt.makers.global.constant.SlackConstant.*;

public record Text(
	String type,
	String text,
	boolean emoji
) {
	public static Text newPlainInstance(String text, boolean emoji) {
		return new Text(TEXT_TYPE_PLAIN, text, emoji);
	}

	public static Text newFieldInstance(String text) {
		return new Text(TEXT_TYPE_MARKDOWN, text, false);
	}
}
