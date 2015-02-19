package org.xllapp.api.util;

import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 请求工具类.
 * 
 * @author dylan.chen Mar 12, 2014
 * 
 */
public abstract class RequestUtils {

	private static final Logger logger = LoggerFactory.getLogger(RequestUtils.class);

	/**
	 * 生成请求ID
	 * 
	 *  @return
	 */
	public static String generateRequestId() {
		return Long.toString(new Date().getTime());
	}

	/**
	 * 获取请求参数.如果请求参数为空时，返回一个默认值.
	 * 
	 * @param request
	 * @param name 参数名
	 * @param defaultValue 默认值
	 * @return
	 */
	public static String getRequestParam(HttpServletRequest request, String name, String defaultValue) {
		String value = request.getParameter(name);
		return value != null ? value : defaultValue;
	}

	/**
	 * 获取请求参数.如果请求参数为空时，返回一个默认值.
	 * 
	 * @param request
	 * @param name 参数名
	 * @param defaultValue 默认值
	 * @return
	 */
	public static int getRequestParam(HttpServletRequest request, String name, int defaultValue) {
		String value = request.getParameter(name);
		return StringUtils.isNotBlank(value) ? Integer.valueOf(value) : defaultValue;
	}

	/**
	 * 获取请求头列表
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> resolveHeaders(HttpServletRequest request) {
		Map<String, Object> result = new HashMap<String, Object>();
		for (Enumeration<String> headerNames = request.getHeaderNames(); headerNames.hasMoreElements();) {
			String headerName = headerNames.nextElement();
			result.put(headerName, request.getHeader(headerName));
		}
		return result;
	}

	/**
	 * 获取请求参数列表
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> resolveParams(HttpServletRequest request) {
		Map<String, Object> result = new HashMap<String, Object>();
		for (Enumeration<String> paramNames = request.getParameterNames(); paramNames.hasMoreElements();) {
			String paramName = paramNames.nextElement();
			result.put(paramName, request.getParameter(paramName));
		}
		return result;
	}

	/**
	 * 获取请求体
	 * 
	 * @param request
	 * @return
	 */
	public static String resolveBody(HttpServletRequest request) {
		String result;
		try {
			result = IOUtils.toString(request.getInputStream());
		} catch (IOException e) {
			logger.error("failure to resolve body", e);
			result = "[failure to resolve body]";
		}
		return result;
	}

	/**
	 * 获取绝对路径,以"/"开头.
	 * 
	 * @param request
	 * @param path 相对路径
	 * @return
	 */	
	public static String getFullPath(HttpServletRequest request, String path) {

		String tmp = StringUtils.defaultIfBlank(path, "");

		if (StringUtils.startsWithAny(tmp.toLowerCase(), "http", "https")) {
			return path;
		}

		if (!StringUtils.startsWith(path, "/")) {
			path = "/" + path;
		}

		return request.getContextPath()+ request.getServletPath() + path;
	}

	/**
	 * 获取完整的URL.
	 * 
	 * @param request
	 * @param path 相对URL
	 * @return
	 */
	public static String getFullUrl(HttpServletRequest request, String path) {
		return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + getFullPath(request, path);
	}

	/**
	 * 格式化URL
	 * 
	 * @param url
	 * @return
	 */
	public static String normalizeUrl(String url) {
		return url.replaceAll("/{2,}", "/");
	}

	/**
	 * 获取客户端的请求IP.
	 * 
	 * @param request
	 * @return
	 */
	public static String getIp(HttpServletRequest request) {
		String ip = request.getHeader("X-Real-IP");
		if (null == ip) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	/**
	 * 验证请求的数字签名.
	 * 
	 * @author: 陈作朋 Sep 18, 2014
	 * @param sign 待校验的数字签名串
	 * @param timestampInRequest 请求中的时间戳,格式：yyyy-MM-dd HH:mm:ss
	 * @param signItems 生成请求签名中MD5部分需要的内容
	 * 
	 *   注意：
	 *   
	 *    1,数组中不能包含app_key和timestamp;
	 *    2,此方法会在验证时自动在此参数的数组后面依次添加app_key和timestamp(既在数字签名规则的MD5部分必须是这样的:MD5(item1+$+...+$+itemN+$+app_key+$+timestamp)
	 *    3,返回数组中元素的顺序与签名规则中的一致;
	 *    
	 *    例：
	 *       
	 *       如果sign规则为：URLEncoding(BASE64(3DES(timestamp+$+MD5(imsi+$+imei+$+app_key+$+timestamp)))),
	 *       假设imsi为123，imei为abc,
	 *       那么此参数为：new String[]{"123","abc")}.
	 *       
	 * @param des3Key 3DES加密的Key
	 * @param appKey  客户端的appKey
	 * @param expiry  请求签名过期时间(单位:秒),当expiry为0时，不进行请求签名过期验证
	 * @return true 表示签名验证通过,false 表示签名验证失败
	 */
	public static boolean verifySign(String sign, String timestampInRequest, String[] signItems, String des3Key, String appKey, int expiry) {
		try {
			if (StringUtils.isBlank(sign) || StringUtils.isBlank(timestampInRequest)) {
				logger.debug("sign or timestamp is blank");
				return false;
			}

			String[] items = CrytoUtils.decode(des3Key, sign);
			if (items.length != 2) {
				logger.debug("invalid sign - expected items.length = 2 but was items.length = {}",items.length);
				return false;
			}
			String timestampInSign = items[0];
			String md5InSign = items[1];

			// 验证请求的时间
			if (!timestampInSign.equals(timestampInRequest)) {
				logger.debug("timestampInSign[{}] and timestampInRequest[{}] is inconsistent",timestampInSign,timestampInRequest);
				return false;
			}
			
			if (expiry > 0) {
				String parseDatePatterns = "yyyy-MM-dd HH:mm:ss";
				Date requestDate = DateUtils.parseDate(timestampInRequest, parseDatePatterns);
				Date expiredDate = DateUtils.addSeconds(requestDate, expiry);
				if (new Date().after(expiredDate)) {
					if (logger.isDebugEnabled()) {
						logger.debug("request expired - timestampInRequest:{},expiredDate:{}", timestampInRequest, DateFormatUtils.format(expiredDate, parseDatePatterns));
					}
					return false;
				}
			}

			// 验证MD5部分
			String md5 = CrytoUtils.md5(ArrayUtils.addAll(signItems, new String[] { appKey, timestampInRequest }));
			boolean b = md5.equals(md5InSign);
			if(!b){
			    logger.debug("md5InSign[{}] and md5InServer[{}] is inconsistent",md5InSign,md5);
			}
			return b;
		} catch (Exception e) {
			logger.debug("failure to verify sign["+sign+"]", e);
		}
		return false;
	}

}
