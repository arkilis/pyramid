package com.pyramidframework.spring;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

/**
 * ÿ������·����bean Factory��ʵ�֡�
 * ����ģ���Ե�ʣ���ͨ��parentBeanFactory�ķ�ʽ�����Ӷ���Ķ��壬����ڱ��ౣ�������е�ȫ�����塣
 * 
 * @author Mikab Peng
 */
class InheritedBeanFactory extends DefaultListableBeanFactory {

	/**
	 * 
	 * @param directParent
	 *            ��ģ������������Ķ���
	 * @param parentFactory
	 *            ��������Ϣ���õ�����
	 */
	public InheritedBeanFactory(BeanDefinitionRegistry directParent, BeanFactory parentFactory) {
		if (parentFactory != null) {
			setParentBeanFactory(parentFactory);
		}

		if (directParent == null) {
			return;
		}

		// ����������������Ϣ
		if (directParent instanceof ConfigurableBeanFactory) {
			copyConfigurationFrom((ConfigurableBeanFactory) directParent);
		}

		// ����bean����
		String[] beans = directParent.getBeanDefinitionNames();
		for (int i = 0; i < beans.length; i++) {
			registerBeanDefinition(beans[i], directParent.getBeanDefinition(beans[i]));

			String[] alias = directParent.getAliases(beans[i]);
			if (alias != null) {
				for (int j = 0; j < alias.length; j++) {
					registerAlias(beans[i], alias[j]);
				}
			}
		}
	}

	/**
	 * �����������ע��
	 */
	public void registerAlias(String name, String alias) {
		String aliases[] = getAliases(name);

		if (aliases != null) {
			for (int i = 0; i < aliases.length; i++) {
				if (aliases[i].equals(alias)) {// �ҵ��Ͳ���ע��
					return;
				}
			}
		}

		super.registerAlias(name, alias);
	}

	/**
	 * ����ϼ�����һ���ģ�����ע��
	 * TODO:����Ǳ���ǿ�����õģ�����Ϊ���µ�����
	 */
	public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeanDefinitionStoreException {
		BeanDefinition definition = null;
		try {
			definition = getBeanDefinition(beanName);
		} catch (NoSuchBeanDefinitionException e) {
			definition = null;
		}

		if (!beanDefinition.equals(definition)) {
			super.registerBeanDefinition(beanName, beanDefinition);
		}
	}

	/**
	 * �ҵ��������Ҳ���ʱ������������
	 */
	public BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
		BeanDefinition definition = null;

		try {
			definition = super.getBeanDefinition(beanName);
		} catch (NoSuchBeanDefinitionException e) {
			// DO NOTHING
			definition = null;
		}

		if (definition == null) {
			BeanFactory parentFactory = getParentBeanFactory();
			if (parentFactory != null) {// �������û�ҵ�������parentFActoryҲ��ΪNULL�Ļ�
				InheritedBeanFactory parentInherited = (InheritedBeanFactory) parentFactory;
				return parentInherited.getBeanDefinition(beanName);
			}
			throw new NoSuchBeanDefinitionException(beanName);
		} else {
			return definition;
		}
	}

}
