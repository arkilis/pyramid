package com.pyramidframework.struts1;

import com.pyramidframework.dao.VOFactory;
import com.pyramidframework.dao.VOSupport;
import com.pyramidframework.dao.ValueObjectDAO;

public class TemplateService {
	private ValueObjectDAO dao = null;
	private VOFactory voFactory = null;
	private ThreadLocal currentBean = new ThreadLocal();
	private String modelName = null;

	public String addInput() {
		
		//确保结构存在
		ActionFormBean bean = getBean();
		bean.getModel(modelName);
		return ActionSupport.SUCCESS;
	}

	public String editInput() {
		ActionFormBean bean = getBean();
		VOSupport support = voFactory.getVOSupport(bean.getModel(modelName));
		Object o = dao.retrieve(modelName, support.getValues());
		bean.setModel(modelName, o);
		return ActionSupport.SUCCESS;
	}

	public String addSubmit() {
		ActionFormBean bean = getBean();
		dao.add(bean.getModel(modelName));

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
		
		VOSupport support = voFactory.getVOSupport(bean.getModel(modelName));

		bean.setQueryResult(dao.query(modelName, support.getValues(),null, bean.getPageSize(), bean.getCurrPage()));
		return ActionSupport.SUCCESS;
	}

	public ValueObjectDAO getDao() {
		return dao;
	}

	public void setDao(ValueObjectDAO dao) {
		this.dao = dao;
	}

	public ActionFormBean getBean() {
		return (ActionFormBean) currentBean.get();
	}

	public void setBean(ActionFormBean currentBean) {
		this.currentBean.set(currentBean);
	}

	public ActionFormBean registerCurrentBean(ActionFormBean currentBean) {
		setBean(currentBean);
		return currentBean;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public VOFactory getVoFactory() {
		return voFactory;
	}

	public void setVoFactory(VOFactory voFactory) {
		this.voFactory = voFactory;
	}

}
