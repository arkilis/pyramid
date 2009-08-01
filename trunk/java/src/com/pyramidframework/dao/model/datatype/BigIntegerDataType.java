package com.pyramidframework.dao.model.datatype;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BigIntegerDataType extends DataType {
	public static final String TYPE_NAME = "BigInteger";
	private int jdbcType;
	private int width=20;

	public String toDBSchema() {
		return "INT(" + width + ")";
	}

	public String getTypeName() {

		return TYPE_NAME;
	}

	public int getJDBCType() {
		return jdbcType;
	}

	public Object getValueFromResultSet(ResultSet resultSet, int index) throws SQLException {
		BigDecimal decimal = resultSet.getBigDecimal(index);
		return decimal == null ? null : decimal.toBigInteger();

	}

	public void setParameter(PreparedStatement statement, int index, Object paramValue) throws SQLException {
		if (paramValue == null) statement.setNull(index, getJDBCType());
		else if ( paramValue instanceof BigInteger) statement.setBigDecimal(index, new BigDecimal((BigInteger) paramValue));
		else if ( paramValue instanceof BigDecimal) statement.setBigDecimal(index, (BigDecimal) paramValue);
		else statement.setBigDecimal(index, new BigDecimal(String.valueOf(paramValue)));
	}

	public BigIntegerDataType() {
		this.jdbcType = DataType.BIGINT;
	}

	public BigIntegerDataType(int JDBCtype) {
		this.jdbcType = JDBCtype;
	}

	public int getJdbcType() {
		return jdbcType;
	}

	public void setJdbcType(int jdbcType) {
		this.jdbcType = jdbcType;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public static DataTypeFactory getFactory() {
		Pattern pattern = Pattern.compile(".*(?i)INT\\((\\d+)\\).*");
		return new AbstractDatatypeFactory(pattern) {
			protected DataType createDataType(Matcher matcher) {
				BigIntegerDataType dataType = new BigIntegerDataType();
				dataType.setWidth(Integer.parseInt(matcher.group(1)));
				return dataType;
			}
			
			public DataType fromString(String typeDescription, Map props) {
				if (TYPE_NAME.equalsIgnoreCase(typeDescription)){
					BigIntegerDataType integerDataType = new BigIntegerDataType();
					return integerDataType;
				}
				return null;
			}
		};
	}

}
