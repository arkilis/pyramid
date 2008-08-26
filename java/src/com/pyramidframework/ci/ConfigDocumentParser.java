package com.pyramidframework.ci;

import com.pyramidframework.sdi.xml.XmlDocument;

/**
 * 将配置文件中的数据解析成配置数据的方式
 * @author Mikab Peng
 *
 */
public interface ConfigDocumentParser {
	
	/**
	 * 根据得到的配置文档得到配置信息的内存对象形式
	 * @param configDocument 直接包含配置信息的解析结果
	 * @param thisDomain 本节点不包含domain信息，但是configdata还是NULL
	 * @return
	 */
	public Object parseConfigDocument(ConfigDomain thisDomain,XmlDocument configDocument);
	
	
	
	/**
	 * 得到默认的配置文档，可以是包含模板的文档
	 * @return
	 */
	public XmlDocument getDefauDocument();
	
	/**
	 * 找到功能对应的配置文件路径。
	 * 该功能所在的模块极其上级模块的配置文件都是搜索的潜在路径，但是以最靠近功能的路径的配置文件内的信息为准
	 * @param functionPath 请求的配置路径，都是目录的形式
	 * @param configType 配置信息的类型
	 */
	public String getConfigFileName(String functionPath,String configType);
	
}
