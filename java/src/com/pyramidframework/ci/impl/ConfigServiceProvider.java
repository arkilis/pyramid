package com.pyramidframework.ci.impl;

import com.pyramidframework.ci.ConfigDocumentParser;
import com.pyramidframework.ci.ConfigDomain;

/**
 * ��ȡ�������ݽڵ�ķ�����ṩ��ʵ�ֵĽӿ�
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