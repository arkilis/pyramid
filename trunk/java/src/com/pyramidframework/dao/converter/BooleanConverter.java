package com.pyramidframework.dao.converter;

public class BooleanConverter extends AbstractStringConverter {

	public Object fromString(String str) {

		try {
			if (str == null) return null;
			return new Boolean("true".equals(str) || "1".equalsIgnoreCase(str) || "y".equalsIgnoreCase(str) || "yes".equalsIgnoreCase(str)
			|| "ЪЧ".equalsIgnoreCase(str) || "ец".equalsIgnoreCase(str));
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
