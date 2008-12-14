package com.pyramidframework.spring;

/**
 * 提供一些类相关的辅助方法，主要在Spring的配置文件中使用
 * 
 * @author Mikab Peng
 * 
 */
public class SpringClassUtil {

	static SpringClassUtil classUtils = new SpringClassUtil();

	public static SpringClassUtil getInstance() {
		return classUtils;
	}
	
	
	/**
	 * 系统中是不是存在某个类
	 * @param className
	 * @return
	 */
	public boolean hasClass(String className) {
		try {
			Class.forName(className);
		} catch (ClassNotFoundException e) {
			return false;
		}
		return true;
	}

	/**
	 * 系统中是不是存在某个类,并且是targetTnterface的子类
	 * @param className
	 * @return
	 */
	public boolean hasClass(String className,String targetTnterface) {
		try {
			return Class.forName(targetTnterface).isAssignableFrom(Class.forName(className));
		} catch (ClassNotFoundException e) {
			return false;
		}
	}
}
