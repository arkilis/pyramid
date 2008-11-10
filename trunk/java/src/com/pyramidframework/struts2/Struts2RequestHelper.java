package com.pyramidframework.struts2;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

public class Struts2RequestHelper {
	
	/**
	 * 得到当前访问的struts中的路径
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
}
