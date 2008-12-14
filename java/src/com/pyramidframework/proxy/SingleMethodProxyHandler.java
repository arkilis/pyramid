package com.pyramidframework.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * ʹ��ֻ��һ����������������ʵ�ֻ���ֻ����һ��������ʵ��
 * @author Mikab Peng
 *
 */
public class SingleMethodProxyHandler implements InvocationHandler {
	Method method = null; 
	Object sourceObject = null;
	String targetMethod = null;
	
	/**
	 * �����ƶ������õĶ���
	 * @param sourceObject
	 */
	public SingleMethodProxyHandler(Object sourceObject) {
		if (sourceObject == null){
			throw new NullPointerException("sourceObject can not be null !");
		}
		this.sourceObject = sourceObject;
	}
	
	/**
	 * �ƶ���Ŀ�걻���õķ���
	 * @param sourceObject
	 * @param targetMethod
	 */
	public SingleMethodProxyHandler(Object sourceObject,String targetMethod) {
		this(sourceObject);
		this.targetMethod = targetMethod;
		
	}
	
	
	/**
	 * ִ�е���
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
