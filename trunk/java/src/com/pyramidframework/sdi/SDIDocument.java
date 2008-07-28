package com.pyramidframework.sdi;

import java.util.List;

/**
 * ��Ҫת�����ĵ�����
 * @author Mikab Peng
 * @version 2008-7-26
 */
public interface  SDIDocument extends SDINode {
	
	/**
	 * �õ�ָ���Ľڵ㣬�����ʾ���������ڵ㣬���Ե�һ���ڵ�Ϊ׼
	 * @param identifier
	 * @return
	 * @throws SDIException
	 */
	public  SDINode getSingleNode(String identifier) throws SDIException;
	
	/**
	 * �õ����������Ľڵ�ļ���
	 * @param identifier
	 * @return
	 * @throws SDIException
	 */
	public  List getNodeList(String identifier) throws SDIException;
	
	/**
	 * ʹ���µĽڵ��滻���ɵĽڵ�
	 * @param identifier
	 * @param node
	 * @throws SDIException
	 */
	public  void setNode(String identifier, SDINode node) throws SDIException;
	
	/**
	 * ��ָ���ĸ��ڵ���Ӷ�Ӧ���ӽڵ�
	 * @param parentidentifier
	 * @param node
	 * @throws SDIException
	 */
	public  void addNode(String parentidentifier, SDINode node) throws SDIException;
	
	/**
	 * �Ƴ�����Ӧ���ӽڵ�
	 * @param identifier
	 * @throws SDIException
	 */
	public  void removeNode(String identifier) throws SDIException;
	
	/**
	 * ����ϵͳ��ʶ�����µ��ĵ�����
	 * @param resourcePath
	 * @return
	 * @throws SDIException
	 */
	public SDIDocument createDocumentFromResource(String resourcePath)throws SDIException;

}
