package org.xllapp.api.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.xllapp.api.core.exception.InvalidRequestArgumentException;

/**
 * 此类提供验证请求参数的工具方法.
 *
 * @author dylan.chen Sep 16, 2014
 * 
 */
public abstract class RequestArgumentAssert {

	public static void assertNotBlankString(Map<String, Object> requestArgument, String argName) throws InvalidRequestArgumentException {
		String value = (String) requestArgument.get(argName);
		if (StringUtils.isBlank(value)) {
			throw new InvalidRequestArgumentException(argName + " not be blank");
		}
	}

	public static void assertValidTimestamp(Map<String, Object> requestArgument, String argName, String parsePattern) throws InvalidRequestArgumentException {
		String timestamp = (String) requestArgument.get(argName);
		try {
			DateUtils.parseDate(timestamp, parsePattern);
		} catch (Exception e) {
			throw new InvalidRequestArgumentException("invalid timestamp[" + argName + "=" + timestamp + "],expected date format:" + parsePattern, e);
		}
	}

	public static void assertValidTimestamp(Map<String, Object> requestArgument, String argName) throws InvalidRequestArgumentException {
		assertValidTimestamp(requestArgument, argName, "yyyy-MM-dd HH:mm:ss");
	}
	
	/**
	 * 验证参数是否为null.
	 * 
	 * 例:
	 * 
	 *    如果requestArgument = [b=>"abc"],那么:
	 * 
	 *       RequestArgumentAssert.assertNotNull(requestArgument,"a"),抛出InvalidRequestArgumentException
	 *       RequestArgumentAssert.assertNotNull(requestArgument,"b"),验证通过
	 * 
	 */
	public static void assertNotNull(Map<String, Object> requestArgument, String argName) throws InvalidRequestArgumentException {
		if (null == requestArgument.get(argName) ) {
			throw new InvalidRequestArgumentException(argName + " not be null");
		}
	}
	
	/**
	 * 验证参数是否为空.
	 * 
	 * 例:
	 * 
	 *    如果requestArgument = {string1=>"",string2=>"  ",array=>[],list=>[],map=>{}},那么:
	 * 
	 *       RequestArgumentAssert.assertNotEmpty(requestArgument,"a"),抛出InvalidRequestArgumentException
	 *       RequestArgumentAssert.assertNotEmpty(requestArgument,"string1"),抛出InvalidRequestArgumentException
	 *       RequestArgumentAssert.assertNotEmpty(requestArgument,"string2"),抛出InvalidRequestArgumentException
	 *       RequestArgumentAssert.assertNotEmpty(requestArgument,"array"),抛出InvalidRequestArgumentException
	 *       RequestArgumentAssert.assertNotEmpty(requestArgument,"list"),抛出InvalidRequestArgumentException
	 *       RequestArgumentAssert.assertNotEmpty(requestArgument,"map"),抛出InvalidRequestArgumentException
	 * 
	 */
	public static void assertNotEmpty(Map<String, Object> requestArgument, String argName) throws InvalidRequestArgumentException {
		if (isEmpty(requestArgument.get(argName))) {
			throw new InvalidRequestArgumentException(argName + " not be empty");
		}
	}
		
	private static boolean isEmpty(Object o) throws IllegalArgumentException {
		if (o == null) {
			return true;
		}

		if (o instanceof String) {
			if(StringUtils.isBlank((String)o)){
				return true;
			}
		} else if (o instanceof Collection) {
			if (((Collection<?>) o).isEmpty()) {
				return true;
			}
		} else if (o.getClass().isArray()) {
			if (Array.getLength(o) == 0) {
				return true;
			}
		} else if (o instanceof Map) {
			if (((Map<?,?>) o).isEmpty()) {
				return true;
			}
		} else {
			return false;
		}

		return false;
	}

}
