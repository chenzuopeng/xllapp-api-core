package org.xllapp.api.core;

import java.util.Date;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.Assert;
import org.junit.Test;
import org.xllapp.api.util.CrytoUtils;
import org.xllapp.api.util.RequestUtils;

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
public class RequestUtilsTest {

	/**
	 * 数字签名校验:通过
	 * @throws Exception
	 */
	@Test
	public void test1() throws Exception{
		String des3Key="b5eefe0437d945b98e82f46fbff8d3552c2ff6f7f8acd8de";
		String appKey="75BD2E98AC17564B2DB7C74B064F5084C6557FDDF3E4C286";
		String[] signItems={"123","abc"};
		String timestampInRequest="2022-09-16 17:08:00";
		String md5 = CrytoUtils.md5(ArrayUtils.addAll(signItems, new String[] { appKey, timestampInRequest }));
		String sign=CrytoUtils.encode(des3Key, timestampInRequest,md5);
		
		System.out.println(sign);
		
		boolean b=RequestUtils.verifySign(sign, timestampInRequest, signItems, des3Key, appKey, 1200);
		Assert.assertTrue(b);
	}
	
	/**
	 * 数字签名校验:请求过期
	 * @throws Exception
	 */
	@Test
	public void test2() throws Exception{
		String des3Key="b5eefe0437d945b98e82f46fbff8d3552c2ff6f7f8acd8de";
		String appKey="75BD2E98AC17564B2DB7C74B064F5084C6557FDDF3E4C286";
		String[] signItems={"a","b"};
		String timestampInRequest="2014-09-12 16:08:00";
		String md5 = CrytoUtils.md5(ArrayUtils.addAll(signItems, new String[] { appKey, timestampInRequest }));
		String sign=CrytoUtils.encode(des3Key, timestampInRequest,md5);
		boolean b=RequestUtils.verifySign(sign, timestampInRequest, signItems, des3Key, appKey, 1);
		Assert.assertFalse(b);
	}
	
	/**
	 * 数字签名校验:无效的签名,null
	 * @throws Exception
	 */
	@Test
	public void test3() throws Exception{
		String des3Key="b5eefe0437d945b98e82f46fbff8d3552c2ff6f7f8acd8de";
		String appKey="75BD2E98AC17564B2DB7C74B064F5084C6557FDDF3E4C286";
		String[] signItems={"a","b"};
		String timestampInRequest="2014-09-12 16:08:00";
		boolean b=RequestUtils.verifySign(null, timestampInRequest, signItems, des3Key, appKey, 1);
		Assert.assertFalse(b);
	}
	
	/**
	 * 数字签名校验:无效的签名,""
	 * @throws Exception
	 */
	@Test
	public void test4() throws Exception{
		String des3Key="b5eefe0437d945b98e82f46fbff8d3552c2ff6f7f8acd8de";
		String appKey="75BD2E98AC17564B2DB7C74B064F5084C6557FDDF3E4C286";
		String[] signItems={"a","b"};
		String timestampInRequest="2014-09-12 16:08:00";
		boolean b=RequestUtils.verifySign("", timestampInRequest, signItems, des3Key, appKey, 1);
		Assert.assertFalse(b);
	}
	
	/**
	 * 数字签名校验:无效的请求时间,null
	 * @throws Exception
	 */
	@Test
	public void test5() throws Exception{
		String des3Key="b5eefe0437d945b98e82f46fbff8d3552c2ff6f7f8acd8de";
		String appKey="75BD2E98AC17564B2DB7C74B064F5084C6557FDDF3E4C286";
		String[] signItems={"a","b"};
		boolean b=RequestUtils.verifySign("abc", null, signItems, des3Key, appKey, 1);
		Assert.assertFalse(b);
	}
	
	/**
	 * 数字签名校验:无效的请求时间,""
	 * @throws Exception
	 */
	@Test
	public void test6() throws Exception{
		String des3Key="b5eefe0437d945b98e82f46fbff8d3552c2ff6f7f8acd8de";
		String appKey="75BD2E98AC17564B2DB7C74B064F5084C6557FDDF3E4C286";
		String[] signItems={"a","b"};
		boolean b=RequestUtils.verifySign("abc", "", signItems, des3Key, appKey, 1);
		Assert.assertFalse(b);
	}
	
	/**
	 * 数字签名校验:请求中的时间与签名中的时间不一致
	 * @throws Exception
	 */
	@Test
	public void test7() throws Exception{
		String des3Key="b5eefe0437d945b98e82f46fbff8d3552c2ff6f7f8acd8de";
		String appKey="75BD2E98AC17564B2DB7C74B064F5084C6557FDDF3E4C286";
		String[] signItems={"a","b"};
		String timestampInRequest="2014-09-12 16:08:00";
		String md5 = CrytoUtils.md5(ArrayUtils.addAll(signItems, new String[] { appKey, "2014-09-12 16:08:00" }));
		String sign=CrytoUtils.encode(des3Key, timestampInRequest,md5);
		boolean b=RequestUtils.verifySign(sign, "2014-09-12 00:00:00", signItems, des3Key, appKey, 1);
		Assert.assertFalse(b);
	}
	
	/**
	 * 数字签名校验:md5部分无效
	 * @throws Exception
	 */
	@Test
	public void test8() throws Exception{
		String des3Key="b5eefe0437d945b98e82f46fbff8d3552c2ff6f7f8acd8de";
		String appKey="75BD2E98AC17564B2DB7C74B064F5084C6557FDDF3E4C286";
		String timestampInRequest=DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
		String md5 = CrytoUtils.md5(ArrayUtils.addAll(new String[]{"a","b1"}, new String[] { appKey, timestampInRequest }));
		String sign=CrytoUtils.encode(des3Key, timestampInRequest,md5);
		boolean b=RequestUtils.verifySign(sign, timestampInRequest, new String[]{"a","b2"}, des3Key, appKey, 1200);
		Assert.assertFalse(b);
	}
	
	/**
	 * 数字签名校验:无效的签名
	 * @throws Exception
	 */
	@Test
	public void test9() throws Exception {
		String des3Key="b5eefe0437d945b98e82f46fbff8d3552c2ff6f7f8acd8de";
		String appKey="75BD2E98AC17564B2DB7C74B064F5084C6557FDDF3E4C286";
		String[] signItems={"a","b1"};
		boolean b=RequestUtils.verifySign("abcdesfafsadfasf", "2014-09-12 00:00:00", signItems, des3Key, appKey, 1);
		Assert.assertFalse(b);
	}
	
	/**
	 * 数字签名校验:md5部分无效
	 * @throws Exception
	 */
	@Test
	public void test10() throws Exception{
		String des3Key="b5eefe0437d945b98e82f46fbff8d3552c2ff6f7f8acd8de";
		String appKey="75BD2E98AC17564B2DB7C74B064F5084C6557FDDF3E4C286";
		String timestampInRequest=DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
		String[] signItems={"a","b1"};
		String sign=CrytoUtils.encode(des3Key, timestampInRequest);
		boolean b=RequestUtils.verifySign(sign, timestampInRequest, signItems, des3Key, appKey, 1200);
		Assert.assertFalse(b);
	}
	
}
