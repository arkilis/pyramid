package com.pyramidframework.ci.impl;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Namespace;

import com.pyramidframework.ci.ConfigDomain;

public class ConfigDomainImpl extends ConfigDomain {
	final static ConfigDomainImpl NULL_CONFIG = new ConfigDomainImpl(null, null);// 用来保存没有配置的数据

	Namespace namespace = null;
	ConfigDomainImpl parentNode = null;
	List children = new ArrayList();

	public String getParentPath() {
		if (this.parentNode == null) {
			return null;
		}
		return parentNode.getTargetPath();
	}

	public void setParentPath(ConfigDomainImpl parentNode) {

		if (this.parentNode != null) {
			List parent = ((ConfigDomainImpl) getParent()).children;
			if (parent.contains(this)) {
				parent.remove(this);
			}
		}
		if (parentNode != null) {
			parentNode.children.add(this);
		}
		this.parentNode = parentNode;
	}

	public List getChildren() {
		return children;
	}

	public ConfigDomain getParent() {
		if (this.parentNode == null) {
			return null;
		}
		return parentNode;
	}

	ConfigDomainImpl(String functionPath, String configType) {
		this.configType = configType;
		this.targetPath = functionPath;
	}

	void setConfigData(Object data) {

		this.configData = data;

	}

}
