package org.sopt.makers.global.exception.checked;

import org.sopt.makers.global.exception.base.BaseErrorCode;

public class SlackMessageBuildException extends SentryCheckedException {
	public SlackMessageBuildException(BaseErrorCode errorCode) {
		super(errorCode);
	}

	public static SlackMessageBuildException from(BaseErrorCode errorCode) {
		return new SlackMessageBuildException(errorCode);
	}
}
