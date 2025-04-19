package org.sopt.makers.global.exception.unchecked;

import org.sopt.makers.global.exception.base.BaseErrorCode;

public class HttpRequestException extends SentryUncheckedException {
	public HttpRequestException(BaseErrorCode errorCode) {
		super(errorCode);
	}

	public static HttpRequestException from(BaseErrorCode errorCode) {
		return new HttpRequestException(errorCode);
	}
}
