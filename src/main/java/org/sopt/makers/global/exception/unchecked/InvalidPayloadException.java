package org.sopt.makers.global.exception.unchecked;

import org.sopt.makers.global.exception.base.BaseErrorCode;

public class InvalidPayloadException extends SentryUncheckedException {
	public InvalidPayloadException(BaseErrorCode errorCode) {
		super(errorCode);
	}

	public static InvalidPayloadException from(BaseErrorCode errorCode) {
		return new InvalidPayloadException(errorCode);
	}
}
