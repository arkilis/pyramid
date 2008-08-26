package com.pyramidframework.ci.impl;

import java.util.ArrayList;
import java.util.List;

import com.pyramidframework.ci.ConfigDomain;

public class ConfigDomainImpl extends ConfigDomain {
	final static ConfigDomainImpl NULL_CONFIG = new ConfigDomainImpl(null, null);// 用来保存没有配置的数据
	
	String parentPath = null;
	List children = new ArrayList();

	public String getParentPath() {
		return parentPath;
	}

	public void setParentPath(String parentPath) {

		if (this.parentPath != null) {
			List parent = ((ConfigDomainImpl) getParent()).children;
			if (parent.contains(this)) {
				parent.remove(this);
			}
		}
		if (parentPath != null) {
			((ConfigDomainImpl) ConfigDamainTree.getConfigDomain(parentPath, this.configType, null)).children.add(this);
		}
		this.parentPath = parentPath;
	}

	public List getChildren() {
		return children;
	}

	public ConfigDomain getParent() {
		if (this.parentPath == null || "none".equals(this.parentPath) || "/".equals(this.parentPath)) {
			return null;
		}
		return ConfigDamainTree.getConfigDomain(parentPath, this.configType, null);
	}

	ConfigDomainImpl(String functionPath, String configType) {
		this.configType = configType;
		this.targetPath = functionPath;
	}

	void setConfigData(Object data) {
		this.configData = data;
	}

	void setCached(boolean cached) {
		this.cached = cached;
	}

}
