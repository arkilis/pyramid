package com.pyramidframework.simpleconfig;

import com.pyramidframework.sdi.xml.XmlNode;
import com.thoughtworks.xstream.XStream;

/**
 * ����XStream������ת���� ����XStream�ĸ�����Ϣ��μ�<a
 * href="http://xstream.codehaus.org/">http://xstream.codehaus.org/</a>
 * 
 * @author Mikab Peng
 * 
 */
public class XstreamBeanReader implements XmlBeanReader {
	XStream stream = null;

	/**
	 * �������ݲ��ƶ���bean
	 * 
	 * @param element
	 */
	public Object readFromXmlElement(XmlNode element) {

		return stream.fromXML(element.getDom4JNode().asXML());
	}

	/**
	 * ���캯��
	 * 
	 * @param stream
	 *            XStream��ʵ�֣���Ҫ��elementת��������javabean
	 */
	public XstreamBeanReader(XStream stream) {
		if (stream == null) {
			throw new NullPointerException("You must define a XStream instance!");
		}
		this.stream = stream;
	}

}
