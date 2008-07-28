package com.pyramidframework.sdi;

import java.util.List;

/**
 * 需要转换的文档界面
 * @author Mikab Peng
 * @version 2008-7-26
 */
public interface  SDIDocument extends SDINode {
	
	/**
	 * 得到指定的节点，如果标示符满足多个节点，则以第一个节点为准
	 * @param identifier
	 * @return
	 * @throws SDIException
	 */
	public  SDINode getSingleNode(String identifier) throws SDIException;
	
	/**
	 * 得到符合特征的节点的集合
	 * @param identifier
	 * @return
	 * @throws SDIException
	 */
	public  List getNodeList(String identifier) throws SDIException;
	
	/**
	 * 使用新的节点替换掉旧的节点
	 * @param identifier
	 * @param node
	 * @throws SDIException
	 */
	public  void setNode(String identifier, SDINode node) throws SDIException;
	
	/**
	 * 在指定的父节点添加对应的子节点
	 * @param parentidentifier
	 * @param node
	 * @throws SDIException
	 */
	public  void addNode(String parentidentifier, SDINode node) throws SDIException;
	
	/**
	 * 移除掉对应的子节点
	 * @param identifier
	 * @throws SDIException
	 */
	public  void removeNode(String identifier) throws SDIException;
	
	/**
	 * 根据系统标识创建新的文档对象
	 * @param resourcePath
	 * @return
	 * @throws SDIException
	 */
	public SDIDocument createDocumentFromResource(String resourcePath)throws SDIException;

}
