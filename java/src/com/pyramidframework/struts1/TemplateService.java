package com.pyramidframework.struts1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.pyramidframework.dao.PaginatedResult;
import com.pyramidframework.dao.SqlTextUtil;
import com.pyramidframework.dao.VOSupport;

public class TemplateService extends AbstractService {
	public String addInput() {

		// 确保结构存在
		ActionFormBean bean = getBean();
		bean.getModel(modelName);
		return ActionSupport.SUCCESS;
	}

	public String editInput() {
		ActionFormBean bean = getBean();
		VOSupport support = getVoFactory().getVOSupport(bean.getModel(modelName));
		Object o = dao.retrieve(modelName, support.getValues());
		bean.setModel(modelName, o);
		return ActionSupport.SUCCESS;
	}

	public String addSubmit() {
		ActionFormBean bean = getBean();
		dao.add(bean.getModel(modelName));

		// /System.err.println(this.getClass().getName());

		return ActionSupport.SUCCESS;
	}

	public String editSubmit() {
		ActionFormBean bean = getBean();
		dao.update(bean.getModel(modelName));

		return ActionSupport.SUCCESS;
	}

	public String deleteSubmit() {
		ActionFormBean bean = getBean();
		dao.delete(bean.getModel(modelName));
		return ActionSupport.SUCCESS;
	}

	public String query() {
		ActionFormBean bean = getBean();
		Object object = bean.getModel(modelName);
		if (object == null) {
			object = getVoFactory().getValueObject(modelName);
		}

		VOSupport support = getVoFactory().getVOSupport(object);
		PaginatedResult result = dao.query(modelName, support.getValues(), bean.getOrderBy(), bean.getPageSize(), bean.getCurrPage());

		bean.setQueryResult(result.getPageDataList());
		bean.setTotalCount(result.getTotalCount());
		return ActionSupport.SUCCESS;
	}
	
	protected String queryByName(String sqlName,String defaultOrderBy){
		ActionFormBean bean = getBean();
		String sql = getCacheAccessor().getCachedSql(sqlName);

		String orderBY = bean.getOrderBy();
		if (orderBY == null) orderBY = defaultOrderBy;
		sql += " order by " + orderBY;

		PaginatedResult result = getDao().queryData(sql, null, null, bean.getPageSize(), bean.getCurrPage());

		bean.setQueryResult(result.getPageDataList());
		bean.setTotalCount(result.getTotalCount());

		return ActionSupport.SUCCESS;
	}

	public String queryByName() {
		ActionFormBean bean = getBean();
		
		Map paramMap = bean.getParameterMap();
		String name = (String) paramMap.get("queryName");
		if (name == null) name = getModelName();
		
		String sql = getCacheAccessor().getCachedSql(name);
		List dataTypes = new ArrayList();
		List paramValues = new ArrayList();
		sql = SqlTextUtil.parseSQL(sql, paramMap, dataTypes, paramValues);

		PaginatedResult result = dao.queryData(sql, dataTypes, paramValues, bean.getPageSize(), bean.getCurrPage());

		bean.setQueryResult(result.getPageDataList());
		bean.setTotalCount(result.getTotalCount());
		return ActionSupport.SUCCESS;
	}
}
