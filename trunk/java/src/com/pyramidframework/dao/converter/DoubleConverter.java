package com.pyramidframework.dao.converter;

public class DoubleConverter extends AbstractStringConverter {

	public Object fromString(String str) {
		try {
			return str == null ? null : new Double(Double.parseDouble(str));
		} catch (Throwable ex) {
			return handleFromStringError(str, ex, this);
		}
	}

	public String toString(Object value) {
		try {
			return value == null ? null : value.toString();
		} catch (Throwable ex) {
			return handleToStringError(value, ex, this);
		}
	}

}
