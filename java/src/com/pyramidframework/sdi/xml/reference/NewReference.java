package com.pyramidframework.sdi.xml.reference;

import java.util.List;

import com.pyramidframework.sdi.SDIContext;
import com.pyramidframework.sdi.SDIException;
import com.pyramidframework.sdi.SDINode;

/**
 * 引用记录变化文档的某个节点的值 需要注意如果该节点有reference类型的子节点，是直接拷贝过来而不求其具体的值
 * 
 * @author Mikab Peng
 * 
 */
public class NewReference extends NodeReference {

	/**
	 * 如果没有找到则返回NULL
	 */
	public SDINode getReferenceValue(SDIContext context, SDINode desc) throws SDIException {

		// 则取本节点的值
		if (identifier == null || "".equals(identifier)) {

			if (attributeName == null || "".equals(attributeName)) {
				List list = desc.getChildren();
				if (list.size() == 0) {
					return null;
				}
				return ((SDINode) list.get(0)).cloneNode();
			} else {
				return createAttribute(desc.getTextValue());
			}
		} else {

			// 如果没找到新节点，则返回NULL
			SDINode node = context.getRule().getSingleNode(identifier);
			return (node == null ? null : node.cloneNode());
		}
	}

}
