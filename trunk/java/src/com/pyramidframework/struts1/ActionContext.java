package com.pyramidframework.struts1;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.BeanFactory;

public class ActionContext {

	static ThreadLocal current = new ThreadLocal();

	public static ActionContext getCurrent() {
		return (ActionContext) current.get();
	}

	public static void setCurrent(ActionContext current) {
		ActionContext.current.set(current);
	}

	public static ActionContext setCurrent(BeanFactory beanFactory, HttpServletRequest request, HttpServletResponse response) {
		ActionContext cur = new ActionContext(beanFactory, request, response);
		ActionContext.current.set(cur);
		return cur;
	}

	protected ActionContext(BeanFactory beanFactory, HttpServletRequest request, HttpServletResponse response) {
		this.beanFactory = beanFactory;
		this.request = request;
		this.response = response;
	}

	BeanFactory beanFactory = null;
	HttpServletRequest request = null;
	HttpServletResponse response = null;
	String path  = null;
	Object formbean = null;
	
	

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	public Object getFormBean() {
		return formbean;
	}

	public void setFormBean(Object formbean) {
		this.formbean = formbean;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public Object getSession(String name){
		return getRequest().getSession(true).getAttribute(name);
	}
	
	public void setSession(String name,Object value){
		getRequest().getSession(true).setAttribute(name,value);
	}
	
	
	public void invalidateSession(){
		if (getRequest().getSession() != null){
			getRequest().getSession().invalidate();
		}
	}

}
