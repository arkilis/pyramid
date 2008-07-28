package com.pyramidframework.sdi;

import java.util.List;

/**
 * ָ�����м̳�ת�����ĵ���һ���ڵ�
 * @author Mikab Peng
 * @version 2008-07-26
 */
public interface SDINode extends Cloneable {
	
	/**
	 * �õ��ӽڵ�
	 * @return
	 * @throws SDIException
	 */
	public List getChildren() throws SDIException;
	
	/**
	 * �õ����ڵ�
	 * @return
	 * @throws SDIException
	 */
	public SDINode getParentNode() throws SDIException;
	
	/**
	 * ���ƽڵ�
	 * @return
	 * @throws SDIException
	 */
	public SDINode cloneNode() throws SDIException;
	
	/**
	 * �õ��ýڵ��������Ϣ
	 * @return
	 * @throws SDIException
	 */
	public String getTextValue()throws SDIException;


}
