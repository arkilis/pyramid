package com.pyramidframework.spring;

import java.util.logging.Logger;

/**
 * �ṩһЩ����صĸ�����������Ҫ��Spring�������ļ���ʹ��
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
	 * ϵͳ���ǲ��Ǵ���ĳ����
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
	 * ϵͳ���ǲ��Ǵ���ĳ����,������targetTnterface������
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
	 * ���ڸ�ʽ����������ĵ�һ����ĸӦ�Ǵ�д
	 * @param name
	 * @return
	 */
	public String upperFirstChar(String name){
		return name.substring(0,1).toUpperCase() + name.substring(1);
	}
	
}
