package com.pyramidframework.spring;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 适应于配置信息继承的应用程序的上下文。 其自动根据应用程序的serverlet的上下文获取对应的functionpath。
 * 需要与{@link RequestContextHolder}配合,因此部署时需要{@link RequestContextListener)
 * 
 * @author Mikab Peng
 * 
 */
public class InheritedRequestContext extends InheritedBeanFactory {

	/** 配置文件默认存在的目录 */
	public static final String DEFAULT_CONFIG_LOCATION_PREFIX = "/WEB-INF/springconfig";

	/**
	 * 默认在request中所存放的位置
	 */
	public static String ROOT_REQUEST_CONTEXT_ATTRIBUTE = InheritedRequestContext.class.getName() + ".ROOT";

	public static SpringFactory contextFactory = new SpringFactory(DEFAULT_CONFIG_LOCATION_PREFIX);
	
	
	/**
	 * 内部使用的构造函数
	 * @param registry
	 */
	protected InheritedRequestContext(BeanDefinitionRegistry registry) {
		super(registry);
	}

	/**
	 * 必须使用ServletRequest使用的场合
	 * 
	 * @return
	 */
	public static InheritedRequestContext getRequestContext() {
		RequestAttributes attributes = RequestContextHolder.getRequestAttributes();

		InheritedRequestContext context = (InheritedRequestContext) attributes.getAttribute(ROOT_REQUEST_CONTEXT_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
		if (context == null) {
			context = createRequestContext(attributes);
		}
		return context;

	}

	/**
	 * 创建与request相关的上下文
	 * @param attributes
	 * @return
	 */
	protected static InheritedRequestContext createRequestContext(RequestAttributes attributes) {
		InheritedRequestContext context;
		// 必须使用这个场合
		ServletRequestAttributes servleRequest = (ServletRequestAttributes) attributes;
		String functionPath = servleRequest.getRequest().getRequestURI();

		context = new InheritedRequestContext(contextFactory.getBeanFactory(functionPath));
		attributes.setAttribute(ROOT_REQUEST_CONTEXT_ATTRIBUTE,context,RequestAttributes.SCOPE_REQUEST);
		return context;
	}
}
