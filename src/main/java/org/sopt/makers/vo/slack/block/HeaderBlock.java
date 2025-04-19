package org.sopt.makers.vo.slack.block;

import org.sopt.makers.vo.slack.text.Text;

import static org.sopt.makers.global.constant.SlackConstant.*;

public record HeaderBlock(
	String type,
	Text text
) implements Block {
	public static HeaderBlock newInstance(String text) {
		return new HeaderBlock(BLOCK_TYPE_HEADER, Text.newPlainInstance(text, true));
	}
}
