package org.xllapp.api.core.event;

import javax.servlet.http.HttpServletRequest;

/**
 * 实现此接口以提供发送事件的逻辑.
 *
 * @author dylan.chen Aug 18, 2014
 * 
 */
public interface EventFirer {

	public void fireEvent(Class<?> clazz,HttpServletRequest request,Object requestArgument,String response);
	
}
