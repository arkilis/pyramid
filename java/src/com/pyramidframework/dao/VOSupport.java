package com.pyramidframework.dao;

import java.io.Serializable;
import java.util.Map;

public interface VOSupport extends Serializable {

	/**
	 * 对应的数据模型的名称
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * 必须是直接返回内部的索引，否则对象的修改将不能被反应回去
	 * 
	 * @return
	 */
	public Map getValues();
	
	/**
	 * 直接通过名字访问属性值
	 * @param name
	 * @return
	 */
	public Object getProperty(String name);
	
	/**
	 * 直接通过属性名设置值
	 * @param name
	 * @param value
	 */
	public void setProperty(String name, Object value);

}
