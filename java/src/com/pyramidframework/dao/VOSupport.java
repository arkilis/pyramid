package com.pyramidframework.dao;

import java.io.Serializable;
import java.util.Map;

public interface VOSupport extends Serializable {

	/**
	 * ��Ӧ������ģ�͵�����
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * ������ֱ�ӷ����ڲ������������������޸Ľ����ܱ���Ӧ��ȥ
	 * 
	 * @return
	 */
	public Map getValues();
	
	/**
	 * ֱ��ͨ�����ַ�������ֵ
	 * @param name
	 * @return
	 */
	public Object getProperty(String name);
	
	/**
	 * ֱ��ͨ������������ֵ
	 * @param name
	 * @param value
	 */
	public void setProperty(String name, Object value);

}
