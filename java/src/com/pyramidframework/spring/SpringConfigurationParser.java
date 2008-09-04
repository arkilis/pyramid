package com.pyramidframework.spring;

import java.util.Map;

import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.FileSystemResource;

import com.pyramidframework.ci.ConfigDocumentParser;
import com.pyramidframework.ci.ConfigDomain;
import com.pyramidframework.sdi.xml.XmlDocument;

/**
 * ʹ��ȫ��ʽ�ļ������ĳ���
 * ��ʼ�����ļ�����ȫ����spring��Ҏ�t���F�ģ�����������Ϣ�������ȫ����xml Inheritance�ĸ�ʽ��ʵ�֡�
 * 
 * @author Mikab Peng
 *
 */
public class SpringConfigurationParser implements ConfigDocumentParser {

	protected String rootDirectory = null;//�����ļ���ʼ��ŵ�Ŀ¼
	
	/**
	 * ��Ҫָ�����ĸ������ļ���Ŀ¼
	 * @param rootDirectory ��������ļ��ĸ�Ŀ¼
	 */
	public SpringConfigurationParser(String rootDirectory) {
		// ȥ�������Ǹ�/
		while (rootDirectory.endsWith("/")) {
			rootDirectory = rootDirectory.substring(0, rootDirectory.length() - 1);
		}
		this.rootDirectory = rootDirectory;
	}

	/**
	 * �����ļ�,���ҵõ�ȫ����bean�Ķ���
	 */
	public Object parseConfigDocument(ConfigDomain thisDomain, XmlDocument configDocument) {
		Object object = thisDomain.getConfigData();
		if (object == null) {
			object = new InheritedBeanFactory(null);
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
	 * DO Nothing
	 */
	public void InitTemplateContext(Map templateContext) {

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