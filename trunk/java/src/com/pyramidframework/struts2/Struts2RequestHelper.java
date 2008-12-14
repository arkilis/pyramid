package com.pyramidframework.struts2;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

public class Struts2RequestHelper {

	/**
	 * �õ���ǰ���ʵ�struts�е�·��
	 * 
	 * @return
	 */
	public static String getCurrentRequestURI() {

		ActionMapping mapping = ServletActionContext.getActionMapping();
		String name = mapping.getNamespace();
		if (!name.endsWith("/")) {
			name += "/";
		}

		return name + mapping.getName();

	}
	
	/**
	 * ��õ�ǰ������
	 * @return
	 */
	public static HttpServletRequest getCurrentRequest() {
		return ServletActionContext.getRequest();

	}
	
	/**
	 * ��õ�ǰ������
	 * @return
	 */
	public static HttpServletResponse getCurrentResponse() {
		return ServletActionContext.getResponse();
	}

}
