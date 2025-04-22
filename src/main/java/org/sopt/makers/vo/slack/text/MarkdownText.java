package org.sopt.makers.vo.slack.text;

import static org.sopt.makers.global.constant.SlackConstant.*;

public record MarkdownText(
	String type,
	String text
) implements Text {
	public static MarkdownText newInstance(String text) {
		return new MarkdownText(TEXT_TYPE_MARKDOWN, text);
	}
}
