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
 * ֵ��������Ľӿ�
 * 
 * @author Mikab Peng
 * @version 2008-7-14
 */
public abstract class NodeReference {

	public abstract SDINode getReferenceValue(SDIContext sdi, SDINode desc) throws SDIException;

	/**
	 * ����һ���µ�XML���Խڵ�
	 * 
	 * @param attributeValue
	 * @return
	 * @throws SDIException
	 */
	protected XmlNode createAttribute(String attributeValue) throws SDIException {

		HashMap map = new HashMap();
		Namespace nn = null;
		if (this.namespace != null && !"".equals(namespace)) {

			// �������һ���������ָ��ǰ��prefix��ð�ź�Ϊuri,���߶���Ϊ��
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

	/** ������ֵ�ı��ʽ��XML�ĵ�����ҪΪXPath���ʽ */
	protected String identifier = null;

	/** ��ֵ��Ϊ����ֵΪ���ԣ���������ԣ�������Ϊ�� */
	protected String attributeName = null;

	/** ����ͨ��()��������ValueReference���ݲ��� */
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
