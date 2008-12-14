package com.pyramidframework.spring;

import java.util.HashMap;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;

/**
 * ר�����ڼ���Spring��beanFactory�Ľű������ĵ�MAPʵ�֡����ڵ���Map��getʵ�֣��Ȳ���MAP�Ƿ���ֵ��Ȼ�����beanfactory�Ƿ���
 * �����û�з���NULL�������������beanFactory�ҵ�ʱ������Ҫ���浽Map��ȥ.
 * ���scope��prototype,����Ҫͨ��beanFactory.getBeanȥ��ȡbeanʵ��
 * 
 * @author Mikab Peng
 * 
 */
public class BeanFactoryScriptContextMap extends HashMap {
	private static final long serialVersionUID = -5900328622965356645L;

	BeanFactory beanFactory = null;

	/**
	 * �����ƶ�һ��BeanFactoryʵ��
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
	 * ���MAP��ûֵ��key��Stringʱ������beanFactory���ң� �������beanFactory�ҵ�ʱ����Ҫ���浽Map��ȥ
	 * ���spring��bean��prototype,����Ҫͨ��beanFactory.getBeanȥ��ȡbeanʵ��
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
	 * ��Ҫͬʱ��MAP��beanFactory���ж�.
	 */
	public boolean containsKey(Object key) {
	
		return super.containsKey(key) || beanFactory.containsBean((String) key);
	}
}
