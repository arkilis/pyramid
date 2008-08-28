package com.pyramidframework.ci.impl;

import com.pyramidframework.ci.ConfigDocumentParser;
import com.pyramidframework.ci.ConfigDomain;

/**
 * 获取配置数据节点的服务的提供者实现的接口
 * @author Mikab Peng
 *
 */
public interface ConfigServiceProvider {

	/**
	 * @param functionPath
	 * @param type
	 * @param parser
	 * @return
	 */
	public abstract ConfigDomain getDomain(String functionPath, String type, ConfigDocumentParser parser);

}