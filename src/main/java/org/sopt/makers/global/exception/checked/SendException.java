package org.sopt.makers.global.exception.checked;

import org.sopt.makers.global.exception.base.BaseErrorCode;

public class SendException extends SentryCheckedException {
	public SendException(BaseErrorCode errorCode) {
		super(errorCode);
	}

	public static SendException from(BaseErrorCode errorCode) {
		return new SendException(errorCode);
	}
}
