package com.pyramidframework.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Ĭ�ϵĵ���������
 * @author Mikab Peng
 *
 */
public class DefaultProxyHandler implements InvocationHandler {
	Map methodMatches = null;
	Object proxyObject = null;
	
	public DefaultProxyHandler(Object source) {
		this(source,null);
	}
	
	public DefaultProxyHandler(Object source,Map methodMatches) {
		proxyObject =source;
		if (methodMatches == null){
			this.methodMatches = new HashMap();
		}else{
			this.methodMatches = new HashMap(methodMatches);
		}
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		
		Method targetMethod =(Method)methodMatches.get(method);
		if (targetMethod == null){
			String name = method.getName();
			
			//��Ҫ�ҹ�ϵ��Ϸ�Ǹ�
			if (methodMatches.containsKey(name)){
				name = (String)methodMatches.get(name);
			}
			
			targetMethod = proxyObject.getClass().getMethod(name, method.getParameterTypes());
			methodMatches.put(method, targetMethod);
		}
		
		return targetMethod.invoke(proxyObject, args);
	}

}
