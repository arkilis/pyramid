package com.pyramidframework.spring;

import java.util.HashMap;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;

/**
 * 专门用于集成Spring的beanFactory的脚本上下文的MAP实现。即在调用Map的get实现，先查找MAP是否有值，然后查找beanfactory是否有
 * 如果都没有返回NULL。如果对象是在beanFactory找到时，会需要缓存到Map中去.
 * 如果scope是prototype,则需要通过beanFactory.getBean去获取bean实现
 * 
 * @author Mikab Peng
 * 
 */
public class BeanFactoryScriptContextMap extends HashMap {
	private static final long serialVersionUID = -5900328622965356645L;

	BeanFactory beanFactory = null;

	/**
	 * 必须制定一个BeanFactory实例
	 * 
	 * @param beanFactory
	 */
	public BeanFactoryScriptContextMap(BeanFactory beanFactory) {
		if (beanFactory == null) {
			throw new NullPointerException("BeanFactory can not be null!");
		}
		this.beanFactory = beanFactory;
	}

	/**
	 * 如果MAP内没值且key是String时，调用beanFactory查找， 如果是找beanFactory找到时，需要缓存到Map中去
	 * 如果spring的bean是prototype,则需要通过beanFactory.getBean去获取bean实现
	 */
	public Object get(Object key) {
		
		
		Object result = super.get(key);
		if (result == null && key instanceof String) {
			try {
				result = beanFactory.getBean((String) key);
				put(key, result);
			} catch (BeansException e) {
				throw new RuntimeException(e);
			}
		}
		return result;
	}

	/**
	 * 需要同时在MAP和beanFactory中判断.
	 */
	public boolean containsKey(Object key) {
	
		return super.containsKey(key) || beanFactory.containsBean((String) key);
	}
}
