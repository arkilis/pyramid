package com.pyramidframework.dao.model.datatype;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IntegerDataType extends DataType {
	private int jdbcType;
	public static final String TYPE_NAME = "Integer";

	public String toDBSchema() {
		return "INTEGER";
	}

	public int getJDBCType() {
		return jdbcType;
	}

	public String getTypeName() {
		return TYPE_NAME;
	}

	public Object getValueFromResultSet(ResultSet resultSet, int index) throws SQLException {
		return new Integer(resultSet.getInt(index));
	}

	public void setParameter(PreparedStatement statement, int index, Object paramValue) throws SQLException {
		if (paramValue == null|| "".equals(paramValue)) statement.setNull(index, getJDBCType());
		else if (paramValue instanceof Number){
			statement.setInt(index, ((Number) paramValue).intValue());
		}else statement.setInt(index, Integer.parseInt(String.valueOf(paramValue)));
	}

	public IntegerDataType() {
		this.jdbcType = DataType.INTEGER;
	}

	public IntegerDataType(int JDBCType) {
		this.jdbcType = JDBCType;
	}

	public static DataTypeFactory getFactory() {
		Pattern pattern = Pattern.compile(".*(?i)INT\\((\\d+)\\).*");
		return new AbstractDatatypeFactory(pattern) {
			protected DataType createDataType(Matcher matcher) {
				int size = Integer.parseInt(matcher.group(1));
				if (size < 12) {
					IntegerDataType dataType = new IntegerDataType();
					return dataType;
				}
				return null;
			}

			public DataType fromString(String typeDescription, Map props) {
				if (TYPE_NAME.equalsIgnoreCase(typeDescription)) {
					IntegerDataType dataType = new IntegerDataType();
					return dataType;
				}
				return null;
			}
		};

	}

}
