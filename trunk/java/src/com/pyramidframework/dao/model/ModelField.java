package com.pyramidframework.dao.model;

import java.util.Collection;
import java.util.Map;

import com.pyramidframework.dao.model.datatype.DataType;

/**
 * 类似于字段
 * 
 * @author Mikab Peng
 * 
 */
public class ModelField {

	// 是否能背程序修改，如果是false则能修改，默认为能修改
	private boolean isPrimary = false;// 是否是主键
	private String name;// 字段名字，程序中操作
	private DataType type; // 数据类型
	private String label;// 标注的名称
	private String sequence;// 自增长的列的类型
	private DataModel dataModel;
	private volatile String fullModelName;

	/**
	 * 名字不能改变
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
