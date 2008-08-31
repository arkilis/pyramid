package com.pyramidframework.ci;

import java.util.Map;

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
	 * 找到功能对应的配置文件路径。
	 * 该功能所在的模块极其上级模块的配置文件都是搜索的潜在路径，但是以最靠近功能的路径的配置文件内的信息为准
	 * @param functionPath 请求的配置路径，都是目录的形式
	 * @param configType 配置信息的类型
	 */
	public String getConfigFileName(String functionPath,String configType);
	
	
	/**
	 * 初始化模板语言的执行的上下文.可以将一些对象放在这个里面，以便在模板中直接引用.模板中可以使用{{和}}将需要执行的赋值脚本括起来。
	 * 如：<br>
	 * <code>templateContext.put("programe_name","simpleconfig");</code><br>
	 * 则在配置文件中可以如下直接使用此变量：<br>
	 * &lt;item name="programe_name"&gt;the programe'name is {{programe_name}} !&lt;/item&gt;</item>
	 * @param templateContext 脚本解析的根上下文
	 */
	public void InitTemplateContext(Map templateContext);
	
}
