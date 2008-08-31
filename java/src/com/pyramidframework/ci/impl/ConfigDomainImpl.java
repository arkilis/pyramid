package com.pyramidframework.ci.impl;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Namespace;

import com.pyramidframework.ci.ConfigDomain;

public class ConfigDomainImpl extends ConfigDomain {
	
	Namespace namespace = null;
	ConfigDomainImpl parentNode = null;
	List children = new ArrayList();
	String parentPath = null;

	public String getParentPath() {
		return parentPath;
	}

	public void setParentNode(ConfigDomainImpl parentNode) {

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

	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
	}

	public String toString() {
		return super.toString() + "[configType=" + configType + "|targetPath=" + targetPath + "|configData=" + configData + "]";
	}

}
