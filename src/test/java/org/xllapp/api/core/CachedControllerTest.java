package org.xllapp.api.core;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.xllapp.api.core.CachedController;

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
public class CachedControllerTest {

	/**
	 * 无缓存
	 */
	@Test
	public void test1() throws Exception {

		final String key = "key";

		final String value = "value";

		final int expiry = 1;

		HttpServletRequest request = EasyMock.createNiceMock(HttpServletRequest.class);

		HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);

		ByteArrayOutputStream os = new ByteArrayOutputStream();

		EasyMock.expect(response.getWriter()).andReturn(new PrintWriter(os)).times(1);

		CachedController cachedController = new CachedController() {

			@Override
			public Object resolveAndVerifyArgument(HttpServletRequest request) throws Exception {
				return null;
			}

			@Override
			public String handleRequest(Object argument, HttpServletRequest request) throws Exception {
				return value;
			}

			@Override
			public String getCacheKey(Object argument, HttpServletRequest request) {
				return key;
			}

			@Override
			public int getCacheExpiry(Object argument, HttpServletRequest request) {
				return expiry;
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
			Thread.sleep(1000);//等待异步执行的cacheProvider.addCache()执行完成
		} catch (Exception e) {
		}

		String actual = new String(os.toByteArray());

		Assert.assertEquals(value, actual);

		EasyMock.verify(request, response, cacheProvider);

	}

	/**
	 * 缓存命中并且缓存有效
	 */
	@Test
	public void test1a() throws Exception {

		final String key = "key";

		final String newValue = "newValue";

		final String oldValue = "oldValue";

		final int expiry = Integer.MAX_VALUE;

		HttpServletRequest request = EasyMock.createNiceMock(HttpServletRequest.class);

		HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);

		ByteArrayOutputStream os = new ByteArrayOutputStream();

		EasyMock.expect(response.getWriter()).andReturn(new PrintWriter(os)).times(1);

		CachedController cachedController = new CachedController() {

			@Override
			public Object resolveAndVerifyArgument(HttpServletRequest request) throws Exception {
				return null;
			}

			@Override
			public String handleRequest(Object argument, HttpServletRequest request) throws Exception {
				return newValue;
			}

			@Override
			public String getCacheKey(Object argument, HttpServletRequest request) {
				return key;
			}

			@Override
			public int getCacheExpiry(Object argument, HttpServletRequest request) {
				return expiry;
			}
		};

		CacheProvider cacheProvider = EasyMock.createMock(CacheProvider.class);

		String fullCacheKey = cachedController.getClass().getName() + "." + key;

		CacheValue cacheValue = new CacheValue(oldValue);

		EasyMock.expect(cacheProvider.getCache(fullCacheKey)).andReturn(cacheValue).times(1);

		CacheTemplate cacheTemplate = new CacheTemplate(cacheProvider);

		cachedController.setCacheTemplate(cacheTemplate);

		EasyMock.replay(request, response, cacheProvider);

		cachedController.handleRequest(request, response);

		String actual = new String(os.toByteArray());

		Assert.assertEquals(oldValue, actual);

		EasyMock.verify(request, response, cacheProvider);

	}

	/**
	 * 缓存命中并且缓存过期
	 */
	@Test
	public void test1b() throws Exception {

		final String key = "key";

		final String newValue = "newValue";

		final String oldValue = "oldValue";

		final int expiry = -1;

		HttpServletRequest request = EasyMock.createNiceMock(HttpServletRequest.class);

		HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);

		ByteArrayOutputStream os = new ByteArrayOutputStream();

		EasyMock.expect(response.getWriter()).andReturn(new PrintWriter(os)).times(1);

		CachedController cachedController = new CachedController() {

			@Override
			public Object resolveAndVerifyArgument(HttpServletRequest request) throws Exception {
				return null;
			}

			@Override
			public String handleRequest(Object argument, HttpServletRequest request) throws Exception {
				return newValue;
			}

			@Override
			public String getCacheKey(Object argument, HttpServletRequest request) {
				return key;
			}

			@Override
			public int getCacheExpiry(Object argument, HttpServletRequest request) {
				return expiry;
			}
		};

		CacheProvider cacheProvider = EasyMock.createMock(CacheProvider.class);

		String fullCacheKey = cachedController.getClass().getName() + "." + key;

		CacheValue cacheValue = new CacheValue(oldValue);

		EasyMock.expect(cacheProvider.getCache(fullCacheKey)).andReturn(cacheValue).times(1);

		cacheProvider.updateCache(EasyMock.eq(fullCacheKey), EasyMock.anyObject(CacheValue.class));

		EasyMock.expectLastCall().times(1);

		CacheTemplate cacheTemplate = new CacheTemplate(cacheProvider);

		cachedController.setCacheTemplate(cacheTemplate);

		EasyMock.replay(request, response, cacheProvider);

		cachedController.handleRequest(request, response);
		
		try {
			Thread.sleep(1000);//等待异步执行的cacheProvider.updateCache()执行完成
		} catch (Exception e) {
		}

		String actual = new String(os.toByteArray());

		Assert.assertEquals(newValue, actual);

		EasyMock.verify(request, response, cacheProvider);

	}

	/**
	 * 删除缓存
	 */
	@Test
	public void test4() throws Exception {

		final String key = "key";

		String value = "value";

		CachedController cachedController = new CachedController() {

			@Override
			public Object resolveAndVerifyArgument(HttpServletRequest request) throws Exception {
				return null;
			}

			@Override
			public String handleRequest(Object argument, HttpServletRequest request) throws Exception {
				return null;
			}

			@Override
			public String getCacheKey(Object argument, HttpServletRequest request) {
				return key;
			}

			@Override
			public int getCacheExpiry(Object argument, HttpServletRequest request) {
				return 0;
			}
		};

		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);

		EasyMock.expect(request.getParameter(CachedController.DELETE_CACHE_KEY)).andReturn("true").times(1);

		HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);

		CacheTemplate cacheTemplate = EasyMock.createMock(CacheTemplate.class);

		EasyMock.expect(cacheTemplate.execute(EasyMock.anyObject(CacheCallback.class), EasyMock.anyInt())).andReturn(value).times(1);

		cacheTemplate.deleteCache(cachedController.getClass().getName() + "." + key);

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
	public void test3() throws Exception {

		final String key = "key";

		final String newValue = "newValue";

		final String oldValue = "oldValue";

		final int expiry = Integer.MAX_VALUE;
		
		String cacheKeyPrefix = "abc";

		HttpServletRequest request = EasyMock.createNiceMock(HttpServletRequest.class);

		HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);

		ByteArrayOutputStream os = new ByteArrayOutputStream();

		EasyMock.expect(response.getWriter()).andReturn(new PrintWriter(os)).times(1);

		CachedController cachedController = new CachedController() {

			@Override
			public Object resolveAndVerifyArgument(HttpServletRequest request) throws Exception {
				return null;
			}

			@Override
			public String handleRequest(Object argument, HttpServletRequest request) throws Exception {
				return newValue;
			}

			@Override
			public String getCacheKey(Object argument, HttpServletRequest request) {
				return key;
			}

			@Override
			public int getCacheExpiry(Object argument, HttpServletRequest request) {
				return expiry;
			}
		};

		CacheProvider cacheProvider = EasyMock.createMock(CacheProvider.class);

		String fullCacheKey = cacheKeyPrefix + "." + key;

		CacheValue cacheValue = new CacheValue(oldValue);

		EasyMock.expect(cacheProvider.getCache(fullCacheKey)).andReturn(cacheValue).times(1);

		CacheTemplate cacheTemplate = new CacheTemplate(cacheProvider);

		cachedController.setCacheTemplate(cacheTemplate);

		EasyMock.replay(request, response, cacheProvider);
		
		cachedController.setCacheKeyPrefix(cacheKeyPrefix);

		cachedController.handleRequest(request, response);

		String actual = new String(os.toByteArray());

		Assert.assertEquals(oldValue, actual);

		EasyMock.verify(request, response, cacheProvider);

	}

	/**
	 * 本地禁用缓存
	 */
	@Test
	public void test2() throws Exception {

		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);

		HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);

		CacheTemplate cacheTemplate = EasyMock.createMock(CacheTemplate.class);

		EasyMock.replay(request, response, cacheTemplate);

		CachedController cachedController = new CachedController() {

			@Override
			public Object resolveAndVerifyArgument(HttpServletRequest request) throws Exception {
				return null;
			}

			@Override
			public String handleRequest(Object argument, HttpServletRequest request) throws Exception {
				return null;
			}

			@Override
			public String getCacheKey(Object argument, HttpServletRequest request) {
				return null;
			}

			@Override
			public int getCacheExpiry(Object argument, HttpServletRequest request) {
				return 0;
			}
		};

		cachedController.setDisableCache(true);

		cachedController.setCacheTemplate(cacheTemplate);

		cachedController.handleRequest(request, response);

		EasyMock.verify(cacheTemplate, request, response);

	}

	/**
	 * 全局禁用缓存
	 */
	@Test
	public void test5() throws Exception {

		System.setProperty(CachedController.DISABLE_CACHE_KEY, "true");

		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);

		HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);

		CacheTemplate cacheTemplate = EasyMock.createMock(CacheTemplate.class);

		EasyMock.replay(request, response, cacheTemplate);

		CachedController cachedController = new CachedController() {

			@Override
			public Object resolveAndVerifyArgument(HttpServletRequest request) throws Exception {
				return null;
			}

			@Override
			public String handleRequest(Object argument, HttpServletRequest request) throws Exception {
				return null;
			}

			@Override
			public String getCacheKey(Object argument, HttpServletRequest request) {
				return null;
			}

			@Override
			public int getCacheExpiry(Object argument, HttpServletRequest request) {
				return 0;
			}
		};

		cachedController.setCacheTemplate(cacheTemplate);

		cachedController.handleRequest(request, response);

		EasyMock.verify(cacheTemplate, request, response);

		System.setProperty(CachedController.DISABLE_CACHE_KEY, "");

	}

}
