package com.pyramidframework.dao.converter.errorhandler;

import com.pyramidframework.dao.converter.StringConverter;

/**
 * 对错误处理器的处理的保存和访问
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
