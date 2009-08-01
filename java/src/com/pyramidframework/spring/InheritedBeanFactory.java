package com.pyramidframework.spring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanReference;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
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

	String functionPath = null;

	public String getFunctionPath() {
		return functionPath;
	}

	void setFunctionPath(String functionPath) {
		this.functionPath = functionPath;
	}

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
		registerScope("process", new ProcessScope());

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
	 */
	public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeanDefinitionStoreException {
		BeanDefinition definition = null;

		try {
			definition = getBeanDefinition(beanName);
		} catch (NoSuchBeanDefinitionException e) {
			definition = null;
		}

		if (!BeanDefinitionsEqual(beanDefinition, definition)) {
			super.registerBeanDefinition(beanName, beanDefinition);
			registerParentAclias(beanName);
		}
	}

	/**
	 * ��Ҫ�ж�������������Ե�ֵ�ǲ���һ��
	 * 
	 * @param bd1
	 * @param bd2
	 * @return
	 */
	protected boolean BeanDefinitionsEqual(BeanDefinition bd1, BeanDefinition bd2) {
		if (bd1.equals(bd2)) {

			PropertyValue[] pvs1 = bd1.getPropertyValues().getPropertyValues();
			PropertyValue[] pvs2 = bd2.getPropertyValues().getPropertyValues();

			for (int i = 0; i < pvs1.length; i++) {
				if (pvs1[i].getValue() == null && pvs2[i].getValue() != null) {
					return false;
				}
				if (!pvs1[i].getValue().equals(pvs2[i].getValue())) {
					return false;
				}
			}

			return true;
		}
		return false;
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

	/**
	 * Ѱ�ҵ��ܵ�����������Ӱ���bean��������BeanDefinition��alias���Ƶ���������
	 */
	void lookupRelatedBeanDefinitions() {
		String[] selfBeans = super.getBeanDefinitionNames();
		ArrayList beans = new ArrayList();
		beans.addAll(Arrays.asList(selfBeans));
		for (int i = 0; i < beans.size(); i++) {
			String beanName = (String) beans.get(i);
			ArrayList list = getParentRelatedBeanDefinitions(beanName);
			for (int j = 0; j < list.size(); j++) {
				String name = (String) list.get(j);
				if (!containsBeanDefinition(name)) {
					super.registerBeanDefinition(name, getBeanDefinition(name));

					beans.add(name);
					registerParentAclias(name);
				}
			}
		}
	}

	/**
	 * @param name
	 */
	protected void registerParentAclias(String name) {
		if (getParentBeanFactory() != null) {
			String alias[] = getParentBeanFactory().getAliases(name);
			for (int k = 0; k < alias.length; k++) {
				if (!isAlias(alias[k])) {
					registerAlias(name, alias[k]);
				}
			}
		}
	}

	/**
	 * �õ��������ڴ��ڵ���ص�bean�Ķ���
	 * 
	 * @param name
	 * @return
	 */
	ArrayList getParentRelatedBeanDefinitions(String name) {
		ArrayList relatedBeans = new ArrayList();
		BeanFactory parentFactory = getParentBeanFactory();
		if (parentFactory != null && parentFactory instanceof BeanDefinitionRegistry) {
			BeanDefinitionRegistry re = (BeanDefinitionRegistry) parentFactory;
			String beannames[] = re.getBeanDefinitionNames();

			for (int i = 0; i < beannames.length; i++) {
				List alias = Arrays.asList(parentFactory.getAliases(beannames[i]));

				// �����beanreference������Ϊ��ͬ
				BeanDefinition definition = re.getBeanDefinition(beannames[i]);
				MutablePropertyValues values = definition.getPropertyValues();
				PropertyValue[] pvs = values.getPropertyValues();
				for (int j = 0; j < pvs.length; j++) {
					if (pvs[j].getValue() instanceof BeanReference) {
						String beanName = ((BeanReference) pvs[j].getValue()).getBeanName();
						if (name.equals(beanName) || alias.contains(beanName)) {
							relatedBeans.add(beannames[i]);
							break;
						}
					}
				}

			}
			if (parentFactory instanceof InheritedBeanFactory) {
				List pList = ((InheritedBeanFactory) parentFactory).getParentRelatedBeanDefinitions(name);
				relatedBeans.addAll(pList);
			}
		}
		return relatedBeans;
	}
}
