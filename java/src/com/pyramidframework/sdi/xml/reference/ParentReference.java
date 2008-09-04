package com.pyramidframework.sdi.xml.reference;

import com.pyramidframework.sdi.SDIContext;
import com.pyramidframework.sdi.SDIException;
import com.pyramidframework.sdi.SDINode;

/**
 * 引用父亲文档中的指定节点。用法如下：
 * &lt;sdi:reference sdi:type="Parent" sdi:path="/catalog/cd[1]" /&gt;
 * 
 * @author Mikab Peng
 * 
 */
public class ParentReference extends NodeReference {

	/**
	 * 未找到对应的节点返回NULL
	 */
	public SDINode getReferenceValue(SDIContext context, SDINode desc) throws SDIException {

		SDINode node = context.getParent().getSingleNode(getIdentifier());

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

}
