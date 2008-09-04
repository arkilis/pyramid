package com.pyramidframework.sdi.xml.reference;

import com.pyramidframework.sdi.SDIContext;
import com.pyramidframework.sdi.SDIException;
import com.pyramidframework.sdi.SDINode;

/**
 * 引用新生成的文档中的某个节点的值，用法如下：
 * &lt;sdi:reference sdi:type="Child" sdi:path="/catalog/cd[1]" /&gt;
 * 
 * @author Mikab Peng
 * 
 */
public class ChildReference extends NodeReference {

	/**
	 * 如果索引的值未找到，则返回NULL
	 */
	public SDINode getReferenceValue(SDIContext context, SDINode desc) throws SDIException {
		SDINode node = context.getChild().getSingleNode(getIdentifier());

		// 如果是空，返回NULL
		if (node == null) {
			return null;
		}

		// 指明是属性，则返回属性
		if (attributeName != null && !"".equals(attributeName)) {
			return createAttribute(desc.getTextValue());
		}

		return node.cloneNode();
	}

}
