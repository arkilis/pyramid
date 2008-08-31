package com.pyramidframework.ci.impl;

import org.dom4j.Namespace;

import com.pyramidframework.ci.ConfigurationManager;
import com.pyramidframework.sdi.SDIException;
import com.pyramidframework.sdi.xml.XmlConvter;
import com.pyramidframework.sdi.xml.XmlDocument;
import com.pyramidframework.sdi.xml.XmlInhertance;
import com.pyramidframework.sdi.xml.reference.NodeReference;

public class ConfigurationInheritance extends XmlInhertance {

	public static final String DEFAULT_NAMESPACE_URI = ConfigurationManager.DEFAULT_NAMESPACE_URI;
	public static final String DEFAULT_NAMESPACE_PREFIX = ConfigurationManager.DEFAULT_NAMESPACE_PREFIX;
	ConfigDamainTree tre = null;

	public ConfigurationInheritance(ConfigDamainTree tree) {
		super();
		tre = tree;
	}

	/**
	 * 重载掉默认的命名空间和前缀
	 */
	protected Namespace createDefaultNamespace() {

		return tre.getNamespace();
	}

	/**
	 * @param document
	 */
	protected void checkIfNeedUseNamespace(XmlDocument document) {
		// 判断是否需要默认命名空间
		if (!useNamespace) {
			useNamespace = DEFAULT_NAMESPACE_URI.equals(document.getNamespaces().get(DEFAULT_NAMESPACE_PREFIX));
		}
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
