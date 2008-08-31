package com.pyramidframework.ci;

import com.pyramidframework.sdi.NodeOperator;
import com.pyramidframework.sdi.xml.XmlNode;

/**
 * 执行增量式类实例解析的解析器类的接口
 * 
 * @author Mikab Peng
 * 
 */
public interface IncrementDocumentParser extends ConfigDocumentParser {

	/**
	 * 对配置信息的根元素进行解析 此处认为每个根元素即是一个可以被单独解析的增量配置单元
	 * 
	 * @param thisConfigData
	 * @param childOfRoot
	 * @param operator 指明何种类型的操作，但是可能为NULL
	 * @return
	 */
	public  Object parseIncrementElement(Object thisConfigData,XmlNode childOfRoot,NodeOperator operator);
	
	

	/**
	 * 得到默认的配置对象。这是一个工厂方法，在解析过程中使用他来创建配置数据的容器。
	 * 如果实现了此方法，不用实现ConfigDocumentParser.getDefauDocument方法，其实现将不会被调用
	 * @param domain 访问软件设定的最根本的配置信息时，即整个系统的默认配置数据，domain为NULL,否则为其父节点的domain
	 * @param parentDataNode 该节点指定的上级节点的数据
	 * @return
	 */
	public  Object getDefaultConfigData(ConfigDomain domain,Object parentDataNode);
}
