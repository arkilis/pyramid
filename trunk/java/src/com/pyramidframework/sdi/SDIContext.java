package com.pyramidframework.sdi;

/**
 * 需要进行转换的数据的内容封装进一个context中
 * 
 * @author Mikab Peng
 * @version 2008-7-26
 */
public class SDIContext {
	SDIDocument parent = null; // 父文档
	SDIDocument child = null; // 得到最终文档
	SDIDocument rule = null; // 继承定义文档

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
