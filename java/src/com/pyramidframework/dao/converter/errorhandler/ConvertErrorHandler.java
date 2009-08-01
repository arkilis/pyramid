package com.pyramidframework.dao.converter.errorhandler;



import com.pyramidframework.dao.DAOException;
import com.pyramidframework.dao.converter.StringConverter;

/**
 * ����ת�������еĴ���
 * @author Mikab Peng
 *
 */
public interface ConvertErrorHandler {
	
	public void setStringConverter(StringConverter instance);
	
	/**
	 * ����toString�����Ĵ���
	 * @param value
	 * @param ex
	 * @param instance
	 * @return
	 */
	public String handleToStringError(Object value,Throwable ex,StringConverter instance)throws DAOException;
	
	
	/**
	 * ����fromString���������з����Ĵ���
	 * @param str
	 * @param ex
	 * @param instance
	 * @return
	 */
	public Object handleFromStringError(String str,Throwable ex,StringConverter instance)throws DAOException;
	
}
