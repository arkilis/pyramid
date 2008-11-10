package com.pyramidframework.proxy;

import java.lang.reflect.InvocationHandler;

/**
 * 对使用动态代理敏感的接口。即执行代理后，会将代理生成的对象和InvocationHandler通知给对象
 * @author Mikab Peng
 *
 */
public interface ProxyAware {
	
	/**
	 * 通知源对象生成代理对象和InvocationHandler
	 * @param instance
	 * @param handler
	 */
	public void setProxyInsatnce(Object instance,InvocationHandler handler);
	
}
