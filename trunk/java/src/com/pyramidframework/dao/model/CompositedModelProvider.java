package com.pyramidframework.dao.model;

import java.util.Iterator;
import java.util.List;

/**
 * ������ModelProvider�Ľ�����кϲ��õ����յĽ���� �ϲ��Ĺ������� <br>
 * <li>���ĳ��MODELֻ��topProvider��buttomProvider�д��ڣ���ֱ�ӷ��ظ�Model</li>
 * <li>��ĳ��Ĥ���˶��������д���ʱ����ϲ���</li>
 * <li>�д����ظ�ʱ����topProviderΪ׼��������sequenceΪ�ն�buttomProvider���ǣ����߲���������buttomProvider�ǡ�
 * ���Բ��ǳ�ʼֵ��ֵΪ���ս����������������ǳ�ʼֵʱ����topProviderΪ׼</li>
 * 
 * @author Mikab Peng
 * 
 */
public class CompositedModelProvider implements ModelProvider {
	private ModelProvider topProvider = null;// ����ע��
	private ModelProvider buttomProvider = null;// ����ע��

	/**
	 * ��ȡ��Ϻ�Ľ��
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
	 * ������ģ�ͽ��кϲ�
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

		// ��model2�в�������model1�е�����ȡ����
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
	 * ���н��з�Ĭ��ֵ�ϲ�
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
			result.setPrimary(field.isPrimary() || field2.isPrimary()); // �Ƿ�����
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
