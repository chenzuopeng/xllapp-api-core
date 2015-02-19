package org.xllapp.api.core;

import org.junit.Test;
import org.xllapp.api.core.vo.ApiResponse;
import org.xllapp.api.core.vo.ResultCode;
import org.xllapp.api.util.JSONHelper;

/**
 *
 *
 * @Copyright: Copyright (c) 2014 FFCS All Rights Reserved 
 * @Company: 北京福富软件有限公司 
 * @author 陈作朋 Sep 12, 2014
 * @version 1.00.00
 * @history:
 * 
 */
public class ApiResponseTest {

	@Test
	public void test1() throws Exception{
		ApiResponse response=new ApiResponse(ResultCode.FAILURE,"B");
		System.out.println(JSONHelper.toJSONString(response,true));
	}
	
}
