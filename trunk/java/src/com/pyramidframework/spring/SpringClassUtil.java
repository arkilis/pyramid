package com.pyramidframework.spring;

/**
 * �ṩһЩ����صĸ�����������Ҫ��Spring�������ļ���ʹ��
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
	 * ϵͳ���ǲ��Ǵ���ĳ����
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
	 * ϵͳ���ǲ��Ǵ���ĳ����,������targetTnterface������
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
