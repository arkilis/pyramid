package com.pyramidframework.dao;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * VOsupport接口的调用包装器.主要在VOfactory中使用
 * 
 * @author Mikab Peng
 * 
 */
public class VOInvocationHandler implements InvocationHandler {
	Method nameMethod = null;
	Method valuesMethod = null;
	Object source = null;

	/**
	 * 构建
	 * 
	 * @param source
	 */
	public VOInvocationHandler(Object source) {
		if (source == null) {
			throw new NullPointerException("source can not be null!");
		}
		this.source = source;
	}
	
	/**
	 * 分别调用
	 */
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if ("getName".equals(method.getName())) {
			if (nameMethod == null) {
				nameMethod = source.getClass().getDeclaredMethod(method.getName(), method.getParameterTypes());
			}
			return nameMethod.invoke(source, args);
		} else {
			if (valuesMethod == null) {
				valuesMethod = source.getClass().getDeclaredMethod(method.getName(), method.getParameterTypes());
			}
			return valuesMethod.invoke(source, args);
		}
	}

}
