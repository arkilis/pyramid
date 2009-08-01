package com.pyramidframework.dao.model.datatype;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DoubleDataType extends DataType {
	private int jdbcType;
	public static final String TYPE_NAME = "Double";

	public String toDBSchema() {
		return "DOUBLE";
	}

	public String getTypeName() {
		return TYPE_NAME;
	}

	public int getJDBCType() {
		return jdbcType;
	}

	public Object getValueFromResultSet(ResultSet resultSet, int index) throws SQLException {

		return new Double(resultSet.getDouble(index));
	}

	public void setParameter(PreparedStatement statement, int index, Object paramValue) throws SQLException {
		if (paramValue == null) statement.setNull(index, getJDBCType());
		else if (paramValue instanceof Double) statement.setDouble(index, ((Double) paramValue).doubleValue());
		else statement.setDouble(index,Double.parseDouble(String.valueOf(paramValue)));
	}

	public DoubleDataType() {
		this.jdbcType = DataType.DOUBLE;
	}

	public DoubleDataType(int JDBCType) {
		this.jdbcType = JDBCType;
	}

	public static DataTypeFactory getFactory() {
		Pattern pattern = Pattern.compile(".*(?i)DOUBLE.*");
		return new AbstractDatatypeFactory(pattern) {
			protected DataType createDataType(Matcher matcher) {
				DoubleDataType dataType = new DoubleDataType();
				return dataType;
			}

			public DataType fromString(String typeDescription, Map props) {
				if (TYPE_NAME.equalsIgnoreCase(typeDescription)) {
					DoubleDataType dataType = new DoubleDataType();
					return dataType;
				}
				return null;
			}
		};
	}

}
