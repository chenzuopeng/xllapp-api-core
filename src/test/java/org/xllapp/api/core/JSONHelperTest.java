package org.xllapp.api.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.xllapp.api.core.vo.ApiResponse;
import org.xllapp.api.core.vo.ResultCode;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

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
public class JSONHelperTest {
	
	public static class Bean{
		
		private String user_name="test";
		
		private String createTime="2014-09-11 12:00:00";

		public String getUser_name() {
			return user_name;
		}

		public void setUser_name(String user_name) {
			this.user_name = user_name;
		}

		public String getCreateTime() {
			return createTime;
		}

		public void setCreateTime(String createTime) {
			this.createTime = createTime;
		}
		

	}

	@Test
	public void test1() throws JsonParseException, JsonMappingException, IOException{
//		String json = "{\"user_name\":\"123\",i:1,'o':{\"o1\":true},\"a\":[],\"creatTime\":\"abc\"}";
//		String json = "{\"user_name\":\"123\"}";
		
		String json = "{\"user_name\":\"123\"}";
		
//		String json="{\"map\":{},\"a\":[]}";
	
/*		System.out.println(getValue(json, "abc"));
		System.out.println(getValue(json, "i1"));
		System.out.println(getValue(json, "o", "o1"));
		System.out.println(getValue(json, "a1", "a1"));*/
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES,true);
		mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES,true);
		mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
		Bean bean=mapper.readValue(json, Bean.class);
		System.out.println(bean);
		
        bean=new Bean();
//        bean.setUserName("abc");
        
/*        String mapJson=mapper.writeValueAsString(bean);
        
        System.out.println(mapJson);
        
        Map<String,Object> map=mapper.readValue(mapJson, Map.class);
        
        List<String> array=(List<String>)map.get("array");
        
        Map<String,Object> map1=(Map<String,Object>)map.get("map");
        
        String string=(String)map.get("string");
        
        Boolean bool=(Boolean)map.get("bool");
        
        Integer i=(Integer)map.get("i");*/
        
        Map<String,String> data=new HashMap<String, String>();
		data.put("user_name","abc");
		data.put("createTime","2014-09-11 12:00:00");

		System.out.println(mapper.writeValueAsString(data));
        
//        System.out.println(mapper.writeValueAsString(bean));
		
		List<Bean> list=new ArrayList<JSONHelperTest.Bean>();
		list.add(bean);
		list.add(bean);
		
		Bean[] beans=new Bean[2];
		beans[0]=bean;
		beans[1]=bean;
        
        ApiResponse apiResponse=new ApiResponse(ResultCode.SUCCESS,ResultCode.SUCCESS.getDesc());
        apiResponse.setData(beans);
        
        System.out.println(mapper.writeValueAsString(apiResponse));
        
//        System.out.println(mapper.readValue(json, Map.class));
        
	}
	
	@Test
	public void test2() throws JsonProcessingException{
		class Bean{
			private char c='a';

			public char getC() {
				return c;
			}

			public void setC(char c) {
				this.c = c;
			}
			
		}
		ObjectMapper mapper = new ObjectMapper();
		System.out.println(mapper.writeValueAsString(new Bean()));
	}
	
}
