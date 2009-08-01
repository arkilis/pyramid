package com.pyramidframework.struts1;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.upload.FormFile;

import com.pyramidframework.dao.VOFactory;

/*******************************************************************************
 * 将转义后的file的转换成合格的表达式,$代替为圆点“.”,#转换为括号“(”和")"
 * 
 * @author Mikab Peng
 * 
 */
public class ActionFormBean {
	public static final  String FILE_NAME_SUFFIX = "_FileName";
	private VOFactory factory = null;
	private List queryResult = null;
	private Map models = new HashMap();
	private int pageSize = 20;
	private int currPage = 1;
	private int totalCount = 0;
	private int pageStart =0;
	private String sort;
	private String dir;

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public void setFile(String name, FormFile file) {
		
		try {
			String propName = convertExpress(name);
			BeanUtils.setProperty(this, propName, file.getFileData());
			
			if (propName.endsWith(")")){
				propName = propName.substring(0, propName.length() -1) + FILE_NAME_SUFFIX +")" ;
			}else{
				propName += FILE_NAME_SUFFIX;
			}
			propName =propName.replace(".bytes(", ".string(");
			
			BeanUtils.setProperty(this, propName, file.getFileName());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
		if (pageSize ==0){
			currPage =0;
			pageStart =0;
		}else{
			currPage = (pageStart / pageSize) +1;
		}
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

	public void setFactory(VOFactory factory) {
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
	
	public Map getParameterMap(){
		return ActionContext.getCurrent().getRequest().getParameterMap();
	}

	/**
	 * 将转义后的file的转换成合格的表达式,$代替为圆点“.”,#转换为括号“(”和")"
	 */
	protected String convertExpress(String name) {
		StringBuffer buffer = new StringBuffer(name);
		int cnt = 0;
		for (int i = 0; i < buffer.length(); i++) {
			char a = buffer.charAt(i);
			if (a == '$') {
				buffer.replace(i, i + 1, ".");
			}
			if (a == '#') {
				if (cnt == 1) {
					buffer.replace(i, i + 1, ")");
					cnt = 0;
				} else {
					cnt = 1;
					buffer.replace(i, i + 1, "(");
				}
			}
		}
		return buffer.toString();
	}

	public void setPageStart(int num) {
		if (pageSize == 0){
			currPage =0;
			pageStart =0;
		}else{
			currPage = (num / pageSize) +1;
			pageStart = num;
		}
		
	}

	public String getOrderBy() {
		if (sort != null && sort.length() > 0) {
			if (dir != null && dir.length() > 0) {
				return sort + " " + dir;
			} else {
				return sort;
			}
		}
		return null;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public Map getModelMap() {
		return models;
	}

}
