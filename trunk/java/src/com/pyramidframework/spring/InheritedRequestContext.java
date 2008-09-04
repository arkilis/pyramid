package com.pyramidframework.spring;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * ��Ӧ��������Ϣ�̳е�Ӧ�ó���������ġ� ���Զ�����Ӧ�ó����serverlet�������Ļ�ȡ��Ӧ��functionpath��
 * ��Ҫ��{@link RequestContextHolder}���,��˲���ʱ��Ҫ{@link RequestContextListener)
 * 
 * @author Mikab Peng
 * 
 */
public class InheritedRequestContext extends InheritedBeanFactory {

	/** �����ļ�Ĭ�ϴ��ڵ�Ŀ¼ */
	public static final String DEFAULT_CONFIG_LOCATION_PREFIX = "/WEB-INF/springconfig";

	/**
	 * Ĭ����request������ŵ�λ��
	 */
	public static String ROOT_REQUEST_CONTEXT_ATTRIBUTE = InheritedRequestContext.class.getName() + ".ROOT";

	public static SpringFactory contextFactory = new SpringFactory(DEFAULT_CONFIG_LOCATION_PREFIX);
	
	
	/**
	 * �ڲ�ʹ�õĹ��캯��
	 * @param registry
	 */
	protected InheritedRequestContext(BeanDefinitionRegistry registry) {
		super(registry);
	}

	/**
	 * ����ʹ��ServletRequestʹ�õĳ���
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
	 * ������request��ص�������
	 * @param attributes
	 * @return
	 */
	protected static InheritedRequestContext createRequestContext(RequestAttributes attributes) {
		InheritedRequestContext context;
		// ����ʹ���������
		ServletRequestAttributes servleRequest = (ServletRequestAttributes) attributes;
		String functionPath = servleRequest.getRequest().getRequestURI();

		context = new InheritedRequestContext(contextFactory.getBeanFactory(functionPath));
		attributes.setAttribute(ROOT_REQUEST_CONTEXT_ATTRIBUTE,context,RequestAttributes.SCOPE_REQUEST);
		return context;
	}
}
