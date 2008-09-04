package com.pyramidframework.spring;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import com.pyramidframework.ci.ConfigDocumentParser;
import com.pyramidframework.ci.TypedManager;

/**
 * ����spriing��beanfactory�Ĺ�����.����ʹ��Ĭ�ϵ�spring�ĵ��Ľ�������Ҳ�����Լ�ʵ��һ���������������ʹ�á�
 * ������Ϣ��ֻ�����چ΂������ģ���˺ܶ��r����Ҫ��SpringFactory��ʵ����
 * 
 * @author Mikab Peng
 * 
 */
public class SpringFactory {
	TypedManager manager = null;	//�ڲ�ֻ�еĹ�������ʵ��
	private ConfigDocumentParser parser = null;	//ʵ�����ĵ�������
	public static String defaultType = "spring";	//Ĭ�ϵ�������Ϣ������
	
	
	/**
	 * ʹ��Ĭ�ϵ�����ʽ�����������spring��beanfactory�Ĺ���ʵ����
	 * Ĭ�ϵ�����ʽ������ʹ��{@link IncrementSpringConfigurationParser}�����ĵ��Ľ�����
	 * @param rootDirectory ��ʼ��������ļ��ĸ�Ŀ¼
	 */
	public SpringFactory(String rootDirectory) {
		if (rootDirectory == null || "".equals(rootDirectory)){
			throw new IllegalArgumentException("the rootDirectory parameter can not be null !");
		}
		
		parser = new IncrementSpringConfigurationParser(rootDirectory);
	}
	
	/**
	 * ʹ���Զ���������ļ��Ľ������������Զ���Ĺ���ʵ��
	 * ���Բο���Ĭ��ʵ����{@link SpringConfigurationParser}��{@link IncrementSpringConfigurationParser}
	 * @param configParser ConfigDocumentParser��ʵ��
	 */
	public SpringFactory(ConfigDocumentParser configParser) {
		
		if (configParser == null || "".equals(configParser)){
			throw new IllegalArgumentException("the configParser parameter can not be null !");
		}
		
		
		parser = configParser;
	}
	
	
	/**
	 * �õ�ָ���Ĺ���·����beanfactory
	 * @param functionPath ��Ӧ���ܵĹ���·��
	 * @return InheritedBeanFactory��ʵ��
	 */
	public DefaultListableBeanFactory getBeanFactory(String functionPath) {
		if (manager == null) {
			createConfigManager();
		}
		return (InheritedBeanFactory) manager.getConfigData(functionPath);
	}

	/**
	 * ������Ļ��������Ϣ������
	 */
	protected void createConfigManager() {
		manager = new TypedManager(defaultType,parser);
	}
}
