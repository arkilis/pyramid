package com.pyramidframework.spring;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import com.pyramidframework.ci.ConfigDocumentParser;
import com.pyramidframework.ci.TypedManager;

/**
 * 构造spriing的beanfactory的工厂类.可以使用默认的spring文档的解析器，也可以自己实现一个解析器并且组成使用。
 * 配置信息是只作用于單個實例的，因此很多時候需要將SpringFactory单实例化
 * 
 * @author Mikab Peng
 * 
 */
public class SpringFactory {
	TypedManager manager = null;	//内部只有的管理器的实例
	private ConfigDocumentParser parser = null;	//实例的文档解析器
	public static String defaultType = "spring";	//默认的配置信息的类型
	
	
	/**
	 * 使用默认的增量式解析器构造的spring的beanfactory的工厂实例。
	 * 默认的增量式管理是使用{@link IncrementSpringConfigurationParser}进行文档的解析的
	 * @param rootDirectory 开始存成配置文件的根目录
	 */
	public SpringFactory(String rootDirectory) {
		if (rootDirectory == null || "".equals(rootDirectory)){
			throw new IllegalArgumentException("the rootDirectory parameter can not be null !");
		}
		
		parser = new IncrementSpringConfigurationParser(rootDirectory);
	}
	
	/**
	 * 使用自定义的配置文件的解析器来定制自定义的工厂实现
	 * 可以参考的默认实现有{@link SpringConfigurationParser}和{@link IncrementSpringConfigurationParser}
	 * @param configParser ConfigDocumentParser的实现
	 */
	public SpringFactory(ConfigDocumentParser configParser) {
		
		if (configParser == null || "".equals(configParser)){
			throw new IllegalArgumentException("the configParser parameter can not be null !");
		}
		
		
		parser = configParser;
	}
	
	
	/**
	 * 得到指定的功能路径的beanfactory
	 * @param functionPath 对应功能的功能路径
	 * @return InheritedBeanFactory的实例
	 */
	public DefaultListableBeanFactory getBeanFactory(String functionPath) {
		if (manager == null) {
			createConfigManager();
		}
		return (InheritedBeanFactory) manager.getConfigData(functionPath);
	}

	/**
	 * 创建内幕的配置信息管理器
	 */
	protected void createConfigManager() {
		manager = new TypedManager(defaultType,parser);
	}
}
