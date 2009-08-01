package com.pyramidframework.dao.converter.errorhandler;



import com.pyramidframework.dao.DAOException;
import com.pyramidframework.dao.converter.StringConverter;

/**
 * 管理转换过程中的错误
 * @author Mikab Peng
 *
 */
public interface ConvertErrorHandler {
	
	public void setStringConverter(StringConverter instance);
	
	/**
	 * 处理toString方法的错误
	 * @param value
	 * @param ex
	 * @param instance
	 * @return
	 */
	public String handleToStringError(Object value,Throwable ex,StringConverter instance)throws DAOException;
	
	
	/**
	 * 处理fromString方法过程中发生的错误
	 * @param str
	 * @param ex
	 * @param instance
	 * @return
	 */
	public Object handleFromStringError(String str,Throwable ex,StringConverter instance)throws DAOException;
	
}
