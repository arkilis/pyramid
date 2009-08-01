package com.pyramidframework.dao;

import com.pyramidframework.dao.model.DataModel;
import com.pyramidframework.dao.model.ModelProvider;
import com.pyramidframework.proxy.ProxyHelper;

public class VOFactory {

	ModelProvider modelProvider = null;

	/**
	 * 新生成一个对象，并将对象转换成VOSupport接口
	 * 
	 * @param valueObject
	 * @return
	 * @throws DAOException
	 */
	public VOSupport getVOSupport(String modelName) throws DAOException {

		return getVOSupport(getValueObject(modelName));
	}

	/**
	 * 需要根据配置的模型文件设置那个类的实例
	 * 
	 * @param modelName
	 * @return
	 * @throws DAOException
	 */
	public Object getValueObject(String modelName) throws DAOException {
		if (modelProvider == null) {
			return new ValueObject(modelName);
		}
		DataModel model = null;
		try {
			model = modelProvider.getModelByName(modelName);
		} catch (Throwable e1) {
			// do nothing
		}
		if (model == null || model.getType() == null) {
			return new ValueObject(modelName);
		} else {
			try {
				return model.getType().newInstance();
			} catch (Exception e) {
				throw new DAOException(e);
			}
		}
	}

	/**
	 * 将对象转换成VOSupport接口
	 * 
	 * @param valueObject
	 * @return
	 * @throws DAOException
	 */
	public VOSupport getVOSupport(Object valueObject) throws DAOException {
		if(valueObject instanceof VOSupport){
			return (VOSupport)valueObject;
		}

		return (VOSupport) ProxyHelper.translateObject(VOSupport.class,new VOInvocationHandler(valueObject),  valueObject);
	}

	public void setModelProvider(ModelProvider modelProvider) {
		this.modelProvider = modelProvider;
	}

	public ModelProvider getModelProvider() {

		return modelProvider;
	}

}
