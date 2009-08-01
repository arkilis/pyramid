package com.pyramidframework.dao.converter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.pyramidframework.dao.model.datatype.BigDecimalType;

/**
 * 大十进制数的转换器，需要设置其宽度和精度
 * 
 * @author Administrator
 * 
 */
public class BigDecimalConverter extends AbstractStringConverter {

	public Object fromString(String str) {

		try {
			return str == null ? null : new BigDecimal(str);
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
		Map props = new HashMap();
		BigDecimalType type = ((BigDecimalType) modelField.getType());

		props.put("precision", String.valueOf(type.getPrecision()));
		props.put("scale", String.valueOf(type.getScale()));

		return props;
	}


	public void setProperties(Map properties) {
		super.setProperties(properties);
		
		BigDecimalType type = ((BigDecimalType) modelField.getType());
		try {
			if (properties.get("precision") != null && !"".equals(properties.get("precision"))){
				type.setPrecision(Integer.parseInt((String)properties.get("precision")));
			}
			
			if (properties.get("scale") != null && !"".equals(properties.get("scale"))){
				type.setScale(Integer.parseInt((String)properties.get("scale")));
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

}
