package com.pyramidframework.struts1;

/**
 * ����struts2��Action�ӿ�
 * 
 * @author Mikab Peng
 * 
 */
public interface ActionSupport {

	/**
	 * ִ��
	 * 
	 * @return
	 * @throws Exception
	 */
	public String execute() throws Exception;

	public static final String SUCCESS = "success";
}
