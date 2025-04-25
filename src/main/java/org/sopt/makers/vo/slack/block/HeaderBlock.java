package org.sopt.makers.vo.slack.block;

import static org.sopt.makers.global.constant.SlackConstant.*;

import org.sopt.makers.vo.slack.text.PlainText;
import org.sopt.makers.vo.slack.text.Text;

public record HeaderBlock(
	String type,
	Text text
) implements Block {
	public static HeaderBlock newInstance(String text) {
		return new HeaderBlock(BLOCK_TYPE_HEADER, PlainText.newInstance(text, true));
	}
}
