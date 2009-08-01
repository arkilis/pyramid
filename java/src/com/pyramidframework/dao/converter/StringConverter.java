package com.pyramidframework.dao.converter;

import java.util.Collection;
import java.util.Map;



import com.pyramidframework.dao.converter.errorhandler.ConvertErrorHandler;
import com.pyramidframework.dao.model.ModelField;

/**
 * ������ַ���֮���ת��
 * 
 * @author Mikab Peng
 * 
 */
public interface StringConverter {

	/**
	 * ��ԭʼ�ַ�������ת��
	 * 
	 * @param str
	 * @return
	 */
	public Object fromString(String str);

	/**
	 * ת����ָ����ʽ���ַ���
	 * 
	 * @param value
	 * @return
	 */
	public String toString(Object value);
	
	/**
	 * ��������
	 * @param handler
	 */
	public void setErrorHandler(ConvertErrorHandler handler);
	
	
	/**
	 * 
	 * @return
	 */
	public ConvertErrorHandler getErrorHandler();
	
	/**
	 * 
	 * @param field
	 */
	public void setModelField(ModelField field);
	
	/**
	 * 
	 * @return
	 */
	public ModelField getModelField();
	
	/**
	 * �������õ���������
	 * @return
	 */
	public Collection getPropertiesNames();
	
	/**
	 * ���ø������Ե�ֵ
	 * @param properties
	 */
	public void setProperties(Map properties);
	
	public Map getProperties();
}