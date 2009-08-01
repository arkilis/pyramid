package com.pyramidframework.dao.converter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.pyramidframework.dao.model.datatype.StringDataType;

public class StringDataTypeConverter extends AbstractStringConverter {
	private static final Collection PROP_NAMES = Collections.unmodifiableCollection(Arrays.asList(new String[] { "maxLength", "doTrim" }));

	private int maxLength = 255;
	private boolean doTrim = true;// 是否截取两头的字符串

	public Object fromString(String str) {
		try {
			if (str == null) return null;
			if (doTrim) str = str.trim();
			if (maxLength <= str.length()) str = str.substring(0, maxLength);
			return str;
		} catch (Throwable e) {
			return handleFromStringError(str, e, this);
		}
	}

	public String toString(Object value) {

		try {
			return value == null ? null : String.valueOf(value);
		} catch (Throwable e) {
			return handleToStringError(value, e, this);
		}
	}

	public Collection getPropertiesNames() {
		return PROP_NAMES;
	}

	public void setProperties(Map properties) {
		if (properties.containsKey("maxLength")) {
			int maxLength = Integer.parseInt((String) properties.get("maxLength"));
			setMaxLength(maxLength);
			StringDataType dataType = (StringDataType) modelField.getType();
			dataType.setMaxLength(maxLength);
		}

		if (properties.containsKey("doTrim")) {
			boolean trim = "true".equalsIgnoreCase((String) properties.get("doTrim")) || "1".equalsIgnoreCase((String) properties.get("doTrim"));
			setDoTrim(trim);
		}
	}

	public Map getProperties() {
		Map ps = new HashMap();
		ps.put("maxLength", String.valueOf(getMaxLength()));
		ps.put("doTrim", String.valueOf(isDoTrim()));
		return ps;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength > 1 ? maxLength : this.maxLength;
	}

	public boolean isDoTrim() {
		return doTrim;
	}

	public void setDoTrim(boolean doTrim) {
		this.doTrim = doTrim;
	}
}
