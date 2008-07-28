package com.pyramidframework.sdi;

import java.util.List;

/**
 * 指代进行继承转换的文档的一个节点
 * @author Mikab Peng
 * @version 2008-07-26
 */
public interface SDINode extends Cloneable {
	
	/**
	 * 得到子节点
	 * @return
	 * @throws SDIException
	 */
	public List getChildren() throws SDIException;
	
	/**
	 * 得到父节点
	 * @return
	 * @throws SDIException
	 */
	public SDINode getParentNode() throws SDIException;
	
	/**
	 * 复制节点
	 * @return
	 * @throws SDIException
	 */
	public SDINode cloneNode() throws SDIException;
	
	/**
	 * 得到该节点的文字信息
	 * @return
	 * @throws SDIException
	 */
	public String getTextValue()throws SDIException;


}
