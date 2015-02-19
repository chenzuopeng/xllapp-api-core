package org.xllapp.api.support.jackson;

import java.io.IOException;

import org.xllapp.api.core.vo.ResultCode;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;


/**
 * 将ResultCode枚举值转换成字符串.
 *
 * @author dylan.chen Aug 18, 2014
 * 
 */
public class ResultCodeSerializer extends JsonSerializer<ResultCode> {

	@Override
	public void serialize(ResultCode value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
		jgen.writeString(Integer.toString(value.getValue())); 		
	}

}
