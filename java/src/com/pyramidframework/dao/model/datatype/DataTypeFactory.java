package com.pyramidframework.dao.model.datatype;

import java.util.Map;


/**
 * 和Data base schema之间的转换
 * @author Mikab Peng
 *
 */
public interface DataTypeFactory {
	
	
	

	public DataType fromDBSchema(String columnDescription);
	
	/**
	 * 从字符串来得到具体的类型
	 * @param typeDescription
	 * @return
	 */
	public DataType fromString(String typeDescription,Map props);
}
