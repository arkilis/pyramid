package com.pyramidframework.dao.converter;

import java.util.Collection;
import java.util.Map;



import com.pyramidframework.dao.converter.errorhandler.ConvertErrorHandler;
import com.pyramidframework.dao.model.ModelField;

/**
 * 完成是字符串之间的转换
 * 
 * @author Mikab Peng
 * 
 */
public interface StringConverter {

	/**
	 * 从原始字符串进行转换
	 * 
	 * @param str
	 * @return
	 */
	public Object fromString(String str);

	/**
	 * 转换成指定格式的字符串
	 * 
	 * @param value
	 * @return
	 */
	public String toString(Object value);
	
	/**
	 * 错误处理器
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
	 * 可以设置的属性名称
	 * @return
	 */
	public Collection getPropertiesNames();
	
	/**
	 * 设置各项属性的值
	 * @param properties
	 */
	public void setProperties(Map properties);
	
	public Map getProperties();
}