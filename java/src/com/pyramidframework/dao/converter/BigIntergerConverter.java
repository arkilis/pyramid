package com.pyramidframework.dao.converter;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import com.pyramidframework.dao.model.datatype.BigIntegerDataType;

/**
 * 无限位数的整数
 * @author Administrator
 *
 */
public class BigIntergerConverter extends AbstractStringConverter {

	public Object fromString(String str) {

		try {
			return str == null ? null : new BigInteger(str);
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
	

	public Map getProperties() {
		Map map = new HashMap();
		BigIntegerDataType type = (BigIntegerDataType)modelField.getType();
		map.put("width", String.valueOf(type.getWidth()));
		return map;
	}
	

	public void setProperties(Map properties) {
		
		super.setProperties(properties);
		BigIntegerDataType type = (BigIntegerDataType)modelField.getType();
		try {
			if (properties.get("width") != null && !"".equals(properties.get("width"))){
				type.setWidth(Integer.parseInt((String)properties.get("width")));
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

}
