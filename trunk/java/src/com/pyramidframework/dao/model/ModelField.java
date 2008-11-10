package com.pyramidframework.dao.model;

/**
 * 类似于字段
 * 
 * @author Mikab Peng
 * 
 */
public class ModelField {

	/**
	 * 名字不能改变
	 * 
	 * @param fieldName
	 */
	public ModelField(String fieldName) {
		if (fieldName == null){
			throw new NullPointerException("Fieldname cann't be null!");
		}
		this.name = fieldName;
	}



	// 是否能背程序修改，如果是false则能修改，默认为能修改
	private boolean isPrimary = false;// 是否是主键
	private String name = null;// 字段名字，程序中操作

	private DataType type = null; // 数据类型
	private String label = null;// 标注的名称
	private String sequence = null;// 自增长的列的类型

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
	
	/**
	 * 主要判断名字和是不是主键
	 */
	public int hashCode() {
		int hashcode = name.hashCode();
		if (isPrimary){
			hashcode &=0x7FFFFFFF;
		}else{
			hashcode &=0xFFFFFFFF;
		}
		return hashcode;
	}
	
	/**
	 * 逐一判断是不是相等
	 */
	public boolean equals(Object obj) {
		if (obj != null) {
			if (obj.getClass().equals(ModelField.class)) {
				ModelField field = (ModelField) obj;
				return ((field.isPrimary == this.isPrimary) && CompareStringWithNull(field.name, this.name) && CompareStringWithNull(field.label, this.label)
						&& CompareStringWithNull(field.sequence, this.sequence) && this.type == null ? field.type == null : this.type.equals(field.type));
			}
		}
		return false;
	}
	
	private boolean CompareStringWithNull(String a, String b) {
		if (a == null && b == null) {
			return true;
		} else if (a != null) {
			return a.equals(b);
		} else {
			return false;
		}
	}
}
