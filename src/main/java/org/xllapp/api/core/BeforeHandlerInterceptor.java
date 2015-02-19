package org.xllapp.api.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *　实现此接口,用于在处理请求前执行一些操作.
 *
 * @author dylan.chen Sep 20, 2013
 * 
 */
public interface BeforeHandlerInterceptor {

	/**
	 * 执行操作.
	 * 
	 * @param request
	 * @param response
	 * @return true 将执行后续的请求处理操作;false 不执行后续的请求处理操作.
	 * @throws Exception 处理出现错误时,抛出此异常
	 */
	public boolean beforeHandle(HttpServletRequest request, HttpServletResponse response) throws Exception;
	
}
