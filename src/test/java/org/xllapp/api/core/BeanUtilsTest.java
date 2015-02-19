package org.xllapp.api.core;

import org.junit.Test;
import org.xllapp.api.support.ApiApplicationConfig;
import org.xllapp.api.util.BeanUtils;

/**
 *
 *
 * @Copyright: Copyright (c) 2014 FFCS All Rights Reserved
 * @Company: 北京福富软件有限公司
 * @author 陈作朋 Sep 1, 2014
 * @version 1.00.00
 * @history:
 * 
 */
public class BeanUtilsTest {

	public static class ApplicationConfig extends ApiApplicationConfig {

		private int i;

		private boolean isShow;

		private String url = "http://www.163.com";

		private long l = 1;

	}

	@Test
	public void test1() throws Exception {

		ApplicationConfig applicationConfig = new ApplicationConfig();

		System.err.println(applicationConfig);

		applicationConfig.afterPropertiesSet();

		System.err.println(applicationConfig);

		BeanUtils.setPropertyValue("l", applicationConfig, 123);

		BeanUtils.setPropertyValue("isShow", applicationConfig, true);

		System.err.println(applicationConfig);

		applicationConfig.resetProperty("l");

		System.err.println(applicationConfig);

//		applicationConfig.resetPropertys();

		System.err.println(applicationConfig);

	}

	@Test
	public void test2() {
		System.out.println(BeanUtils.getFields(ApplicationConfig.class, "originalApplicationConfig"));
	}


}
