package org.sopt.makers.vo.slack.element;

import org.sopt.makers.vo.slack.text.Text;
import static org.sopt.makers.global.constant.SlackConstant.*;

public record Button(
	String type,
	Text text,
	String style,
	String url
) implements Element {
	public static Button newInstance(String text, String url) {
		return new Button(ELEMENT_TYPE_BUTTON, Text.newPlainInstance(text, true), STYLE_PRIMARY, url);
	}
}
