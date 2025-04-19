package org.sopt.makers.vo.slack.block;

import org.sopt.makers.vo.slack.element.Element;
import static org.sopt.makers.global.constant.SlackConstant.*;

import java.util.List;

public record ActionsBlock(
	String type,
	List<Element> elements
) implements Block {
	public static ActionsBlock newInstance(List<Element> elements) {
		return new ActionsBlock(BLOCK_TYPE_ACTIONS, elements);
	}
}
