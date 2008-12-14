package com.pyramidframework.spring;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.FileSystemResource;
import org.w3c.dom.Document;

import com.pyramidframework.ci.ConfigDomain;
import com.pyramidframework.ci.IncrementDocumentParser;
import com.pyramidframework.sdi.NodeOperator;
import com.pyramidframework.sdi.xml.XmlNode;

/**
 * ��Ӧ�����ü̳е�����ʽ�����Ľ���������Ҫ���ڽ���spring�������ļ��� �ڽ�������ʽ����ʱ�������ڿ�Խ�汾ʱ���ܴ������⡣
 * ��ʼʱʹ�õĲ��԰汾��spring 2.5.5��
 * ���ڱ�ģ��ֵ��עbean��ע�룬����δ��spring���������Խ��в��ԣ���aop�������������������Զ�û����Ӱ�졣
 * 
 * @author Mikab Peng
 * 
 */
public class IncrementSpringConfigurationParser extends SpringConfigurationParser implements IncrementDocumentParser {

	/**
	 * �����ļ���ŵĸ�Ŀ¼
	 * 
	 * @param rootDirectory
	 *            ��������ļ��ĸ�Ŀ¼
	 * @param factory
	 *            SpringFactory��ʵ������Ҫ��������ʵ��ʵ��Spring�ĵ�ʵ��������
	 */
	public IncrementSpringConfigurationParser(String rootDirectory, SpringFactory factory) {
		super(rootDirectory, factory);
	}

	/**
	 * ����һ�������document����Ȼ�������
	 */
	public Object parseIncrementElement(Object thisConfigData, XmlNode childOfRoot, NodeOperator operator) {

		InheritedBeanFactory factory = (InheritedBeanFactory) thisConfigData;
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);

		try {
			Document document = getEmptyDocument(childOfRoot);
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

		return createDomainFactory((InheritedBeanFactory) parentDataNode, domain);

	}

	protected Document getEmptyDocument(XmlNode node) throws Exception {
		String xml = "<beans>" + node.getDom4JNode().asXML() + "</beans>";
		InputStream is = new ByteArrayInputStream(xml.getBytes());
		return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);

	}

	/**
	 * �����Ϊ�������õ����ݣ����޸�֮
	 */
	protected Object createDomainFactory(InheritedBeanFactory directFactory, ConfigDomain thisDomain) {
		if (thisDomain == null) {
			return null;
		}

		InheritedBeanFactory factory = (InheritedBeanFactory) super.createDomainFactory(directFactory, thisDomain);

		return factory;
	}

}
