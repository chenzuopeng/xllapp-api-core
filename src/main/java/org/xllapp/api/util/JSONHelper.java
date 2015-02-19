package org.xllapp.api.util;

import java.util.Map;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.core.JsonParser;

/**
 * json工具类.
 *
 * @author dylan.chen Sep 21, 2013
 * 
 */
public class JSONHelper {

	private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	
	private final static ObjectMapper LCWU_OBJECT_MAPPER = new ObjectMapper();
	
	static{
		OBJECT_MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES,true);
		OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES,true);
		
		LCWU_OBJECT_MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		LCWU_OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES,true);
		LCWU_OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES,true);
		LCWU_OBJECT_MAPPER.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
	}
	
	/**
	 * 将json字符串转换成Map对象
	 * 
	 *  json类型与java类型的对应关系查看：http://wiki.fasterxml.com/JacksonInFiveMinutess
	 * 
	 * @author: 陈作朋 Aug 18, 2014
	 * @param json
	 * @return Map
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static Map<String,Object> toMap(String json) throws Exception {
		return OBJECT_MAPPER.readValue(json, Map.class);
	}

	/**
	 * 将对象转换成json字符串
	 * 
	 * @author: 陈作朋 Aug 18, 2014
	 * @param object
	 * @param camelCaseToLowerCaseWithUnderscores 将小写下划线分割的json属性名到驼峰格式的java属性名
	 * @return String
	 * @throws Exception
	 */
	public static String toJSONString(Object object, boolean camelCaseToLowerCaseWithUnderscores) throws Exception {
		ObjectMapper mapper = camelCaseToLowerCaseWithUnderscores ? LCWU_OBJECT_MAPPER : OBJECT_MAPPER;
		return mapper.writeValueAsString(object);
	}
	
	/**
	 * 将对象转换成json字符串
	 * 
	 * @author: 陈作朋 Aug 18, 2014
	 * @param object
	 * @return String
	 * @throws Exception
	 */
	public static String toJSONString(Object object) throws Exception {
		return toJSONString(object, false);
	}

	/**
	 * 类似toJSONString()方法，除了在转换过程出现异常时,返回""(空串).
	 * 
	 * @author: 陈作朋 Aug 18, 2014
	 * @param object
	 * @param camelCaseToLowerCaseWithUnderscores 将小写下划线分割的json属性名到驼峰格式的java属性名
	 * @return String
	 */
	public static String toJSONStringQuietly(Object object, boolean camelCaseToLowerCaseWithUnderscores) {
		ObjectMapper mapper = camelCaseToLowerCaseWithUnderscores ? LCWU_OBJECT_MAPPER : OBJECT_MAPPER;
		try {
			return mapper.writeValueAsString(object);
		} catch (Exception e) {
			return "";
		}
	}
	
	/**
	 * 类似toJSONString()方法，除了在转换过程出现异常时,返回""(空串).
	 * 
	 * @author: 陈作朋 Aug 18, 2014
	 * @param object
	 * @return String
	 */
	public static String toJSONStringQuietly(Object object) {
		return toJSONStringQuietly(object, false);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getValue(String json, String... paths) throws Exception {
		Object value = null;
		Map<String, Object> node = OBJECT_MAPPER.readValue(json, Map.class);
		for (int i = 0; i < paths.length; i++) {
			value = node.get(paths[i]);
			if (i == paths.length - 1) {
				break;
			} else if (value instanceof Map) {
				node = (Map<String, Object>) value;
			} else {
				return null;
			}
		}
		return (T)value;
	}
	
}
