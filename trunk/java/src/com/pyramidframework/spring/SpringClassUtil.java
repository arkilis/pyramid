package com.pyramidframework.spring;

import java.util.logging.Logger;

/**
 * 提供一些类相关的辅助方法，主要在Spring的配置文件中使用
 * 
 * @author Mikab Peng
 * 
 */
public class SpringClassUtil {

	static SpringClassUtil classUtils = new SpringClassUtil();
	private static final Logger logger = Logger.getLogger(SpringClassUtil.class.getName());

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
			logger.info("hasClass(" + className +")=false");
			return false;
		}
		logger.info("hasClass(" + className +")=true");
		return true;
	}

	/**
	 * 系统中是不是存在某个类,并且是targetTnterface的子类
	 * @param className
	 * @return
	 */
	public boolean hasClass(String className,String targetTnterface) {
		try {
			Class.forName(targetTnterface).isAssignableFrom(Class.forName(className));
		} catch (ClassNotFoundException e) {
			logger.info("hasClass(" + className +")=false");
			return false;
		}
		logger.info("hasClass(" + className +")=true");
		return true;
	}
	
	/**
	 * 用于格式化类名，类的第一个字母应是大写
	 * @param name
	 * @return
	 */
	public String upperFirstChar(String name){
		return name.substring(0,1).toUpperCase() + name.substring(1);
	}
	
}
