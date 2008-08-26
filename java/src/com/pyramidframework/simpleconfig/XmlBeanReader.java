package com.pyramidframework.simpleconfig;

import com.pyramidframework.sdi.xml.XmlNode;

/**
 * ��XML�н�������Ϣ�Ľڵ�ת����Bean��ʵ���Ľӿ�
 * Ĭ��ʵ������xstream��Դ��Ŀʵ��
 * @author Mikab Peng
 *
 */
public interface XmlBeanReader {
	
	/**
	 * ��ָ����xml�ļ��Ľڵ��й���һ��javabean��ʵ��
	 * @param element XmlNode��ʵ���������ɵ�Ϊһ��Elment�ڵ�
	 * @return bean��ʵ��
	 */
	public Object readFromXmlElement(XmlNode element);
}
