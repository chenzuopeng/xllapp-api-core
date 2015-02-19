package org.xllapp.api.util;

import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author dylan.chen Nov 10, 2014
 * 
 */
public abstract class RequestArgumentHelper {

	public static String getString(Map<String, Object> requestArgument, String name) {
		return getArgument(requestArgument, name);
	}

	public static String getString(Map<String, Object> requestArgument, String name, String defaultValue) {
		return getArgument(requestArgument, name, defaultValue);
	}

	public static <T> List<T> getList(Map<String, Object> requestArgument, String name) {
		return getArgument(requestArgument, name);
	}

	public static <T> List<T> getList(Map<String, Object> requestArgument, String name, List<T> defaultValue) {
		return getArgument(requestArgument, name, defaultValue);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getArgument(Map<String, Object> requestArgument, String name) {
		return (T) requestArgument.get(name);
	}

	public static <T> T getArgument(Map<String, Object> requestArgument, String name, T defaultValue) {
		T value = getArgument(requestArgument, name);
		return null == value ? defaultValue : value;
	}

}
