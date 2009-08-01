package com.pyramidframework.dao.model.datatype;

import java.util.Map;


/**
 * ��Data base schema֮���ת��
 * @author Mikab Peng
 *
 */
public interface DataTypeFactory {
	
	
	

	public DataType fromDBSchema(String columnDescription);
	
	/**
	 * ���ַ������õ����������
	 * @param typeDescription
	 * @return
	 */
	public DataType fromString(String typeDescription,Map props);
}
