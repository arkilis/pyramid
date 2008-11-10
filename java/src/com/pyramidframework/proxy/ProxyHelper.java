package com.pyramidframework.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * �ṩ�ൽ�ӿڵĴ���proxy����
 * @author Mikab Peng
 *
 */
public class ProxyHelper {
	
	/**
	 * ��source����ת��ΪtargetInterfaceָ���Ľӿ����͡�source����Ҫʵ��targetInterface��ֻ������ͬ�ķ������ɡ��������Ŀ��ӿڣ���ֱ�ӷ���
	 * @param targetInterface Ŀ��ӿ�
	 * @param source Դ����֧��{@see ProxyAware}�ӿ�
	 * @return
	 */
	public static Object translateObject(Class targetInterface,Object source){
		return translateObject(targetInterface,source,null);
	}
	
	/**
	 * ��source����ת��ΪtargetInterfaceָ���Ľӿ����͡�source����Ҫʵ��targetInterface��ֻ������ͬ�ķ������ɡ��������Ŀ��ӿڣ���ֱ�ӷ���
	 * @param targetInterface Ŀ��ӿ�����
	 * @param source Դ����֧��{@see ProxyAware}�ӿ�
	 * @return
	 */
	public static Object translateObject(String targetInterface,Object source){
		Class interf = null;
		try {
			interf = Class.forName(targetInterface);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return translateObject(interf,source,null);
	}
	
	/**
	 * ��source����ת��ΪtargetInterfaceָ���Ľӿ����͡�source����Ҫʵ��targetInterface��ֻ������ͬ�ķ�������,
	 * ������methodMatches��ָ�������Ķ�Ӧ��ϵ���������Ŀ��ӿڣ���ֱ�ӷ��ء�
	 * @param targetInterface Ŀ��ӿ�
	 * @param source Դ���� ��֧��{@see ProxyAware}�ӿ�
	 * @param methodMatches ������ӳ���ϵ
	 * @return
	 */
	public static Object translateObject(Class targetInterface,Object source,Map methodMatches){
		//����������࣬��ֱ�ӷ���
		if (targetInterface.isAssignableFrom(source.getClass())){
			return source;
		}
		
		ProxyHandler handler = new ProxyHandler(source,methodMatches);
		
		Object result = translateObject(targetInterface, handler);
		//֪ͨ������
		if(source instanceof ProxyAware){
			((ProxyAware)source).setProxyInsatnce(result, handler);
		}
		
		return result;
		
	}

	/**
	 * �õ�������󣬶�Proxy.newProxyInstance�������з�װ���ѡ�
	 * @param targetInterface
	 * @param handler
	 * @return
	 */
	public static Object translateObject(Class targetInterface, InvocationHandler handler) {
		Object result = Proxy.newProxyInstance(targetInterface.getClassLoader(), 
				new Class[]{targetInterface}, handler);
		
		return result;
	}
}
