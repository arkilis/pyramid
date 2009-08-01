package com.pyramidframework.dao.model.datatype;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DateDataType extends DataType {
	private int jdbcType;
	public static final String TYPE_NAME = "Date";

	public String getTypeName() {
		return TYPE_NAME;
	}
	public String toDBSchema() {
		return hasTimePart() ?"DATETIME":"DATE";
	}

	public int getJDBCType() {

		return jdbcType;
	}

	public Object getValueFromResultSet(ResultSet resultSet, int index) throws SQLException {

		return resultSet.getDate(index);
	}

	public void setParameter(PreparedStatement statement, int index, Object paramValue) throws SQLException {
		if (paramValue == null) statement.setNull(index, getJDBCType());
		else if(paramValue instanceof Date){
			Date date = (Date) paramValue;
			statement.setDate(index, new java.sql.Date(date.getTime()));
		}else if(paramValue instanceof java.sql.Date){
			statement.setDate(index, (java.sql.Date)paramValue);
		}else{
			statement.setString(index, String.valueOf(paramValue));
		}
	}

	public DateDataType() {
		this.jdbcType = DataType.DATETIME;
	}

	public DateDataType(int JDBCType) {
		this.jdbcType = JDBCType;
	}

	public boolean hasTimePart() {
		return getJDBCType() == DataType.DATETIME;
	}
	
	public int getJdbcType() {
		return jdbcType;
	}
	public void setJdbcType(int jdbcType) {
		this.jdbcType = jdbcType;
	}
	public static DataTypeFactory getFactory(){
		Pattern pattern = Pattern.compile(".*(?i)DATE(TIME)?.*");
		return new AbstractDatatypeFactory(pattern){
			protected DataType createDataType(Matcher matcher) {
				DateDataType dataType = new DateDataType();
				if (matcher.groupCount() <1){
					dataType.setJdbcType(DataType.DATE);
				}else{
					dataType.setJdbcType(DataType.DATETIME);
				}
				return dataType;
			}
			
			public DataType fromString(String typeDescription, Map props) {
				if (TYPE_NAME.equalsIgnoreCase(typeDescription)){
					DateDataType decimalType = new DateDataType();
					return decimalType;
				}else if ("Datetime".equalsIgnoreCase(typeDescription)){
					DateDataType decimalType = new DateDataType(DataType.DATETIME);
					return decimalType;
				}
				return null;
			}
		};
	}
}
