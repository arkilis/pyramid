package com.pyramidframework.dao.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataModel {

	protected List fields = new ArrayList();
	protected List pramaryKeys = new ArrayList();
	protected Map name2filed = new HashMap();
	private String modelName = null;
	private Class type = null;	//值对象的类类型
	
	protected volatile int columnLength = 0;
	protected volatile int primaryLength = 0;

	public DataModel(String modelName) {
		if (modelName == null) {
			throw new NullPointerException("modelName can not be null!");
		}
		this.modelName = modelName;
	}

	public List getFiledList() {
		return fields;
	}

	public ModelField getFieldByName(String name) {
		return (ModelField) name2filed.get(name);
	}

	public List getPramaryKeys() {
		return pramaryKeys;
	}

	public String getModelName() {
		return modelName;
	}

	public void addModelFiled(ModelField field) {
		fields.add(field);
		name2filed.put(field.getName(), field);
		if (field.isPrimary()) {
			this.pramaryKeys.add(field);
			primaryLength += field.getName().length();
		}
		this.columnLength += field.getName().length();
		field.setDataModel(this);
	}

	public void setModelName(String name) {
		this.modelName = name;
	}
	
	/**所有字段名字长度和*/
	public int getColumnLength() {
		return columnLength;
	}
	
	/**所有主键字段名字长度和*/
	public int getPrimaryLength() {
		return primaryLength;
	}

	
	/**
	 * 设置值对象的类类型，默认为null时使用{@see ValueObject}类型
	 * @return
	 */
	public Class getType() {
		return type;
	}

	public void setType(Class type) {
		this.type = type;
	}

}
