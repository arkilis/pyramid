package com.pyramidframework.ci.impl;

import org.dom4j.Namespace;

import com.pyramidframework.sdi.SDIException;
import com.pyramidframework.sdi.xml.XmlConvter;
import com.pyramidframework.sdi.xml.XmlInhertance;
import com.pyramidframework.sdi.xml.reference.NodeReference;

public class ConfigurationInheritance extends XmlInhertance {
	ConfigDamainTree tre = null;

	public ConfigurationInheritance(ConfigDamainTree tree) {
		tre = tree;
	}

	/**
	 * ���ص�Ĭ�ϵ������ռ��ǰ׺
	 */
	protected Namespace createDefaultNamespace() {

		return tre.getNamespace();
	}

	/**
	 * ����ʵ�ֻ��Ƶ����ƣ�ֻ֧������reference��new��resource�������parent��child������ʽ�����r����ֱ�ӫ@��
	 * 
	 * @return
	 */
	public XmlConvter getIncrementInheritanceConvter() {

		XmlConvter convter = new XmlConvter(this) {
			protected NodeReference createValueReference(String referType) throws SDIException {
				if ("child".equalsIgnoreCase(referType) || "parent".equalsIgnoreCase(referType)) {
					throw new IllegalArgumentException("You can not use child or parent reference in Increment inheritance!");
				}
				return super.createValueReference(referType);
			}
		};
		return convter;
	}

}
