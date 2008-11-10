package com.pyramidframework.spring;

import java.util.ArrayList;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.pyramidframework.ci.ConfigDocumentParser;
import com.pyramidframework.ci.ConfigDomain;
import com.pyramidframework.ci.TypedManager;

/**
 * 构造spriing的beanfactory的工厂类.可以使用默认的spring文档的解析器，也可以自己实现一个解析器并且组成使用。
 * 配置信息是只作用于單個實例的，因此很多時候需要將SpringFactory单实例化
 * 
 * @author Mikab Peng
 * 
 */
public class SpringFactory {

	public static String defaultType = "spring"; // 默认的配置信息的类型

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
	public BeanFactory getBeanFactory(String functionPath) {
		try {
			currentPathStack.set(new ArrayList());
			return (InheritedBeanFactory) manager.getConfigData(functionPath);
		} finally {
			currentPathStack.set(null);
		}
	}

	/** 配置文件默认存在的目录 */
	public static final String DEFAULT_CONFIG_LOCATION_PREFIX = "/config";

	/**
	 * 默认在request中所存放的位置
	 */
	public static String ROOT_REQUEST_CONTEXT_ATTRIBUTE = SpringFactory.class.getName() + ".ROOT";

	/**
	 * 必须使用ServletRequest使用的场合,只能在WEB项目中使用此方法。 适应于配置信息继承的应用程序的上下文。
	 * 其自动根据应用程序的serverlet的上下文获取对应的functionpath。 需要与{@link RequestContextHolder}配合,因此部署时需要{@link RequestContextListener)
	 * 
	 * @return
	 */
	public BeanFactory getRequestContextBeanFactory() {
		RequestAttributes attributes = RequestContextHolder.getRequestAttributes();

		InheritedBeanFactory context = (InheritedBeanFactory) attributes.getAttribute(ROOT_REQUEST_CONTEXT_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
		if (context == null) {
			context = createRequestContext(attributes);
		}
		return context;

	}

	/**
	 * 创建与request相关的上下文
	 * 
	 * @param attributes
	 * @return
	 */
	protected InheritedBeanFactory createRequestContext(RequestAttributes attributes) {
		InheritedBeanFactory context;
		// 必须使用这个场合
		ServletRequestAttributes servleRequest = (ServletRequestAttributes) attributes;
		String functionPath = servleRequest.getRequest().getRequestURI();

		context = (InheritedBeanFactory) getBeanFactory(functionPath);
		attributes.setAttribute(ROOT_REQUEST_CONTEXT_ATTRIBUTE, context, RequestAttributes.SCOPE_REQUEST);
		return context;
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

	// 内部只有的管理器的实例
	TypedManager manager = new TypedManager(defaultType) {
		public ConfigDomain getConfigDomain(String functionPath) {
			ArrayList stack = (ArrayList) currentPathStack.get();

			try {
				stack.add(functionPath);

				return super.getConfigDomain(functionPath);
			} finally {
				stack.remove(stack.size() - 1);
			}
		}

		public Object getConfigData(String functionPath) {
			ArrayList stack = (ArrayList) currentPathStack.get();

			try {
				stack.add(functionPath);

				return super.getConfigData(functionPath);
			} finally {
				stack.remove(stack.size() - 1);
			}
		}

		public Object getConfigData(String functionPath, String configType) {
			ArrayList stack = (ArrayList) currentPathStack.get();

			try {
				stack.add(functionPath);

				return super.getConfigData(functionPath, configType);
			} finally {
				stack.remove(stack.size() - 1);
			}
		}

		public ConfigDomain getConfigDomain(String functionPath, String configType) {
			ArrayList stack = (ArrayList) currentPathStack.get();

			try {
				stack.add(functionPath);

				return super.getConfigDomain(functionPath, configType);
			} finally {
				stack.remove(stack.size() - 1);
			}
		}
	};
}
