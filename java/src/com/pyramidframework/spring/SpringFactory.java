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
 * ����spriing��beanfactory�Ĺ�����.����ʹ��Ĭ�ϵ�spring�ĵ��Ľ�������Ҳ�����Լ�ʵ��һ���������������ʹ�á�
 * ������Ϣ��ֻ�����چ΂������ģ���˺ܶ��r����Ҫ��SpringFactory��ʵ����
 * 
 * @author Mikab Peng
 * 
 */
public class SpringFactory {

	// Ĭ�ϵ�ʵ��
	static SpringFactory defaultInstance = new SpringFactory();

	public static final String defaultType = "spring"; // Ĭ�ϵ�������Ϣ������
	/** �����ļ�Ĭ�ϴ��ڵ�Ŀ¼ */
	public static final String DEFAULT_CONFIG_LOCATION_PREFIX = "/config";

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
	public AutowireCapableBeanFactory getBeanFactory(String functionPath) {
		return (InheritedBeanFactory) manager.getConfigData(functionPath);
	}

	/**
	 * �õ�ָ���Ĺ���·����beanfactory��ruquestָ�������L����URL�����Ҍ�web��ص������Ľ��г�ʼ���� ��web������Ӧ��ʹ�ô˷���
	 * 
	 * @param functionPath
	 *            ��Ӧ���ܵĹ���·��
	 * @param request
	 *            ���η��ʵ�����,���ڳ�ʼ��RequestContextHolder��Ϊsession��request��Χ��֧��
	 * @return InheritedBeanFactory��ʵ��
	 */
	public AutowireCapableBeanFactory getBeanFactory(String functionPath, HttpServletRequest request) {
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

		return getBeanFactory(functionPath);
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

	// �ڲ����еĹ�������ʵ��
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
							 * ��������·��
							 */
							protected ConfigDomainImpl lookupAndConstructDomain(String functionPath, String configType, ConfigDocumentParser parser, String targetPath) {
								
								ArrayList a = ((ArrayList) currentPathStack.get());
								boolean init = false;
								
								if (a == null) {
									a = new ArrayList();
									currentPathStack.set(a);
									init = true;
								}
								
								try {	//ֻ����������һ���µĽڵ�����ʱ���ż�ס��ǰ������·��
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
							 * ��������ɺ���Ҫ�ж���Щ���úͱ��������˱仯�����䶼���õ�������
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
