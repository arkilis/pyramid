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
 * 每个功能路径的bean Factory的实现。
 * 由于模板的缘故，不通过parentBeanFactory的方式来连接顶层的定义，因此在本类保存其所有的全部定义。
 * 
 * @author Mikab Peng
 */
class InheritedBeanFactory extends DefaultListableBeanFactory {

	ThreadLocal rootBeanFactory = new ThreadLocal();

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
	 * 如果上级包含一样的，则不再注册 TODO:如果是本级强行配置的，则认为是新的配置
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
	 * 确保在最开始开始初始化
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
