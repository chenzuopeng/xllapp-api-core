package org.xllapp.api.core.vo;

import java.util.Date;

import org.xllapp.api.support.RequestContextHolder;
import org.xllapp.api.support.jackson.DateJsonSerializer;
import org.xllapp.api.support.jackson.ResultCodeSerializer;
import org.xllapp.api.util.JSONHelper;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 接口响应.
 *
 * @author dylan.chen Aug 18, 2014
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {

	public final static ApiResponse FAILURE_RESPONSE = new ApiResponse(ResultCode.FAILURE, ResultCode.FAILURE.getDesc());
	
	public final static String JSON_FAILURE_RESPONSE = JSONHelper.toJSONStringQuietly(FAILURE_RESPONSE,false);

	private String requestId = RequestContextHolder.getRequestId();
	
	private ResultCode resultCode;

	private String resultDesc;

	private Object data;
	
	private Date timestamp;
	
	public ApiResponse(ResultCode resultCode, String resultDesc, Object data, Date timestamp) {
		this.resultCode = resultCode;
		this.resultDesc = resultDesc;
		this.data = data;
		this.timestamp = timestamp;
	}

	public ApiResponse(ResultCode resultCode, String resultDesc, Object data) {
		this(resultCode, resultDesc, data, new Date());
	}

	public ApiResponse(ResultCode resultCode, String resultDesc) {
		this(resultCode, resultDesc, null);
	}

	public ApiResponse() {
	}

	@JsonSerialize(using = ResultCodeSerializer.class)
	@JsonProperty("result_code")
	public ResultCode getResultCode() {
		return this.resultCode;
	}

	public void setResultCode(ResultCode resultCode) {
		this.resultCode = resultCode;
	}

	@JsonProperty("result_desc")
	public String getResultDesc() {
		return this.resultDesc;
	}

	public void setResultDesc(String resultDesc) {
		this.resultDesc = resultDesc;
	}

	public Object getData() {
		return this.data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	@JsonSerialize(using = DateJsonSerializer.class)
	public Date getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	@JsonProperty("request_id")
	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	
	public static String getabc(String server,String uri){
		if((!server.endsWith("/")) && (!uri.startsWith("/"))){
          return server + "/" + uri;
		}
		return server + uri;
	}

	public static void main(String[] args) {
      System.out.println(getabc("afdsfs", "123"));
      System.out.println(getabc("afdsfs/", "123"));
      System.out.println(getabc("afdsfs", "/123"));
      System.out.println(getabc("afdsfs//", "//123"));
	}
	
	
}
