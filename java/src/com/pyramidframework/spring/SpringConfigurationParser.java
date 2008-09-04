package com.pyramidframework.spring;

import java.util.Map;

import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.FileSystemResource;

import com.pyramidframework.ci.ConfigDocumentParser;
import com.pyramidframework.ci.ConfigDomain;
import com.pyramidframework.sdi.xml.XmlDocument;

/**
 * 使用全量式文件解析的程序。
 * 初始數據文件是完全按照spring的規則實現的，而增量的信息则必须完全按照xml Inheritance的格式来实现。
 * 
 * @author Mikab Peng
 *
 */
public class SpringConfigurationParser implements ConfigDocumentParser {

	protected String rootDirectory = null;//配置文件开始存放的目录
	
	/**
	 * 需要指定是哪个配置文件的目录
	 * @param rootDirectory 存放配置文件的根目录
	 */
	public SpringConfigurationParser(String rootDirectory) {
		// 去掉最后的那个/
		while (rootDirectory.endsWith("/")) {
			rootDirectory = rootDirectory.substring(0, rootDirectory.length() - 1);
		}
		this.rootDirectory = rootDirectory;
	}

	/**
	 * 解析文件,并且得到全部的bean的定义
	 */
	public Object parseConfigDocument(ConfigDomain thisDomain, XmlDocument configDocument) {
		Object object = thisDomain.getConfigData();
		if (object == null) {
			object = new InheritedBeanFactory(null);
		}
		InheritedBeanFactory factory = (InheritedBeanFactory) object;

		// 装在数据
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
		try {
			reader.registerBeanDefinitions(configDocument.getDomDocument(), new FileSystemResource(rootDirectory));
			// 所有的import必须为绝对路径
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return object;
	}

	/**
	 * DO Nothing
	 */
	public void InitTemplateContext(Map templateContext) {

	}

	/**
	 * 配置文件命名规则：每个目录下的spring.xml
	 */
	public String getConfigFileName(String functionPath, String configType) {
		if (functionPath == null) {
			functionPath = "/";
		}
		return rootDirectory + functionPath + configType + ".xml";
	}

}