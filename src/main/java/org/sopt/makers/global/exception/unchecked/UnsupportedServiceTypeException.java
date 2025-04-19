package org.sopt.makers.global.exception.unchecked;

import org.sopt.makers.global.exception.base.BaseErrorCode;

public class UnsupportedServiceTypeException extends SentryUncheckedException {
	public UnsupportedServiceTypeException(BaseErrorCode errorCode) {
		super(errorCode);
	}

	public static UnsupportedServiceTypeException from(BaseErrorCode errorCode) {
		return new UnsupportedServiceTypeException(errorCode);
	}
}
