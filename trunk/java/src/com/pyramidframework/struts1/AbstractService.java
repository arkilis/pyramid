package com.pyramidframework.struts1;

import com.pyramidframework.cache.DBCacheAccessor;
import com.pyramidframework.dao.SqlDAO;
import com.pyramidframework.dao.VOFactory;

/**
 * 框架内服务的基准类
 * @author Administrator
 *
 */
public abstract class AbstractService {

	protected SqlDAO dao = null;
	private ThreadLocal currentBean = new ThreadLocal();
	protected String modelName = null;
	private DBCacheAccessor cacheAccessor = null;

	public AbstractService() {
		super();
	}

	/**
	 * @return
	 */
	protected VOFactory getVoFactory() {
		return getDao().getVOFactory();
	}

	public SqlDAO getDao() {
		return dao;
	}

	public void setDao(SqlDAO dao) {
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

	public DBCacheAccessor getCacheAccessor() {
		return cacheAccessor;
	}

	public void setCacheAccessor(DBCacheAccessor cacheAccessor) {
		this.cacheAccessor = cacheAccessor;
	}

}