package org.xllapp.api.core;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import org.xllapp.cache.CacheCallback;
import org.xllapp.cache.CacheTemplate;

/**
 * 提供缓存支持的API基类.
 * 
 * @author dylan.chen Sep 20, 2013
 * 
 */
public abstract class CachedController extends BaseController {

	public final static String DISABLE_CACHE_KEY = "icity.disable.cache";

	public final static String DELETE_CACHE_KEY = "icityDeleteCache";

	protected CacheTemplate cacheTemplate;

	protected boolean disableCache = false;

	protected String cacheKeyPrefix;

	public boolean isDeleteCache(HttpServletRequest request) {
		return "true".equalsIgnoreCase(request.getParameter(DELETE_CACHE_KEY));
	}

	public boolean isDisableCache() {

		boolean _disable = this.disableCache;

		this.logger.debug("local disable cache:{}", _disable);

		if (!_disable) {

			_disable = "true".equalsIgnoreCase(System.getProperty(DISABLE_CACHE_KEY));

			this.logger.debug("global disable cache:{}", _disable);
		}
		return _disable;
		
	}

	@Override
	public void handleRequest(final Object requestArgument, final HttpServletRequest request, final HttpServletResponse response) throws Exception {

		if (isDisableCache()) {
			this.logger.debug("disable cache");
			super.handleRequest(requestArgument, request, response);
			return;
		}

		final String cacheKey = getFullCacheKey(requestArgument, request);

		this.logger.debug("cache key:{}", cacheKey);

		if (isDeleteCache(request)) {
			this.cacheTemplate.deleteCache(cacheKey);
			this.logger.debug("deleted key[{}] cache", cacheKey);
		}

		String responseContent = (String) this.cacheTemplate.execute(new CacheCallback() {

			@Override
			public String getKey() {
				return cacheKey;
			}

			@Override
			public Serializable getValue() throws Exception {
				return handleRequest(requestArgument, request);
			}

		}, getCacheExpiry(requestArgument, request));

		out(response, responseContent);

	}

	public String getFullCacheKey(Object requestArgument, HttpServletRequest request) {
		String keyPrefix = this.cacheKeyPrefix;
		if (StringUtils.isBlank(keyPrefix)) {
			keyPrefix = this.getClass().getName();
		}
		return keyPrefix + "." + getCacheKey(requestArgument, request);
	}

	/**
	 * 获取表示当前请求的缓存key
	 */
	public abstract String getCacheKey(Object requestArgument, HttpServletRequest request);

	/**
	 * 获取缓存的过期时长,单位:秒
	 */
	public int getCacheExpiry(Object requestArgument, HttpServletRequest request){
		return getApplicationConfig().getCacheExpiry();
	}

	public void setCacheTemplate(CacheTemplate cacheTemplate) {
		this.cacheTemplate = cacheTemplate;
	}

	public void setDisableCache(boolean disableCache) {
		this.disableCache = disableCache;
	}

	public void setCacheKeyPrefix(String cacheKeyPrefix) {
		this.cacheKeyPrefix = cacheKeyPrefix;
	}

}
