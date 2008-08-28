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
	 * 重载掉默认的命名空间和前缀
	 */
	protected Namespace createDefaultNamespace() {

		return tre.getNamespace();
	}

	/**
	 * 由于实现机制的限制，只支持两种reference：new和resource，因為其parent和child在增量式解析時不可直接獲得
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
