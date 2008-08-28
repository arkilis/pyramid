package com.pyramidframework.ci;

import com.pyramidframework.sdi.xml.XmlDocument;

/**
 * 增加获取软件自己默认的配置信息的文档的接口
 * 
 * @author Mikab Peng
 *
 */
public interface DefaultDocumentParser extends ConfigDocumentParser {

	/**
	 * 得到软件默认的配置文档，可以是包含模板的文档
	 * @return
	 */
	public abstract XmlDocument getDefaultDocument();

}