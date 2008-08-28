package com.pyramidframework.ci.impl;

import java.util.List;

import org.dom4j.Element;
import org.dom4j.QName;

import com.pyramidframework.ci.ConfigDocumentParser;
import com.pyramidframework.ci.ConfigurationManager;
import com.pyramidframework.ci.DefaultDocumentParser;
import com.pyramidframework.ci.IncrementDocumentParser;
import com.pyramidframework.sdi.NodeOperator;
import com.pyramidframework.sdi.SDIContext;
import com.pyramidframework.sdi.xml.XmlConvter;
import com.pyramidframework.sdi.xml.XmlDocument;
import com.pyramidframework.sdi.xml.XmlNode;

/**
 * ���������������Ϣ�Ľ�����
 * 
 * @author Mikab Peng
 * 
 */
public class ConfigurationFileParser {

	ConfigDomainImpl domain = null;
	XmlDocument xmlDocument = null;
	ConfigDocumentParser parser = null;
	ConfigDamainTree tree = null;

	final String none_inheritance = ConfigurationManager.INHERITE_TYPE_CONSTANTS[3];

	QName DomianInheriteFromAttribute = null;

	/**
	 * �����ļ�
	 * 
	 * @param domainImpl
	 * @param dataNode
	 * @param parser
	 * @param instance
	 */
	public ConfigurationFileParser(ConfigDomainImpl domainImpl, XmlNode dataNode, ConfigDocumentParser parser, ConfigDamainTree instance) {
		domain = domainImpl;
		this.parser = parser;

		tree = instance;
		//DomainCachedAttribute = new QName("cached", instance.managerInstance.getNamespace());
		DomianInheriteFromAttribute = new QName("inheriteFrom", instance.managerInstance.getNamespace());

		xmlDocument = parseDomainInfo(dataNode, domain);
	}

	/**
	 * ��������������Ϣ�����Ϣ
	 * 
	 * @param dataNode
	 * @param configDomain
	 * @return
	 */
	public XmlDocument parseDomainInfo(XmlNode dataNode, ConfigDomainImpl configDomain) {

		try {
			Element element = (Element) dataNode.getDom4JNode();
			
			String parentPath = null;
			String inheriteFrom = element.attributeValue(DomianInheriteFromAttribute);

			// ���ϼ��̳�
			if (inheriteFrom == null || inheriteFrom.length() == 0 || inheriteFrom.equals(ConfigurationManager.INHERITE_TYPE_CONSTANTS[0])) {

				parentPath = ConfigurationManager.getParentPath(domain.getTargetPath());

			} else if (inheriteFrom.equals(ConfigurationManager.INHERITE_TYPE_CONSTANTS[1])) {
				parentPath = "/";
			} else if (inheriteFrom.equals(ConfigurationManager.INHERITE_TYPE_CONSTANTS[2])) {
				parentPath = null;

			} else if (inheriteFrom.equals(none_inheritance)) { // none,�����κεط��̳�
				parentPath = none_inheritance;
			} else {
				parentPath = inheriteFrom;
			}

			if (parentPath != null && parentPath.equals(domain.getTargetPath())) {
				throw new IllegalArgumentException("Parent path can not be same with this path!");
			}

			if (!ConfigurationManager.isCorrectFunctionPath(parentPath)) {
				throw new IllegalArgumentException("The value of the attribute inheriteFrom is not valid!");
			}

			domain.setParentPath((ConfigDomainImpl) tree.getDomain(parentPath, domain.getConfigType(), parser));

			// �ж���û���趨�̳й�������
			List d = dataNode.getChildren();
			if (d.size() > 0) {
				return XmlDocument.createXmlDocumentFromNode((XmlNode) d.get(0));
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
			// TODO: handle exception
		}
		return null;
	}

	public ConfigDomainImpl parseData(String targetPath) {
		if (parser instanceof IncrementDocumentParser) {
			IncrementDocumentParser iParser = (IncrementDocumentParser) parser;

			incrementParse(iParser, targetPath);
		} else {
			xmlDocument = inheriteAndParse(xmlDocument, targetPath, domain);

			// ִ�н���
			domain.setConfigData(parser.parseConfigDocument(domain, xmlDocument));
		}
		return domain;
	}

	public void incrementParse(IncrementDocumentParser iParser, String targetPath) {
		Object parentNode = null;

		String parentPath = domain.getParentPath();

		if (none_inheritance.equals(parentPath)) {// �����κεط��̳�
			// �������趨�̳���������

		} else if (parentPath == null || parentPath.length() == 0) {// �̳�Ĭ������
			parentNode = iParser.getConfigData(null, null);
		} else {
			ConfigDomainImpl d = tree.lookupAndConstructDomain(parentPath, domain.getConfigType(), iParser, targetPath);
			parentNode = d == null ? null : d.getConfigData();
		}

		Object d = iParser.getConfigData(domain, parentNode);
		domain.setConfigData(d);

		// ����ϼ�δָ�������¼���Ϊ�������ݶ�����ģ��
		if (parentNode == null) {
			if (xmlDocument == null) {
				domain = ConfigDamainTree.NULL_CONFIG;
			} else {
				domain.setConfigData(parser.parseConfigDocument(domain, xmlDocument));
			}
		} else {
			if (xmlDocument == null) {
				domain.setConfigData(parentNode);
			} else {
				// this.xmlDocument =
				// expandTemplateExpression(this.xmlDocument,targetPath);//
				// �ȼ�����ʽ��Ȼ�����

				ConfigurationInheritance inheritance = new ConfigurationInheritance(tree);
				XmlConvter convter = (XmlConvter) inheritance.getIncrementInheritanceConvter();
				SDIContext context = new SDIContext();
				context.setRule(xmlDocument);
				try {
					List list = xmlDocument.getChildren();

					Object data = domain.getConfigData();

					for (int i = 0; i < list.size(); i++) {
						XmlNode node = (XmlNode) list.get(i);
						// �����operator��Ҫ�Ƚ�����operator����ʽ
						Element element = (Element) node.getDom4JNode();
						NodeOperator operator = null;

						if (element.getQName().equals(convter.operatorElementName)) {
							operator = convter.parseOperate(context, node);
							node = (XmlNode) operator.getOperhand();
							operator.setOperhand(null);
						} else {
							node = (XmlNode) convter.parseInternDataNode(context, node);
						}
						iParser.parseIncrementElement(data, node, operator);

					}
				} catch (Exception e) {
					// TODO: handle exception
					throw new RuntimeException(e);
				}
			}
		}
	}

	public XmlDocument inheriteAndParse(XmlDocument document, String targetPath, ConfigDomainImpl configDomain) {
		String parentPath = configDomain.getParentPath();

		XmlDocument parent = null;
		if (none_inheritance.equals(parentPath)) {

		} else if (parentPath == null || parentPath.length() < 0) {
			if (parser instanceof DefaultDocumentParser) {
				parent = ((DefaultDocumentParser) parser).getDefaultDocument();
			}
		} else {
			XmlNode parentNode = tree.lookupConfigedDomain(parentPath, configDomain.getConfigType(), parser, targetPath);
			if (parentNode != null) {
				ConfigDomainImpl parentImpl = new ConfigDomainImpl(parentPath, configDomain.getConfigType());
				parent = parseDomainInfo(parentNode, parentImpl);
				parent = inheriteAndParse(parent, targetPath, parentImpl); // �ݹ�֮
			}
		}

		if (parent == null) {
			return document;
		} else if (document == null) {
			return parent;
		} else {
			// ��ִ�м̳�
			ConfigurationInheritance inheritance = new ConfigurationInheritance(tree);
			try {
				return (XmlDocument) inheritance.getTargetDocument(parent, document);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
}
