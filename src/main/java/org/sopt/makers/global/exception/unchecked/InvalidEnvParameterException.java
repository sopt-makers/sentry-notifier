package org.sopt.makers.global.exception.unchecked;

import org.sopt.makers.global.exception.base.BaseErrorCode;

public class InvalidEnvParameterException extends SentryUncheckedException {

	public InvalidEnvParameterException(BaseErrorCode errorCode) {
		super(errorCode);
	}

	public static InvalidEnvParameterException from(BaseErrorCode errorCode) {
		return new InvalidEnvParameterException(errorCode);
	}
}
