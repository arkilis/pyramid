package com.pyramidframework.simpleconfig;

import com.pyramidframework.sdi.xml.XmlNode;

/**
 * 在XML中将配置信息的节点转换成Bean的实例的接口
 * 默认实例采用xstream开源项目实现
 * @author Mikab Peng
 *
 */
public interface XmlBeanReader {
	
	/**
	 * 从指定的xml文件的节点中构建一个javabean的实例
	 * @param element XmlNode的实例，其容纳的为一个Elment节点
	 * @return bean的实例
	 */
	public Object readFromXmlElement(XmlNode element);
}
