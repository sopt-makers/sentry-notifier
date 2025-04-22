package org.sopt.makers.vo.slack.text;

import static org.sopt.makers.global.constant.SlackConstant.*;

public record PlainText(
	String type,
	String text,
	boolean emoji
) implements Text {
	public static PlainText newInstance(String text, boolean emoji) {
		return new PlainText(TEXT_TYPE_PLAIN, text, emoji);
	}
}
