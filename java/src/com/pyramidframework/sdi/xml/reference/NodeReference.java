package com.pyramidframework.sdi.xml.reference;

import java.util.HashMap;

import org.dom4j.Attribute;
import org.dom4j.Namespace;

import com.pyramidframework.sdi.SDIContext;
import com.pyramidframework.sdi.SDIException;
import com.pyramidframework.sdi.SDINode;
import com.pyramidframework.sdi.xml.XmlDocument;
import com.pyramidframework.sdi.xml.XmlNode;

/**
 * 值索引对象的接口
 * 
 * @author Mikab Peng
 * @version 2008-7-14
 */
public abstract class NodeReference {

	public abstract SDINode getReferenceValue(SDIContext sdi, SDINode desc) throws SDIException;

	/**
	 * 创建一个新的XML属性节点
	 * 
	 * @param attributeValue
	 * @return
	 * @throws SDIException
	 */
	protected XmlNode createAttribute(String attributeValue) throws SDIException {

		HashMap map = new HashMap();
		Namespace nn = null;
		if (this.namespace != null && !"".equals(namespace)) {

			// 必须包含一个：用来分割，：前是prefix，冒号后为uri,两者都能为空
			String prefix = "";
			String uri = namespace;
			int index = namespace.indexOf(':');
			if (index > 0) {
				prefix = namespace.substring(0, index);
				uri = namespace.substring(index + 1);
			}
			nn = new Namespace(prefix, uri);
			map.put(prefix, uri);
		}
		Attribute attribute = XmlDocument.creatXmlAttribute(attributeName, attributeValue, nn);

		return new XmlNode(attribute, map);

	}

	/** 索引到值的表达式，XML文档中主要为XPath表达式 */
	protected String identifier = null;

	/** 此值不为空新值为属性，如果是属性，则此项不能为空 */
	protected String attributeName = null;

	/** 可以通过()包起来向ValueReference传递参数 */
	protected String parameter = null;
	protected String namespace = null;

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}
}
