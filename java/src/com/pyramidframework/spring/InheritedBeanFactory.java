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
 * 每个功能路径的bean Factory的实现。
 * 由于模板的缘故，不通过parentBeanFactory的方式来连接顶层的定义，因此在本类保存其所有的全部定义。
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
	 *            从模板解析而的来的定义
	 * @param parentFactory
	 *            从配置信息树得到父级
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

		// 拷贝其他的配置信息
		if (directParent instanceof ConfigurableBeanFactory) {
			copyConfigurationFrom((ConfigurableBeanFactory) directParent);
		}

		// 拷贝bean定义
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
	 * 如果包含则不再注册
	 */
	public void registerAlias(String name, String alias) {
		String aliases[] = getAliases(name);

		if (aliases != null) {
			for (int i = 0; i < aliases.length; i++) {
				if (aliases[i].equals(alias)) {// 找到就不再注册
					return;
				}
			}
		}

		super.registerAlias(name, alias);
	}

	/**
	 * 如果上级包含一样的，则不再注册
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
	 * 需要判断两个对象的属性的值是不是一样
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
	 * 找到本级查找不到时到父工厂查找
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
			if (parentFactory != null) {// 如果本级没找到，并且parentFActory也不为NULL的话
				InheritedBeanFactory parentInherited = (InheritedBeanFactory) parentFactory;
				return parentInherited.getBeanDefinition(beanName);
			}
			throw new NoSuchBeanDefinitionException(beanName);
		} else {
			return definition;
		}
	}

	/**
	 * 寻找到受到本工厂配置影响的bean，并将其BeanDefinition和alias复制到本工厂内
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
	 * 得到父工厂内存在的相关的bean的定义
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

				// 如果有beanreference，则认为相同
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
