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
 * ʹ��ȫ��ʽ�ļ������ĳ��� ��ʼ�����ļ�����ȫ����spring��Ҏ�t���F�ģ�����������Ϣ�������ȫ����xml Inheritance�ĸ�ʽ��ʵ�֡�
 * 
 * @author Mikab Peng
 * 
 */
public class SpringConfigurationParser implements ConfigDocumentParser {

	protected String rootDirectory = null;// �����ļ���ʼ��ŵ�Ŀ¼
	protected SpringFactory factoryInstance = null;

	/**
	 * ��Ҫָ�����ĸ������ļ���Ŀ¼
	 * 
	 * @param rootDirectory
	 *            ��������ļ��ĸ�Ŀ¼
	 * @param instance
	 *            SpringFactory��ʵ��
	 */
	public SpringConfigurationParser(String rootDirectory, SpringFactory instance) {
		// ȥ�������Ǹ�/
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
	 * �����ļ�,���ҵõ�ȫ����bean�Ķ���
	 */
	public Object parseConfigDocument(ConfigDomain domain, XmlDocument configDocument) {
		Object object = domain.getConfigData();
		if (object == null) {
			object = createDomainFactory(null, domain);
		}

		InheritedBeanFactory factory = (InheritedBeanFactory) object;

		// װ������
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
		try {
			reader.registerBeanDefinitions(configDocument.getDomDocument(), new FileSystemResource(rootDirectory));
			// ���е�import����Ϊ����·��
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return object;
	}

	/**
	 * ����ָ�����beanfactory��ʵ��
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

			// parentDomain������NULL
			if (parentDomain != null) {
				beanFactory = (BeanFactory) parentDomain.getConfigData();
			}
			object = new InheritedBeanFactory(directFactory, beanFactory);
		} else {

			// ���͵�ǰ���ʵ���Դ·����һ����parentPathΪNULL����Ҫ��ȡͬ���͵�������Ϣ,��Ҫ��������ȡ��parentFactory
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
		templateContext.put("classUtil", SpringClassUtil.getInstance());//���ڽű��м���ǲ���ĳЩ�����
	}

	/**
	 * �����ļ���������ÿ��Ŀ¼�µ�spring.xml
	 */
	public String getConfigFileName(String functionPath, String configType) {
		if (functionPath == null) {
			functionPath = "/";
		}
		return rootDirectory + functionPath + configType + ".xml";
	}

}