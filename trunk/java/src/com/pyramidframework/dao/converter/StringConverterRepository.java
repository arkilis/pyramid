package com.pyramidframework.dao.converter;

import com.pyramidframework.dao.DAOException;
import com.pyramidframework.dao.model.ModelField;

/**
 * 存储格式转换器，一般需要由高级机构实现
 * @author Mikab Peng
 *
 */
public interface StringConverterRepository {
	
	/***
	 * 得到指定字段的转换器
	 * @param fieldPath
	 * @return
	 * @throws DAOException
	 */
	public StringConverter getConverter(ModelField field) throws DAOException;
	
	/***
	 * 存储指定字段的字符串转换器
	 * @param fieldPath
	 * @param converter
	 * @throws DAOException
	 */
	public void storeConverter(StringConverter converter)throws DAOException;
	
}
