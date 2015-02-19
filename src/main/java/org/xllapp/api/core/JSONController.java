package org.xllapp.api.core;

import static org.xllapp.api.util.RequestArgumentAssert.assertNotBlankString;
import static org.xllapp.api.util.RequestArgumentAssert.assertValidTimestamp;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.xllapp.api.core.exception.ApiException;
import org.xllapp.api.core.exception.InvalidRequestArgumentException;
import org.xllapp.api.core.vo.ApiResponse;
import org.xllapp.api.core.vo.ResultCode;
import org.xllapp.api.util.JSONHelper;
import org.xllapp.api.util.RequestUtils;

/**
 * 符合二期接口规范的API基类.
 * 
 * 实现功能:
 * 
 *   1,JSON格式请求参数的解析和响应内容转换成JSON； 
 *   2,验证二期接口规范中定义的系统参数; 
 *   3,验证数字签名;
 *
 * @author dylan.chen Sep 16, 2014
 * 
 */
public abstract class JSONController extends BaseController {
	
	private final static String CHARSET = "UTF-8";

	@Override
	public Object resolveAndVerifyArgument(HttpServletRequest request) throws InvalidRequestArgumentException {
		Map<String, Object> requestArgument = resolveArgument(request);
		verifySystemArgument(requestArgument);
		verifyBusiArgument(requestArgument);
		verifySign(requestArgument);
		return requestArgument;
	}

	public Map<String, Object> resolveArgument(HttpServletRequest request) throws InvalidRequestArgumentException {
		if ("GET".equals(request.getMethod())) {
			return resolveGetArgument(request);
		} else {
			return resolvePostArgument(request);
		}
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> resolveGetArgument(final HttpServletRequest request) throws InvalidRequestArgumentException {
		Map<String, Object> requestArgument = new HashMap<String, Object>();
		for (Enumeration<String> paramNames = request.getParameterNames(); paramNames.hasMoreElements();) {
			String paramName = paramNames.nextElement();
			String[] values = request.getParameterValues(paramName);
			try {
				if (values.length > 1) {
					List<String> rawValues=new ArrayList<String>();
					for (int i = 0; i < values.length; i++) {
						rawValues.add(URLDecoder.decode(values[i], CHARSET));
					}
					requestArgument.put(paramName, rawValues);
				} else {
					requestArgument.put(paramName, URLDecoder.decode(values[0], CHARSET));
				}
			} catch (Exception e) {
				throw new InvalidRequestArgumentException(e.getLocalizedMessage());
			}
		}
		return requestArgument;
	}

	public Map<String, Object> resolvePostArgument(HttpServletRequest request) throws InvalidRequestArgumentException {
		Map<String, Object> requestArgument = null;
		try {
			String json = IOUtils.toString(request.getInputStream());
			if (StringUtils.isBlank(json)) {
				throw new InvalidRequestArgumentException("request body is empty");
			}
			requestArgument = JSONHelper.toMap(json);
		} catch (Exception e) {
			if (!(e instanceof InvalidRequestArgumentException)) {
				throw new InvalidRequestArgumentException("failure to resolve argument", e);
			} else {
				throw (InvalidRequestArgumentException) e;
			}
		}
		return requestArgument;
	}

	public void verifySystemArgument(Map<String, Object> requestArgument) throws InvalidRequestArgumentException {
		assertNotBlankString(requestArgument, "product_id");
		assertNotBlankString(requestArgument, "org_code");
		assertNotBlankString(requestArgument, "city_code");
		assertNotBlankString(requestArgument, "client_type");
		assertNotBlankString(requestArgument, "client_version");
		assertNotBlankString(requestArgument, "client_channel_type");
		assertNotBlankString(requestArgument, "os_type");
		assertNotBlankString(requestArgument, "imsi");
		assertNotBlankString(requestArgument, "imei");
		assertNotBlankString(requestArgument, "mobile");
		assertNotBlankString(requestArgument, "longitude");
		assertNotBlankString(requestArgument, "latitude");
		assertValidTimestamp(requestArgument, "timestamp");
	}

	public abstract void verifyBusiArgument(Map<String, Object> requestArgument) throws InvalidRequestArgumentException;

	public void verifySign(Map<String, Object> requestArgument) throws InvalidRequestArgumentException {

		if (!isVerifyRequestSign()) {
			return;
		}

		boolean isValidSign = RequestUtils.verifySign(resolveSign(requestArgument), resolveTimestamp(requestArgument), getSignItems(requestArgument), getDes3Key(), getAppKey(), getRequestSignExpiry());
		if (!isValidSign) {
			throw new InvalidRequestArgumentException("invalid request sign");
		}

	}

	public boolean isVerifyRequestSign() {
		return getApplicationConfig().isVerifyRequestSign();
	}

	public String resolveSign(Map<String, Object> requestArgument) {
		return (String) requestArgument.get("sign");
	}

	public String resolveTimestamp(Map<String, Object> requestArgument) {
		return (String) requestArgument.get("timestamp");
	}

	public String getDes3Key() {
		return getApplicationConfig().getDes3Key();
	}

	public String getAppKey() {
		return getApplicationConfig().getAppKey();
	}

	public int getRequestSignExpiry() {
		return getApplicationConfig().getRequestSignExpiry();
	}

	/**
	 * 返回生成请求签名中MD5部分需要的请求参数值.
	 * 
	 * 注意： 
	 * 
	 *    1,返回的数组中不能包含app_key和timestamp;
	 *    2,验证时会自动在此方法返回的数组后面依次添加app_key和timestamp
	 *      (既在数字签名规则的MD5部分必须是这样的:MD5(item1+$+...+$+itemN+$+app_key+$+timestamp)
	 *    3,返回数组中元素的顺序与签名规则中的一致; 例：
	 *       如果sign规则为：URLEncoding(BASE64(3DES(timestamp+$+MD5(imsi+$+imei+$+app_key+$+timestamp)))) 
	 *       那么此方法返回的数组为：new String[]{(String)requestArgument.get("imsi"),(String)requestArgument.get("imei")}
	 */
	public abstract String[] getSignItems(Map<String, Object> requestArgument);

	@SuppressWarnings("unchecked")
	@Override
	public String handleRequest(Object requestArgument, HttpServletRequest request) throws Exception {
		Object resultData = handleRequest((Map<String, Object>) requestArgument);
		ApiResponse response = null;
		if (resultData instanceof ApiResponse) {
			response = (ApiResponse) resultData;
		} else {
			response = new ApiResponse();
			response.setResultCode(ResultCode.SUCCESS);
			response.setResultDesc(ResultCode.SUCCESS.getDesc());
			response.setTimestamp(new Date());
			response.setData(resultData);
		}
		return JSONHelper.toJSONString(response, true);
	}

	public abstract Object handleRequest(Map<String, Object> requestArgument) throws ApiException;

}
