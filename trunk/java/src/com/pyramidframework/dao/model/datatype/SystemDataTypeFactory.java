package com.pyramidframework.dao.model.datatype;

import java.util.Map;

import com.pyramidframework.dao.DAOException;

public class SystemDataTypeFactory implements DataTypeFactory {

	public static DataTypeFactory[] typeFactories = new DataTypeFactory[] { BooleanDataType.getFactory(), IntegerDataType.getFactory(), DoubleDataType.getFactory(),
			StringDataType.getFactory(), DateDataType.getFactory(), BigIntegerDataType.getFactory(), BigDecimalType.getFactory(), BlobDataType.getFactory() };

	public DataType fromDBSchema(String columnDescription) {
		for (int i = 0; i < typeFactories.length; i++) {
			DataType dataType = typeFactories[i].fromDBSchema(columnDescription);
			if (dataType != null) return dataType;
		}
		throw new DAOException("unknown column definition:" + columnDescription);
	}

	public DataType fromString(String typeDescription, Map props) {
		for (int i = 0; i < typeFactories.length; i++) {
			DataType dataType = typeFactories[i].fromString(typeDescription, props);
			if (dataType != null) return dataType;
		}
		return null;
	}
	
	private static final SystemDataTypeFactory instance = new SystemDataTypeFactory();

	public static SystemDataTypeFactory getInstance() {
		return instance;
	}
	
}
