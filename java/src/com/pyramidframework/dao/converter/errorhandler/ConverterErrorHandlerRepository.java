package com.pyramidframework.dao.converter.errorhandler;

import com.pyramidframework.dao.converter.StringConverter;

/**
 * �Դ��������Ĵ���ı���ͷ���
 * @author Mikab Peng
 *
 */
public interface ConverterErrorHandlerRepository {
	public static String ERROR_HANDLER_TYPENAME ="errorHandler";
	
	/**
	 * 
	 * @param converter
	 */
	public void storeErrorHandler(StringConverter converter);
	
	/**
	 * 
	 * @param converter
	 * @return
	 */
	public ConvertErrorHandler getErrorHandler(StringConverter converter);
	
}
