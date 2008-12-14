package com.pyramidframework.spring;

import java.util.Map;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.FileSystemResource;

import com.pyramidframework.ci.ConfigDocumentParser;
import com.pyramidframework.ci.ConfigDomain;
import com.pyramidframework.ci.impl.ConfigDomainImpl;
import com.pyramidframework.sdi.xml.XmlDocument;

/**
 * 使用全量式文件解析的程序。 初始數據文件是完全按照spring的規則實現的，而增量的信息则必须完全按照xml Inheritance的格式来实现。
 * 
 * @author Mikab Peng
 * 
 */
public class SpringConfigurationParser implements ConfigDocumentParser {

	protected String rootDirectory = null;// 配置文件开始存放的目录
	protected SpringFactory factoryInstance = null;

	/**
	 * 需要指定是哪个配置文件的目录
	 * 
	 * @param rootDirectory
	 *            存放配置文件的根目录
	 * @param instance
	 *            SpringFactory的实例
	 */
	public SpringConfigurationParser(String rootDirectory, SpringFactory instance) {
		// 去掉最后的那个/
		while (rootDirectory.endsWith("/")) {
			rootDirectory = rootDirectory.substring(0, rootDirectory.length() - 1);
		}
		this.rootDirectory = rootDirectory;

		if (instance == null) {
			throw new IllegalArgumentException("SpringFactory parameter can not be null !");
		}
		this.factoryInstance = instance;

	}

	/**
	 * 解析文件,并且得到全部的bean的定义
	 */
	public Object parseConfigDocument(ConfigDomain domain, XmlDocument configDocument) {
		Object object = domain.getConfigData();
		if (object == null) {
			object = createDomainFactory(null, domain);
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
	 * 创建指定域的beanfactory的实例
	 * 
	 * @param thisDomain
	 * @return
	 */
	protected Object createDomainFactory(InheritedBeanFactory directFactory, ConfigDomain thisDomain) {
		if (thisDomain == null) {
			return null;
		}
		Object object = null;
		String parentPath = ((ConfigDomainImpl) thisDomain).getParentPath();
		// System.err.println("parentPath" + parentPath);

		if (parentPath != null && !"".equals(parentPath)) {
			ConfigDomain parentDomain = factoryInstance.manager.getConfigDomain(parentPath);
			BeanFactory beanFactory = null;

			// parentDomain可能是NULL
			if (parentDomain != null) {
				beanFactory = (BeanFactory) parentDomain.getConfigData();
			}
			object = new InheritedBeanFactory(directFactory, beanFactory);
		} else {

			// 当和当前访问的资源路径不一致且parentPath为NULL，需要获取同类型的配置信息,主要是用作获取其parentFactory
			if (!thisDomain.getTargetPath().equals(factoryInstance.getCurrentTargetPath())) {
				ConfigDomain domain = factoryInstance.manager.getConfigDomain(thisDomain.getTargetPath());
				object = new InheritedBeanFactory(directFactory, (BeanFactory) domain.getConfigData());
			} else {
				object = new InheritedBeanFactory(directFactory, null);
			}
		}

		return object;
	}

	/**
	 * DO Nothing
	 */
	public void InitTemplateContext(Map templateContext) {
		templateContext.put("classUtil", SpringClassUtil.getInstance());//可在脚本中检查是不是某些类存在
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