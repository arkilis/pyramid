package com.pyramidframework.dao.model.datatype;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Boolean¿‡–Õµƒhandler
 * 
 * @author Mikab Peng
 * 
 */
public class BooleanDataType extends DataType {
	public static final String TYPE_NAME = "Boolean";

	public String getTypeName() {
		return TYPE_NAME;
	}

	public int getJDBCType() {
		return DataType.BOOLEAN;
	}

	public String toDBSchema() {
		return "BOOLEAN";
	}

	public Object getValueFromResultSet(ResultSet resultSet, int index) throws SQLException {
		return resultSet.getBoolean(index) ? Boolean.TRUE : Boolean.FALSE;

	}

	public void setParameter(PreparedStatement statement, int index, Object paramValue) throws SQLException {

		if (paramValue == null) statement.setNull(index, getJDBCType());
		else if (paramValue instanceof Boolean) statement.setBoolean(index, ((Boolean) paramValue).booleanValue());
		else statement.setBoolean(index, "true".equalsIgnoreCase(String.valueOf(paramValue)));

	}

	public static DataTypeFactory getFactory() {
		Pattern pattern = Pattern.compile(".*((?i)BOOLEAN)|((?i)tinyint\\(1\\)).*");
		return new AbstractDatatypeFactory(pattern) {
			protected DataType createDataType(Matcher matcher) {
				BooleanDataType dataType = new BooleanDataType();
				return dataType;
			}

			public DataType fromString(String typeDescription, Map props) {
				if (TYPE_NAME.equalsIgnoreCase(typeDescription)) {
					BooleanDataType dataType = new BooleanDataType();
					return dataType;
				}
				return null;
			}
		};
	}

}
