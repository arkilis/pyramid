package com.pyramidframework.proxy;

import java.lang.reflect.InvocationHandler;

/**
 * ��ʹ�ö�̬�������еĽӿڡ���ִ�д���󣬻Ὣ�������ɵĶ����InvocationHandler֪ͨ������
 * @author Mikab Peng
 *
 */
public interface ProxyAware {
	
	/**
	 * ֪ͨԴ�������ɴ�������InvocationHandler
	 * @param instance
	 * @param handler
	 */
	public void setProxyInsatnce(Object instance,InvocationHandler handler);
	
}
