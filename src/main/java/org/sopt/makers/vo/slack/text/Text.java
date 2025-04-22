package org.sopt.makers.vo.slack.text;

public sealed interface Text permits PlainText, MarkdownText {
	String type();
	String text();
}
