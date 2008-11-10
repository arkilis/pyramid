package com.pyramidframework.dao.model;

/**
 * �������ֶ�
 * 
 * @author Mikab Peng
 * 
 */
public class ModelField {

	/**
	 * ���ֲ��ܸı�
	 * 
	 * @param fieldName
	 */
	public ModelField(String fieldName) {
		if (fieldName == null){
			throw new NullPointerException("Fieldname cann't be null!");
		}
		this.name = fieldName;
	}



	// �Ƿ��ܱ������޸ģ������false�����޸ģ�Ĭ��Ϊ���޸�
	private boolean isPrimary = false;// �Ƿ�������
	private String name = null;// �ֶ����֣������в���

	private DataType type = null; // ��������
	private String label = null;// ��ע������
	private String sequence = null;// ���������е�����

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
	 * ��Ҫ�ж����ֺ��ǲ�������
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
	 * ��һ�ж��ǲ������
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
