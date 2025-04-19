package org.sopt.makers.vo.slack;

import org.sopt.makers.vo.slack.block.Block;
import java.util.List;

public record SlackMessage(
	List<Block> blocks,
	String color
) {
	public static SlackMessage newInstance(List<Block> blocks, String color) {
		return new SlackMessage(blocks, color);
	}
}
