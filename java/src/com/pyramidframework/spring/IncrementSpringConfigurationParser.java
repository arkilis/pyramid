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
 * 适应于配置继承的增量式解析的解析器，主要用于解析spring的配置文件。 在进行增量式解析时，可能在跨越版本时可能存在问题。
 * 开始时使用的测试版本是spring 2.5.5。
 * 由于本模块值关注bean的注入，并且未对spring的其他特性进行测试，如aop，不担保其他所有特性都没有受影响。
 * 
 * @author Mikab Peng
 * 
 */
public class IncrementSpringConfigurationParser extends SpringConfigurationParser implements IncrementDocumentParser {

	/**
	 * 配置文件存放的根目录
	 * 
	 * @param rootDirectory
	 *            存放配置文件的根目录
	 * @param factory
	 *            SpringFactory的实例，需要依靠他来实现实现Spring的单实例等特性
	 */
	public IncrementSpringConfigurationParser(String rootDirectory, SpringFactory factory) {
		super(rootDirectory, factory);
	}

	/**
	 * 构建一个虚拟的document对象，然后解析他
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
	 * 直接将上级的bean的定义拷贝过来
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
	 * 如果是为本级配置的数据，则修改之
	 */
	protected Object createDomainFactory(InheritedBeanFactory directFactory, ConfigDomain thisDomain) {
		if (thisDomain == null) {
			return null;
		}

		InheritedBeanFactory factory = (InheritedBeanFactory) super.createDomainFactory(directFactory, thisDomain);

		return factory;
	}

}
