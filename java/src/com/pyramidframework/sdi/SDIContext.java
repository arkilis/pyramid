package com.pyramidframework.sdi;

/**
 * ��Ҫ����ת�������ݵ����ݷ�װ��һ��context��
 * 
 * @author Mikab Peng
 * @version 2008-7-26
 */
public class SDIContext {
	SDIDocument parent = null; // ���ĵ�
	SDIDocument child = null; // �õ������ĵ�
	SDIDocument rule = null; // �̳ж����ĵ�

	public SDIDocument getParent() {
		return parent;
	}

	public void setParent(SDIDocument parent) {
		this.parent = parent;
	}

	public SDIDocument getChild() {
		return child;
	}

	public void setChild(SDIDocument child) {
		this.child = child;
	}

	public SDIDocument getRule() {
		return rule;
	}

	public void setRule(SDIDocument rule) {
		this.rule = rule;
	}

}
