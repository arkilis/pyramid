package com.pyramidframework.dao.model;

import java.util.HashMap;
import java.util.Map;

public class CachedModelProvider implements ModelProvider {

	private Map modelCacheMap = new HashMap();
	private ModelProvider modelProvider = null;// ͨ��ע��

	/**
	 * ���ڻ������ң�Ȼ���ٴ�modelProvider��Ѱ��
	 */
	public DataModel getModelByName(String modelName) {
		Object model = modelCacheMap.get(modelName);
		
		if (model == null){
			synchronized (this) {
				model = modelCacheMap.get(modelName);
				if (model == null){
					model = modelProvider.getModelByName(modelName);
					modelCacheMap.put(modelName, model);
				}
			}
		}

		return ((DataModel)model);
	}
	
	/**
	 * ֪ͨ��ģ�Ͳ���
	 */
	public void setModel(DataModel model) {
		//modelCacheMap.put(model.getModelName(), model);
		if (modelProvider != null ){
			modelProvider.setModel(model);
		}
	}
	

	public ModelProvider getModelProvider() {
		return modelProvider;
	}

	public void setModelProvider(ModelProvider modelProvider) {
		this.modelProvider = modelProvider;
	}

}
