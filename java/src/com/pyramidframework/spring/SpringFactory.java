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
 * ����spriing��beanfactory�Ĺ�����.����ʹ��Ĭ�ϵ�spring�ĵ��Ľ�������Ҳ�����Լ�ʵ��һ���������������ʹ�á�
 * ������Ϣ��ֻ�����چ΂������ģ���˺ܶ��r����Ҫ��SpringFactory��ʵ����
 * 
 * @author Mikab Peng
 * 
 */
public class SpringFactory {

	public static String defaultType = "spring"; // Ĭ�ϵ�������Ϣ������

	// ����Ķ�CI�Ĵ��룬ֻ�мӸ������������ȡ�ĳ�ʼ�ķ���·��
	ThreadLocal currentPathStack = new ThreadLocal();

	/**
	 * ʹ��Ĭ�ϵ�����ʽ�����������spring��beanfactory�Ĺ���ʵ���� Ĭ�ϵ�����ʽ������ʹ��{@link IncrementSpringConfigurationParser}�����ĵ��Ľ�����
	 * 
	 * @param rootDirectory
	 *            ��ʼ��������ļ��ĸ�Ŀ¼
	 */
	public SpringFactory(String rootDirectory) {
		if (rootDirectory == null || "".equals(rootDirectory)) {
			throw new IllegalArgumentException("the rootDirectory parameter can not be null !");
		}

		manager.setParser(new IncrementSpringConfigurationParser(rootDirectory, this));
	}

	/**
	 * ʹ��Ĭ�ϵ������ļ���·�� ��ҪӦ��Ӧ����WEB����
	 */
	public SpringFactory() {
		this(DEFAULT_CONFIG_LOCATION_PREFIX);
	}

	/**
	 * ʹ���Զ���������ļ��Ľ������������Զ���Ĺ���ʵ�� ���Բο���Ĭ��ʵ����{@link SpringConfigurationParser}��{@link IncrementSpringConfigurationParser}
	 * 
	 * @param configParser
	 *            ConfigDocumentParser��ʵ��
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
	 * �õ�ָ���Ĺ���·����beanfactory
	 * 
	 * @param functionPath
	 *            ��Ӧ���ܵĹ���·��
	 * @return InheritedBeanFactory��ʵ��
	 */
	public BeanFactory getBeanFactory(String functionPath) {
		try {
			currentPathStack.set(new ArrayList());
			return (InheritedBeanFactory) manager.getConfigData(functionPath);
		} finally {
			currentPathStack.set(null);
		}
	}

	/** �����ļ�Ĭ�ϴ��ڵ�Ŀ¼ */
	public static final String DEFAULT_CONFIG_LOCATION_PREFIX = "/config";

	/**
	 * Ĭ����request������ŵ�λ��
	 */
	public static String ROOT_REQUEST_CONTEXT_ATTRIBUTE = SpringFactory.class.getName() + ".ROOT";

	/**
	 * ����ʹ��ServletRequestʹ�õĳ���,ֻ����WEB��Ŀ��ʹ�ô˷����� ��Ӧ��������Ϣ�̳е�Ӧ�ó���������ġ�
	 * ���Զ�����Ӧ�ó����serverlet�������Ļ�ȡ��Ӧ��functionpath�� ��Ҫ��{@link RequestContextHolder}���,��˲���ʱ��Ҫ{@link RequestContextListener)
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
	 * ������request��ص�������
	 * 
	 * @param attributes
	 * @return
	 */
	protected InheritedBeanFactory createRequestContext(RequestAttributes attributes) {
		InheritedBeanFactory context;
		// ����ʹ���������
		ServletRequestAttributes servleRequest = (ServletRequestAttributes) attributes;
		String functionPath = servleRequest.getRequest().getRequestURI();

		context = (InheritedBeanFactory) getBeanFactory(functionPath);
		attributes.setAttribute(ROOT_REQUEST_CONTEXT_ATTRIBUTE, context, RequestAttributes.SCOPE_REQUEST);
		return context;
	}

	/**
	 * ��ǰ�����õĹ���·����ʲô�� �ú���ֻ����ConfigDocumentParser��ʵ������ò���Ч���������������NULL
	 * 
	 * @return ��ȡbeanfactory����Դ·��
	 */
	String getCurrentTargetPath() {
		ArrayList a = ((ArrayList) currentPathStack.get());
		return (String) a.get(a.size() - 1);
	}

	// �ڲ�ֻ�еĹ�������ʵ��
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
