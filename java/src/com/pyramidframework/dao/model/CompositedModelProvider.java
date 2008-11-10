package com.pyramidframework.dao.model;

import java.util.Iterator;
import java.util.List;

/**
 * 将两个ModelProvider的结果进行合并得到最终的结果。 合并的规则如下 <br>
 * <li>如果某个MODEL只在topProvider和buttomProvider中存在，则直接返回该Model</li>
 * <li>当某个膜的了都在两个中存在时，则合并列</li>
 * <li>列存在重复时，以topProvider为准，除非其sequence为空而buttomProvider不是，或者不是主键而buttomProvider是。
 * 即以不是初始值的值为最终结果，如果两个都不是初始值时，以topProvider为准</li>
 * 
 * @author Mikab Peng
 * 
 */
public class CompositedModelProvider implements ModelProvider {
	private ModelProvider topProvider = null;// 依赖注入
	private ModelProvider buttomProvider = null;// 依赖注入

	/**
	 * 获取组合后的结果
	 */
	public DataModel getModelByName(String modelName) {
		DataModel model = topProvider.getModelByName(modelName);
		if (model == null) {
			return buttomProvider.getModelByName(modelName);
		} else {
			DataModel model2 = buttomProvider.getModelByName(modelName);
			if (model2 != null) {
				model = mergeModels(model, model2);
			}
		}

		return model;
	}

	/**
	 * 将两个模型进行合并
	 * 
	 * @param model
	 * @param model2
	 * @return
	 */
	protected DataModel mergeModels(DataModel model, DataModel model2) {
		DataModel result = new DataModel(model.getModelName());
		List list = model.getFiledList();
		Iterator iterator = list.iterator();
		while (iterator.hasNext()) {
			ModelField field = (ModelField) iterator.next();
			ModelField field2 = model2.getFieldByName(field.getName());
			field = mergeField(field, field2);
			result.addModelFiled(field);
		}

		// 将model2中不包含在model1中的数据取出来
		iterator = model2.getFiledList().iterator();
		while (iterator.hasNext()) {
			ModelField field = (ModelField) iterator.next();
			ModelField field2 = model.getFieldByName(field.getName());
			if (field2 == null) {
				result.addModelFiled(field);
			}
		}
		
		return result;

	}

	/**
	 * 对列进行非默认值合并
	 * 
	 * @param field
	 * @param field2
	 * @return
	 */
	protected ModelField mergeField(ModelField field, ModelField field2) {
		if (field2 == null) {
			return field;
		} else {
			ModelField result = new ModelField(field.getName());
			result.setPrimary(field.isPrimary() || field2.isPrimary()); // 是否主键
			result.setLabel(field.getLabel() == null ? field2.getLabel() : field.getLabel());
			result.setType(field.getType() == null ? field2.getType() : field.getType());
			result.setSequence(field.getSequence() == null ? field2.getSequence() : field.getSequence());
			return result;
		}
	}

	public ModelProvider getTopProvider() {
		return topProvider;
	}

	public void setTopProvider(ModelProvider topProvider) {
		this.topProvider = topProvider;
	}

	public ModelProvider getButtomProvider() {
		return buttomProvider;
	}

	public void setButtomProvider(ModelProvider buttomProvider) {
		this.buttomProvider = buttomProvider;
	}

}
