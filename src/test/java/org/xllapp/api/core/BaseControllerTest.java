package org.xllapp.api.core;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.xllapp.api.core.BaseController;
import org.xllapp.api.core.BeforeHandlerInterceptor;
import org.xllapp.api.core.event.EventFirer;
import org.xllapp.api.support.RequestContextHolder;

/**
 *
 *
 * @Copyright: Copyright (c) 2013 FFCS All Rights Reserved
 * @Company: 北京福富软件有限公司
 * @author 陈作朋 Sep 20, 2013
 * @version 1.00.00
 * @history:
 * 
 */
public class BaseControllerTest {

	/**
	 * 设置BeforeHandlerInterceptor并且BeforeHandlerInterceptor.handle()返回false
	 */
	@Test
	public void testBeforeHandlerInterceptor1() throws Exception {

		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		HttpServletResponse response = EasyMock.createMock(HttpServletResponse.class);
		EasyMock.replay(request, response);

		BeforeHandlerInterceptor beforeHandlerInterceptor = EasyMock.createMock(BeforeHandlerInterceptor.class);
		EasyMock.expect(beforeHandlerInterceptor.beforeHandle(request, response)).andReturn(false).times(1);
		EasyMock.replay(beforeHandlerInterceptor);

		BaseController baseController = new BaseController() {
			@Override
			public Object resolveAndVerifyArgument(HttpServletRequest request) throws Exception {
				throw new RuntimeException("1");
			}

			@Override
			public String handleRequest(Object argument, HttpServletRequest request) throws Exception {
				throw new RuntimeException("2");
			}

			@Override
			public void handleException(Exception exception, HttpServletRequest request, HttpServletResponse response) {
				throw new RuntimeException(exception);
			}

		};
		baseController.setBeforeHandlerInterceptor(beforeHandlerInterceptor);
		baseController.handleRequest(request, response);

		EasyMock.verify(beforeHandlerInterceptor, request, response);
	}

	/**
	 * 没有设置BeforeHandlerInterceptor
	 */
	@Test
	public void testBeforeHandlerInterceptor2() throws Exception {

		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		HttpServletResponse response = EasyMock.createMock(HttpServletResponse.class);
		EasyMock.replay(request, response);

		final Exception exception = new RuntimeException("1");

		BaseController baseController = new BaseController() {
			@Override
			public Object resolveAndVerifyArgument(HttpServletRequest request) throws Exception {
				return null;
			}

			@Override
			public String handleRequest(Object argument, HttpServletRequest request) throws Exception {
				throw exception;
			}

			@Override
			public void handleException(Exception exception, HttpServletRequest request, HttpServletResponse response) {
				throw new RuntimeException(exception);
			}

		};

		try {
			baseController.handleRequest(request, response);
		} catch (Exception e) {
			Assert.assertEquals(exception, e.getCause());
		}
		EasyMock.verify(request, response);
	}

	/**
	 * 设置BeforeHandlerInterceptor并且BeforeHandlerInterceptor.handle()返回true
	 */
	@Test
	public void testBeforeHandlerInterceptor3() throws Exception {

		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		HttpServletResponse response = EasyMock.createMock(HttpServletResponse.class);
		EasyMock.replay(request, response);

		BeforeHandlerInterceptor beforeHandlerInterceptor = EasyMock.createMock(BeforeHandlerInterceptor.class);
		EasyMock.expect(beforeHandlerInterceptor.beforeHandle(request, response)).andReturn(true).times(1);
		EasyMock.replay(beforeHandlerInterceptor);

		final Exception exception = new RuntimeException("1");

		BaseController baseController = new BaseController() {
			@Override
			public Object resolveAndVerifyArgument(HttpServletRequest request) throws Exception {
				throw exception;
			}

			@Override
			public String handleRequest(Object argument, HttpServletRequest request) throws Exception {
				throw new RuntimeException("2");
			}

			@Override
			public void handleException(Exception exception, HttpServletRequest request, HttpServletResponse response) {
				throw new RuntimeException(exception);
			}

		};

		try {
			baseController.setBeforeHandlerInterceptor(beforeHandlerInterceptor);
			baseController.handleRequest(request, response);
		} catch (Exception e) {
			Assert.assertEquals(exception, e.getCause());
		}

		EasyMock.verify(beforeHandlerInterceptor, request, response);

	}

	/**
	 * 设置BeforeHandlerInterceptor并且执行BeforeHandlerInterceptor.handle()方法抛出异常
	 */
	@Test
	public void testBeforeHandlerInterceptor4() throws Exception {

		final Exception exception = new Exception("0");

		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		HttpServletResponse response = EasyMock.createMock(HttpServletResponse.class);
		EasyMock.replay(request, response);

		BeforeHandlerInterceptor beforeHandlerInterceptor = EasyMock.createMock(BeforeHandlerInterceptor.class);
		EasyMock.expect(beforeHandlerInterceptor.beforeHandle(request, response)).andThrow(exception).times(1);
		EasyMock.replay(beforeHandlerInterceptor);

		BaseController baseController = new BaseController() {
			@Override
			public Object resolveAndVerifyArgument(HttpServletRequest request) throws Exception {
				throw new RuntimeException("1");
			}

			@Override
			public String handleRequest(Object argument, HttpServletRequest request) throws Exception {
				throw new RuntimeException("2");
			}

			@Override
			public void handleException(Exception exception, HttpServletRequest request, HttpServletResponse response) {
				throw new RuntimeException(exception);
			}

		};

		try {
			baseController.setBeforeHandlerInterceptor(beforeHandlerInterceptor);
			baseController.handleRequest(request, response);
		} catch (Exception e) {
			Assert.assertEquals(exception, e.getCause());
		}

		EasyMock.verify(beforeHandlerInterceptor, request, response);

	}

	/**
	 * 发送事件
	 */
	@Test
	public void testFireEvent() throws ServletException, IOException {

		final String responseContent = "test";

		BaseController baseController = new BaseController() {

			@Override
			public String handleRequest(Object argument, HttpServletRequest request) throws Exception {
				return responseContent;
			}

			@Override
			public Object resolveAndVerifyArgument(HttpServletRequest request) throws Exception {
				return null;
			}

		};

		HttpServletRequest request = EasyMock.createNiceMock(HttpServletRequest.class);
		HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);
		EasyMock.expect(response.getWriter()).andReturn(new PrintWriter(System.err)).times(1);
		EventFirer eventFirer = EasyMock.createMock(EventFirer.class);
		eventFirer.fireEvent(baseController.getClass(), request, null, responseContent);
		EasyMock.expectLastCall().times(1);
		EasyMock.replay(request, response, eventFirer);

		RequestContextHolder.setRequest(request);

		baseController.setEventFirer(eventFirer);

		baseController.handleRequest(request, response);

		EasyMock.verify(request,response,eventFirer);
		
	}

}
