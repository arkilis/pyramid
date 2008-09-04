package com.pyramidframework.spring;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

/**
 * ÿ������·����bean Factory��ʵ�֡�
 * ����ģ���Ե�ʣ���ͨ��parentBeanFactory�ķ�ʽ�����Ӷ���Ķ��壬����ڱ��ౣ�������е�ȫ�����塣
 * 
 * @author Mikab Peng
 */
public class InheritedBeanFactory extends DefaultListableBeanFactory {

	/**
	 * ���ϼ���BeanFactory���г�ʼ�������ϼ���ȫ����BeanDefinitionȫ�������������С�
	 * ֻ����BeanDefinitions��bean Alias��������������Ϣ
	 * ����ʹ��{@link DefaultListableBeanFactory.copyConfigurationFrom}�����ֹ����и��ơ�
	 * 
	 * @param parent
	 *            ������ʵ��ģ����ϼ�·����BeanFactoryʵ��
	 */
	public InheritedBeanFactory(BeanDefinitionRegistry parent) {
		if (parent != null) {
			// ����beanDefinition
			String beanNames[] = parent.getBeanDefinitionNames();
			for (int i = 0; i < beanNames.length; i++) {
				registerBeanDefinition(beanNames[i], parent.getBeanDefinition(beanNames[i]));

				// //����beanAlias
				String beanAlias[] = parent.getAliases(beanNames[i]);
				for (int j = 0; j < beanAlias.length; j++) {
					registerAlias(beanNames[i], beanAlias[j]);
				}
			}
		}
		
		//����������������Ϣ
		if (parent instanceof ConfigurableBeanFactory){
			copyConfigurationFrom((ConfigurableBeanFactory)parent);
		}
	}
	
	

}
