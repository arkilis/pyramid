package com.pyramidframework.spring;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.FileSystemResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.pyramidframework.ci.ConfigDomain;
import com.pyramidframework.ci.IncrementDocumentParser;
import com.pyramidframework.sdi.NodeOperator;
import com.pyramidframework.sdi.xml.XmlNode;

/**
 * ��Ӧ�����ü̳е�����ʽ�����Ľ���������Ҫ���ڽ���spring�������ļ���
 * �ڽ�������ʽ����ʱ�������ڿ�Խ�汾ʱ���ܴ������⡣ ��ʼʱʹ�õĲ��԰汾��spring 2.5.5��
 * ���ڱ�ģ��ֵ��עbean��ע�룬����δ��spring���������Խ��в��ԣ���aop�������������������Զ�û����Ӱ�졣
 * 
 * @author Mikab Peng
 * 
 */
public class IncrementSpringConfigurationParser extends SpringConfigurationParser implements IncrementDocumentParser {
	private Document emptyDocument = null;// ����һ���հ׵�����bean�Ķ�����ĵ�����

	/**
	 * �����ļ���ŵĸ�Ŀ¼
	 * 
	 * @param rootDirectory
	 *            ��������ļ��ĸ�Ŀ¼
	 */
	public IncrementSpringConfigurationParser(String rootDirectory) {
		super(rootDirectory);
	}

	/**
	 * ����һ�������document����Ȼ�������
	 */
	public Object parseIncrementElement(Object thisConfigData, XmlNode childOfRoot, NodeOperator operator) {

		InheritedBeanFactory factory = (InheritedBeanFactory) thisConfigData;
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);

		try {
			Element element = (Element) childOfRoot.getDomNode();
			Document document = getEmptyDocument();
			document.getDocumentElement().appendChild(element);
			reader.registerBeanDefinitions(document, new FileSystemResource(rootDirectory));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return factory;
	}

	/**
	 * ֱ�ӽ��ϼ���bean�Ķ��忽������
	 */
	public Object getDefaultConfigData(ConfigDomain domain, Object parentDataNode) {

		return new InheritedBeanFactory((BeanDefinitionRegistry) parentDataNode);

	}

	protected Document getEmptyDocument() throws SAXException, ParserConfigurationException, IOException {
		if (emptyDocument == null) {
			InputStream is = new ByteArrayInputStream("<beans></beans>".getBytes());
			emptyDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);

		}

		return (Document) emptyDocument.cloneNode(false);
	}

}
