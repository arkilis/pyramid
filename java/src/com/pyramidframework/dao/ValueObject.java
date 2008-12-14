package com.pyramidframework.dao;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统内部的默认的值对象,包含对各种类型的容器对象的封装。
 * 其中除去getString方法外，都是直接从Map中取得值直接多类型转换；getString方法则直接调用String.valueOf方法
 * 
 * @author Mikab Peng
 * 
 */
public class ValueObject implements VOSupport {

	private static final long serialVersionUID = -6850166814685809995L;
	private String modelName = null;
	private Map values = new HashMap();

	/** 需要制定模型名称 */
	ValueObject(String modelName) {
		this.modelName = modelName;
	}

	/**
	 * 返回数据模型名称
	 */
	public String getName() {
		return modelName;
	}

	/**
	 * 返回内部的全部属性值
	 */
	public Map getValues() {
		return values;
	}

	/**
	 * 返回该名字下的属性值，直接返回内部持有的数据类型
	 * 
	 * @param name
	 * @return
	 */
	public Object getProperty(String name) {
		return values.get(name);
	}

	/**
	 * 设置该名字下的属性值，不对类型做检查
	 * 
	 * @param name
	 * @param value
	 */
	public void setProperty(String name, Object value) {
		values.put(name, value);
	}

	/**
	 * 直接在值中去除该名字下的属性值
	 * 
	 * @param name
	 */
	public void removeProperty(String name) {
		values.remove(name);
	}

	/**
	 * 如果存在该对象，调用String.valueOf方式转换，否则返回NULL
	 * 
	 * @param name
	 * @return
	 */
	public String getString(String name) {
		Object object = values.get(name);
		if (object != null) {
			return String.valueOf(object);
		}
		return null;
	}

	public byte[] getBytes(String name) {
		return (byte[]) values.get(name);
	}

	public BigDecimal getBigDecimal(String name) {
		return (BigDecimal) values.get(name);
	}

	public BigInteger getBigInteger(String name) {
		return (BigInteger) values.get(name);
	}

	public Date getDate(String name) {
		return (Date) values.get(name);
	}

	public List getList(String name) {
		return (List) values.get(name);
	}

	public Boolean getBoolean(String name) {
		return (Boolean) values.get(name);
	}

	public Byte getByte(String name) {
		return (Byte) values.get(name);
	}

	public Character getChar(String name) {
		return (Character) values.get(name);
	}

	public Double getDouble(String name) {
		return (Double) values.get(name);
	}

	public Float getFloat(String name) {
		return (Float) values.get(name);
	}

	public Integer getInt(String name) {
		return (Integer) values.get(name);
	}

	public Long getLong(String name) {
		return (Long) values.get(name);
	}

	public Short getShort(String name) {
		return (Short) values.get(name);
	}

	public void setBoolean(String name, Boolean value) {
		values.put(name, value);
	}

	public void setByte(String name, Byte value) {
		values.put(name, value);
	}

	void setChar(String name, Character value) {
		values.put(name, value);
	}

	public void setDouble(String name, Double value) {
		values.put(name, value);
	}

	public void setFloat(String name, Float value) {
		values.put(name, value);
	}

	public void setInt(String name, Integer value) {
		values.put(name, value);
	}

	public void setLong(String name, Long value) {
		values.put(name, value);
	}

	public void setShort(String name, Short value) {
		values.put(name, value);
	}

	public void setBytes(String name, byte[] value) {
		values.put(name, value);
	}

	public void setBigDecimal(String name, BigDecimal value) {
		values.put(name, value);
	}

	public void setBigInteger(String name, BigInteger value) {
		values.put(name, value);
	}

	public void setDate(String name, Date value) {
		values.put(name, value);
	}

	public void setString(String name, String value) {
		values.put(name, value);
	}

	public void setList(String name, List value) {
		values.put(name, value);
	}

}
