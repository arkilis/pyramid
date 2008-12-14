package com.pyramidframework.struts1;

/**
 * 类似struts2的Action接口
 * 
 * @author Mikab Peng
 * 
 */
public interface ActionSupport {

	/**
	 * 执行
	 * 
	 * @return
	 * @throws Exception
	 */
	public String execute() throws Exception;

	public static final String SUCCESS = "success";
}
