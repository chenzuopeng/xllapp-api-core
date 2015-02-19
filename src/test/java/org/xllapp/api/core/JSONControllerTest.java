package org.xllapp.api.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.xllapp.api.core.BaseController;
import org.xllapp.api.core.JSONController;
import org.xllapp.api.core.exception.ApiException;
import org.xllapp.api.core.exception.InvalidRequestArgumentException;
import org.xllapp.api.support.ApiApplicationConfig;
import org.xllapp.api.support.RequestContextHolder;
import org.xllapp.api.util.CrytoUtils;
import org.xllapp.api.util.RequestUtils;

/**
 *
 *
 * @Copyright: Copyright (c) 2014 FFCS All Rights Reserved
 * @Company: 北京福富软件有限公司
 * @author 陈作朋 Sep 16, 2014
 * @version 1.00.00
 * @history:
 * 
 */
public class JSONControllerTest {

	static class TestServletInputStream extends ServletInputStream {

		private InputStream is;

		public TestServletInputStream(String json) {
			try {
				this.is = new ByteArrayInputStream(json.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		@Override
		public int read() throws IOException {
			return this.is.read();
		}

	}

	static {
		RequestContextHolder.setRequestId(RequestUtils.generateRequestId());
	}

	/**
	 * 请求Body为空
	 */
	@Test
	public void test1() throws ServletException, IOException {

		JSONController controller = new JSONController() {

			@Override
			public void verifyBusiArgument(Map<String, Object> requestArgument) throws InvalidRequestArgumentException {

			}

			@Override
			public Object handleRequest(Map<String, Object> requestArgument) throws ApiException {
				return null;
			}

			@Override
			public String[] getSignItems(Map<String, Object> requestArgument) {
				return null;
			}

			@Override
			public void handleException(Exception exception, HttpServletRequest request, HttpServletResponse response) {
				Assert.assertTrue(exception.getLocalizedMessage().contains("request body"));
				super.handleException(exception, request, response);
			}

		};

		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);
		EasyMock.expect(response.getWriter()).andReturn(new PrintWriter(System.err)).times(1);
		EasyMock.expect(request.getMethod()).andReturn("POST").times(1);

		EasyMock.expect(request.getParameter(BaseController.REQUEST_PARAM_JSONP_CALLBACK)).andReturn(null).times(1);
		RequestContextHolder.setRequest(request);

		String json = "";

		EasyMock.expect(request.getInputStream()).andReturn(new TestServletInputStream(json)).times(1);

		EasyMock.replay(request, response);

		controller.handleRequest(request, response);

		EasyMock.verify(request, response);

	}

	/**
	 * 系统参数验证通过
	 */
	@Test
	public void test2() throws ServletException, IOException {

		JSONController controller = new JSONController() {

			@Override
			public void verifyBusiArgument(Map<String, Object> requestArgument) throws InvalidRequestArgumentException {
				throw new InvalidRequestArgumentException("abc");
			}

			@Override
			public Object handleRequest(Map<String, Object> requestArgument) throws ApiException {
				return null;
			}

			@Override
			public String[] getSignItems(Map<String, Object> requestArgument) {
				return null;
			}

			@Override
			public void handleException(Exception exception, HttpServletRequest request, HttpServletResponse response) {
				Assert.assertTrue(exception.getLocalizedMessage().equals("abc"));
				super.handleException(exception, request, response);
			}

		};

		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);
		EasyMock.expect(response.getWriter()).andReturn(new PrintWriter(System.err)).times(1);
		EasyMock.expect(request.getMethod()).andReturn("POST").times(1);

		EasyMock.expect(request.getParameter(BaseController.REQUEST_PARAM_JSONP_CALLBACK)).andReturn(null).times(1);
		RequestContextHolder.setRequest(request);

		String json = "{\"product_id\":\"abc\",\"org_code\":\"3501\",\"client_type\":\"icity_ver\",\"client_version\":\"700\",\"client_channel_type\":\"123\",\"os_type\":\"android\",\"imsi\":\"123\",\"imei\":\"abc\",\"timestamp\":\"2014-09-16 22:00:01\",\"city_code\":\"abc\",\"mobile\":\"189\",\"longitude\":\"123\",\"latitude\":\"456\"}";

		EasyMock.expect(request.getInputStream()).andReturn(new TestServletInputStream(json)).times(1);

		EasyMock.replay(request, response);

		controller.handleRequest(request, response);

		EasyMock.verify(request, response);

	}

	/**
	 * product_id为空
	 */
	@Test
	public void test3() throws ServletException, IOException {

		JSONController controller = new JSONController() {

			@Override
			public void verifyBusiArgument(Map<String, Object> requestArgument) throws InvalidRequestArgumentException {
			}

			@Override
			public Object handleRequest(Map<String, Object> requestArgument) throws ApiException {
				return null;
			}

			@Override
			public String[] getSignItems(Map<String, Object> requestArgument) {
				return null;
			}

			@Override
			public void handleException(Exception exception, HttpServletRequest request, HttpServletResponse response) {
				Assert.assertTrue(exception.getLocalizedMessage().contains("product_id"));
				super.handleException(exception, request, response);
			}

		};

		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);
		EasyMock.expect(response.getWriter()).andReturn(new PrintWriter(System.err)).times(1);
		EasyMock.expect(request.getMethod()).andReturn("POST").times(1);

		EasyMock.expect(request.getParameter(BaseController.REQUEST_PARAM_JSONP_CALLBACK)).andReturn(null).times(1);
		RequestContextHolder.setRequest(request);

		String json = "{}";

		EasyMock.expect(request.getInputStream()).andReturn(new TestServletInputStream(json)).times(1);

		EasyMock.replay(request, response);

		controller.handleRequest(request, response);

		EasyMock.verify(request, response);

	}

	/**
	 * org_code为空
	 */
	@Test
	public void test4() throws ServletException, IOException {

		JSONController controller = new JSONController() {

			@Override
			public void verifyBusiArgument(Map<String, Object> requestArgument) throws InvalidRequestArgumentException {
			}

			@Override
			public Object handleRequest(Map<String, Object> requestArgument) throws ApiException {
				return null;
			}

			@Override
			public String[] getSignItems(Map<String, Object> requestArgument) {
				return null;
			}

			@Override
			public void handleException(Exception exception, HttpServletRequest request, HttpServletResponse response) {
				Assert.assertTrue(exception.getLocalizedMessage().contains("org_code"));
				super.handleException(exception, request, response);
			}

		};

		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);
		EasyMock.expect(response.getWriter()).andReturn(new PrintWriter(System.err)).times(1);

		EasyMock.expect(request.getParameter(BaseController.REQUEST_PARAM_JSONP_CALLBACK)).andReturn(null).times(1);
		RequestContextHolder.setRequest(request);

		String json = "{\"city_code\":\"abc\",\"mobile\":\"189\",\"longitude\":\"123\",\"latitude\":\"456\",\"product_id\":\"abc\",\"client_type\":\"icity_ver\",\"client_version\":\"700\",\"client_channel_type\":\"123\",\"os_type\":\"android\",\"imsi\":\"123\",\"imei\":\"abc\",\"timestamp\":\"2014-09-16 12:00:01\"}";

		EasyMock.expect(request.getInputStream()).andReturn(new TestServletInputStream(json)).times(1);

		EasyMock.expect(request.getMethod()).andReturn("POST").times(1);

		EasyMock.replay(request, response);

		controller.handleRequest(request, response);

		EasyMock.verify(request, response);

	}

	/**
	 * client_version为空
	 */
	@Test
	public void test5() throws ServletException, IOException {

		JSONController controller = new JSONController() {

			@Override
			public void verifyBusiArgument(Map<String, Object> requestArgument) throws InvalidRequestArgumentException {
			}

			@Override
			public Object handleRequest(Map<String, Object> requestArgument) throws ApiException {
				return null;
			}

			@Override
			public String[] getSignItems(Map<String, Object> requestArgument) {
				return null;
			}

			@Override
			public void handleException(Exception exception, HttpServletRequest request, HttpServletResponse response) {
				Assert.assertTrue(exception.getLocalizedMessage().contains("client_version"));
				super.handleException(exception, request, response);
			}

		};

		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);
		EasyMock.expect(response.getWriter()).andReturn(new PrintWriter(System.err)).times(1);

		EasyMock.expect(request.getMethod()).andReturn("POST").times(1);

		EasyMock.expect(request.getParameter(BaseController.REQUEST_PARAM_JSONP_CALLBACK)).andReturn(null).times(1);
		RequestContextHolder.setRequest(request);

		String json = "{\"city_code\":\"abc\",\"mobile\":\"189\",\"longitude\":\"123\",\"latitude\":\"456\",\"product_id\":\"abc\",\"client_type\":\"icity_ver\",\"org_code\":\"3501\",\"client_channel_type\":\"123\",\"os_type\":\"android\",\"imsi\":\"123\",\"imei\":\"abc\",\"timestamp\":\"2014-09-16 12:00:01\"}";

		EasyMock.expect(request.getInputStream()).andReturn(new TestServletInputStream(json)).times(1);

		EasyMock.replay(request, response);

		controller.handleRequest(request, response);

		EasyMock.verify(request, response);

	}

	/**
	 * client_channel_type为空
	 */
	@Test
	public void test6() throws ServletException, IOException {

		JSONController controller = new JSONController() {

			@Override
			public void verifyBusiArgument(Map<String, Object> requestArgument) throws InvalidRequestArgumentException {
			}

			@Override
			public Object handleRequest(Map<String, Object> requestArgument) throws ApiException {
				return null;
			}

			@Override
			public String[] getSignItems(Map<String, Object> requestArgument) {
				return null;
			}

			@Override
			public void handleException(Exception exception, HttpServletRequest request, HttpServletResponse response) {
				Assert.assertTrue(exception.getLocalizedMessage().contains("client_channel_type"));
				super.handleException(exception, request, response);
			}

		};

		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);
		EasyMock.expect(response.getWriter()).andReturn(new PrintWriter(System.err)).times(1);

		EasyMock.expect(request.getMethod()).andReturn("POST").times(1);

		EasyMock.expect(request.getParameter(BaseController.REQUEST_PARAM_JSONP_CALLBACK)).andReturn(null).times(1);
		RequestContextHolder.setRequest(request);

		String json = "{\"city_code\":\"abc\",\"mobile\":\"189\",\"longitude\":\"123\",\"latitude\":\"456\",\"product_id\":\"abc\",\"client_type\":\"icity_ver\",\"org_code\":\"3501\",\"client_version\":\"700\",\"os_type\":\"android\",\"imsi\":\"123\",\"imei\":\"abc\",\"timestamp\":\"2014-09-16 12:00:01\"}";

		EasyMock.expect(request.getInputStream()).andReturn(new TestServletInputStream(json)).times(1);

		EasyMock.replay(request, response);

		controller.handleRequest(request, response);

		EasyMock.verify(request, response);

	}

	/**
	 * os_type为空
	 */
	@Test
	public void test7() throws ServletException, IOException {

		JSONController controller = new JSONController() {

			@Override
			public void verifyBusiArgument(Map<String, Object> requestArgument) throws InvalidRequestArgumentException {
			}

			@Override
			public Object handleRequest(Map<String, Object> requestArgument) throws ApiException {
				return null;
			}

			@Override
			public String[] getSignItems(Map<String, Object> requestArgument) {
				return null;
			}

			@Override
			public void handleException(Exception exception, HttpServletRequest request, HttpServletResponse response) {
				Assert.assertTrue(exception.getLocalizedMessage().contains("os_type"));
				super.handleException(exception, request, response);
			}

		};

		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);
		EasyMock.expect(response.getWriter()).andReturn(new PrintWriter(System.err)).times(1);

		EasyMock.expect(request.getMethod()).andReturn("POST").times(1);

		EasyMock.expect(request.getParameter(BaseController.REQUEST_PARAM_JSONP_CALLBACK)).andReturn(null).times(1);
		RequestContextHolder.setRequest(request);

		String json = "{\"city_code\":\"abc\",\"mobile\":\"189\",\"longitude\":\"123\",\"latitude\":\"456\",\"product_id\":\"abc\",\"client_type\":\"icity_ver\",\"org_code\":\"3501\",\"client_version\":\"700\",\"client_channel_type\":\"abc\",\"imsi\":\"123\",\"imei\":\"abc\",\"timestamp\":\"2014-09-16 12:00:01\"}";

		EasyMock.expect(request.getInputStream()).andReturn(new TestServletInputStream(json)).times(1);

		EasyMock.replay(request, response);

		controller.handleRequest(request, response);

		EasyMock.verify(request, response);

	}

	/**
	 * imsi为空
	 */
	@Test
	public void test8() throws ServletException, IOException {

		JSONController controller = new JSONController() {

			@Override
			public void verifyBusiArgument(Map<String, Object> requestArgument) throws InvalidRequestArgumentException {
			}

			@Override
			public Object handleRequest(Map<String, Object> requestArgument) throws ApiException {
				return null;
			}

			@Override
			public String[] getSignItems(Map<String, Object> requestArgument) {
				return null;
			}

			@Override
			public void handleException(Exception exception, HttpServletRequest request, HttpServletResponse response) {
				Assert.assertTrue(exception.getLocalizedMessage().contains("imsi"));
				super.handleException(exception, request, response);
			}

		};

		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);
		EasyMock.expect(response.getWriter()).andReturn(new PrintWriter(System.err)).times(1);

		EasyMock.expect(request.getMethod()).andReturn("POST").times(1);

		EasyMock.expect(request.getParameter(BaseController.REQUEST_PARAM_JSONP_CALLBACK)).andReturn(null).times(1);
		RequestContextHolder.setRequest(request);

		String json = "{\"city_code\":\"abc\",\"mobile\":\"189\",\"longitude\":\"123\",\"latitude\":\"456\",\"product_id\":\"abc\",\"client_type\":\"icity_ver\",\"org_code\":\"3501\",\"client_version\":\"700\",\"client_channel_type\":\"abc\",\"os_type\":\"ios\",\"imei\":\"abc\",\"timestamp\":\"2014-09-16 12:00:01\"}";

		EasyMock.expect(request.getInputStream()).andReturn(new TestServletInputStream(json)).times(1);

		EasyMock.replay(request, response);

		controller.handleRequest(request, response);

		EasyMock.verify(request, response);

	}

	/**
	 * imei为空
	 */
	@Test
	public void test9() throws ServletException, IOException {

		JSONController controller = new JSONController() {

			@Override
			public void verifyBusiArgument(Map<String, Object> requestArgument) throws InvalidRequestArgumentException {
			}

			@Override
			public Object handleRequest(Map<String, Object> requestArgument) throws ApiException {
				return null;
			}

			@Override
			public String[] getSignItems(Map<String, Object> requestArgument) {
				return null;
			}

			@Override
			public void handleException(Exception exception, HttpServletRequest request, HttpServletResponse response) {
				Assert.assertTrue(exception.getLocalizedMessage().contains("imei"));
				super.handleException(exception, request, response);
			}

		};

		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);
		EasyMock.expect(response.getWriter()).andReturn(new PrintWriter(System.err)).times(1);

		EasyMock.expect(request.getMethod()).andReturn("POST").times(1);

		EasyMock.expect(request.getParameter(BaseController.REQUEST_PARAM_JSONP_CALLBACK)).andReturn(null).times(1);
		RequestContextHolder.setRequest(request);

		String json = "{\"city_code\":\"abc\",\"mobile\":\"189\",\"longitude\":\"123\",\"latitude\":\"456\",\"product_id\":\"abc\",\"org_code\":\"3501\",\"client_type\":\"icity_ver\",\"client_version\":\"700\",\"client_channel_type\":\"abc\",\"os_type\":\"ios\",\"imsi\":\"123\",\"timestamp\":\"2014-09-16 12:00:01\"}";

		EasyMock.expect(request.getInputStream()).andReturn(new TestServletInputStream(json)).times(1);

		EasyMock.replay(request, response);

		controller.handleRequest(request, response);

		EasyMock.verify(request, response);

	}

	/**
	 * timestamp验证失败
	 */
	@Test
	public void test10() throws ServletException, IOException {

		JSONController controller = new JSONController() {

			@Override
			public void verifyBusiArgument(Map<String, Object> requestArgument) throws InvalidRequestArgumentException {
			}

			@Override
			public Object handleRequest(Map<String, Object> requestArgument) throws ApiException {
				return null;
			}

			@Override
			public String[] getSignItems(Map<String, Object> requestArgument) {
				return null;
			}

			@Override
			public void handleException(Exception exception, HttpServletRequest request, HttpServletResponse response) {
				Assert.assertTrue(exception.getLocalizedMessage().contains("invalid timestamp"));
				super.handleException(exception, request, response);
			}

		};

		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);
		EasyMock.expect(response.getWriter()).andReturn(new PrintWriter(System.err)).times(1);

		EasyMock.expect(request.getMethod()).andReturn("POST").times(1);

		EasyMock.expect(request.getParameter(BaseController.REQUEST_PARAM_JSONP_CALLBACK)).andReturn(null).times(1);
		RequestContextHolder.setRequest(request);

		String json = "{\"city_code\":\"abc\",\"mobile\":\"189\",\"longitude\":\"123\",\"latitude\":\"456\",\"product_id\":\"abc\",\"org_code\":\"3501\",\"client_type\":\"icity_ver\",\"client_version\":\"700\",\"client_channel_type\":\"abc\",\"os_type\":\"ios\",\"imsi\":\"123\",\"imei\":\"abc\",\"timestamp\":\"2014-09-12 22:01\"}";

		EasyMock.expect(request.getInputStream()).andReturn(new TestServletInputStream(json)).times(1);

		EasyMock.replay(request, response);

		controller.handleRequest(request, response);

		EasyMock.verify(request, response);

	}

	/**
	 * timestamp为空
	 */
	@Test
	public void test11() throws ServletException, IOException {

		JSONController controller = new JSONController() {

			@Override
			public void verifyBusiArgument(Map<String, Object> requestArgument) throws InvalidRequestArgumentException {
			}

			@Override
			public Object handleRequest(Map<String, Object> requestArgument) throws ApiException {
				return null;
			}

			@Override
			public String[] getSignItems(Map<String, Object> requestArgument) {
				return null;
			}

			@Override
			public void handleException(Exception exception, HttpServletRequest request, HttpServletResponse response) {
				Assert.assertTrue(exception.getLocalizedMessage().contains("timestamp"));
				super.handleException(exception, request, response);
			}

		};

		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);
		EasyMock.expect(response.getWriter()).andReturn(new PrintWriter(System.err)).times(1);

		EasyMock.expect(request.getMethod()).andReturn("POST").times(1);

		EasyMock.expect(request.getParameter(BaseController.REQUEST_PARAM_JSONP_CALLBACK)).andReturn(null).times(1);
		RequestContextHolder.setRequest(request);

		String json = "{\"city_code\":\"abc\",\"mobile\":\"189\",\"longitude\":\"123\",\"latitude\":\"456\",\"product_id\":\"abc\",\"org_code\":\"3501\",\"client_type\":\"icity_ver\",\"client_version\":\"700\",\"client_channel_type\":\"abc\",\"os_type\":\"ios\",\"imsi\":\"123\",\"imei\":\"abc\"}";

		EasyMock.expect(request.getInputStream()).andReturn(new TestServletInputStream(json)).times(1);

		EasyMock.replay(request, response);

		controller.handleRequest(request, response);

		EasyMock.verify(request, response);

	}

	/**
	 * 请求签名过期
	 * 
	 * @throws Exception
	 */
	@Test
	public void test111() throws Exception {
		JSONController controller = new JSONController() {

			@Override
			public void verifyBusiArgument(Map<String, Object> requestArgument) throws InvalidRequestArgumentException {
			}

			@Override
			public Object handleRequest(Map<String, Object> requestArgument) throws ApiException {
				throw new RuntimeException("");
			}

			@Override
			public String[] getSignItems(Map<String, Object> requestArgument) {
				return new String[] { (String) requestArgument.get("imsi"), (String) requestArgument.get("imei") };
			}

			@Override
			public void handleException(Exception exception, HttpServletRequest request, HttpServletResponse response) {
				Assert.assertTrue(exception.getLocalizedMessage().contains("invalid request sign"));
				super.handleException(exception, request, response);
			}

			@Override
			public ApiApplicationConfig getApplicationConfig() {
				return new ApiApplicationConfig() {

					@Override
					public int getRequestSignExpiry() {
						return 1;
					}

					@Override
					public boolean isVerifyRequestSign() {
						return true;
					}

					@Override
					public String getDes3Key() {
						return "b5eefe0437d945b98e82f46fbff8d3552c2ff6f7f8acd8de";
					}

					@Override
					public String getAppKey() {
						return "75BD2E98AC17564B2DB7C74B064F5084C6557FDDF3E4C286";
					}

				};
			}

		};

		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);
		EasyMock.expect(response.getWriter()).andReturn(new PrintWriter(System.err)).times(1);

		EasyMock.expect(request.getMethod()).andReturn("POST").times(1);

		EasyMock.expect(request.getParameter(BaseController.REQUEST_PARAM_JSONP_CALLBACK)).andReturn(null).times(1);
		RequestContextHolder.setRequest(request);

		String imsi = "123";

		String imei = "abc";

		String timestamp = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");

		String sign = CrytoUtils.encode(controller.getDes3Key(), timestamp, CrytoUtils.md5(imsi, imei, controller.getAppKey(), timestamp));

		String json = "{\"city_code\":\"abc\",\"mobile\":\"189\",\"longitude\":\"123\",\"latitude\":\"456\",\"product_id\":\"abc\",\"org_code\":\"3501\",\"client_type\":\"icity_ver\",\"client_version\":\"700\",\"client_channel_type\":\"abc\",\"os_type\":\"ios\",\"imsi\":\"" + imsi + "\",\"imei\":\"" + imei + "\",\"timestamp\":\"" + timestamp + "\",\"sign\":\"" + sign + "\"}";

		EasyMock.expect(request.getInputStream()).andReturn(new TestServletInputStream(json)).times(1);

		EasyMock.replay(request, response);

		Thread.sleep(2000);

		controller.handleRequest(request, response);

		EasyMock.verify(request, response);
	}

	/**
	 * 关闭请求签名过期验证
	 * 
	 * @throws Exception
	 */
	@Test
	public void test112() throws Exception {

		final String responseContent = "abcAFDSFAFASFASFF@$$@#%@";

		JSONController controller = new JSONController() {

			@Override
			public void verifyBusiArgument(Map<String, Object> requestArgument) throws InvalidRequestArgumentException {
			}

			@Override
			public Object handleRequest(Map<String, Object> requestArgument) throws ApiException {
				return responseContent;
			}

			@Override
			public String[] getSignItems(Map<String, Object> requestArgument) {
				return new String[] { (String) requestArgument.get("imsi"), (String) requestArgument.get("imei") };
			}

			@Override
			public void handleException(Exception exception, HttpServletRequest request, HttpServletResponse response) {
				throw new RuntimeException(exception);
			}

			@Override
			public void out(HttpServletResponse response, String content) {
				Assert.assertTrue(content.contains(responseContent));
				super.out(response, content);
			}

			@Override
			public ApiApplicationConfig getApplicationConfig() {
				return new ApiApplicationConfig() {

					@Override
					public int getRequestSignExpiry() {
						return 0;
					}

					@Override
					public boolean isVerifyRequestSign() {
						return true;
					}

					@Override
					public String getDes3Key() {
						return "b5eefe0437d945b98e82f46fbff8d3552c2ff6f7f8acd8de";
					}

					@Override
					public String getAppKey() {
						return "75BD2E98AC17564B2DB7C74B064F5084C6557FDDF3E4C286";
					}

				};
			}

		};

		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);
		EasyMock.expect(response.getWriter()).andReturn(new PrintWriter(System.err)).times(1);

		EasyMock.expect(request.getMethod()).andReturn("POST").times(1);

		EasyMock.expect(request.getParameter(BaseController.REQUEST_PARAM_JSONP_CALLBACK)).andReturn(null).times(1);
		RequestContextHolder.setRequest(request);

		String imsi = "123";

		String imei = "abc";

		String timestamp = "2014-01-01 11:11:00";

		String sign = CrytoUtils.encode(controller.getDes3Key(), timestamp, CrytoUtils.md5(imsi, imei, controller.getAppKey(), timestamp));

		String json = "{\"city_code\":\"abc\",\"mobile\":\"189\",\"longitude\":\"123\",\"latitude\":\"456\",\"product_id\":\"abc\",\"org_code\":\"3501\",\"client_type\":\"icity_ver\",\"client_version\":\"700\",\"client_channel_type\":\"abc\",\"os_type\":\"ios\",\"imsi\":\"" + imsi + "\",\"imei\":\"" + imei + "\",\"timestamp\":\"" + timestamp + "\",\"sign\":\"" + sign + "\"}";

		EasyMock.expect(request.getInputStream()).andReturn(new TestServletInputStream(json)).times(1);

		EasyMock.replay(request, response);

		controller.handleRequest(request, response);

		EasyMock.verify(request, response);
	}

	/**
	 * 业务参数验证失败
	 */
	@Test
	public void test12() throws ServletException, IOException {

		JSONController controller = new JSONController() {

			@Override
			public void verifyBusiArgument(Map<String, Object> requestArgument) throws InvalidRequestArgumentException {
				String mobile = (String) requestArgument.get("mobile");
				if (!mobile.equals("123")) {
					throw new InvalidRequestArgumentException("abc");
				}
			}

			@Override
			public Object handleRequest(Map<String, Object> requestArgument) throws ApiException {
				return null;
			}

			@Override
			public String[] getSignItems(Map<String, Object> requestArgument) {
				return null;
			}

			@Override
			public void handleException(Exception exception, HttpServletRequest request, HttpServletResponse response) {
				Assert.assertTrue(exception.getLocalizedMessage().equals("abc"));
				super.handleException(exception, request, response);
			}

		};

		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);
		EasyMock.expect(response.getWriter()).andReturn(new PrintWriter(System.err)).times(1);

		EasyMock.expect(request.getMethod()).andReturn("POST").times(1);

		EasyMock.expect(request.getParameter(BaseController.REQUEST_PARAM_JSONP_CALLBACK)).andReturn(null).times(1);
		RequestContextHolder.setRequest(request);

		String json = "{\"city_code\":\"abc\",\"mobile\":\"189\",\"longitude\":\"123\",\"latitude\":\"456\",\"product_id\":\"abc\",\"org_code\":\"3501\",\"client_type\":\"icity_ver\",\"client_version\":\"700\",\"client_channel_type\":\"123\",\"os_type\":\"android\",\"imsi\":\"123\",\"imei\":\"abc\",\"timestamp\":\"2014-09-16 22:00:01\",\"mobile\":\"12345678901\"}";

		EasyMock.expect(request.getInputStream()).andReturn(new TestServletInputStream(json)).times(1);

		EasyMock.replay(request, response);

		controller.handleRequest(request, response);

		EasyMock.verify(request, response);

	}

	/**
	 * 请求执行成功.handleRequest()方法返回map
	 */
	@Test
	public void test13() throws ServletException, IOException {

		JSONController controller = new JSONController() {

			@Override
			public void verifyBusiArgument(Map<String, Object> requestArgument) throws InvalidRequestArgumentException {
			}

			@Override
			public Object handleRequest(Map<String, Object> requestArgument) throws ApiException {
				Map<String, Object> resultData = new HashMap<String, Object>();
				resultData.put("user_name", "abc");
				return resultData;
			}

			@Override
			public String[] getSignItems(Map<String, Object> requestArgument) {
				return new String[] { (String) requestArgument.get("imsi"), (String) requestArgument.get("imei") };
			}

			@Override
			public void out(HttpServletResponse response, String content) {
				Assert.assertTrue(content.contains("user_name"));
				super.out(response, content);
			}

			@Override
			public ApiApplicationConfig getApplicationConfig() {
				return new ApiApplicationConfig() {

					@Override
					public int getRequestSignExpiry() {
						return 60;
					}

					@Override
					public boolean isVerifyRequestSign() {
						return true;
					}

					@Override
					public String getDes3Key() {
						return "b5eefe0437d945b98e82f46fbff8d3552c2ff6f7f8acd8de";
					}

					@Override
					public String getAppKey() {
						return "75BD2E98AC17564B2DB7C74B064F5084C6557FDDF3E4C286";
					}

				};
			}

		};

		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);
		EasyMock.expect(response.getWriter()).andReturn(new PrintWriter(System.err)).times(1);

		EasyMock.expect(request.getMethod()).andReturn("POST").times(1);

		EasyMock.expect(request.getParameter(BaseController.REQUEST_PARAM_JSONP_CALLBACK)).andReturn(null).times(1);
		RequestContextHolder.setRequest(request);

		String json = "{\"city_code\":\"abc\",\"mobile\":\"189\",\"longitude\":\"123\",\"latitude\":\"456\",\"product_id\":\"abc\",\"org_code\":\"3501\",\"client_type\":\"icity_ver\",\"client_version\":\"700\",\"client_channel_type\":\"123\",\"os_type\":\"android\",\"imsi\":\"123\",\"imei\":\"abc\",\"timestamp\":\"2022-09-16 17:08:00\",\"sign\":\"9qS0v1ImAICQbT8qFx6TAMZnLrOXPQBK6L%2FL%2Fhg4KuC%2BRw3m9RQ1ilTvzx7ovkzyyPFWHva0H%2BQ%3D\"}";

		EasyMock.expect(request.getInputStream()).andReturn(new TestServletInputStream(json)).times(1);

		EasyMock.replay(request, response);

		controller.handleRequest(request, response);

		EasyMock.verify(request, response);
	}

	public static class Bean {

		public Bean(String userName) {
			super();
			this.userName = userName;
		}

		private String userName;

		public String getUserName() {
			return this.userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

	}

	/**
	 * 请求执行成功.handleRequest()方法返回object
	 */
	@Test
	public void test14() throws ServletException, IOException {

		JSONController controller = new JSONController() {

			@Override
			public void verifyBusiArgument(Map<String, Object> requestArgument) throws InvalidRequestArgumentException {
			}

			@Override
			public Object handleRequest(Map<String, Object> requestArgument) throws ApiException {
				return new Bean("abc");
			}

			@Override
			public String[] getSignItems(Map<String, Object> requestArgument) {
				return new String[] { (String) requestArgument.get("imsi"), (String) requestArgument.get("imei") };
			}

			@Override
			public void out(HttpServletResponse response, String content) {
				Assert.assertTrue(content.contains("user_name"));
				super.out(response, content);
			}

			@Override
			public ApiApplicationConfig getApplicationConfig() {
				return new ApiApplicationConfig() {

					@Override
					public int getRequestSignExpiry() {
						return 60;
					}

					@Override
					public boolean isVerifyRequestSign() {
						return true;
					}

					@Override
					public String getDes3Key() {
						return "b5eefe0437d945b98e82f46fbff8d3552c2ff6f7f8acd8de";
					}

					@Override
					public String getAppKey() {
						return "75BD2E98AC17564B2DB7C74B064F5084C6557FDDF3E4C286";
					}

				};
			}

		};

		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);
		EasyMock.expect(response.getWriter()).andReturn(new PrintWriter(System.err)).times(1);

		EasyMock.expect(request.getMethod()).andReturn("POST").times(1);

		EasyMock.expect(request.getParameter(BaseController.REQUEST_PARAM_JSONP_CALLBACK)).andReturn(null).times(1);
		RequestContextHolder.setRequest(request);

		String json = "{\"city_code\":\"abc\",\"mobile\":\"189\",\"longitude\":\"123\",\"latitude\":\"456\",\"product_id\":\"abc\",\"org_code\":\"3501\",\"client_type\":\"icity_ver\",\"client_version\":\"700\",\"client_channel_type\":\"123\",\"os_type\":\"android\",\"imsi\":\"123\",\"imei\":\"abc\",\"timestamp\":\"2022-09-16 17:08:00\",\"sign\":\"9qS0v1ImAICQbT8qFx6TAMZnLrOXPQBK6L%2FL%2Fhg4KuC%2BRw3m9RQ1ilTvzx7ovkzyyPFWHva0H%2BQ%3D\"}";

		EasyMock.expect(request.getInputStream()).andReturn(new TestServletInputStream(json)).times(1);

		EasyMock.replay(request, response);

		controller.handleRequest(request, response);

		EasyMock.verify(request, response);

	}

	/**
	 * 关闭请求签名验证
	 */
	@Test
	public void test15() throws ServletException, IOException {

		JSONController controller = new JSONController() {

			@Override
			public void verifyBusiArgument(Map<String, Object> requestArgument) throws InvalidRequestArgumentException {
			}

			@Override
			public Object handleRequest(Map<String, Object> requestArgument) throws ApiException {
				return new Bean("abc");
			}

			@Override
			public String[] getSignItems(Map<String, Object> requestArgument) {
				return new String[] { (String) requestArgument.get("imsi"), (String) requestArgument.get("imei") };
			}

			@Override
			public void out(HttpServletResponse response, String content) {
				Assert.assertTrue(content.contains("user_name"));
				super.out(response, content);
			}

			@Override
			public ApiApplicationConfig getApplicationConfig() {
				return new ApiApplicationConfig() {

					@Override
					public int getRequestSignExpiry() {
						return 60;
					}

					@Override
					public boolean isVerifyRequestSign() {
						return true;
					}

					@Override
					public String getDes3Key() {
						return "b5eefe0437d945b98e82f46fbff8d3552c2ff6f7f8acd8de";
					}

					@Override
					public String getAppKey() {
						return "75BD2E98AC17564B2DB7C74B064F5084C6557FDDF3E4C286";
					}

				};
			}

			@Override
			public boolean isVerifyRequestSign() {
				return false;
			}

		};

		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);
		EasyMock.expect(response.getWriter()).andReturn(new PrintWriter(System.err)).times(1);

		EasyMock.expect(request.getMethod()).andReturn("POST").times(1);

		EasyMock.expect(request.getParameter(BaseController.REQUEST_PARAM_JSONP_CALLBACK)).andReturn(null).times(1);
		RequestContextHolder.setRequest(request);

		String json = "{\"city_code\":\"abc\",\"mobile\":\"189\",\"longitude\":\"123\",\"latitude\":\"456\",\"product_id\":\"abc\",\"org_code\":\"3501\",\"client_type\":\"icity_ver\",\"client_version\":\"700\",\"client_channel_type\":\"123\",\"os_type\":\"android\",\"imsi\":\"123\",\"imei\":\"abc\",\"timestamp\":\"2022-09-16 17:08:00\",\"sign\":\"abc\"}";

		EasyMock.expect(request.getInputStream()).andReturn(new TestServletInputStream(json)).times(1);

		EasyMock.replay(request, response);

		controller.handleRequest(request, response);

		EasyMock.verify(request, response);

	}

	/**
	 * client_type为空
	 */
	@Test
	public void test16() throws ServletException, IOException {

		JSONController controller = new JSONController() {

			@Override
			public void verifyBusiArgument(Map<String, Object> requestArgument) throws InvalidRequestArgumentException {
				throw new InvalidRequestArgumentException("abc");
			}

			@Override
			public Object handleRequest(Map<String, Object> requestArgument) throws ApiException {
				return null;
			}

			@Override
			public String[] getSignItems(Map<String, Object> requestArgument) {
				return null;
			}

			@Override
			public void handleException(Exception exception, HttpServletRequest request, HttpServletResponse response) {
				Assert.assertTrue(exception.getLocalizedMessage().contains("client_type"));
				super.handleException(exception, request, response);
			}

		};

		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);
		EasyMock.expect(response.getWriter()).andReturn(new PrintWriter(System.err)).times(1);
		EasyMock.expect(request.getMethod()).andReturn("POST").times(1);

		EasyMock.expect(request.getParameter(BaseController.REQUEST_PARAM_JSONP_CALLBACK)).andReturn(null).times(1);
		RequestContextHolder.setRequest(request);

		String json = "{\"city_code\":\"abc\",\"mobile\":\"189\",\"longitude\":\"123\",\"latitude\":\"456\",\"product_id\":\"abc\",\"org_code\":\"3501\",\"client_version\":\"700\",\"client_channel_type\":\"123\",\"os_type\":\"android\",\"imsi\":\"123\",\"imei\":\"abc\",\"timestamp\":\"2014-09-16 22:00:01\"}";

		EasyMock.expect(request.getInputStream()).andReturn(new TestServletInputStream(json)).times(1);

		EasyMock.replay(request, response);

		controller.handleRequest(request, response);

		EasyMock.verify(request, response);

	}

	/**
	 * city_code为空
	 */
	@Test
	public void test17() throws ServletException, IOException {

		JSONController controller = new JSONController() {

			@Override
			public void verifyBusiArgument(Map<String, Object> requestArgument) throws InvalidRequestArgumentException {
				throw new InvalidRequestArgumentException("abc");
			}

			@Override
			public Object handleRequest(Map<String, Object> requestArgument) throws ApiException {
				return null;
			}

			@Override
			public String[] getSignItems(Map<String, Object> requestArgument) {
				return null;
			}

			@Override
			public void handleException(Exception exception, HttpServletRequest request, HttpServletResponse response) {
				Assert.assertTrue(exception.getLocalizedMessage().contains("city_code"));
				super.handleException(exception, request, response);
			}

		};

		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);
		EasyMock.expect(response.getWriter()).andReturn(new PrintWriter(System.err)).times(1);
		EasyMock.expect(request.getMethod()).andReturn("POST").times(1);

		EasyMock.expect(request.getParameter(BaseController.REQUEST_PARAM_JSONP_CALLBACK)).andReturn(null).times(1);
		RequestContextHolder.setRequest(request);

		String json = "{\"client_type\":\"icity_ver\",\"mobile\":\"189\",\"longitude\":\"123\",\"latitude\":\"456\",\"product_id\":\"abc\",\"org_code\":\"3501\",\"client_version\":\"700\",\"client_channel_type\":\"123\",\"os_type\":\"android\",\"imsi\":\"123\",\"imei\":\"abc\",\"timestamp\":\"2014-09-16 22:00:01\"}";

		EasyMock.expect(request.getInputStream()).andReturn(new TestServletInputStream(json)).times(1);

		EasyMock.replay(request, response);

		controller.handleRequest(request, response);

		EasyMock.verify(request, response);

	}

	/**
	 * mobile为空
	 */
	@Test
	public void test18() throws ServletException, IOException {

		JSONController controller = new JSONController() {

			@Override
			public void verifyBusiArgument(Map<String, Object> requestArgument) throws InvalidRequestArgumentException {
				throw new InvalidRequestArgumentException("abc");
			}

			@Override
			public Object handleRequest(Map<String, Object> requestArgument) throws ApiException {
				return null;
			}

			@Override
			public String[] getSignItems(Map<String, Object> requestArgument) {
				return null;
			}

			@Override
			public void handleException(Exception exception, HttpServletRequest request, HttpServletResponse response) {
				Assert.assertTrue(exception.getLocalizedMessage().contains("mobile"));
				super.handleException(exception, request, response);
			}

		};

		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);
		EasyMock.expect(response.getWriter()).andReturn(new PrintWriter(System.err)).times(1);
		EasyMock.expect(request.getMethod()).andReturn("POST").times(1);

		EasyMock.expect(request.getParameter(BaseController.REQUEST_PARAM_JSONP_CALLBACK)).andReturn(null).times(1);
		RequestContextHolder.setRequest(request);

		String json = "{\"city_code\":\"abc\",\"client_type\":\"icity_ver\",\"longitude\":\"123\",\"latitude\":\"456\",\"product_id\":\"abc\",\"org_code\":\"3501\",\"client_version\":\"700\",\"client_channel_type\":\"123\",\"os_type\":\"android\",\"imsi\":\"123\",\"imei\":\"abc\",\"timestamp\":\"2014-09-16 22:00:01\"}";

		EasyMock.expect(request.getInputStream()).andReturn(new TestServletInputStream(json)).times(1);

		EasyMock.replay(request, response);

		controller.handleRequest(request, response);

		EasyMock.verify(request, response);

	}

	/**
	 * longitude为空
	 */
	@Test
	public void test19() throws ServletException, IOException {

		JSONController controller = new JSONController() {

			@Override
			public void verifyBusiArgument(Map<String, Object> requestArgument) throws InvalidRequestArgumentException {
				throw new InvalidRequestArgumentException("abc");
			}

			@Override
			public Object handleRequest(Map<String, Object> requestArgument) throws ApiException {
				return null;
			}

			@Override
			public String[] getSignItems(Map<String, Object> requestArgument) {
				return null;
			}

			@Override
			public void handleException(Exception exception, HttpServletRequest request, HttpServletResponse response) {
				Assert.assertTrue(exception.getLocalizedMessage().contains("longitude"));
				super.handleException(exception, request, response);
			}

		};

		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);
		EasyMock.expect(response.getWriter()).andReturn(new PrintWriter(System.err)).times(1);
		EasyMock.expect(request.getMethod()).andReturn("POST").times(1);

		EasyMock.expect(request.getParameter(BaseController.REQUEST_PARAM_JSONP_CALLBACK)).andReturn(null).times(1);
		RequestContextHolder.setRequest(request);

		String json = "{\"product_id\":\"abc\",\"org_code\":\"3501\",\"client_type\":\"icity_ver\",\"client_version\":\"700\",\"client_channel_type\":\"123\",\"os_type\":\"android\",\"imsi\":\"123\",\"imei\":\"abc\",\"timestamp\":\"2014-09-16 22:00:01\",\"city_code\":\"abc\",\"mobile\":\"189\",\"latitude\":\"456\"}";

		EasyMock.expect(request.getInputStream()).andReturn(new TestServletInputStream(json)).times(1);

		EasyMock.replay(request, response);

		controller.handleRequest(request, response);

		EasyMock.verify(request, response);

	}

	/**
	 * latitude为空
	 */
	@Test
	public void test20() throws ServletException, IOException {

		JSONController controller = new JSONController() {

			@Override
			public void verifyBusiArgument(Map<String, Object> requestArgument) throws InvalidRequestArgumentException {
				throw new InvalidRequestArgumentException("abc");
			}

			@Override
			public Object handleRequest(Map<String, Object> requestArgument) throws ApiException {
				return null;
			}

			@Override
			public String[] getSignItems(Map<String, Object> requestArgument) {
				return null;
			}

			@Override
			public void handleException(Exception exception, HttpServletRequest request, HttpServletResponse response) {
				Assert.assertTrue(exception.getLocalizedMessage().contains("latitude"));
				super.handleException(exception, request, response);
			}

		};

		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);
		EasyMock.expect(response.getWriter()).andReturn(new PrintWriter(System.err)).times(1);
		EasyMock.expect(request.getMethod()).andReturn("POST").times(1);

		EasyMock.expect(request.getParameter(BaseController.REQUEST_PARAM_JSONP_CALLBACK)).andReturn(null).times(1);
		RequestContextHolder.setRequest(request);

		String json = "{\"product_id\":\"abc\",\"org_code\":\"3501\",\"client_type\":\"icity_ver\",\"client_version\":\"700\",\"client_channel_type\":\"123\",\"os_type\":\"android\",\"imsi\":\"123\",\"imei\":\"abc\",\"timestamp\":\"2014-09-16 22:00:01\",\"city_code\":\"abc\",\"mobile\":\"189\",\"longitude\":\"123\"}";

		EasyMock.expect(request.getInputStream()).andReturn(new TestServletInputStream(json)).times(1);

		EasyMock.replay(request, response);

		controller.handleRequest(request, response);

		EasyMock.verify(request, response);

	}

	/**
	 * jsonp请求
	 */
	@Test
	public void test21() throws ServletException, IOException {

		JSONController controller = new JSONController() {

			@Override
			public void verifyBusiArgument(Map<String, Object> requestArgument) throws InvalidRequestArgumentException {

			}

			@Override
			public Object handleRequest(Map<String, Object> requestArgument) throws ApiException {
				return null;
			}

			@Override
			public String[] getSignItems(Map<String, Object> requestArgument) {
				return null;
			}

			@Override
			public void handleException(Exception exception, HttpServletRequest request, HttpServletResponse response) {
				Assert.assertTrue(exception.getLocalizedMessage().contains("request body"));
				super.handleException(exception, request, response);
			}

		};

		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		EasyMock.expect(response.getWriter()).andReturn(new PrintWriter(bos)).times(1);
		EasyMock.expect(request.getMethod()).andReturn("POST").times(1);

		String jsonpCallback = "test";
		EasyMock.expect(request.getParameter(BaseController.REQUEST_PARAM_JSONP_CALLBACK)).andReturn(jsonpCallback).times(1);
		RequestContextHolder.setRequest(request);

		String json = "";

		EasyMock.expect(request.getInputStream()).andReturn(new TestServletInputStream(json)).times(1);

		EasyMock.replay(request, response);

		controller.handleRequest(request, response);

		EasyMock.verify(request, response);

		Assert.assertTrue(bos.toString("UTF-8").startsWith(jsonpCallback + "("));

	}

	/**
	 * 非jsonp请求
	 */
	@Test
	public void test22() throws ServletException, IOException {

		JSONController controller = new JSONController() {

			@Override
			public void verifyBusiArgument(Map<String, Object> requestArgument) throws InvalidRequestArgumentException {

			}

			@Override
			public Object handleRequest(Map<String, Object> requestArgument) throws ApiException {
				return null;
			}

			@Override
			public String[] getSignItems(Map<String, Object> requestArgument) {
				return null;
			}

			@Override
			public void handleException(Exception exception, HttpServletRequest request, HttpServletResponse response) {
				Assert.assertTrue(exception.getLocalizedMessage().contains("request body"));
				super.handleException(exception, request, response);
			}

		};

		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		EasyMock.expect(response.getWriter()).andReturn(new PrintWriter(bos)).times(1);
		EasyMock.expect(request.getMethod()).andReturn("POST").times(1);

		EasyMock.expect(request.getParameter(BaseController.REQUEST_PARAM_JSONP_CALLBACK)).andReturn(null).times(1);
		RequestContextHolder.setRequest(request);

		String json = "";

		EasyMock.expect(request.getInputStream()).andReturn(new TestServletInputStream(json)).times(1);

		EasyMock.replay(request, response);

		controller.handleRequest(request, response);

		EasyMock.verify(request, response);

		Assert.assertTrue(bos.toString("UTF-8").startsWith("{"));

	}

}
