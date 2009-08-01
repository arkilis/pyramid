package com.pyramidframework.dao.converter.errorhandler;



import com.pyramidframework.dao.DAOException;
import com.pyramidframework.dao.converter.StringConverter;

public class ThrowErrorHandler implements ConvertErrorHandler {

	public Object handleFromStringError(String str, Throwable ex, StringConverter instance) throws DAOException {
		if (ex instanceof DAOException) {
			throw (DAOException) ex;
		} else {
			throw new DAOException(ex);
		}
	}

	public String handleToStringError(Object value, Throwable ex, StringConverter instance) throws DAOException {
		if (ex instanceof DAOException) {
			throw (DAOException) ex;
		} else {
			throw new DAOException(ex);
		}
	}

	public void setStringConverter(StringConverter instance) {

	}

}
