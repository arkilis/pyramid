package com.pyramidframework.dao;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统内部的默认的值对象
 * @author Mikab Peng
 *
 */
public class ValueObject implements VOSupport {

	private static final long serialVersionUID = -6850166814685809995L;
	private String name = null;
	private Map values = new HashMap();
	
	
	/**需要制定模型名称*/
	ValueObject(String modelName) {
		this.name = modelName;
	}
	
	
	public String getName() {
		return name;
	}

	public Map getValues() {
		return values;
	}
	
	public Object getProperty(String name){
		return values.get(name);
	}
	
	public Object getString(String name){
		
		return values.get(name);
	}
	
	public void setProperty(String name,Object value){
		values.put(name, value);
	}
	
}
