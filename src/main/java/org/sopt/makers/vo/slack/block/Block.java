package org.sopt.makers.vo.slack.block;

public sealed interface Block permits HeaderBlock, SectionBlock, ActionsBlock {
	String type();
}
