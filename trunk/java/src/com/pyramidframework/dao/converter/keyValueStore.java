package com.pyramidframework.dao.converter;

import java.util.Map;

public interface keyValueStore {
	
	public Map getProperties(String catalog);
	
	
	public void storeProperties(String catalog,Map properties);
}
