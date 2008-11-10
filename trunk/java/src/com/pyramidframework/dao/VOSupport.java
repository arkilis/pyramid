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

}
