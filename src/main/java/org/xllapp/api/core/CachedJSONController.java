package org.xllapp.api.core;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xllapp.cache.CacheTemplate;

/**
 * 提供缓存支持的符合二期接口规范的接口的API基类.
 *
 * @author dylan.chen Sep 17, 2014
 * 
 */
public abstract class CachedJSONController extends JSONController {

	protected CachedController delegate = new InternalCachedController();

	@Override
	public void handleRequest(Object requestArgument, HttpServletRequest request, HttpServletResponse response) throws Exception {
		this.delegate.handleRequest(requestArgument, request, response);
	}

	public abstract String getCacheKey(Map<String, Object> requestArgument);

	public int getCacheExpiry(Map<String, Object> requestArgument) {
		return getApplicationConfig().getCacheExpiry();
	}

	public void setCacheTemplate(CacheTemplate cacheTemplate) {
		this.delegate.setCacheTemplate(cacheTemplate);
	}

	public void setDisableCache(boolean disableCache) {
		this.delegate.setDisableCache(disableCache);
	}

	public void setCacheKeyPrefix(String cacheKeyPrefix) {
		this.delegate.setCacheKeyPrefix(cacheKeyPrefix);
	}

	class InternalCachedController extends CachedController {

		public InternalCachedController() {
			setCacheKeyPrefix(CachedJSONController.this.getClass().getName());
		}

		@SuppressWarnings("unchecked")
		@Override
		public String getCacheKey(Object requestArgument, HttpServletRequest request) {
			return CachedJSONController.this.getCacheKey((Map<String, Object>) requestArgument);
		}

		@SuppressWarnings("unchecked")
		@Override
		public int getCacheExpiry(Object requestArgument, HttpServletRequest request) {
			return CachedJSONController.this.getCacheExpiry((Map<String, Object>) requestArgument);
		}

		@Override
		public Object resolveAndVerifyArgument(HttpServletRequest request) throws Exception {
			return null;
		}

		@Override
		public String handleRequest(Object requestArgument, HttpServletRequest request) throws Exception {
			return CachedJSONController.this.handleRequest(requestArgument, request);
		}

	}

}
