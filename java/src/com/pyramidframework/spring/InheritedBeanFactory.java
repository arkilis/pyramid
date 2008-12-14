package com.pyramidframework.spring;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.web.context.request.RequestScope;
import org.springframework.web.context.request.SessionScope;

/**
 * ÿ������·����bean Factory��ʵ�֡�
 * ����ģ���Ե�ʣ���ͨ��parentBeanFactory�ķ�ʽ�����Ӷ���Ķ��壬����ڱ��ౣ�������е�ȫ�����塣
 * 
 * @author Mikab Peng
 */
class InheritedBeanFactory extends DefaultListableBeanFactory {

	ThreadLocal rootBeanFactory = new ThreadLocal();

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

		registerScope("request", new RequestScope());
		registerScope("session", new SessionScope(false));
		registerScope("globalSession", new SessionScope(true));

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
	 * ����ϼ�����һ���ģ�����ע�� TODO:����Ǳ���ǿ�����õģ�����Ϊ���µ�����
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

	public Object getBean(String name, Class requiredType, Object[] args) throws BeansException {
		boolean startAtThis = false;
		try {
			if (rootBeanFactory.get() == null) {
				rootBeanFactory.set(this);
				startAtThis = true;
			}
			return super.getBean(name, requiredType, args);
		} finally {
			if (startAtThis) {
				rootBeanFactory.set(null);
			}
		}
	}

	/**
	 * ȷ�����ʼ��ʼ��ʼ��
	 */
	protected void populateBean(String beanName, AbstractBeanDefinition mbd, BeanWrapper bw) {
		InheritedBeanFactory rootFactory = (InheritedBeanFactory) rootBeanFactory.get();
		if (this == rootFactory) {
			super.populateBean(beanName, mbd, bw);
		} else {
			rootFactory.populateBean(beanName, mbd, bw);
		}
	}
}
