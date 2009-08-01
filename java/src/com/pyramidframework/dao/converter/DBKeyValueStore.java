package com.pyramidframework.dao.converter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.pyramidframework.dao.VOSupport;
import com.pyramidframework.dao.ValueObjectDAO;

public class DBKeyValueStore implements keyValueStore {
	protected ValueObjectDAO dao;
	protected String storeModelName = "key_value_setting";
	protected String fieldNameColumn = "field_name";
	protected String propertyKeyColumn = "property_key";
	protected String propertyValueColumn = "property_value";

	public Map getProperties(String catalog) {
		Map queryValues = new HashMap();
		queryValues.put(fieldNameColumn, catalog);
		List listvalues = getDao().query(storeModelName, queryValues, null, 0, 0).getPageDataList();

		for (int i = 0; i < listvalues.size(); i++) {
			VOSupport vo = dao.getVOFactory().getVOSupport(listvalues.get(i));
			queryValues.put(vo.getProperty(propertyKeyColumn).toString(), vo.getProperty(propertyValueColumn).toString());
		}
		return queryValues;
	}

	public void storeProperties(String catalog, Map properties) {
		Map queryValues = new HashMap();
		queryValues.put(fieldNameColumn, catalog);
		List listvalues = getDao().query(storeModelName, queryValues, null, 0, 0).getPageDataList();

		for (int i = 0; i < listvalues.size(); i++) {
			VOSupport vo = dao.getVOFactory().getVOSupport(listvalues.get(i));
			getDao().delete(vo);
		}

		if (properties != null) {
			Iterator iterator = properties.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry entry = (Map.Entry) iterator.next();
				VOSupport newVO = getDao().getVOFactory().getVOSupport(storeModelName);
				newVO.setProperty(fieldNameColumn, catalog);
				newVO.setProperty(propertyKeyColumn, entry.getKey());
				newVO.setProperty(propertyValueColumn, entry.getValue());
				getDao().add(newVO);
			}
		}
	}

	public ValueObjectDAO getDao() {
		return dao;
	}

	public void setDao(ValueObjectDAO dao) {
		this.dao = dao;
	}

	public String getStoreModelName() {
		return storeModelName;
	}

	public void setStoreModelName(String storeModelName) {
		this.storeModelName = storeModelName;
	}

	public String getFieldNameColumn() {
		return fieldNameColumn;
	}

	public void setFieldNameColumn(String fieldNameColumn) {
		this.fieldNameColumn = fieldNameColumn;
	}

	public String getPropertyKeyColumn() {
		return propertyKeyColumn;
	}

	public void setPropertyKeyColumn(String propertyKeyColumn) {
		this.propertyKeyColumn = propertyKeyColumn;
	}

	public String getPropertyValueColumn() {
		return propertyValueColumn;
	}

	public void setPropertyValueColumn(String propertyValueColumn) {
		this.propertyValueColumn = propertyValueColumn;
	}

}
