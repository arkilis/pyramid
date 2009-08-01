package com.pyramidframework.dao.converter;

import com.pyramidframework.dao.DAOException;
import com.pyramidframework.dao.model.ModelField;

/**
 * �洢��ʽת������һ����Ҫ�ɸ߼�����ʵ��
 * @author Mikab Peng
 *
 */
public interface StringConverterRepository {
	
	/***
	 * �õ�ָ���ֶε�ת����
	 * @param fieldPath
	 * @return
	 * @throws DAOException
	 */
	public StringConverter getConverter(ModelField field) throws DAOException;
	
	/***
	 * �洢ָ���ֶε��ַ���ת����
	 * @param fieldPath
	 * @param converter
	 * @throws DAOException
	 */
	public void storeConverter(StringConverter converter)throws DAOException;
	
}
