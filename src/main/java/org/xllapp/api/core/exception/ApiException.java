package org.xllapp.api.core.exception;

import org.xllapp.api.core.vo.ResultCode;

/**
 * 
 * @author dylan.chen Sep 16, 2014
 * 
 */
public class ApiException extends Exception{

	private static final long serialVersionUID = -3268599774645610911L;
	
	private ResultCode resultCode = ResultCode.FAILURE;
	
	private String resultDesc = ResultCode.FAILURE.getDesc();

	public ApiException() {
		super();
	}

	public ApiException(String resultDesc, Throwable cause) {
		super(resultDesc, cause);
		this.setResultDesc(resultDesc);
	}

	public ApiException(String resultDesc) {
		super(resultDesc);
		this.setResultDesc(resultDesc);
	}
	
	public ApiException(Throwable cause) {
		super(cause);
	}
	
	public ApiException(ResultCode resultCode,String resultDesc) {
		this.setResultCode(resultCode);
		this.setResultDesc(resultDesc);
	}

	public ResultCode getResultCode() {
		return resultCode;
	}

	public void setResultCode(ResultCode resultCode) {
		this.resultCode = resultCode;
	}

	public String getResultDesc() {
		return resultDesc;
	}

	public void setResultDesc(String resultDesc) {
		this.resultDesc = resultDesc;
	}

}
