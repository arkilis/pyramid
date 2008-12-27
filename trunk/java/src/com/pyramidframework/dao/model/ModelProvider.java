package com.pyramidframework.dao.model;

/**
 * 数据模型管理器
 * @author Mikab Peng
 *
 */
public interface ModelProvider {
	
	/**
	 * 根据名字得到
	 * @param modelName
	 * @return
	 */
	public DataModel getModelByName(String modelName);
	
	/**
	 * 往里面放入模型
	 * @param model
	 */
	public void setModel(DataModel model);
	
	
}
