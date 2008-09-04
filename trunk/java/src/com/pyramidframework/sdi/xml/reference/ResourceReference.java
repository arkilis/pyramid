package com.pyramidframework.sdi.xml.reference;

import com.pyramidframework.sdi.SDIContext;
import com.pyramidframework.sdi.SDIDocument;
import com.pyramidframework.sdi.SDIException;
import com.pyramidframework.sdi.SDINode;

/**
 * �����ⲿ��Դ�ĵ��� �÷����£�<br>
 * <code>&lt;reference type="resource(ddd.xml)" path="/"&gt;</code><br>
 * �����ڵ��ַ��������ļ�ϵͳ��·������Ҳ������jar�������Դ��·����
 * @author Mikab Peng
 * 
 */
public class ResourceReference extends NodeReference {

	/**
	 * ����Դ�в��Ҷ�Ӧ���ļ���Ȼ�����ҵ�ָ���Ľڵ� ����YԴ�ļ����߹��cδ�ҵ���������NULL
	 */
	public SDINode getReferenceValue(SDIContext context, SDINode desc) throws SDIException {

		SDIDocument resDocument = getOtherDocument(context);
		if (resDocument == null) {
			return null;
		}

		SDINode node = resDocument.getSingleNode(getIdentifier());

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

	protected SDIDocument getOtherDocument(SDIContext context) throws SDIException {
		SDIDocument resDocument = context.getRule().createDocumentFromResource(this.getParameter());
		return resDocument;
	}

}
