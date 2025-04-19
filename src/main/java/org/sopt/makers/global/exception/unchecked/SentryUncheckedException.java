package org.sopt.makers.global.exception.unchecked;

import org.sopt.makers.global.exception.base.BaseErrorCode;
import org.sopt.makers.global.exception.base.SentryException;

import lombok.Getter;

@Getter
public class SentryUncheckedException extends RuntimeException implements SentryException {
	private final BaseErrorCode baseErrorCode;

	protected SentryUncheckedException(BaseErrorCode baseErrorCode) {
		super(baseErrorCode.getMessage());
		this.baseErrorCode = baseErrorCode;
	}
}
