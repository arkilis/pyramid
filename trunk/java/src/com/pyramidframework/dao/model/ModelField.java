package com.pyramidframework.dao.model;

import java.util.Collection;
import java.util.Map;

import com.pyramidframework.dao.model.datatype.DataType;

/**
 * �������ֶ�
 * 
 * @author Mikab Peng
 * 
 */
public class ModelField {

	// �Ƿ��ܱ������޸ģ������false�����޸ģ�Ĭ��Ϊ���޸�
	private boolean isPrimary = false;// �Ƿ�������
	private String name;// �ֶ����֣������в���
	private DataType type; // ��������
	private String label;// ��ע������
	private String sequence;// ���������е�����
	private DataModel dataModel;
	private volatile String fullModelName;

	/**
	 * ���ֲ��ܸı�
	 * 
	 * @param fieldName
	 */
	public ModelField(String fieldName) {
		if (fieldName == null) {
			throw new NullPointerException("Fieldname cann't be null!");
		}
		this.name = fieldName;
	}

	public Collection getPropertiesNames() {
		throw new UnsupportedOperationException("Not impelmented");
	}

	public void setProperties(Map properties) {
		throw new UnsupportedOperationException("Not impelmented");
	}

	public boolean isPrimary() {
		return isPrimary;
	}

	public void setPrimary(boolean isPrimary) {
		this.isPrimary = isPrimary;
	}

	public String getName() {
		return name;
	}

	public DataType getType() {
		return type;
	}

	public void setType(DataType type) {
		this.type = type;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public DataModel getDataModel() {
		return dataModel;
	}

	public void setDataModel(DataModel dataModel) {
		this.dataModel = dataModel;
	}

	public String getFullModelName() {
		if (fullModelName == null) {
			synchronized (this) {
				if (fullModelName == null) {
					fullModelName = getDataModel().getModelName() + "." + getName();
				}
			}
		}
		return fullModelName;
	}

	public void setFullModelName(String fullModelName) {
		this.fullModelName = fullModelName;
	}

}
