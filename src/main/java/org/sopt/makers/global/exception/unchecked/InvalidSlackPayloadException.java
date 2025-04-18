package org.sopt.makers.global.exception.unchecked;

import org.sopt.makers.global.exception.base.BaseErrorCode;

public class InvalidSlackPayloadException extends SentryUncheckedException {
	public InvalidSlackPayloadException(BaseErrorCode errorCode) {
		super(errorCode);
	}

	public static InvalidSlackPayloadException from(BaseErrorCode errorCode) {
		return new InvalidSlackPayloadException(errorCode);
	}
}
