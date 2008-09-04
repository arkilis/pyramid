package com.pyramidframework.sdi.xml.reference;

import com.pyramidframework.sdi.SDIContext;
import com.pyramidframework.sdi.SDIException;
import com.pyramidframework.sdi.SDINode;

/**
 * ���ø����ĵ��е�ָ���ڵ㡣�÷����£�
 * &lt;sdi:reference sdi:type="Parent" sdi:path="/catalog/cd[1]" /&gt;
 * 
 * @author Mikab Peng
 * 
 */
public class ParentReference extends NodeReference {

	/**
	 * δ�ҵ���Ӧ�Ľڵ㷵��NULL
	 */
	public SDINode getReferenceValue(SDIContext context, SDINode desc) throws SDIException {

		SDINode node = context.getParent().getSingleNode(getIdentifier());

		// �����nULL���򷵻�NULL
		if (node == null) {
			return null;
		}
		// ָ�����ԣ��践������
		if (attributeName != null && !"".equals(attributeName)) {
			return createAttribute(desc.getTextValue());
		}
		return node.cloneNode();
	}

}
