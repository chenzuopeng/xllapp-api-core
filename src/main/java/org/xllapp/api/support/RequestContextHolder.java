package org.xllapp.api.support;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 此类保存当前请求的上下文信息.
 * 
 * @author dylan.chen Mar 2, 2013
 * 
 */
public class RequestContextHolder {

	/**
	 * 当前请求的请求对象
	 */
	private static ThreadLocal<HttpServletRequest> request = new ThreadLocal<HttpServletRequest>();

	/**
	 * 当前请求的请求对象
	 */
	private static ThreadLocal<HttpServletResponse> response = new ThreadLocal<HttpServletResponse>();

	/**
	 * 当前请求的请求参数
	 */
	private static ThreadLocal<Object> requestArgument = new ThreadLocal<Object>();
	
	/**
	 * 当前请求的请求ID
	 */
	private static ThreadLocal<String> requestId = new ThreadLocal<String>();

	public static HttpServletRequest getRequest() {
		return request.get();
	}

	public static void setRequest(HttpServletRequest request) {
		RequestContextHolder.request.set(request);
	}

	public static HttpServletResponse getResponse() {
		return response.get();
	}

	public static void setResponse(HttpServletResponse response) {
		RequestContextHolder.response.set(response);
	}

	public static Object getRequestArgument() {
		return requestArgument.get();
	}

	public static void setRequestArgument(Object requestArgument) {
		RequestContextHolder.requestArgument.set(requestArgument);
	}
	
	public static String getRequestId() {
		return requestId.get();
	}

	public static void setRequestId(String requestId) {
		RequestContextHolder.requestId.set(requestId);
	}

	public static void clear() {
		request.remove();
		response.remove();
		requestArgument.remove();
		requestId.remove();
	}

}
