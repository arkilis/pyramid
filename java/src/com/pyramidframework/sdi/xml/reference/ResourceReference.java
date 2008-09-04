package com.pyramidframework.sdi.xml.reference;

import com.pyramidframework.sdi.SDIContext;
import com.pyramidframework.sdi.SDIDocument;
import com.pyramidframework.sdi.SDIException;
import com.pyramidframework.sdi.SDINode;

/**
 * 引用外部资源文档的 用法如下：<br>
 * <code>&lt;reference type="resource(ddd.xml)" path="/"&gt;</code><br>
 * 括号内的字符串可以文件系统的路径名，也可以是jar包里的资源的路径名
 * @author Mikab Peng
 * 
 */
public class ResourceReference extends NodeReference {

	/**
	 * 从资源中查找对应的文件，然后在找到指定的节点 如果資源文件或者節點未找到，都返回NULL
	 */
	public SDINode getReferenceValue(SDIContext context, SDINode desc) throws SDIException {

		SDIDocument resDocument = getOtherDocument(context);
		if (resDocument == null) {
			return null;
		}

		SDINode node = resDocument.getSingleNode(getIdentifier());

		// 如果是nULL，则返回NULL
		if (node == null) {
			return null;
		}
		// 指明属性，需返回属性
		if (attributeName != null && !"".equals(attributeName)) {
			return createAttribute(desc.getTextValue());
		}
		return node.cloneNode();
	}

	protected SDIDocument getOtherDocument(SDIContext context) throws SDIException {
		SDIDocument resDocument = context.getRule().createDocumentFromResource(this.getParameter());
		return resDocument;
	}

}
