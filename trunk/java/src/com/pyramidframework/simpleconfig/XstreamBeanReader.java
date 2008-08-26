package com.pyramidframework.simpleconfig;

import com.pyramidframework.sdi.xml.XmlNode;
import com.thoughtworks.xstream.XStream;

/**
 * 基于XStream的数据转换器 关于XStream的更多信息请参见<a
 * href="http://xstream.codehaus.org/">http://xstream.codehaus.org/</a>
 * 
 * @author Mikab Peng
 * 
 */
public class XstreamBeanReader implements XmlBeanReader {
	XStream stream = null;

	/**
	 * 解析数据并制定成bean
	 * 
	 * @param element
	 */
	public Object readFromXmlElement(XmlNode element) {

		return stream.fromXML(element.getDom4JNode().asXML());
	}

	/**
	 * 构造函数
	 * 
	 * @param stream
	 *            XStream的实现，主要将element转换成数据javabean
	 */
	public XstreamBeanReader(XStream stream) {
		if (stream == null) {
			throw new NullPointerException("You must define a XStream instance!");
		}
		this.stream = stream;
	}

}
