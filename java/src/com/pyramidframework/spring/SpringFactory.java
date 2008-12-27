package com.pyramidframework.spring;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.pyramidframework.ci.ConfigDocumentParser;
import com.pyramidframework.ci.TypedManager;
import com.pyramidframework.ci.impl.ConfigDamainTree;
import com.pyramidframework.ci.impl.ConfigDomainImpl;
import com.pyramidframework.ci.impl.ConfigServiceProvider;
import com.pyramidframework.sdi.xml.XmlNode;

/**
 * 构造spriing的beanfactory的工厂类.可以使用默认的spring文档的解析器，也可以自己实现一个解析器并且组成使用。
 * 配置信息是只作用于單個實例的，因此很多時候需要將SpringFactory单实例化
 * 
 * @author Mikab Peng
 * 
 */
public class SpringFactory {

	// 默认的实例
	static SpringFactory defaultInstance = new SpringFactory();

	public static final String defaultType = "spring"; // 默认的配置信息的类型
	/** 配置文件默认存在的目录 */
	public static final String DEFAULT_CONFIG_LOCATION_PREFIX = "/config";

	// 不想改动CI的代码，只有加个这个东西来获取的初始的访问路径
	ThreadLocal currentPathStack = new ThreadLocal();

	/**
	 * 使用默认的增量式解析器构造的spring的beanfactory的工厂实例。 默认的增量式管理是使用{@link IncrementSpringConfigurationParser}进行文档的解析的
	 * 
	 * @param rootDirectory
	 *            开始存成配置文件的根目录
	 */
	public SpringFactory(String rootDirectory) {
		if (rootDirectory == null || "".equals(rootDirectory)) {
			throw new IllegalArgumentException("the rootDirectory parameter can not be null !");
		}

		manager.setParser(new IncrementSpringConfigurationParser(rootDirectory, this));
	}

	/**
	 * 使用默认的配置文件的路径 主要应该应用于WEB场合
	 */
	public SpringFactory() {
		this(DEFAULT_CONFIG_LOCATION_PREFIX);
	}

	/**
	 * 使用自定义的配置文件的解析器来定制自定义的工厂实现 可以参考的默认实现有{@link SpringConfigurationParser}和{@link IncrementSpringConfigurationParser}
	 * 
	 * @param configParser
	 *            ConfigDocumentParser的实现
	 */
	public SpringFactory(ConfigDocumentParser documentParser) {

		setParser(documentParser);
	}

	public void setParser(ConfigDocumentParser documentParser) {
		if (documentParser == null || "".equals(documentParser)) {
			throw new IllegalArgumentException("the documentParser parameter can not be null !");
		}

		manager.setParser(documentParser);
	}

	/**
	 * 得到指定的功能路径的beanfactory
	 * 
	 * @param functionPath
	 *            对应功能的功能路径
	 * @return InheritedBeanFactory的实例
	 */
	public AutowireCapableBeanFactory getBeanFactory(String functionPath) {
		return (InheritedBeanFactory) manager.getConfigData(functionPath);
	}

	/**
	 * 得到指定的功能路径的beanfactory，ruquest指定本次訪問的URL，并且對web相关的上下文进行初始化。 在web环境下应该使用此方法
	 * 
	 * @param functionPath
	 *            对应功能的功能路径
	 * @param request
	 *            本次访问的请求,用于初始化RequestContextHolder，为session和request范围做支持
	 * @return InheritedBeanFactory的实例
	 */
	public AutowireCapableBeanFactory getBeanFactory(String functionPath, HttpServletRequest request) {
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

		return getBeanFactory(functionPath);
	}

	/**
	 * 当前请求获得的功能路径是什么。 该函数只有在ConfigDocumentParser的实现里调用才有效，其他情况均返回NULL
	 * 
	 * @return 获取beanfactory的资源路径
	 */
	String getCurrentTargetPath() {
		ArrayList a = ((ArrayList) currentPathStack.get());
		return (String) a.get(a.size() - 1);
	}

	// 内部持有的管理器的实例
	TypedManager manager = new TypedManager(defaultType) {
		protected ConfigServiceProvider getDataProvider() {
			if (_providerInstance == null) {
				synchronized (this) {
					if (_providerInstance == null) {
						_providerInstance = new ConfigDamainTree(this) {
							protected Map getTypedContainer(String type) {
								return this.rootContainerMap;
							}

							/**
							 * 构建访问路径
							 */
							protected ConfigDomainImpl lookupAndConstructDomain(String functionPath, String configType, ConfigDocumentParser parser, String targetPath) {
								
								ArrayList a = ((ArrayList) currentPathStack.get());
								boolean init = false;
								
								if (a == null) {
									a = new ArrayList();
									currentPathStack.set(a);
									init = true;
								}
								
								try {	//只有真正构造一个新的节点数据时，才记住当前操作的路径
									if (functionPath != null && functionPath.equals(targetPath)){
										a.add(functionPath);
									}
									return super.lookupAndConstructDomain(functionPath, configType, parser, targetPath);
								} finally {
									if (functionPath != null && functionPath.equals(targetPath)){
										a.remove(a.size() - 1);
									}
									if (init){
										currentPathStack.set(null);
									}
								}
							}

							/**
							 * 当构建完成后需要判断那些引用和别名发生了变化，将其都放置到本域中
							 */
							public ConfigDomainImpl parseAndConstructDomain(ConfigDomainImpl domainImpl, String targetPath, XmlNode node, ConfigDocumentParser parser) {

								ConfigDomainImpl impl = super.parseAndConstructDomain(domainImpl, targetPath, node, parser);

								if (getCurrentTargetPath().equals(targetPath)) {
									InheritedBeanFactory factory = (InheritedBeanFactory) impl.getConfigData();

									if (factory != null) {
										factory.lookupRelatedBeanDefinitions();
									}
								}

								return impl;
							}
						};
					}
				}
			}

			return _providerInstance;
		}
	};

	public static SpringFactory getDefault() {
		return defaultInstance;
	}

	public static SpringFactory getDefaultInstance() {
		return defaultInstance;
	}

	public static void setDefault(SpringFactory defaultInstance) {
		SpringFactory.defaultInstance = defaultInstance;
	}

}
