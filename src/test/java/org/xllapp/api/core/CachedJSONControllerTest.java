package org.xllapp.api.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.xllapp.api.core.BaseController;
import org.xllapp.api.core.CachedController;
import org.xllapp.api.core.CachedJSONController;
import org.xllapp.api.core.JSONControllerTest.TestServletInputStream;
import org.xllapp.api.core.exception.ApiException;
import org.xllapp.api.core.exception.InvalidRequestArgumentException;
import org.xllapp.api.support.ApiApplicationConfig;
import org.xllapp.api.support.RequestContextHolder;
import org.xllapp.api.util.JSONHelper;

import org.xllapp.cache.CacheCallback;
import org.xllapp.cache.CacheProvider;
import org.xllapp.cache.CacheTemplate;
import org.xllapp.cache.CacheValue;

/**
 *
 *
 * @Copyright: Copyright (c) 2013 FFCS All Rights Reserved
 * @Company: 北京福富软件有限公司
 * @author 陈作朋 Sep 24, 2013
 * @version 1.00.00
 * @history:
 * 
 */
public class CachedJSONControllerTest {

	/**
	 * 无缓存
	 */
	@Test
	public void testCachedController1() throws Exception {

		final String key = "key";

		final String value = "value";

		final int expiry = Integer.MAX_VALUE;

		HttpServletRequest request = EasyMock.createNiceMock(HttpServletRequest.class);
		
		EasyMock.expect(request.getParameter(BaseController.REQUEST_PARAM_JSONP_CALLBACK)).andReturn(null).times(1);
		RequestContextHolder.setRequest(request);
		
		EasyMock.expect(request.getMethod()).andReturn("POST").times(1);

		String json = "{\"city_code\":\"abc\",\"mobile\":\"189\",\"longitude\":\"123\",\"latitude\":\"456\",\"product_id\":\"abc\",\"org_code\":\"3501\",\"client_type\":\"icity_ver\",\"client_version\":\"700\",\"client_channel_type\":\"123\",\"os_type\":\"android\",\"imsi\":\"123\",\"imei\":\"abc\",\"timestamp\":\"2022-09-16 17:08:00\",\"sign\":\"9qS0v1ImAICQbT8qFx6TAMZnLrOXPQBK6L%2FL%2Fhg4KuC%2BRw3m9RQ1ilTvzx7ovkzyyPFWHva0H%2BQ%3D\"}";

		EasyMock.expect(request.getInputStream()).andReturn(new TestServletInputStream(json)).times(1);
		
		HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);

		ByteArrayOutputStream os = new ByteArrayOutputStream();

		EasyMock.expect(response.getWriter()).andReturn(new PrintWriter(os)).times(1);
		
		CachedJSONController cachedController = new CachedJSONController() {

			@Override
			public String getCacheKey(Map<String, Object> requestArgument) {
				return key;
			}

			@Override
			public int getCacheExpiry(Map<String, Object> requestArgument) {
				return expiry;
			}

			@Override
			public void verifyBusiArgument(Map<String, Object> requestArgument) throws InvalidRequestArgumentException {

			}

			@Override
			public String[] getSignItems(Map<String, Object> requestArgument) {
				return new String[] { (String) requestArgument.get("imsi"), (String) requestArgument.get("imei") };
			}

			@Override
			public Object handleRequest(Map<String, Object> requestArgument) throws ApiException {
				return value;
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

		CacheProvider cacheProvider = EasyMock.createMock(CacheProvider.class);

		String fullCacheKey = cachedController.getClass().getName() + "." + key;

		EasyMock.expect(cacheProvider.getCache(fullCacheKey)).andReturn(null).times(1);

		cacheProvider.addCache(EasyMock.eq(fullCacheKey), EasyMock.anyObject(CacheValue.class));

		EasyMock.expectLastCall().times(1);

		CacheTemplate cacheTemplate = new CacheTemplate(cacheProvider);

		cachedController.setCacheTemplate(cacheTemplate);

		EasyMock.replay(request, response, cacheProvider);

		cachedController.handleRequest(request, response);
		
		try {
			Thread.sleep(1000); //等待异步执行的cacheProvider.addCache()执行完成
		} catch (Exception e) {
		}

		String actual = JSONHelper.getValue(new String(os.toByteArray()), "data");

		Assert.assertEquals(value, actual);

		EasyMock.verify(request, response, cacheProvider);

	}

	/**
	 * 缓存命中并且缓存有效
	 */
	@Test
	public void testCachedController1a() throws Exception {

		final String key = "key";

		final String value = "value";

		final int expiry = Integer.MAX_VALUE;

		HttpServletRequest request = EasyMock.createNiceMock(HttpServletRequest.class);
		
		EasyMock.expect(request.getParameter(BaseController.REQUEST_PARAM_JSONP_CALLBACK)).andReturn(null).times(1);
		RequestContextHolder.setRequest(request);
		
		EasyMock.expect(request.getMethod()).andReturn("POST").times(1);

		String json = "{\"city_code\":\"abc\",\"mobile\":\"189\",\"longitude\":\"123\",\"latitude\":\"456\",\"product_id\":\"abc\",\"org_code\":\"3501\",\"client_type\":\"icity_ver\",\"client_version\":\"700\",\"client_channel_type\":\"123\",\"os_type\":\"android\",\"imsi\":\"123\",\"imei\":\"abc\",\"timestamp\":\"2022-09-16 17:08:00\",\"sign\":\"9qS0v1ImAICQbT8qFx6TAMZnLrOXPQBK6L%2FL%2Fhg4KuC%2BRw3m9RQ1ilTvzx7ovkzyyPFWHva0H%2BQ%3D\"}";

		EasyMock.expect(request.getInputStream()).andReturn(new TestServletInputStream(json)).times(1);

		HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);

		ByteArrayOutputStream os = new ByteArrayOutputStream();

		EasyMock.expect(response.getWriter()).andReturn(new PrintWriter(os)).times(1);

		CachedJSONController cachedController = new CachedJSONController() {

			@Override
			public String getCacheKey(Map<String, Object> requestArgument) {
				return key;
			}

			@Override
			public int getCacheExpiry(Map<String, Object> requestArgument) {
				return expiry;
			}

			@Override
			public void verifyBusiArgument(Map<String, Object> requestArgument) throws InvalidRequestArgumentException {

			}

			@Override
			public String[] getSignItems(Map<String, Object> requestArgument) {
				return new String[] { (String) requestArgument.get("imsi"), (String) requestArgument.get("imei") };
			}

			@Override
			public Object handleRequest(Map<String, Object> requestArgument) throws ApiException {
				return null;
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

		CacheProvider cacheProvider = EasyMock.createMock(CacheProvider.class);

		CacheValue cacheValue = new CacheValue(value);

		String fullCacheKey = cachedController.getClass().getName() + "." + key;

		EasyMock.expect(cacheProvider.getCache(fullCacheKey)).andReturn(cacheValue).times(1);

		CacheTemplate cacheTemplate = new CacheTemplate(cacheProvider);

		cachedController.setCacheTemplate(cacheTemplate);

		EasyMock.replay(request, response, cacheProvider);

		cachedController.handleRequest(request, response);

		String actual = new String(os.toByteArray());

		Assert.assertEquals(value, actual);

		EasyMock.verify(request, response, cacheProvider);

	}
	
	/**
	 * 缓存命中并且缓存过期,全局过期时间
	 */
	@Test
	public void testCachedController1b() throws Exception {

		final String key = "key";

		final String newValue = "newValue";

		final String oldValue = "oldValue";

		final int expiry = -1;

		HttpServletRequest request = EasyMock.createNiceMock(HttpServletRequest.class);
		
		EasyMock.expect(request.getParameter(BaseController.REQUEST_PARAM_JSONP_CALLBACK)).andReturn(null).times(1);
		RequestContextHolder.setRequest(request);
		
		EasyMock.expect(request.getMethod()).andReturn("POST").times(1);

		String json = "{\"city_code\":\"abc\",\"mobile\":\"189\",\"longitude\":\"123\",\"latitude\":\"456\",\"product_id\":\"abc\",\"org_code\":\"3501\",\"client_type\":\"icity_ver\",\"client_version\":\"700\",\"client_channel_type\":\"123\",\"os_type\":\"android\",\"imsi\":\"123\",\"imei\":\"abc\",\"timestamp\":\"2022-09-16 17:08:00\",\"sign\":\"9qS0v1ImAICQbT8qFx6TAMZnLrOXPQBK6L%2FL%2Fhg4KuC%2BRw3m9RQ1ilTvzx7ovkzyyPFWHva0H%2BQ%3D\"}";

		EasyMock.expect(request.getInputStream()).andReturn(new TestServletInputStream(json)).times(1);

		HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);

		ByteArrayOutputStream os = new ByteArrayOutputStream();

		EasyMock.expect(response.getWriter()).andReturn(new PrintWriter(os)).times(1);

		CachedJSONController cachedController = new CachedJSONController() {

			@Override
			public String getCacheKey(Map<String, Object> requestArgument) {
				return key;
			}

			@Override
			public void verifyBusiArgument(Map<String, Object> requestArgument) throws InvalidRequestArgumentException {

			}

			@Override
			public String[] getSignItems(Map<String, Object> requestArgument) {
				return new String[] { (String) requestArgument.get("imsi"), (String) requestArgument.get("imei") };
			}

			@Override
			public Object handleRequest(Map<String, Object> requestArgument) throws ApiException {
				return newValue;
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

					@Override
					public int getCacheExpiry() {
						return expiry;
					}
					
				};
			}

		};

		CacheProvider cacheProvider = EasyMock.createMock(CacheProvider.class);

		CacheValue cacheValue = new CacheValue(oldValue);

		String fullCacheKey = cachedController.getClass().getName() + "." + key;

		EasyMock.expect(cacheProvider.getCache(fullCacheKey)).andReturn(cacheValue).times(1);

		cacheProvider.updateCache(EasyMock.eq(fullCacheKey), EasyMock.anyObject(CacheValue.class));

		EasyMock.expectLastCall().times(1);

		CacheTemplate cacheTemplate = new CacheTemplate(cacheProvider);

		cachedController.setCacheTemplate(cacheTemplate);

		EasyMock.replay(request, response, cacheProvider);

		cachedController.handleRequest(request, response);

		String actual = JSONHelper.getValue(new String(os.toByteArray()), "data");
		
		try {
			Thread.sleep(1000);//等待异步执行的cacheProvider.updateCache()执行完成
		} catch (Exception e) {
		}

		Assert.assertEquals(newValue, actual);

		EasyMock.verify(request, response, cacheProvider);

	}

	/**
	 * 缓存命中并且缓存过期,子类定义过期时间
	 */
	@Test
	public void testCachedController1c() throws Exception {

		final String key = "key";

		final String newValue = "newValue";

		final String oldValue = "oldValue";

		final int expiry = -1;

		HttpServletRequest request = EasyMock.createNiceMock(HttpServletRequest.class);
		
		EasyMock.expect(request.getParameter(BaseController.REQUEST_PARAM_JSONP_CALLBACK)).andReturn(null).times(1);
		RequestContextHolder.setRequest(request);
		
		EasyMock.expect(request.getMethod()).andReturn("POST").times(1);

		String json = "{\"city_code\":\"abc\",\"mobile\":\"189\",\"longitude\":\"123\",\"latitude\":\"456\",\"product_id\":\"abc\",\"org_code\":\"3501\",\"client_type\":\"icity_ver\",\"client_version\":\"700\",\"client_channel_type\":\"123\",\"os_type\":\"android\",\"imsi\":\"123\",\"imei\":\"abc\",\"timestamp\":\"2022-09-16 17:08:00\",\"sign\":\"9qS0v1ImAICQbT8qFx6TAMZnLrOXPQBK6L%2FL%2Fhg4KuC%2BRw3m9RQ1ilTvzx7ovkzyyPFWHva0H%2BQ%3D\"}";

		EasyMock.expect(request.getInputStream()).andReturn(new TestServletInputStream(json)).times(1);

		HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);

		ByteArrayOutputStream os = new ByteArrayOutputStream();

		EasyMock.expect(response.getWriter()).andReturn(new PrintWriter(os)).times(1);

		CachedJSONController cachedController = new CachedJSONController() {

			@Override
			public String getCacheKey(Map<String, Object> requestArgument) {
				return key;
			}

			@Override
			public int getCacheExpiry(Map<String, Object> requestArgument) {
				return expiry;
			}

			@Override
			public void verifyBusiArgument(Map<String, Object> requestArgument) throws InvalidRequestArgumentException {

			}

			@Override
			public String[] getSignItems(Map<String, Object> requestArgument) {
				return new String[] { (String) requestArgument.get("imsi"), (String) requestArgument.get("imei") };
			}

			@Override
			public Object handleRequest(Map<String, Object> requestArgument) throws ApiException {
				return newValue;
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

		CacheProvider cacheProvider = EasyMock.createMock(CacheProvider.class);

		CacheValue cacheValue = new CacheValue(oldValue);

		String fullCacheKey = cachedController.getClass().getName() + "." + key;

		EasyMock.expect(cacheProvider.getCache(fullCacheKey)).andReturn(cacheValue).times(1);

		cacheProvider.updateCache(EasyMock.eq(fullCacheKey), EasyMock.anyObject(CacheValue.class));

		EasyMock.expectLastCall().times(1);

		CacheTemplate cacheTemplate = new CacheTemplate(cacheProvider);

		cachedController.setCacheTemplate(cacheTemplate);

		EasyMock.replay(request, response, cacheProvider);

		cachedController.handleRequest(request, response);

		String actual = JSONHelper.getValue(new String(os.toByteArray()), "data");
		
		try {
			Thread.sleep(1000);//等待异步执行的cacheProvider.updateCache()执行完成
		} catch (Exception e) {
		}

		Assert.assertEquals(newValue, actual);

		EasyMock.verify(request, response, cacheProvider);

	}

	/**
	 * 删除缓存
	 */
	@Test
	public void testCachedController2() throws Exception {

		final String key = "key";

		String value = "value";

		CachedJSONController cachedController = new CachedJSONController() {

			@Override
			public String getCacheKey(Map<String, Object> requestArgument) {
				return key;
			}

			@Override
			public int getCacheExpiry(Map<String, Object> requestArgument) {
				return 0;
			}

			@Override
			public void verifyBusiArgument(Map<String, Object> requestArgument) throws InvalidRequestArgumentException {

			}

			@Override
			public String[] getSignItems(Map<String, Object> requestArgument) {
				return new String[] { (String) requestArgument.get("imsi"), (String) requestArgument.get("imei") };
			}

			@Override
			public Object handleRequest(Map<String, Object> requestArgument) throws ApiException {
				return null;
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
		
		EasyMock.expect(request.getParameter(BaseController.REQUEST_PARAM_JSONP_CALLBACK)).andReturn(null).times(1);
		RequestContextHolder.setRequest(request);
		
		EasyMock.expect(request.getMethod()).andReturn("POST").times(1);

		String json = "{\"city_code\":\"abc\",\"mobile\":\"189\",\"longitude\":\"123\",\"latitude\":\"456\",\"product_id\":\"abc\",\"org_code\":\"3501\",\"client_type\":\"icity_ver\",\"client_version\":\"700\",\"client_channel_type\":\"123\",\"os_type\":\"android\",\"imsi\":\"123\",\"imei\":\"abc\",\"timestamp\":\"2022-09-16 17:08:00\",\"sign\":\"9qS0v1ImAICQbT8qFx6TAMZnLrOXPQBK6L%2FL%2Fhg4KuC%2BRw3m9RQ1ilTvzx7ovkzyyPFWHva0H%2BQ%3D\"}";

		EasyMock.expect(request.getInputStream()).andReturn(new TestServletInputStream(json)).times(1);

		EasyMock.expect(request.getParameter(CachedController.DELETE_CACHE_KEY)).andReturn("true").times(1);

		HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);

		EasyMock.expect(response.getWriter()).andReturn(new PrintWriter(System.err)).times(1);

		CacheTemplate cacheTemplate = EasyMock.createMock(CacheTemplate.class);

		EasyMock.expect(cacheTemplate.execute(EasyMock.anyObject(CacheCallback.class), EasyMock.anyInt())).andReturn(value).times(1);

		String fullCacheKey = cachedController.getClass().getName() + "." + key;

		cacheTemplate.deleteCache(fullCacheKey);

		EasyMock.expectLastCall().times(1);

		EasyMock.replay(request, response, cacheTemplate);

		cachedController.setCacheTemplate(cacheTemplate);

		cachedController.handleRequest(request, response);

		EasyMock.verify(cacheTemplate, request, response);

	}

	/**
	 * 设置缓存key前缀
	 */
	@Test
	public void testCachedController3() throws Exception {

		final String key = "key";

		String cacheKeyPrefix = "abc";

		String value = "value";
		
		final int expiry = Integer.MAX_VALUE;

		CachedJSONController cachedController = new CachedJSONController() {

			@Override
			public String getCacheKey(Map<String, Object> requestArgument) {
				return key;
			}

			@Override
			public int getCacheExpiry(Map<String, Object> requestArgument) {
				return expiry;
			}

			@Override
			public void verifyBusiArgument(Map<String, Object> requestArgument) throws InvalidRequestArgumentException {

			}

			@Override
			public String[] getSignItems(Map<String, Object> requestArgument) {
				return new String[] { (String) requestArgument.get("imsi"), (String) requestArgument.get("imei") };
			}

			@Override
			public Object handleRequest(Map<String, Object> requestArgument) throws ApiException {
				return null;
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

		cachedController.setCacheKeyPrefix(cacheKeyPrefix);

		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		
		EasyMock.expect(request.getParameter(BaseController.REQUEST_PARAM_JSONP_CALLBACK)).andReturn(null).times(1);
		RequestContextHolder.setRequest(request);

		String json = "{\"city_code\":\"abc\",\"mobile\":\"189\",\"longitude\":\"123\",\"latitude\":\"456\",\"product_id\":\"abc\",\"org_code\":\"3501\",\"client_type\":\"icity_ver\",\"client_version\":\"700\",\"client_channel_type\":\"123\",\"os_type\":\"android\",\"imsi\":\"123\",\"imei\":\"abc\",\"timestamp\":\"2022-09-16 17:08:00\",\"sign\":\"9qS0v1ImAICQbT8qFx6TAMZnLrOXPQBK6L%2FL%2Fhg4KuC%2BRw3m9RQ1ilTvzx7ovkzyyPFWHva0H%2BQ%3D\"}";

		EasyMock.expect(request.getInputStream()).andReturn(new TestServletInputStream(json)).times(1);
		
		EasyMock.expect(request.getMethod()).andReturn("POST").times(1);

		EasyMock.expect(request.getParameter(CachedController.DELETE_CACHE_KEY)).andReturn("false").times(1);

		HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);

		ByteArrayOutputStream os = new ByteArrayOutputStream();

		EasyMock.expect(response.getWriter()).andReturn(new PrintWriter(os)).times(1);

		CacheProvider cacheProvider = EasyMock.createMock(CacheProvider.class);

		String fullCacheKey = cacheKeyPrefix + "." + key;
		
		CacheValue cacheValue = new CacheValue(value);

		EasyMock.expect(cacheProvider.getCache(fullCacheKey)).andReturn(cacheValue).times(1);

		CacheTemplate cacheTemplate = new CacheTemplate(cacheProvider);

		EasyMock.replay(request, response, cacheProvider);

		cachedController.setCacheTemplate(cacheTemplate);

		cachedController.handleRequest(request, response);
		
		String actual = new String(os.toByteArray());

		Assert.assertEquals(value, actual);

		EasyMock.verify(request, response,cacheProvider);

	}

	/**
	 * 本地禁用缓存
	 */
	@Test
	public void testCachedController4() throws Exception {

		final String value = "value";

		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		
		EasyMock.expect(request.getParameter(BaseController.REQUEST_PARAM_JSONP_CALLBACK)).andReturn(null).times(1);
		RequestContextHolder.setRequest(request);
		
		EasyMock.expect(request.getMethod()).andReturn("POST").times(1);

		String json = "{\"city_code\":\"abc\",\"mobile\":\"189\",\"longitude\":\"123\",\"latitude\":\"456\",\"product_id\":\"abc\",\"org_code\":\"3501\",\"client_type\":\"icity_ver\",\"client_version\":\"700\",\"client_channel_type\":\"123\",\"os_type\":\"android\",\"imsi\":\"123\",\"imei\":\"abc\",\"timestamp\":\"2022-09-16 17:08:00\",\"sign\":\"9qS0v1ImAICQbT8qFx6TAMZnLrOXPQBK6L%2FL%2Fhg4KuC%2BRw3m9RQ1ilTvzx7ovkzyyPFWHva0H%2BQ%3D\"}";

		EasyMock.expect(request.getInputStream()).andReturn(new TestServletInputStream(json)).times(1);

		HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);

		EasyMock.expect(response.getWriter()).andReturn(new PrintWriter(System.err)).times(1);

		CacheTemplate cacheTemplate = EasyMock.createMock(CacheTemplate.class);

		EasyMock.replay(request, response, cacheTemplate);

		class TestCachedJSONController extends CachedJSONController {

			public String result;

			@Override
			public String getCacheKey(Map<String, Object> requestArgument) {
				return null;
			}

			@Override
			public int getCacheExpiry(Map<String, Object> requestArgument) {
				return 0;
			}

			@Override
			public void verifyBusiArgument(Map<String, Object> requestArgument) throws InvalidRequestArgumentException {

			}

			@Override
			public String[] getSignItems(Map<String, Object> requestArgument) {
				return new String[] { (String) requestArgument.get("imsi"), (String) requestArgument.get("imei") };
			}

			@Override
			public Object handleRequest(Map<String, Object> requestArgument) throws ApiException {
				this.result = value;
				return this.result;
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

		}
		;

		TestCachedJSONController cachedController = new TestCachedJSONController();

		cachedController.setDisableCache(true);

		cachedController.setCacheTemplate(cacheTemplate);

		cachedController.handleRequest(request, response);

		Assert.assertEquals(value, cachedController.result);

		EasyMock.verify(cacheTemplate, request, response);

	}

	/**
	 * 全局禁用缓存
	 */
	@Test
	public void testCachedController5() throws Exception {

		System.setProperty(CachedController.DISABLE_CACHE_KEY, "true");

		final String value = "value";

		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		
		EasyMock.expect(request.getParameter(BaseController.REQUEST_PARAM_JSONP_CALLBACK)).andReturn(null).times(1);
		RequestContextHolder.setRequest(request);
		
		EasyMock.expect(request.getMethod()).andReturn("POST").times(1);

		String json = "{\"city_code\":\"abc\",\"mobile\":\"189\",\"longitude\":\"123\",\"latitude\":\"456\",\"product_id\":\"abc\",\"org_code\":\"3501\",\"client_type\":\"icity_ver\",\"client_version\":\"700\",\"client_channel_type\":\"123\",\"os_type\":\"android\",\"imsi\":\"123\",\"imei\":\"abc\",\"timestamp\":\"2022-09-16 17:08:00\",\"sign\":\"9qS0v1ImAICQbT8qFx6TAMZnLrOXPQBK6L%2FL%2Fhg4KuC%2BRw3m9RQ1ilTvzx7ovkzyyPFWHva0H%2BQ%3D\"}";

		EasyMock.expect(request.getInputStream()).andReturn(new TestServletInputStream(json)).times(1);

		HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);

		EasyMock.expect(response.getWriter()).andReturn(new PrintWriter(System.err)).times(1);

		CacheTemplate cacheTemplate = EasyMock.createMock(CacheTemplate.class);

		EasyMock.replay(request, response, cacheTemplate);

		class TestCachedJSONController extends CachedJSONController {

			public String result;

			@Override
			public String getCacheKey(Map<String, Object> requestArgument) {
				return null;
			}

			@Override
			public int getCacheExpiry(Map<String, Object> requestArgument) {
				return 0;
			}

			@Override
			public void verifyBusiArgument(Map<String, Object> requestArgument) throws InvalidRequestArgumentException {

			}

			@Override
			public String[] getSignItems(Map<String, Object> requestArgument) {
				return new String[] { (String) requestArgument.get("imsi"), (String) requestArgument.get("imei") };
			}

			@Override
			public Object handleRequest(Map<String, Object> requestArgument) throws ApiException {
				this.result = value;
				return this.result;
			}

			@Override
			public ApiApplicationConfig getApplicationConfig() {
				return new ApiApplicationConfig() {
				};
			}

		}
		;

		TestCachedJSONController cachedController = new TestCachedJSONController();

		cachedController.setCacheTemplate(cacheTemplate);

		cachedController.handleRequest(request, response);

		Assert.assertEquals(value, cachedController.result);

		EasyMock.verify(cacheTemplate, request, response);

		System.setProperty(CachedController.DISABLE_CACHE_KEY, "");

	}

	/**
	 * 请求Body为空
	 */
	@Test
	public void testJSONController1() throws ServletException, IOException {

		CachedJSONController controller = new CachedJSONController() {

			@Override
			public String getCacheKey(Map<String, Object> requestArgument) {
				return null;
			}

			@Override
			public int getCacheExpiry(Map<String, Object> requestArgument) {
				return 0;
			}

			@Override
			public void verifyBusiArgument(Map<String, Object> requestArgument) throws InvalidRequestArgumentException {

			}

			@Override
			public String[] getSignItems(Map<String, Object> requestArgument) {
				return null;
			}

			@Override
			public Object handleRequest(Map<String, Object> requestArgument) throws ApiException {
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

		String json = "";

		EasyMock.expect(request.getInputStream()).andReturn(new TestServletInputStream(json)).times(1);

		EasyMock.replay(request, response);

		controller.setDisableCache(true);

		controller.handleRequest(request, response);

		EasyMock.verify(request, response);

	}

	/**
	 * 系统参数验证通过
	 */
	@Test
	public void testJSONController2() throws ServletException, IOException {

		CachedJSONController controller = new CachedJSONController() {

			@Override
			public String getCacheKey(Map<String, Object> requestArgument) {
				return null;
			}

			@Override
			public int getCacheExpiry(Map<String, Object> requestArgument) {
				return 0;
			}

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

		String json = "{\"city_code\":\"abc\",\"mobile\":\"189\",\"longitude\":\"123\",\"latitude\":\"456\",\"product_id\":\"abc\",\"org_code\":\"3501\",\"client_type\":\"icity_ver\",\"client_version\":\"700\",\"client_channel_type\":\"123\",\"os_type\":\"android\",\"imsi\":\"123\",\"imei\":\"abc\",\"timestamp\":\"2014-09-16 22:00:01\"}";

		EasyMock.expect(request.getInputStream()).andReturn(new TestServletInputStream(json)).times(1);

		EasyMock.replay(request, response);

		controller.setDisableCache(true);

		controller.handleRequest(request, response);

		EasyMock.verify(request, response);

	}

	/**
	 * product_id为空
	 */
	@Test
	public void testJSONController3() throws ServletException, IOException {

		CachedJSONController controller = new CachedJSONController() {

			@Override
			public String getCacheKey(Map<String, Object> requestArgument) {
				return null;
			}

			@Override
			public int getCacheExpiry(Map<String, Object> requestArgument) {
				return 0;
			}

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

		String json = "{\"city_code\":\"abc\",\"mobile\":\"189\",\"longitude\":\"123\",\"latitude\":\"456\",\"org_code\":\"3501\",\"client_type\":\"icity_ver\",\"client_version\":\"700\",\"client_channel_type\":\"123\",\"os_type\":\"android\",\"imsi\":\"123\",\"imei\":\"abc\",\"timestamp\":\"2022-09-16 17:08:00\",\"sign\":\"9qS0v1ImAICQbT8qFx6TAMZnLrOXPQBK6L%2FL%2Fhg4KuC%2BRw3m9RQ1ilTvzx7ovkzyyPFWHva0H%2BQ%3D\"}";

		EasyMock.expect(request.getInputStream()).andReturn(new TestServletInputStream(json)).times(1);

		EasyMock.replay(request, response);

		controller.setDisableCache(true);

		controller.handleRequest(request, response);

		EasyMock.verify(request, response);

	}

	/**
	 * org_code为空
	 */
	@Test
	public void testJSONController4() throws ServletException, IOException {

		CachedJSONController controller = new CachedJSONController() {

			@Override
			public String getCacheKey(Map<String, Object> requestArgument) {
				return null;
			}

			@Override
			public int getCacheExpiry(Map<String, Object> requestArgument) {
				return 0;
			}

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
		
		EasyMock.expect(request.getMethod()).andReturn("POST").times(1);

		String json = "{\"city_code\":\"abc\",\"mobile\":\"189\",\"longitude\":\"123\",\"latitude\":\"456\",\"product_id\":\"abc\",\"client_type\":\"icity_ver\",\"client_version\":\"700\",\"client_channel_type\":\"123\",\"os_type\":\"android\",\"imsi\":\"123\",\"imei\":\"abc\",\"timestamp\":\"2014-09-16 12:00:01\"}";

		EasyMock.expect(request.getInputStream()).andReturn(new TestServletInputStream(json)).times(1);

		EasyMock.replay(request, response);

		controller.setDisableCache(true);

		controller.handleRequest(request, response);

		EasyMock.verify(request, response);

	}

	/**
	 * client_version为空
	 */
	@Test
	public void testJSONController5() throws ServletException, IOException {

		CachedJSONController controller = new CachedJSONController() {

			@Override
			public String getCacheKey(Map<String, Object> requestArgument) {
				return null;
			}

			@Override
			public int getCacheExpiry(Map<String, Object> requestArgument) {
				return 0;
			}

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

		String json = "{\"city_code\":\"abc\",\"mobile\":\"189\",\"longitude\":\"123\",\"latitude\":\"456\",\"product_id\":\"abc\",\"org_code\":\"3501\",\"client_type\":\"icity_ver\",\"client_channel_type\":\"123\",\"os_type\":\"android\",\"imsi\":\"123\",\"imei\":\"abc\",\"timestamp\":\"2014-09-16 12:00:01\"}";

		EasyMock.expect(request.getInputStream()).andReturn(new TestServletInputStream(json)).times(1);

		EasyMock.replay(request, response);

		controller.setDisableCache(true);

		controller.handleRequest(request, response);

		EasyMock.verify(request, response);

	}

	/**
	 * client_channel_type为空
	 */
	@Test
	public void testJSONController6() throws ServletException, IOException {

		CachedJSONController controller = new CachedJSONController() {

			@Override
			public String getCacheKey(Map<String, Object> requestArgument) {
				return null;
			}

			@Override
			public int getCacheExpiry(Map<String, Object> requestArgument) {
				return 0;
			}

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

		String json = "{\"city_code\":\"abc\",\"mobile\":\"189\",\"longitude\":\"123\",\"latitude\":\"456\",\"product_id\":\"abc\",\"org_code\":\"3501\",\"client_type\":\"icity_ver\",\"client_version\":\"700\",\"os_type\":\"android\",\"imsi\":\"123\",\"imei\":\"abc\",\"timestamp\":\"2014-09-16 12:00:01\"}";

		EasyMock.expect(request.getInputStream()).andReturn(new TestServletInputStream(json)).times(1);

		EasyMock.replay(request, response);

		controller.setDisableCache(true);

		controller.handleRequest(request, response);

		EasyMock.verify(request, response);

	}

	/**
	 * os_type为空
	 */
	@Test
	public void testJSONController7() throws ServletException, IOException {

		CachedJSONController controller = new CachedJSONController() {

			@Override
			public String getCacheKey(Map<String, Object> requestArgument) {
				return null;
			}

			@Override
			public int getCacheExpiry(Map<String, Object> requestArgument) {
				return 0;
			}

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

		String json = "{\"city_code\":\"abc\",\"mobile\":\"189\",\"longitude\":\"123\",\"latitude\":\"456\",\"product_id\":\"abc\",\"org_code\":\"3501\",\"client_type\":\"icity_ver\",\"client_version\":\"700\",\"client_channel_type\":\"abc\",\"imsi\":\"123\",\"imei\":\"abc\",\"timestamp\":\"2014-09-16 12:00:01\"}";

		EasyMock.expect(request.getInputStream()).andReturn(new TestServletInputStream(json)).times(1);

		EasyMock.replay(request, response);

		controller.setDisableCache(true);

		controller.handleRequest(request, response);

		EasyMock.verify(request, response);

	}

	/**
	 * imsi为空
	 */
	@Test
	public void testJSONController8() throws ServletException, IOException {

		CachedJSONController controller = new CachedJSONController() {

			@Override
			public String getCacheKey(Map<String, Object> requestArgument) {
				return null;
			}

			@Override
			public int getCacheExpiry(Map<String, Object> requestArgument) {
				return 0;
			}

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

		String json = "{\"city_code\":\"abc\",\"mobile\":\"189\",\"longitude\":\"123\",\"latitude\":\"456\",\"product_id\":\"abc\",\"org_code\":\"3501\",\"client_type\":\"icity_ver\",\"client_version\":\"700\",\"client_channel_type\":\"abc\",\"os_type\":\"ios\",\"imei\":\"abc\",\"timestamp\":\"2014-09-16 12:00:01\"}";

		EasyMock.expect(request.getInputStream()).andReturn(new TestServletInputStream(json)).times(1);

		EasyMock.replay(request, response);

		controller.setDisableCache(true);

		controller.handleRequest(request, response);

		EasyMock.verify(request, response);

	}

	/**
	 * imei为空
	 */
	@Test
	public void testJSONController9() throws ServletException, IOException {

		CachedJSONController controller = new CachedJSONController() {

			@Override
			public String getCacheKey(Map<String, Object> requestArgument) {
				return null;
			}

			@Override
			public int getCacheExpiry(Map<String, Object> requestArgument) {
				return 0;
			}

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

		String json = "{\"city_code\":\"abc\",\"mobile\":\"189\",\"longitude\":\"123\",\"latitude\":\"456\",\"product_id\":\"abc\",\"org_code\":\"3501\",\"client_type\":\"icity_ver\",\"client_version\":\"700\",\"client_channel_type\":\"abc\",\"os_type\":\"ios\",\"imsi\":\"123\",\"timestamp\":\"2014-09-16 12:00:01\"}";

		EasyMock.expect(request.getInputStream()).andReturn(new TestServletInputStream(json)).times(1);

		EasyMock.replay(request, response);

		controller.setDisableCache(true);

		controller.handleRequest(request, response);

		EasyMock.verify(request, response);

	}

	/**
	 * timestamp验证失败
	 */
	@Test
	public void testJSONController10() throws ServletException, IOException {

		CachedJSONController controller = new CachedJSONController() {

			@Override
			public String getCacheKey(Map<String, Object> requestArgument) {
				return null;
			}

			@Override
			public int getCacheExpiry(Map<String, Object> requestArgument) {
				return 0;
			}

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

		String json = "{\"city_code\":\"abc\",\"mobile\":\"189\",\"longitude\":\"123\",\"latitude\":\"456\",\"product_id\":\"abc\",\"org_code\":\"3501\",\"client_type\":\"icity_ver\",\"client_version\":\"700\",\"client_channel_type\":\"abc\",\"os_type\":\"ios\",\"imsi\":\"123\",\"imei\":\"abc\",\"timestamp\":\"2014-09-12 22:01\"}";

		EasyMock.expect(request.getInputStream()).andReturn(new TestServletInputStream(json)).times(1);

		EasyMock.replay(request, response);

		controller.setDisableCache(true);

		controller.handleRequest(request, response);

		EasyMock.verify(request, response);

	}

	/**
	 * timestamp为空
	 */
	@Test
	public void testJSONController11() throws ServletException, IOException {

		CachedJSONController controller = new CachedJSONController() {

			@Override
			public String getCacheKey(Map<String, Object> requestArgument) {
				return null;
			}

			@Override
			public int getCacheExpiry(Map<String, Object> requestArgument) {
				return 0;
			}

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

		String json = "{\"city_code\":\"abc\",\"mobile\":\"189\",\"longitude\":\"123\",\"latitude\":\"456\",\"product_id\":\"abc\",\"org_code\":\"3501\",\"client_type\":\"icity_ver\",\"client_version\":\"700\",\"client_channel_type\":\"abc\",\"os_type\":\"ios\",\"imsi\":\"123\",\"imei\":\"abc\"}";

		EasyMock.expect(request.getInputStream()).andReturn(new TestServletInputStream(json)).times(1);

		EasyMock.replay(request, response);

		controller.setDisableCache(true);

		controller.handleRequest(request, response);

		EasyMock.verify(request, response);

	}

	/**
	 * 业务参数验证失败
	 */
	@Test
	public void testJSONController12() throws ServletException, IOException {

		CachedJSONController controller = new CachedJSONController() {

			@Override
			public String getCacheKey(Map<String, Object> requestArgument) {
				return null;
			}

			@Override
			public int getCacheExpiry(Map<String, Object> requestArgument) {
				return 0;
			}

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

		String json = "{\"city_code\":\"abc\",\"mobile\":\"189\",\"longitude\":\"123\",\"latitude\":\"456\",\"product_id\":\"abc\",\"org_code\":\"3501\",\"client_type\":\"icity_ver\",\"client_version\":\"700\",\"client_channel_type\":\"123\",\"os_type\":\"android\",\"imsi\":\"123\",\"imei\":\"abc\",\"timestamp\":\"2014-09-16 22:00:01\",\"mobile\":\"12345678901\"}";

		EasyMock.expect(request.getInputStream()).andReturn(new TestServletInputStream(json)).times(1);

		EasyMock.replay(request, response);

		controller.setDisableCache(true);

		controller.handleRequest(request, response);

		EasyMock.verify(request, response);

	}

	/**
	 * 请求执行成功.handleRequest()方法返回map
	 */
	@Test
	public void testJSONController13() throws ServletException, IOException {

		CachedJSONController controller = new CachedJSONController() {

			@Override
			public String getCacheKey(Map<String, Object> requestArgument) {
				return null;
			}

			@Override
			public int getCacheExpiry(Map<String, Object> requestArgument) {
				return 0;
			}

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

		String json = "{\"city_code\":\"abc\",\"mobile\":\"189\",\"longitude\":\"123\",\"latitude\":\"456\",\"product_id\":\"abc\",\"org_code\":\"3501\",\"client_type\":\"icity_ver\",\"client_version\":\"700\",\"client_channel_type\":\"123\",\"os_type\":\"android\",\"imsi\":\"123\",\"imei\":\"abc\",\"timestamp\":\"2022-09-16 17:08:00\",\"sign\":\"9qS0v1ImAICQbT8qFx6TAMZnLrOXPQBK6L%2FL%2Fhg4KuC%2BRw3m9RQ1ilTvzx7ovkzyyPFWHva0H%2BQ%3D\"}";

		EasyMock.expect(request.getInputStream()).andReturn(new TestServletInputStream(json)).times(1);

		EasyMock.replay(request, response);

		controller.setDisableCache(true);

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
	public void testJSONController14() throws ServletException, IOException {

		CachedJSONController controller = new CachedJSONController() {

			@Override
			public String getCacheKey(Map<String, Object> requestArgument) {
				return null;
			}

			@Override
			public int getCacheExpiry(Map<String, Object> requestArgument) {
				return 0;
			}

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

		String json = "{\"city_code\":\"abc\",\"mobile\":\"189\",\"longitude\":\"123\",\"latitude\":\"456\",\"product_id\":\"abc\",\"org_code\":\"3501\",\"client_type\":\"icity_ver\",\"client_version\":\"700\",\"client_channel_type\":\"123\",\"os_type\":\"android\",\"imsi\":\"123\",\"imei\":\"abc\",\"timestamp\":\"2022-09-16 17:08:00\",\"sign\":\"9qS0v1ImAICQbT8qFx6TAMZnLrOXPQBK6L%2FL%2Fhg4KuC%2BRw3m9RQ1ilTvzx7ovkzyyPFWHva0H%2BQ%3D\"}";

		EasyMock.expect(request.getInputStream()).andReturn(new TestServletInputStream(json)).times(1);

		EasyMock.replay(request, response);

		controller.setDisableCache(true);

		controller.handleRequest(request, response);

		EasyMock.verify(request, response);

	}
	
	
	/**
	 * city_code为空
	 */
	@Test
	public void testJSONController15() throws ServletException, IOException {

		CachedJSONController controller = new CachedJSONController() {

			@Override
			public String getCacheKey(Map<String, Object> requestArgument) {
				return null;
			}

			@Override
			public int getCacheExpiry(Map<String, Object> requestArgument) {
				return 0;
			}

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
				Assert.assertTrue(exception.getLocalizedMessage().contains("city_code"));
				super.handleException(exception, request, response);
			}

		};

		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);
		EasyMock.expect(response.getWriter()).andReturn(new PrintWriter(System.err)).times(1);
		
		EasyMock.expect(request.getMethod()).andReturn("POST").times(1);

		String json = "{\"mobile\":\"189\",\"longitude\":\"123\",\"latitude\":\"456\",\"product_id\":\"abc\",\"org_code\":\"3501\",\"client_type\":\"icity_ver\",\"client_version\":\"700\",\"client_channel_type\":\"123\",\"os_type\":\"android\",\"imsi\":\"123\",\"imei\":\"abc\",\"timestamp\":\"2022-09-16 17:08:00\",\"sign\":\"9qS0v1ImAICQbT8qFx6TAMZnLrOXPQBK6L%2FL%2Fhg4KuC%2BRw3m9RQ1ilTvzx7ovkzyyPFWHva0H%2BQ%3D\"}";

		EasyMock.expect(request.getInputStream()).andReturn(new TestServletInputStream(json)).times(1);

		EasyMock.replay(request, response);

		controller.setDisableCache(true);

		controller.handleRequest(request, response);

		EasyMock.verify(request, response);

	}
	
	/**
	 * mobile为空
	 */
	@Test
	public void testJSONController16() throws ServletException, IOException {

		CachedJSONController controller = new CachedJSONController() {

			@Override
			public String getCacheKey(Map<String, Object> requestArgument) {
				return null;
			}

			@Override
			public int getCacheExpiry(Map<String, Object> requestArgument) {
				return 0;
			}

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
				Assert.assertTrue(exception.getLocalizedMessage().contains("mobile"));
				super.handleException(exception, request, response);
			}

		};

		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);
		EasyMock.expect(response.getWriter()).andReturn(new PrintWriter(System.err)).times(1);
		
		EasyMock.expect(request.getMethod()).andReturn("POST").times(1);

		String json = "{\"city_code\":\"abc\",\"longitude\":\"123\",\"latitude\":\"456\",\"product_id\":\"abc\",\"org_code\":\"3501\",\"client_type\":\"icity_ver\",\"client_version\":\"700\",\"client_channel_type\":\"123\",\"os_type\":\"android\",\"imsi\":\"123\",\"imei\":\"abc\",\"timestamp\":\"2022-09-16 17:08:00\",\"sign\":\"9qS0v1ImAICQbT8qFx6TAMZnLrOXPQBK6L%2FL%2Fhg4KuC%2BRw3m9RQ1ilTvzx7ovkzyyPFWHva0H%2BQ%3D\"}";

		EasyMock.expect(request.getInputStream()).andReturn(new TestServletInputStream(json)).times(1);

		EasyMock.replay(request, response);

		controller.setDisableCache(true);

		controller.handleRequest(request, response);

		EasyMock.verify(request, response);

	}
	
	/**
	 * longitude为空
	 */
	@Test
	public void testJSONController17() throws ServletException, IOException {

		CachedJSONController controller = new CachedJSONController() {

			@Override
			public String getCacheKey(Map<String, Object> requestArgument) {
				return null;
			}

			@Override
			public int getCacheExpiry(Map<String, Object> requestArgument) {
				return 0;
			}

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
				Assert.assertTrue(exception.getLocalizedMessage().contains("longitude"));
				super.handleException(exception, request, response);
			}

		};

		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);
		EasyMock.expect(response.getWriter()).andReturn(new PrintWriter(System.err)).times(1);
		
		EasyMock.expect(request.getMethod()).andReturn("POST").times(1);

		String json = "{\"city_code\":\"abc\",\"mobile\":\"189\",\"latitude\":\"456\",\"product_id\":\"abc\",\"org_code\":\"3501\",\"client_type\":\"icity_ver\",\"client_version\":\"700\",\"client_channel_type\":\"123\",\"os_type\":\"android\",\"imsi\":\"123\",\"imei\":\"abc\",\"timestamp\":\"2022-09-16 17:08:00\",\"sign\":\"9qS0v1ImAICQbT8qFx6TAMZnLrOXPQBK6L%2FL%2Fhg4KuC%2BRw3m9RQ1ilTvzx7ovkzyyPFWHva0H%2BQ%3D\"}";

		EasyMock.expect(request.getInputStream()).andReturn(new TestServletInputStream(json)).times(1);

		EasyMock.replay(request, response);

		controller.setDisableCache(true);

		controller.handleRequest(request, response);

		EasyMock.verify(request, response);

	}
	
	/**
	 * latitude为空
	 */
	@Test
	public void testJSONController18() throws ServletException, IOException {

		CachedJSONController controller = new CachedJSONController() {

			@Override
			public String getCacheKey(Map<String, Object> requestArgument) {
				return null;
			}

			@Override
			public int getCacheExpiry(Map<String, Object> requestArgument) {
				return 0;
			}

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
				Assert.assertTrue(exception.getLocalizedMessage().contains("latitude"));
				super.handleException(exception, request, response);
			}

		};

		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);
		EasyMock.expect(response.getWriter()).andReturn(new PrintWriter(System.err)).times(1);
		
		EasyMock.expect(request.getMethod()).andReturn("POST").times(1);

		String json = "{\"city_code\":\"abc\",\"mobile\":\"189\",\"longitude\":\"123\",\"product_id\":\"abc\",\"org_code\":\"3501\",\"client_type\":\"icity_ver\",\"client_version\":\"700\",\"client_channel_type\":\"123\",\"os_type\":\"android\",\"imsi\":\"123\",\"imei\":\"abc\",\"timestamp\":\"2022-09-16 17:08:00\",\"sign\":\"9qS0v1ImAICQbT8qFx6TAMZnLrOXPQBK6L%2FL%2Fhg4KuC%2BRw3m9RQ1ilTvzx7ovkzyyPFWHva0H%2BQ%3D\"}";

		EasyMock.expect(request.getInputStream()).andReturn(new TestServletInputStream(json)).times(1);

		EasyMock.replay(request, response);

		controller.setDisableCache(true);

		controller.handleRequest(request, response);

		EasyMock.verify(request, response);

	}
	
	/**
	 * jsonp请求
	 */
	@Test
	public void testCachedControllerJSONP1() throws Exception {

		final String key = "key";

		final String value = "value";

		final int expiry = Integer.MAX_VALUE;

		HttpServletRequest request = EasyMock.createNiceMock(HttpServletRequest.class);
		
		String jsonpCallback="test";
		EasyMock.expect(request.getParameter(BaseController.REQUEST_PARAM_JSONP_CALLBACK)).andReturn(jsonpCallback).times(1);
		RequestContextHolder.setRequest(request);
		
		EasyMock.expect(request.getMethod()).andReturn("POST").times(1);

		String json = "{\"city_code\":\"abc\",\"mobile\":\"189\",\"longitude\":\"123\",\"latitude\":\"456\",\"product_id\":\"abc\",\"org_code\":\"3501\",\"client_type\":\"icity_ver\",\"client_version\":\"700\",\"client_channel_type\":\"123\",\"os_type\":\"android\",\"imsi\":\"123\",\"imei\":\"abc\",\"timestamp\":\"2022-09-16 17:08:00\",\"sign\":\"9qS0v1ImAICQbT8qFx6TAMZnLrOXPQBK6L%2FL%2Fhg4KuC%2BRw3m9RQ1ilTvzx7ovkzyyPFWHva0H%2BQ%3D\"}";

		EasyMock.expect(request.getInputStream()).andReturn(new TestServletInputStream(json)).times(1);

		HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);

		ByteArrayOutputStream os = new ByteArrayOutputStream();

		EasyMock.expect(response.getWriter()).andReturn(new PrintWriter(os)).times(1);

		CachedJSONController cachedController = new CachedJSONController() {

			@Override
			public String getCacheKey(Map<String, Object> requestArgument) {
				return key;
			}

			@Override
			public int getCacheExpiry(Map<String, Object> requestArgument) {
				return expiry;
			}

			@Override
			public void verifyBusiArgument(Map<String, Object> requestArgument) throws InvalidRequestArgumentException {

			}

			@Override
			public String[] getSignItems(Map<String, Object> requestArgument) {
				return new String[] { (String) requestArgument.get("imsi"), (String) requestArgument.get("imei") };
			}

			@Override
			public Object handleRequest(Map<String, Object> requestArgument) throws ApiException {
				return null;
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

		CacheProvider cacheProvider = EasyMock.createMock(CacheProvider.class);

		CacheValue cacheValue = new CacheValue(value);

		String fullCacheKey = cachedController.getClass().getName() + "." + key;

		EasyMock.expect(cacheProvider.getCache(fullCacheKey)).andReturn(cacheValue).times(1);

		CacheTemplate cacheTemplate = new CacheTemplate(cacheProvider);

		cachedController.setCacheTemplate(cacheTemplate);

		EasyMock.replay(request, response, cacheProvider);

		cachedController.handleRequest(request, response);

		String actual = new String(os.toByteArray());
        String expected = jsonpCallback +"("+value+");";
		Assert.assertEquals(expected, actual);

		EasyMock.verify(request, response, cacheProvider);

	}

}
