package com.pyramidframework.dao.converter;

import java.util.Map;

import com.pyramidframework.dao.DAOException;
import com.pyramidframework.dao.converter.errorhandler.ConverterErrorHandlerRepository;
import com.pyramidframework.dao.model.ModelField;

public class DBStoreConverterRepository implements StringConverterRepository {

	protected ConverterErrorHandlerRepository errorHandlerRepository;
	protected keyValueStore keyValueStore;

	public StringConverter getConverter(ModelField field) throws DAOException {
		StringConverter converter = null;
		try {
			converter = (StringConverter) AbstractStringConverter.getConverterClass(field.getType().getClass()).newInstance();
			converter.setModelField(field);

			Map props = getKeyValueStore().getProperties(field.getFullModelName());
			converter.setProperties(props);

			converter.setErrorHandler(errorHandlerRepository.getErrorHandler(converter));
		} catch (Throwable e) {
			throw new DAOException(e);
		}
		return converter;
	}

	public void storeConverter(StringConverter converter) throws DAOException {

		Map properties = converter.getProperties();
		getKeyValueStore().storeProperties(converter.getModelField().getFullModelName(), properties);
		errorHandlerRepository.storeErrorHandler(converter);
	}

	public ConverterErrorHandlerRepository getErrorHandlerRepository() {
		return errorHandlerRepository;
	}

	public void setErrorHandlerRepository(ConverterErrorHandlerRepository errorHandlerRepository) {
		this.errorHandlerRepository = errorHandlerRepository;
	}

	public keyValueStore getKeyValueStore() {
		return keyValueStore;
	}

	public void setKeyValueStore(keyValueStore keyValueStore) {
		this.keyValueStore = keyValueStore;
	}
}
