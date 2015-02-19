package org.xllapp.api.support;

import org.springframework.beans.factory.annotation.Value;
import org.xllapp.config.BaseApplicationConfig;

/**
 * 此类用于存放Api框架相关的配置.
 *
 * @author dylan.chen Aug 20, 2014
 * 
 */
public abstract class ApiApplicationConfig extends BaseApplicationConfig {

	@Value("${request.sign.requestSignExpiry:0}")
	@FieldDescription("请求的过期时长,单位:秒,当为0或小于0时,表示请求永不过期")
	private int requestSignExpiry = 0;

	@Value("${request.sign.isVerifyRequestSign:false}")
	@FieldDescription("是否验证请求的签名")
	private boolean isVerifyRequestSign = false;

	@Value("${request.sign.des3Key:}")
	@FieldDescription("验证请求签名所用的3DES KEY")
	private String des3Key = "";

	@Value("${request.sign.appKey:}")
	@FieldDescription("验证请求签名所用的APP KEY")
	private String appKey = "";

	@Value("${request.cacheExpiry:1800}")
	@FieldDescription("缓存的过期时长,单位:秒")
	private int cacheExpiry = 0;

	public int getRequestSignExpiry() {
		return this.requestSignExpiry;
	}

	public boolean isVerifyRequestSign() {
		return this.isVerifyRequestSign;
	}

	public String getDes3Key() {
		return this.des3Key;
	}

	public String getAppKey() {
		return this.appKey;
	}

	public int getCacheExpiry() {
		return this.cacheExpiry;
	}

}
