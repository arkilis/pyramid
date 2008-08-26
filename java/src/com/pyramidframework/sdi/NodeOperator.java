/*
 * 创建于：2008-06-22
 */
package com.pyramidframework.sdi;

/**
 * 在继承过程中允许的操作，即视为可对原文件进行扩张的类型
 * 
 * @author Mikab Peng
 * @version 0.01
 */
public abstract class NodeOperator {

	protected SDINode operhand = null;			//操作的新值对象
	protected String operatorName = null;		//操作符的名称
	protected String targetIdentifier = null;	//需要替换掉的旧值的对象

	/**
	 * 得到该操作符在XML文档的名称
	 * 
	 * @return
	 */
	public String getOperatorName()  {
		return operatorName;
	}

	/**
	 * 主要用于一个操作支持多种操作名称
	 * 
	 * @param newName
	 * @return 如果此操作符以前被赋予旧的名称，则返回就名称，否则返回<code>NULL</code>
	 */
	public void setOperatorName(String newName) throws SDIException {
		this.operatorName = newName;
	}

	/**
	 * 操作符操作的对象，可能为NULL
	 * 
	 * @return
	 */
	public SDINode getOperhand() {
		return operhand;
	}

	public void setOperhand(SDINode operhand) {
		this.operhand = operhand;
	}

	/**
	 * 对文件进行继承关系转换，本方法应该是在放在继承定义文档的根节点的子节点时使用
	 * 
	 * @param parent
	 * @param child
	 * @param identifier
	 *            应用此操作的文档部分的标示符，适应于XML文件时通常应该是XPath表达式
	 * @return <code>true<code>表示成功
	 * @throws SDIException
	 */
	public abstract boolean operateWithDocument(SDIDocument document) throws SDIException;
	
	
	/**
	 * 是否需要操作的对象
	 * @return true or false,如果是删除等操作则应为false
	 */
	public boolean needOperhand(){
		return true;
	}
	

	public String getTargetIdentifier() {
		return targetIdentifier;
	}

	public void setTargetIdentifier(String targetIdentifier) {
		this.targetIdentifier = targetIdentifier;
	}

}
