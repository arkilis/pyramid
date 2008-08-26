package com.pyramidframework.sdi.xml;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.tree.Dom4JHelper;

import com.pyramidframework.sdi.NodeOperator;
import com.pyramidframework.sdi.OperatorConvter;
import com.pyramidframework.sdi.SDIContext;
import com.pyramidframework.sdi.SDIDocument;
import com.pyramidframework.sdi.SDIException;
import com.pyramidframework.sdi.SDINode;
import com.pyramidframework.sdi.xml.reference.NodeReference;

/**
 * ����¼������XML��Ϣת����Ӧ��operator�����滻ֵ
 * 
 * @author Mikab Peng
 * @version 2008-7-14
 */
public class XmlConvter implements OperatorConvter {

	/**
	 * �õ���Ԫ���µĲ������б�
	 */
	public List getRootOperatorsList(SDIDocument changesDocument) throws SDIException {
		XmlDocument document = (XmlDocument) changesDocument;

		Element element = document.getDom4jDocument().getRootElement();

		if (element.getQName().equals(rootElementName)) {

			List r = element.elements(operatorElementName);
			ArrayList result = new ArrayList(r.size());
			for (int i = 0; i < r.size(); i++) {
				result.add(new XmlNode((Element) r.get(i), document.namespaces));
			}
			return result;
		}

		return null;
	}

	/**
	 * ����XML�����ļ�������¼�Ĳ����� �����������״̬����ʹ����NameSpace��NameSpace��ʹ�õķ���
	 */
	public NodeOperator parseOperate(SDIContext context, SDINode node) throws SDIException {
		return parseInternOperator(context, (XmlNode) node);
	}

	/**
	 * ����Ƕ�ױ��ʽ���
	 * 
	 * @param context
	 * @param operatorNode
	 * @param refParent
	 * @return
	 * @throws SDIException
	 */
	public NodeOperator parseInternOperator(SDIContext context, XmlNode operatorNode) throws SDIException {

		Element element = (Element) operatorNode.rootNode;

		String type = element.attributeValue(operatorTypeAttributeName);
		String path = element.attributeValue(operatorTargetPathAttributeName);

		// �����ṹ
		NodeOperator operator = createOperator(type);
		operator.setOperatorName(type);
		operator.setTargetIdentifier(path);

		if (operator.needOperhand()) {
			XmlNode node = null;
			List list = operatorNode.getChildren();
			if (list.size() > 0) {

				// ������һ��������Ϊ������
				node = (XmlNode) list.get(0);

				Element nodeElement = (Element) node.rootNode;
				QName nodeName = nodeElement.getQName();
				if (nodeName.equals(referenceElementName)) { // ��������
					node = parseInternReference(context, node);

				} else {// �����Ľڵ㶼��Ϊ���ݽڵ�������
					node = (XmlNode) parseInternDataNode(context, node).cloneNode();
				}
			} else {
				node = new XmlNode(XmlDocument.createXmlText(operatorNode.getTextValue()), operatorNode.namespaces);
			}
			operator.setOperhand(node);
		}

		return operator;
	}

	/**
	 * �����õĽ�����ʽ���н���,
	 * 
	 * @param dataNode
	 * @param type
	 * @param path
	 * @return
	 */
	public XmlNode parseInternDataNode(SDIContext context, XmlNode dataNode) throws SDIException {

		List children = dataNode.getChildren();
		if (children.size() > 0) {
			int size = children.size();
			for (int i = 0; i < size; i++) {
				XmlNode xn = (XmlNode) children.get(i);

				Element element = ((Element) xn.rootNode);
				QName nodeName = element.getQName();

				if (nodeName.equals(referenceElementName)) {
					// ֻȡ����һ���ڵ��ֵ��Ϊ��������
					XmlNode node = parseInternReference(context, xn);
					if (node.rootNode instanceof Attribute) { // ������������ԣ���ֱ���޸ĸ��ڵ������

						((Element) dataNode.rootNode).remove((Element) xn.rootNode);
						Dom4JHelper.replaceAttribute((Element) dataNode.rootNode, (Attribute) node.rootNode);
					} else {// ֱ���滻��ԭ�ڵ�
						Dom4JHelper.replaceNode((Element) xn.rootNode, node.rootNode);
					}
				} else if (nodeName.equals(operatorElementName)) {
					NodeOperator operator = parseInternOperator(context, xn);

					// ��Ҫ�Ȱѽڵ�ɾ����
					Element tempRoot = (Element) dataNode.rootNode;
					tempRoot.remove((Element) xn.rootNode);

					// ��ʱ����һ���ĵ��������ڼ���,ע�⣬�˴������������ĸ��ڵ�Ϊ�ĵ��ĸ��ڵ㣬������ı��ʽҲӦ����������Ϊ���ڵ��XPath���ʽ
					XmlDocument document = XmlDocument.createXmlDocumentFromNode(dataNode);

					operator.operateWithDocument(document);
					XmlNode node = new XmlNode(document.rootNode, document.namespaces);
					Dom4JHelper.replaceNode((Element) dataNode.rootNode, document.rootNode);
					dataNode = node;

					// ��Ҫ���������飬�������ݻ��޷�ɾ��
					children = dataNode.getChildren();
					int temp = (children.size() - size);
					size += temp;

					// ������һ��ִ�е���λ��
					i += temp;

				} else {
					parseInternDataNode(context, xn);
				}
			}
		}

		return dataNode;
	}

	/**
	 * ����ָ���Ĳ��������õ���Ӧ��ֵ��������
	 * 
	 * @return
	 * @throws SDIException
	 */
	public XmlNode parseInternReference(SDIContext context, XmlNode node) throws SDIException {
		Element element = (Element) node.rootNode;

		String referType = element.attributeValue(referenceTypeName);

		String parameter = null;
		if (referType != null) {
			if (referType.indexOf('(') > 0) {
				parameter = referType.substring(referType.indexOf('(') + 1, referType.indexOf(')'));
				referType = referType.substring(0, referType.indexOf('('));
			}

			// ����ĸ�����д������������������
			referType = referType.substring(0, 1).toUpperCase() + referType.substring(1);
		}

		NodeReference reference = createValueReference(referType);
		reference.setParameter(parameter);

		reference.setIdentifier(element.attributeValue(referencePathAttributeName));
		reference.setAttributeName(element.attributeValue(referenceTargetAttributeName));
		reference.setNamespace(element.attributeValue(referencePathAttributeNamespace));

		XmlNode xmlNode = (XmlNode) reference.getReferenceValue(context, node);

		// ���ֵ�������Ӷ��������Ϊ��������
		List list = node.getChildren();
		int size = list.size();
		XmlDocument document = null;
		
		//ֻ����Elementʱ�����޸�
		if (size > 0 && xmlNode.rootNode instanceof Element) {
			document = XmlDocument.createXmlDocumentFromNode(xmlNode);
		} else {
			return xmlNode;
		}

		for (int i = 0; i < size; i++) {
			XmlNode operatNode = (XmlNode) list.get(i);
			Element pElement = (Element) operatNode.rootNode;
			if (pElement.getQName().equals(operatorElementName)) {
				NodeOperator operator = parseInternOperator(context, operatNode);
				operator.operateWithDocument(document);
			}
			// ���������ͺ���
		}
		return new XmlNode(document.getDom4jDocument().getRootElement().detach(), document.namespaces);
	}

	protected XmlInhertance inheriInstance = null;
	public Namespace nameSpace = null;
	public QName referenceElementName = null;
	public QName referenceTargetAttributeName = null;
	public QName referencePathAttributeName = null;
	public QName referencePathAttributeNamespace = null;
	public QName referenceTypeName = null;

	public QName rootElementName = null;
	public QName operatorElementName = null;
	public QName operatorTypeAttributeName = null;
	public QName operatorTargetPathAttributeName = null;

	/**
	 * ����һ��ת������
	 * 
	 * @param inheriInstance
	 */
	public XmlConvter(XmlInhertance inheriInstance) {
		this.inheriInstance = inheriInstance;

		// ��¼�仯���ĵ���Ԫ����
		rootElementName = new QName("operates", getNameSpace());
		operatorElementName = new QName("operate", getNameSpace());
		operatorTypeAttributeName = new QName("type", getNameSpace());
		operatorTargetPathAttributeName = new QName("path", getNameSpace());

		referenceElementName = new QName("reference", getNameSpace());
		referencePathAttributeName = new QName("path", getNameSpace());
		referenceTargetAttributeName = new QName("attribute", getNameSpace());
		referencePathAttributeNamespace = new QName("namespace", getNameSpace());
		referenceTypeName = new QName("type", getNameSpace());
	}

	/**
	 * �õ���ǰ�ĵ�ʹ�õ�NameSpace
	 * 
	 * @return
	 */
	protected Namespace getNameSpace() {
		if (inheriInstance.useNamespace && nameSpace == null) {
			nameSpace = inheriInstance.createDefaultNamespace();
		}
		return nameSpace;
	}

	/**
	 * �����µ�
	 * 
	 * @param operateType
	 * @return
	 * @throws SDIException
	 */
	protected NodeOperator createOperator(String operateType) throws SDIException {
		return (NodeOperator) ClassResolver.OPERATOR_RESOLVER.newInstance(operateType);
	}

	/**
	 * ����ֵ��������
	 * 
	 * @param referType
	 * @return
	 * @throws SDIException
	 */
	protected NodeReference createValueReference(String referType) throws SDIException {
		return (NodeReference) ClassResolver.REFERENCE_RESOLVER.newInstance(referType);
	}
}
