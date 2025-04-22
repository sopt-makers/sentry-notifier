package org.sopt.makers.vo.slack.element;

import static org.sopt.makers.global.constant.SlackConstant.*;

import org.sopt.makers.vo.slack.text.PlainText;
import org.sopt.makers.vo.slack.text.Text;

public record Button(
	String type,
	Text text,
	String style,
	String url
) implements Element {
	public static Button newInstance(String text, String url) {
		return new Button(ELEMENT_TYPE_BUTTON, PlainText.newInstance(text, true), STYLE_PRIMARY, url);
	}
}
