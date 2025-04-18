package org.sopt.makers.global.exception.checked;

import org.sopt.makers.global.exception.base.BaseErrorCode;

public class SlackSendException extends SentryCheckedException {
	public SlackSendException(BaseErrorCode errorCode) {
		super(errorCode);
	}

	public static SlackSendException from(BaseErrorCode errorCode) {
		return new SlackSendException(errorCode);
	}
}
