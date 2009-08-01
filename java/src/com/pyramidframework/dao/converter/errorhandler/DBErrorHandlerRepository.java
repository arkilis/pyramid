package com.pyramidframework.dao.converter.errorhandler;

import java.util.HashMap;
import java.util.Map;

import com.pyramidframework.dao.converter.StringConverter;
import com.pyramidframework.dao.converter.keyValueStore;



public class DBErrorHandlerRepository implements ConverterErrorHandlerRepository {
	protected keyValueStore keyValueStore;
	public static String DefaultValueKey = "defaultValue";

	public ConvertErrorHandler getErrorHandler(StringConverter converter) {

		ConvertErrorHandler errorHandler = null;
		String catalog = getErrorHandlerCatalog(converter);
		Map props = getKeyValueStore().getProperties(catalog);
		if (props == null || props.size() > 0) {
			errorHandler = new ThrowErrorHandler();
		} else {
			DefaultValueHandler defaultValueHandler = new DefaultValueHandler();
			defaultValueHandler.setDefaultValueStr((String)props.get(DefaultValueKey));
			errorHandler = defaultValueHandler;
		}
		errorHandler.setStringConverter(converter);

		return errorHandler;
	}

	public void storeErrorHandler(StringConverter converter) {
		String catalog = getErrorHandlerCatalog(converter);
		ConvertErrorHandler errorHandler = converter.getErrorHandler();
		if (errorHandler instanceof DefaultValueHandler) {
			Map props = new HashMap();
			props.put(DefaultValueKey, ((DefaultValueHandler) errorHandler).getDefaultValueStr());
			getKeyValueStore().storeProperties(catalog, props);
		} else {
			getKeyValueStore().storeProperties(catalog, null);
		}
	}

	public String getErrorHandlerCatalog(StringConverter converter) {
		return converter.getModelField().getFullModelName() + ".ErrorHandler";
	}

	public keyValueStore getKeyValueStore() {
		return keyValueStore;
	}

	public void setKeyValueStore(keyValueStore keyValueStore) {
		this.keyValueStore = keyValueStore;
	}
}
