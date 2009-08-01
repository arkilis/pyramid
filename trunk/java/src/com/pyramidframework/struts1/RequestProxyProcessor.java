package com.pyramidframework.struts1;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.RequestProcessor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import com.pyramidframework.spring.SpringFactory;

public class RequestProxyProcessor extends RequestProcessor {
	protected SpringFactory springFactory = SpringFactory.getDefaultInstance();
	public static final String FORM_BEAN_KEY = RequestProxyProcessor.class.getName() + ".formbean";

	/**
	 * 重载以支持ActionContext
	 */
	public void process(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		setCharacterEncoding(request);

		String path = getPath(request, response);
		if (path == null) {
			return;
		}

		BeanFactory beanFactory = springFactory.getBeanFactory(path, request);
		try {
			ActionContext.setCurrent(beanFactory, request, response);
			ActionContext.getCurrent().setPath(path);

			super.process(request, response);
		} finally {
			ActionContext.setCurrent(null);
		}
	}

	/**
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	protected String getPath(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String path = super.processPath(request, response);
		if (moduleConfig.getPrefix() != null && !"".equals(moduleConfig.getPrefix())) {
			path = moduleConfig.getPrefix() + path;
		}

		request.setAttribute(Globals.ORIGINAL_URI_KEY, path);
		// System.err.println("getPath:" + path);

		return path;
	}

	/**
	 * @param request
	 * @throws UnsupportedEncodingException
	 */
	protected void setCharacterEncoding(HttpServletRequest request) throws UnsupportedEncodingException {
		if ("XMLHttpRequest".equalsIgnoreCase(request.getHeader("x-requested-with"))) {
			request.setCharacterEncoding("UTF-8");
		} else {
			request.setCharacterEncoding("GBK");
		}

	}

	/**
	 * 重载以支持ForxProxy；无论何种域都不在request进行寻找
	 */
	protected ActionForm processActionForm(HttpServletRequest request, HttpServletResponse response, ActionMapping mapping) {
		String attribute = mapping.getAttribute();
		if (attribute == null) {
			return (null);
		}

		String name = mapping.getName();
		String scope = ((ConfigurableListableBeanFactory) ActionContext.getCurrent().getBeanFactory()).getBeanDefinition(name).getScope();

		ActionForm instance = null;
		Object bean = ActionContext.getCurrent().getBeanFactory().getBean(name);

		ActionContext.getCurrent().setFormBean(bean);
		if (bean instanceof ActionForm) {
			instance = (ActionForm) bean;
		} else {
			instance = FormBeanProxy.getProxyForm(bean);
		}

		if ("request".equals(scope) || "prototype".equals(scope)) {
			request.setAttribute(attribute, instance);
		} else {
			HttpSession session = request.getSession();
			session.setAttribute(attribute, instance);
		}

		request.setAttribute(FORM_BEAN_KEY, instance);
		return instance;
	}

	/**
	 * 重载以支持ActionProxy
	 */
	protected Action processActionCreate(HttpServletRequest request, HttpServletResponse response, ActionMapping mapping) throws IOException {
		String className = mapping.getType();
		if (className == null) {
			return null;
		}

		Object bean = ActionContext.getCurrent().getBeanFactory().getBean(className);
		if (bean instanceof Action) {
			return (Action) bean;
		} else {
			return ActionProxy.getActionProxy(bean);
		}
	}
}
