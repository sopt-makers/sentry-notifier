package org.sopt.makers.vo.slack.block;

import org.sopt.makers.vo.slack.text.Text;
import static org.sopt.makers.global.constant.SlackConstant.*;

import java.util.List;

public record SectionBlock(
	String type,
	List<Text> fields,
	Text text
) implements Block {
	public static SectionBlock newInstanceWithFields(List<Text> fields) {
		return new SectionBlock(BLOCK_TYPE_SECTION, fields, null);
	}

	public static SectionBlock newInstanceWithText(Text text) {
		return new SectionBlock(BLOCK_TYPE_SECTION, null, text);
	}
}
