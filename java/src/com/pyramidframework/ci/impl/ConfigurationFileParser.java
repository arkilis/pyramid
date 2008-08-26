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
 * 配置命令和配置信息的解析器
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
	 * 解析文件
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
	 * 解析声明配置信息域的信息
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

			// 从上级继承
			if (inheriteFrom == null || inheriteFrom.length() == 0 || inheriteFrom.equals(ConfigurationManager.INHERITE_TYPE_CONSTANTS[0])) {
				
				parentPath = ConfigurationManager.getParentPath(domain.getTargetPath());
				
			} else if (inheriteFrom.equals(ConfigurationManager.INHERITE_TYPE_CONSTANTS[1])) {
				parentPath = "/";
			} else if (inheriteFrom.equals(ConfigurationManager.INHERITE_TYPE_CONSTANTS[2])) {
				parentPath = null;
				
			} else if (inheriteFrom.equals(none_inheritance)) { // none,不从任何地方继承
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

			// 判断有没有设定继承规则数据
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

			// 执行解析
			parseOriginalData();
		}
		return domain;
	}

	public void incrementParse(IncrementDocumentParser iParser) {
		Object parentNode = null;

		String parentPath = domain.getParentPath();

		if (none_inheritance.equals(parentPath)) {// 不从任何地方继承
			// 不给其设定继承来的数据

		} else if (parentPath == null || parentPath.length() == 0) {// 继承默认设置
			parentNode = iParser.getConfigData(null,null);
		} else {
			ConfigDomainImpl d = tree.getDomain(parentPath, domain.getConfigType(), iParser);
			parentNode = d == null ? null : d.getConfigData();
		}
		
		Object d = iParser.getConfigData(domain, parentNode);
		domain.setConfigData(d);

		// 如果上级未指定，则下级作为根本数据而不是模板
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
				this.xmlDocument = expandTemplateExpression(this.xmlDocument);// 先计算表达式，然后进行

				ConfigurationInheritance inheritance = new ConfigurationInheritance();
				XmlConvter convter = (XmlConvter) inheritance.getIncrementInheritanceConvter();
				SDIContext context = new SDIContext();
				context.setRule(xmlDocument);
				try {
					List list = xmlDocument.getChildren();
					
					Object data = iParser.getConfigData(domain,parentNode);
					
					for (int i = 0; i < list.size(); i++) {
						XmlNode node = (XmlNode) list.get(i);
						// 如果是operator需要先解析成operator的形式
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
		this.xmlDocument = expandTemplateExpression(this.xmlDocument);// 先计算表达式，然后进行
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
				parent = inheriteAndParse(parent, parentImpl); // 递归之
			}
		}

		if (parent == null) {
			return document;
		} else if (document == null) {
			return parent;
		} else {
			// 先执行继承
			ConfigurationInheritance inheritance = new ConfigurationInheritance();
			try {
				return (XmlDocument) inheritance.getTargetDocument(parent, document);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * 计算模板的结果
	 */
	public XmlDocument expandTemplateExpression(XmlDocument document) {
			OGNLExpressionInterpreter interpreter = new OGNLExpressionInterpreter(namespace,document,domain.getTargetPath());
			return interpreter.expandExpression();
	}
}
