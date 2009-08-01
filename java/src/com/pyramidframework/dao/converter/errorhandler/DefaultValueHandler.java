package com.pyramidframework.dao.converter.errorhandler;



import com.pyramidframework.dao.DAOException;
import com.pyramidframework.dao.converter.StringConverter;

public class DefaultValueHandler implements ConvertErrorHandler {
	private String defaultValueStr;
	private Object defaultValue;
	private StringConverter stringConverter;
	
	public Object handleFromStringError(String str, Throwable ex, StringConverter instance) throws DAOException {
		return defaultValue;
	}

	public String handleToStringError(Object value, Throwable ex, StringConverter instance) throws DAOException {
		return defaultValueStr;
	}

	public void setStringConverter(StringConverter instance) {
		if (defaultValueStr != null){
			defaultValue = instance.fromString(defaultValueStr);
		}else{
			defaultValue= null;
		}
		this.stringConverter =instance;
	}

	public StringConverter getStringConverter() {
		return stringConverter;
	}

	public String getDefaultValueStr() {
		return defaultValueStr;
	}

	public void setDefaultValueStr(String defaultValueStr) {
		if (this.stringConverter != null){
			this.defaultValue = stringConverter.fromString(defaultValueStr);
		}
		this.defaultValueStr = defaultValueStr;
	}

	public Object getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}
	
}
