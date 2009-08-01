package com.pyramidframework.ajax;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import uk.ltd.getahead.dwr.Creator;
import uk.ltd.getahead.dwr.WebContextFactory;

import com.pyramidframework.spring.SpringFactory;

/**
 * 必须额外指定beanName属性
 * 
 * @author Mikab Peng
 * 
 */
public class PyramidCreator implements Creator {
	private String beanName = null;
	SpringFactory sfactory = SpringFactory.getDefaultInstance();
	protected String functionPath;

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
		functionPath = "/dwr/" + beanName;
	}

	public Object getInstance() throws InstantiationException {
		HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
		BeanFactory factory = (BeanFactory) request.getAttribute(SpringFactory.FACTORY_KEY);
		return factory.getBean(beanName);
	}

	public String getScope() {
		DefaultListableBeanFactory factory = (DefaultListableBeanFactory) sfactory.getBeanFactory(functionPath);
		return factory.getBeanDefinition(beanName).getScope();
	}

	public Class getType() {
		HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
		
		DefaultListableBeanFactory factory =(DefaultListableBeanFactory) sfactory.getBeanFactory(functionPath,request);
	
		return factory.getBean(beanName).getClass();
	}

	public void setProperties(Map prop) throws IllegalArgumentException {

		beanName = (String) prop.get("beanName");
		if (beanName == null) {
			beanName = (String) prop.get("javascript");
		}
		functionPath = "/dwr/" + beanName;
	}
	
}
