package com.pyramidframework.struts1;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pyramidframework.dao.VOFactory;

public class ActionFormBean {
	private VOFactory factory = null;
	private List queryResult = null;
	private Map models = new HashMap();
	private int pageSize = 10;
	private int currPage = 1;
	private int totalCount = 0;
	
	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getCurrPage() {
		return currPage;
	}
	
	public String getCurrentPage() {
		return String.valueOf(currPage);
	}

	public void setCurrPage(int currPage) {
		this.currPage = currPage;
	}

	public Object getModel(String modelName) {
		Object model = models.get(modelName);
		if (model == null) {
			model = factory.getValueObject(modelName);
			models.put(modelName, model);
		}
		return model;
	}

	public void setModel(String modelName, Object model) {
		models.put(modelName, model);
	}

	public VOFactory getFactory() {
		return factory;
	}

	public void setFactory(VOFactory factory){
		this.factory = factory;
	}

	public List getQueryResult() {
		return queryResult;
	}

	public void setQueryResult(List queryResult) {
		this.queryResult = queryResult;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
}
