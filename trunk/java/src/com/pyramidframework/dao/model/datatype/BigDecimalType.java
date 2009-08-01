package com.pyramidframework.dao.model.datatype;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BigDecimalType extends DataType {
	public static final String TYPE_NAME = "BigDecimal";
	private int jdbcType;
	private int precision=20;
	private int scale=5;

	
	public String getTypeName() {
		return TYPE_NAME;
	}
	
	public String toDBSchema() {
		return "DECIMAL("+ precision+"," + scale +")";
	}

	public int getJDBCType() {
		return jdbcType;
	}

	public Object getValueFromResultSet(ResultSet resultSet, int index) throws SQLException {
		return resultSet.getBigDecimal(index);

	}

	public void setParameter(PreparedStatement statement, int index, Object paramValue) throws SQLException {
		if (paramValue == null)
			statement.setNull(index, getJDBCType());
		else if (paramValue instanceof BigDecimal)
			statement.setBigDecimal(index, (BigDecimal) paramValue);
		else
			statement.setBigDecimal(index, new BigDecimal(String.valueOf(paramValue) ));
	}

	public BigDecimalType(int JDBCType) {
		this.jdbcType = JDBCType;
	}

	public BigDecimalType() {
		this.jdbcType = DataType.DECIMAL;
	}

	public int getJdbcType() {
		return jdbcType;
	}

	public void setJdbcType(int jdbcType) {
		this.jdbcType = jdbcType;
	}

	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}
	
	public static DataTypeFactory getFactory(){
		Pattern pattern = Pattern.compile(".*(?i)DECIMAL\\((\\d+),(\\d+)\\).*");
		return new AbstractDatatypeFactory(pattern){
			protected DataType createDataType(Matcher matcher) {
				BigDecimalType decimalType = new BigDecimalType();
				decimalType.setPrecision(Integer.parseInt(matcher.group(1)));
				decimalType.setScale(Integer.parseInt(matcher.group(2)));
				return decimalType;
			}
			
			public DataType fromString(String typeDescription, Map props) {
				if (TYPE_NAME.equalsIgnoreCase(typeDescription)){
					BigDecimalType decimalType = new BigDecimalType();
					return decimalType;
				}
				return null;
			}
		};
		
	}
}
