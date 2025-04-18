package org.sopt.makers.global.exception.unchecked;

import org.sopt.makers.global.exception.base.BaseErrorCode;

public class WebhookUrlNotFoundException extends SentryUncheckedException {
	public WebhookUrlNotFoundException(BaseErrorCode errorCode) {
		super(errorCode);
	}

	public static WebhookUrlNotFoundException from(BaseErrorCode errorCode) {
		return new WebhookUrlNotFoundException(errorCode);
	}
}
