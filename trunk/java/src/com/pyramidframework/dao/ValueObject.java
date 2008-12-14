package com.pyramidframework.dao;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ϵͳ�ڲ���Ĭ�ϵ�ֵ����,�����Ը������͵���������ķ�װ��
 * ���г�ȥgetString�����⣬����ֱ�Ӵ�Map��ȡ��ֱֵ�Ӷ�����ת����getString������ֱ�ӵ���String.valueOf����
 * 
 * @author Mikab Peng
 * 
 */
public class ValueObject implements VOSupport {

	private static final long serialVersionUID = -6850166814685809995L;
	private String modelName = null;
	private Map values = new HashMap();

	/** ��Ҫ�ƶ�ģ������ */
	ValueObject(String modelName) {
		this.modelName = modelName;
	}

	/**
	 * ��������ģ������
	 */
	public String getName() {
		return modelName;
	}

	/**
	 * �����ڲ���ȫ������ֵ
	 */
	public Map getValues() {
		return values;
	}

	/**
	 * ���ظ������µ�����ֵ��ֱ�ӷ����ڲ����е���������
	 * 
	 * @param name
	 * @return
	 */
	public Object getProperty(String name) {
		return values.get(name);
	}

	/**
	 * ���ø������µ�����ֵ���������������
	 * 
	 * @param name
	 * @param value
	 */
	public void setProperty(String name, Object value) {
		values.put(name, value);
	}

	/**
	 * ֱ����ֵ��ȥ���������µ�����ֵ
	 * 
	 * @param name
	 */
	public void removeProperty(String name) {
		values.remove(name);
	}

	/**
	 * ������ڸö��󣬵���String.valueOf��ʽת�������򷵻�NULL
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
