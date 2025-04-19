package org.sopt.makers.vo.slack.element;

public sealed interface Element permits Button {
	String type();
}
