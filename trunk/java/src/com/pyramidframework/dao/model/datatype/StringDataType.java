package com.pyramidframework.dao.model.datatype;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串相关的类型
 * 
 * @author Mikab Peng
 * 
 */
public class StringDataType extends DataType {
	private int jdbcType;
	private int maxLength = 20;
	public static final String TYPE_NAME = "String";

	public int getJDBCType() {
		return jdbcType;
	}

	public String toDBSchema() {
		return "VARCHAR(" + (2 * maxLength) + ")";
	}

	public String getTypeName() {
		return TYPE_NAME;
	}

	public Object getValueFromResultSet(ResultSet resultSet, int index) throws SQLException {
		return resultSet.getString(index);
	}

	public void setParameter(PreparedStatement statement, int index, Object paramValue) throws SQLException {
		if (paramValue == null) statement.setNull(index, getJDBCType());
		else statement.setString(index, String.valueOf(paramValue));// For
		// compatility
	}

	public StringDataType(int JDBCType) {
		this.jdbcType = JDBCType;
	}

	public StringDataType() {
		this.jdbcType = DataType.VARCHAR;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public int getJdbcType() {
		return jdbcType;
	}

	public void setJdbcType(int jdbcType) {
		this.jdbcType = jdbcType;
	}

	public static DataTypeFactory getFactory() {
		Pattern pattern = Pattern.compile(".*(?i)CHAR\\((\\d+)\\).*");
		return new AbstractDatatypeFactory(pattern) {
			protected DataType createDataType(Matcher matcher) {
				StringDataType dataType = new StringDataType();
				dataType.setMaxLength(Integer.parseInt(matcher.group(1)) / 2);
				return dataType;
			}

			public DataType fromString(String typeDescription, Map props) {
				if (TYPE_NAME.equalsIgnoreCase(typeDescription)) {
					StringDataType dataType = new StringDataType();
					return dataType;
				}
				return null;
			}
		};
	}

}
