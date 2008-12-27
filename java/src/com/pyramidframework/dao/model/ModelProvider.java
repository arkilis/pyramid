package com.pyramidframework.dao.model;

/**
 * ����ģ�͹�����
 * @author Mikab Peng
 *
 */
public interface ModelProvider {
	
	/**
	 * �������ֵõ�
	 * @param modelName
	 * @return
	 */
	public DataModel getModelByName(String modelName);
	
	/**
	 * ���������ģ��
	 * @param model
	 */
	public void setModel(DataModel model);
	
	
}
