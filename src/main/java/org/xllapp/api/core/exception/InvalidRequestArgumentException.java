package org.xllapp.api.core.exception;

import org.xllapp.api.core.vo.ResultCode;

/**
 * 当请求参数验证失败时抛出此异常.
 *
 * @author dylan.chen Sep 16, 2014
 * 
 */
public class InvalidRequestArgumentException extends ApiException {

	private static final long serialVersionUID = -1130145056097768300L;

	public InvalidRequestArgumentException() {
		super();
	}

	public InvalidRequestArgumentException(ResultCode resultCode, String resultDesc) {
		super(resultCode, resultDesc);
	}

	public InvalidRequestArgumentException(String resultDesc, Throwable cause) {
		super(resultDesc, cause);
	}

	public InvalidRequestArgumentException(String resultDesc) {
		super(resultDesc);
	}

	public InvalidRequestArgumentException(Throwable cause) {
		super(cause);
	}

}
