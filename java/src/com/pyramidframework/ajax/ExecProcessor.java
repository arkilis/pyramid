package com.pyramidframework.ajax;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ltd.getahead.dwr.impl.DefaultExecProcessor;

import com.pyramidframework.spring.SpringFactory;

public class ExecProcessor extends DefaultExecProcessor {
	SpringFactory springFactory = SpringFactory.getDefaultInstance();

	/**
	 * ×¢ÈëBeanfactory
	 */
	public void handle(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
		
		String path = request.getPathInfo();
		String functionPath = request.getServletPath() + path.substring(5,path.indexOf("."));

		springFactory.getBeanFactory(functionPath, request);

		super.handle(request, resp);
	}
}
