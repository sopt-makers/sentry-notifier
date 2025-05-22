package org.sopt.makers.global.exception.checked;

import org.sopt.makers.global.exception.base.BaseErrorCode;

public class MessageBuildException extends SentryCheckedException {
	public MessageBuildException(BaseErrorCode errorCode) {
		super(errorCode);
	}

	public static MessageBuildException from(BaseErrorCode errorCode) {
		return new MessageBuildException(errorCode);
	}
}
