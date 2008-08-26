package com.pyramidframework.ci.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ognl.Ognl;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.dom4j.QName;
import org.dom4j.tree.Dom4JHelper;

import com.pyramidframework.sdi.xml.XmlDocument;

/**
 * 使用OGNL表达式语言作为表达式
 * 
 * @author Mikab Peng
 * 
 */
public class OGNLExpressionInterpreter {
	Namespace namespace = null;
	String source;
	XmlDocument xmlDocument = null;

	QName templateElementName = null;
	QName templatePatternAttributeName = null;
	QName templateIDAttributeName = null;

	public OGNLExpressionInterpreter(Namespace namespace, XmlDocument document, String functionPath) {
		this.namespace = namespace;

		templateElementName = new QName("template", namespace);
		templatePatternAttributeName = new QName("pattern", namespace);
		templateIDAttributeName = new QName("id", namespace);
		this.source = functionPath;
		xmlDocument = document;

	}

	public XmlDocument expandExpression() {
		Document document = xmlDocument.getDom4jDocument();
		Map context = new HashMap();
		Element rootElement = document.getRootElement();

		Element element = expandElement(rootElement, context);
		if (element == null) {
			return null;
		} else if (element == rootElement) {
			// 节点未变化，不用修改
		} else {
			element.detach();
			document.setRootElement(element);
		}

		return xmlDocument;
	}

	public Element expandElement(Element element, Map context) {
		if (templateElementName.equals(element.getQName())) {
			String pattern = element.attributeValue(templatePatternAttributeName);
			String id = element.attributeValue(templateIDAttributeName);

			Pattern sp = Pattern.compile(pattern);
			Matcher matcher = sp.matcher(source);
			if (matcher.matches()) {
				context.put(id, matcher);
				for (int i = 0; i < element.nodeCount(); i++) {
					Node n = element.node(i);
					if (n instanceof Attribute) {
						// 模板的属性不做处理
					} else if (n instanceof Element) {
						Element newE = expandElement((Element) n, context);
						if (newE == null) {
							element.detach();
						} else if (newE != n) {
							newE.detach();
							Dom4JHelper.replaceNode((Element) n, newE);
						}
					} else {
						String t = n.getText();
						t = expandString(t, context);
						if (t != null) {
							n.setText(t);
						}
					}
				}
				context.remove(id);
				List list = element.elements();
				if (list.size() > 0) {
					return (Element) list.get(0);
				} else {
					if (element.getParent() != null) {
						Dom4JHelper.replaceText(element.getParent(), XmlDocument.createXmlText(element.getText()));
					}
					return null;
				}
			} else {
				return null;
			}

		} else {
			for (int i = 0; i < element.nodeCount(); i++) {
				Node n = element.node(i);
				if (n instanceof Attribute) {
					Attribute attribute = (Attribute) n;
					String v = attribute.getValue();
					v = expandString(v, context);
					if (v != null) {
						Dom4JHelper.replaceAttribute(element, XmlDocument.creatXmlAttribute(attribute.getName(), v, attribute.getNamespace()));
					}
				} else if (n instanceof Element) {
					Element newE = expandElement((Element) n, context);
					if (newE == null) {
						element.detach();
					} else if (newE != n) {
						newE.detach();
						Dom4JHelper.replaceNode((Element) n, newE);
					}
				} else {
					String t = n.getText();
					t = expandString(t, context);
					if (t != null) {
						n.setText(t);
					}
				}
			}
			return element;
		}
	}

	/**
	 * 如果有表达式且计算了，则返回新字符串，否则返回NULL
	 * 
	 * @param expression
	 * @param context
	 * @return
	 */
	public static String expandString(String expression, Map context) {
		if (expression == null || expression.length() == 0) {
			return null;
		}
		int indx = 0, eindx = 0;
		StringBuffer buffer = new StringBuffer(expression);

		while (indx >= 0) {
			indx = buffer.indexOf("{{", indx);
			if (indx >= 0) {
				eindx = buffer.indexOf("}}", indx);
				if (eindx > 0) {
					String press = buffer.substring(indx + 2, eindx);
					String value = null;
					try {
						value =  Ognl.getValue(press, context).toString();
					} catch (Exception e) {
						// TODO: handle exception
						throw new RuntimeException(e);
					}
					if (value == null) {
						value = "";
					}
					buffer.replace(indx, eindx + 2, value);
					indx += value.length();
				} else {
					indx = -1;
				}
			}
		}

		if (eindx > 0) {
			return buffer.toString();
		} else {
			return null;
		}

	}

}
