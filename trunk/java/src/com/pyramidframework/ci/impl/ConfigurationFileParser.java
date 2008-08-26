package com.pyramidframework.ci.impl;

import java.util.List;

import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;

import com.pyramidframework.ci.ConfigDocumentParser;
import com.pyramidframework.ci.ConfigurationManager;
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

	Namespace namespace = new Namespace(ConfigurationManager.DEFAULT_NAMESPACE_PREFIX, ConfigurationManager.DEFAULT_NAMESPACE_URI);

	QName DomainCachedAttribute = new QName("cached", namespace);
	QName DomianInheriteFromAttribute = new QName("inheriteFrom", namespace);

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
			configDomain.setCached("true".equalsIgnoreCase(element.attributeValue(DomainCachedAttribute)));

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

			if (!ConfigurationManager.isCorrectFunctionPath(parentPath) ) {
				throw new IllegalArgumentException("The value of the attribute inheriteFrom is not valid!");
			}
			
			domain.setParentPath(parentPath);

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

	public ConfigDomainImpl parseData() {
		if (parser instanceof IncrementDocumentParser) {
			IncrementDocumentParser iParser = (IncrementDocumentParser) parser;
			incrementParse(iParser);
		} else {
			xmlDocument = inheriteAndParse(xmlDocument, domain);

			// ִ�н���
			parseOriginalData();
		}
		return domain;
	}

	public void incrementParse(IncrementDocumentParser iParser) {
		Object parentNode = null;

		String parentPath = domain.getParentPath();

		if (none_inheritance.equals(parentPath)) {// �����κεط��̳�
			// �������趨�̳���������

		} else if (parentPath == null || parentPath.length() == 0) {// �̳�Ĭ������
			parentNode = iParser.getConfigData(null,null);
		} else {
			ConfigDomainImpl d = tree.getDomain(parentPath, domain.getConfigType(), iParser);
			parentNode = d == null ? null : d.getConfigData();
		}
		
		Object d = iParser.getConfigData(domain, parentNode);
		domain.setConfigData(d);

		// ����ϼ�δָ�������¼���Ϊ�������ݶ�����ģ��
		if (parentNode == null) {
			if (xmlDocument == null) {
				domain = ConfigDamainTree.NULL_CONFIG;
			} else {
				parseOriginalData();
			}
		} else {
			if (xmlDocument == null) {
				domain.setConfigData(parentNode);
			} else {
				this.xmlDocument = expandTemplateExpression(this.xmlDocument);// �ȼ�����ʽ��Ȼ�����

				ConfigurationInheritance inheritance = new ConfigurationInheritance();
				XmlConvter convter = (XmlConvter) inheritance.getIncrementInheritanceConvter();
				SDIContext context = new SDIContext();
				context.setRule(xmlDocument);
				try {
					List list = xmlDocument.getChildren();
					
					Object data = iParser.getConfigData(domain,parentNode);
					
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

	protected void parseOriginalData() {
		this.xmlDocument = expandTemplateExpression(this.xmlDocument);// �ȼ�����ʽ��Ȼ�����
		domain.setConfigData(parser.parseConfigDocument(domain, xmlDocument));
	}

	public XmlDocument inheriteAndParse(XmlDocument document, ConfigDomainImpl configDomain) {
		String parentPath = configDomain.getParentPath();

		XmlDocument parent = null;
		if (none_inheritance.equals(parentPath)) {

		} else if (parentPath == null || parentPath.length() < 0) {
			parent = parser.getDefauDocument();
		} else {
			XmlNode parentNode = tree.lookupConfigedDomain(parentPath, configDomain.parentPath, parser);
			if (parentNode != null) {
				ConfigDomainImpl parentImpl = new ConfigDomainImpl(parentPath, configDomain.getConfigType());
				parent = parseDomainInfo(parentNode, parentImpl);
				parent = inheriteAndParse(parent, parentImpl); // �ݹ�֮
			}
		}

		if (parent == null) {
			return document;
		} else if (document == null) {
			return parent;
		} else {
			// ��ִ�м̳�
			ConfigurationInheritance inheritance = new ConfigurationInheritance();
			try {
				return (XmlDocument) inheritance.getTargetDocument(parent, document);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * ����ģ��Ľ��
	 */
	public XmlDocument expandTemplateExpression(XmlDocument document) {
			OGNLExpressionInterpreter interpreter = new OGNLExpressionInterpreter(namespace,document,domain.getTargetPath());
			return interpreter.expandExpression();
	}
}
