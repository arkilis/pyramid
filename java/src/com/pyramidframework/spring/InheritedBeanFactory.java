package com.pyramidframework.spring;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

/**
 * 每个功能路径的bean Factory的实现。
 * 由于模板的缘故，不通过parentBeanFactory的方式来连接顶层的定义，因此在本类保存其所有的全部定义。
 * 
 * @author Mikab Peng
 */
public class InheritedBeanFactory extends DefaultListableBeanFactory {

	/**
	 * 从上级的BeanFactory进行初始化。将上级的全部的BeanDefinition全部拷贝到本类中。
	 * 只拷贝BeanDefinitions和bean Alias，其他的配置信息
	 * 可以使用{@link DefaultListableBeanFactory.copyConfigurationFrom}方法手工进行复制。
	 * 
	 * @param parent
	 *            代入了实际模板的上级路径的BeanFactory实例
	 */
	public InheritedBeanFactory(BeanDefinitionRegistry parent) {
		if (parent != null) {
			// 拷贝beanDefinition
			String beanNames[] = parent.getBeanDefinitionNames();
			for (int i = 0; i < beanNames.length; i++) {
				registerBeanDefinition(beanNames[i], parent.getBeanDefinition(beanNames[i]));

				// //拷贝beanAlias
				String beanAlias[] = parent.getAliases(beanNames[i]);
				for (int j = 0; j < beanAlias.length; j++) {
					registerAlias(beanNames[i], beanAlias[j]);
				}
			}
		}
		
		//拷贝其他的配置信息
		if (parent instanceof ConfigurableBeanFactory){
			copyConfigurationFrom((ConfigurableBeanFactory)parent);
		}
	}
	
	

}
