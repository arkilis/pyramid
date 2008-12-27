package com.pyramidframework.dao.mysql;

import java.util.List;
import java.util.Map;

import com.pyramidframework.dao.VOFactory;
import com.pyramidframework.dao.VOSupport;

public class VOResultSetHandler extends QueryResultSetHandler {

	VOFactory factory = null;
	String modelName = null;

	public VOResultSetHandler(VOFactory factory, String model) {
		this.factory = factory;
		this.modelName = model;
	}

	protected Map createValueMap(List datas) {
		VOSupport support = factory.getVOSupport(modelName);
		datas.add(support);
		return support.getValues();
	}

}
