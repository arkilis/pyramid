package com.pyramidframework.sdi.xml.reference;

import com.pyramidframework.sdi.SDIContext;
import com.pyramidframework.sdi.SDIException;
import com.pyramidframework.sdi.SDINode;

/**
 * ���������ɵ��ĵ��е�ĳ���ڵ��ֵ���÷����£�
 * &lt;sdi:reference sdi:type="Child" sdi:path="/catalog/cd[1]" /&gt;
 * 
 * @author Mikab Peng
 * 
 */
public class ChildReference extends NodeReference {

	/**
	 * ���������ֵδ�ҵ����򷵻�NULL
	 */
	public SDINode getReferenceValue(SDIContext context, SDINode desc) throws SDIException {
		SDINode node = context.getChild().getSingleNode(getIdentifier());

		// ����ǿգ�����NULL
		if (node == null) {
			return null;
		}

		// ָ�������ԣ��򷵻�����
		if (attributeName != null && !"".equals(attributeName)) {
			return createAttribute(desc.getTextValue());
		}

		return node.cloneNode();
	}

}
