package com.pyramidframework.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 使用只有一个方法的拦截器的实现或者只调用一个方法的实现
 * @author Mikab Peng
 *
 */
public class SingleMethodProxyHandler implements InvocationHandler {
	Method method = null; 
	Object sourceObject = null;
	String targetMethod = null;
	
	/**
	 * 必须制定被调用的对象
	 * @param sourceObject
	 */
	public SingleMethodProxyHandler(Object sourceObject) {
		if (sourceObject == null){
			throw new NullPointerException("sourceObject can not be null !");
		}
		this.sourceObject = sourceObject;
	}
	
	/**
	 * 制定了目标被调用的方法
	 * @param sourceObject
	 * @param targetMethod
	 */
	public SingleMethodProxyHandler(Object sourceObject,String targetMethod) {
		this(sourceObject);
		this.targetMethod = targetMethod;
		
	}
	
	
	/**
	 * 执行调用
	 */
	public Object invoke(Object proxy, Method invokeMethod, Object[] args) throws Throwable {
		if(method == null){
			synchronized (this) {
				if (targetMethod == null){
					method = sourceObject.getClass().getDeclaredMethod(invokeMethod.getName(), invokeMethod.getParameterTypes());
				}else{
					method = sourceObject.getClass().getDeclaredMethod(targetMethod, invokeMethod.getParameterTypes());
				}
			}
		}
		return method.invoke(sourceObject, args);
	}

}
