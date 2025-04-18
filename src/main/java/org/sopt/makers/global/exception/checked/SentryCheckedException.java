package org.sopt.makers.global.exception.checked;

import org.sopt.makers.global.exception.base.BaseErrorCode;
import org.sopt.makers.global.exception.base.SentryException;

import lombok.Getter;

@Getter
public class SentryCheckedException extends Exception implements SentryException {
	private final BaseErrorCode baseErrorCode;

	protected SentryCheckedException(BaseErrorCode baseErrorCode) {
		super(baseErrorCode.getMessage());
		this.baseErrorCode = baseErrorCode;
	}
}
