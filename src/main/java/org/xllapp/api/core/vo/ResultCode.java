package org.xllapp.api.core.vo;

/**
 * 结果码枚举.
 *
 * @author dylan.chen Aug 18, 2014
 * 
 */
public enum ResultCode{
	
	SUCCESS(0,"request successful"),FAILURE(1,"request failed");
	
	private int value;
	
	private String desc;
	
	private ResultCode(int value,String desc){
		this.value=value;
		this.desc=desc;
	}
	
	public int getValue() {
		return value;
	}

	public String getDesc() {
		return desc;
	}

}