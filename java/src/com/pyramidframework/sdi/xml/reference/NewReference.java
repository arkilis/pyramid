package com.pyramidframework.sdi.xml.reference;

import java.util.List;

import com.pyramidframework.sdi.SDIContext;
import com.pyramidframework.sdi.SDIException;
import com.pyramidframework.sdi.SDINode;

/**
 * ���ü�¼�仯�ĵ���ĳ���ڵ��ֵ ��Ҫע������ýڵ���reference���͵��ӽڵ㣬��ֱ�ӿ�������������������ֵ
 * 
 * @author Mikab Peng
 * 
 */
public class NewReference extends NodeReference {

	/**
	 * ���û���ҵ��򷵻�NULL
	 */
	public SDINode getReferenceValue(SDIContext context, SDINode desc) throws SDIException {

		// ��ȡ���ڵ��ֵ
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

			// ���û�ҵ��½ڵ㣬�򷵻�NULL
			SDINode node = context.getRule().getSingleNode(identifier);
			return (node == null ? null : node.cloneNode());
		}
	}

}
