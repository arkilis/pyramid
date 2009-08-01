package com.pyramidframework.dao.converter;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.pyramidframework.dao.DAOException;
import com.pyramidframework.dao.converter.errorhandler.ConvertErrorHandler;
import com.pyramidframework.dao.converter.errorhandler.DefaultValueHandler;
import com.pyramidframework.dao.model.ModelField;
import com.pyramidframework.dao.model.datatype.BigDecimalType;
import com.pyramidframework.dao.model.datatype.BigIntegerDataType;
import com.pyramidframework.dao.model.datatype.BooleanDataType;
import com.pyramidframework.dao.model.datatype.DataType;
import com.pyramidframework.dao.model.datatype.DateDataType;
import com.pyramidframework.dao.model.datatype.DoubleDataType;
import com.pyramidframework.dao.model.datatype.IntegerDataType;
import com.pyramidframework.dao.model.datatype.StringDataType;
import com.pyramidframework.spring.SpringClassUtil;

public abstract class AbstractStringConverter implements StringConverter {
	private static final Map PROP_VALUES = Collections.unmodifiableMap(new HashMap());
	protected static Map CONVERTER_TYPE_MAPS = new HashMap();

	protected ConvertErrorHandler errorHandler;
	protected ModelField modelField;

	public ConvertErrorHandler getErrorHandler() {
		return errorHandler;
	}

	public void setErrorHandler(ConvertErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
		errorHandler.setStringConverter(this);
	}

	public String handleToStringError(Object value, Throwable ex, StringConverter instance) throws DAOException {
		if (errorHandler != null) return errorHandler.handleToStringError(value, ex, instance);
		throw new DAOException(ex);
	}

	public Object handleFromStringError(String str, Throwable ex, StringConverter instance) throws DAOException {
		if (errorHandler != null) return errorHandler.handleFromStringError(str, ex, instance);
		throw new DAOException(ex);
	}

	public ModelField getModelField() {
		return modelField;
	}

	public void setModelField(ModelField modelField) {
		this.modelField = modelField;
	}

	public Collection getPropertiesNames() {
		return getProperties().keySet();
	}

	public void setProperties(Map properties) {
		try {
			String clazz =(String)properties.get("errorHandler");
			if (clazz != null && SpringClassUtil.getInstance().hasClass(clazz)) {
				ConvertErrorHandler errorHandler = (ConvertErrorHandler) Class.forName(clazz).newInstance();
				setErrorHandler(errorHandler);

				if (errorHandler instanceof DefaultValueHandler) {
					((DefaultValueHandler) errorHandler).setDefaultValueStr((String)properties.get("defaultValue"));
				}
			}
		} catch (Throwable e) {
			// do nothing
		}
	}

	public Map getProperties() {
		return PROP_VALUES;
	}

	public static void setConverterDataTypeMap(Class datatypeClass, Class converterClass) {
		CONVERTER_TYPE_MAPS.put(datatypeClass, converterClass);
	}

	public static Class getConverterClass(Class datatypeClass) {
		return (Class)CONVERTER_TYPE_MAPS.get(datatypeClass);
	}

	public static StringConverter createConverter(DataType dataType) {
		try {
			return (StringConverter) getConverterClass(dataType.getClass()).newInstance();
		} catch (Throwable e) {
			return null;
		}
	}

	static {
		setConverterDataTypeMap(BigDecimalType.class, BigDecimalConverter.class);
		setConverterDataTypeMap(BigIntegerDataType.class, BigIntergerConverter.class);
		setConverterDataTypeMap(BooleanDataType.class, BooleanConverter.class);
		setConverterDataTypeMap(DateDataType.class, DateConverter.class);
		setConverterDataTypeMap(DoubleDataType.class, DoubleConverter.class);
		setConverterDataTypeMap(StringDataType.class, StringDataTypeConverter.class);
		setConverterDataTypeMap(IntegerDataType.class, IntegerConverter.class);
	}

}
