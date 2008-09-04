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
 * 适应于配置继承的增量式解析的解析器，主要用于解析spring的配置文件。
 * 在进行增量式解析时，可能在跨越版本时可能存在问题。 开始时使用的测试版本是spring 2.5.5。
 * 由于本模块值关注bean的注入，并且未对spring的其他特性进行测试，如aop，不担保其他所有特性都没有受影响。
 * 
 * @author Mikab Peng
 * 
 */
public class IncrementSpringConfigurationParser extends SpringConfigurationParser implements IncrementDocumentParser {
	private Document emptyDocument = null;// 生成一个空白的容纳bean的定义的文档对象

	/**
	 * 配置文件存放的根目录
	 * 
	 * @param rootDirectory
	 *            存放配置文件的根目录
	 */
	public IncrementSpringConfigurationParser(String rootDirectory) {
		super(rootDirectory);
	}

	/**
	 * 构建一个虚拟的document对象，然后解析他
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
	 * 直接将上级的bean的定义拷贝过来
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
