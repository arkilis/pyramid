package com.pyramidframework.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * 提供类到接口的代理、proxy服务
 * @author Mikab Peng
 *
 */
public class ProxyHelper {
	
	/**
	 * 将source对象转换为targetInterface指定的接口类型。source不需要实现targetInterface，只需有相同的方法即可。如果包含目标接口，则直接返回
	 * @param targetInterface 目标接口
	 * @param source 源对象，支持{@see ProxyAware}接口
	 * @return
	 */
	public static Object translateObject(Class targetInterface,Object source){
		return translateObject(targetInterface,source,null);
	}
	
	/**
	 * 将source对象转换为targetInterface指定的接口类型。source不需要实现targetInterface，只需有相同的方法即可。如果包含目标接口，则直接返回
	 * @param targetInterface 目标接口名称
	 * @param source 源对象，支持{@see ProxyAware}接口
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
	 * 将source对象转换为targetInterface指定的接口类型。source不需要实现targetInterface，只需有相同的方法即可,
	 * 或者在methodMatches里指明函数的对应关系。如果包含目标接口，则直接返回。
	 * @param targetInterface 目标接口
	 * @param source 源对象 ，支持{@see ProxyAware}接口
	 * @param methodMatches 方法的映射关系
	 * @return
	 */
	public static Object translateObject(Class targetInterface,Object source,Map methodMatches){
		//如果是其子类，则直接返回
		if (targetInterface.isAssignableFrom(source.getClass())){
			return source;
		}
		
		ProxyHandler handler = new ProxyHandler(source,methodMatches);
		
		Object result = translateObject(targetInterface, handler);
		//通知其内容
		if(source instanceof ProxyAware){
			((ProxyAware)source).setProxyInsatnce(result, handler);
		}
		
		return result;
		
	}

	/**
	 * 得到代理对象，对Proxy.newProxyInstance方法进行封装而已。
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
