package com.pyramidframework.dao.converter;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.pyramidframework.dao.model.datatype.DateDataType;

public class DateConverter extends AbstractStringConverter {
	private static final Collection PROP_NAMES = Collections.unmodifiableCollection(Arrays.asList(new String[] { "format" }));
	private transient SimpleDateFormat dateFormat;
	private String format;
	public static String DEFAULT_DATE_FORMATTER = "yyyy-MM-dd";
	public static String DEFAULT_TIME_FORMATTER = "yyyy-MM-dd HH:mm:ss";

	public Object fromString(String str) {
		try {
			return str == null ? null : dateFormat.parse(str);
		} catch (Throwable ex) {
			return handleFromStringError(str, ex, this);
		}

	}

	public String toString(Object value) {
		try {
			return value == null ? null : dateFormat.format((Date)value);
		} catch (Throwable ex) {
			return handleToStringError(value, ex, this);
		}
	}


	public Collection getPropertiesNames() {
		return PROP_NAMES;
	}


	public void setProperties(Map properties) {
		format = (String)properties.get("format");
		if (format == null || "".equals(format)) {
			boolean hasTime = ((DateDataType) getModelField().getType()).hasTimePart();
			format = hasTime ? DEFAULT_TIME_FORMATTER : DEFAULT_DATE_FORMATTER;
		}
		dateFormat = new SimpleDateFormat(format);
		super.setProperties(properties);
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}
	

	public Map getProperties() {
		Map ps = new HashMap();
		ps.put("format", getFormat());
		return ps;
	}
}
