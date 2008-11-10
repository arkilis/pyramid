package com.pyramidframework.ci.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.dom4j.QName;
import org.dom4j.tree.Dom4JHelper;

import com.pyramidframework.ci.ConfigDocumentParser;
import com.pyramidframework.script.CompilableScriptEngine;
import com.pyramidframework.sdi.xml.XmlDocument;
import com.pyramidframework.sdi.xml.XmlNode;

/**
 * 使用OGNL表达式语言作为表达式
 * 
 * @author Mikab Peng
 * 
 */
public class ExpressionInterpreter {
	Namespace namespace = null;
	String source;
	XmlNode xmlElement = null;

	QName templateElementName = null;
	QName templatePatternAttributeName = null;
	QName templateIDAttributeName = null;

	CompilableScriptEngine scriptEngine = null;

	public ExpressionInterpreter(Namespace namespace, XmlNode element, String functionPath, CompilableScriptEngine scriptEngine) {
		this.namespace = namespace;

		templateElementName = new QName("template", namespace);
		templatePatternAttributeName = new QName("pattern", namespace);
		templateIDAttributeName = new QName("id", namespace);
		this.source = functionPath;
		this.xmlElement = element;

		this.scriptEngine = scriptEngine;
	}

	/**
	 * 计算模板的结果
	 */
	public static XmlNode expandTemplateExpression(XmlNode element, String targetPath, Namespace namespace, ConfigDocumentParser parser, CompilableScriptEngine scriptEngine) {
		ExpressionInterpreter interpreter = new ExpressionInterpreter(namespace, element, targetPath, scriptEngine);

		Map context = new HashMap();
		parser.InitTemplateContext(context);

		return interpreter.expandExpression(context);
	}

	public XmlNode expandExpression(Map context) {

		Element rootElement = (Element) xmlElement.getDom4JNode();

		Element element = expandElement(rootElement, context);
		if (element == null) {
			return null;
		} else if (element == rootElement) {
			// 节点未变化，不用修改
			return xmlElement;
		} else {
			element.detach();
			return new XmlNode(element, xmlElement.getNamespaces());
		}
	}

	public Element expandElement(Element element, Map context) {
		if (templateElementName.equals(element.getQName())) {
			String pattern = element.attributeValue(templatePatternAttributeName);
			String id = element.attributeValue(templateIDAttributeName);

			Pattern sp = Pattern.compile(pattern);
			Matcher matcher = sp.matcher(source);
			if (matcher.matches()) {
				Object OldObject = context.put(id, matcher);
				for (int i = 0; i < element.nodeCount(); i++) {
					Node n = element.node(i);
					if (n instanceof Attribute) {
						// 模板的属性不做处理
					} else if (n instanceof Element) {
						Element newE = expandElement((Element) n, context);
						if (newE == null) {
							n.detach();
						} else if (newE != n) {
							newE.detach();
							Dom4JHelper.replaceNode((Element) n, newE);
						} else {
							// 如果返回的是模板，需要把模板的下级子项替换掉模板的那项
							if (templateElementName.equals(newE.getQName())) {
								i += Dom4JHelper.replaceNodeWithChildren(newE) - 1;
							}
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
				if (OldObject != null) {
					context.put(id, OldObject);
				}

				// 如果原来有对象的对象，需要再放置回去，迎接后继的计算

				List list = element.elements();
				if (list.size() > 0) {

					// 如果有父亲节点，则把本节点的全部子节点添加到其中，否则取第一个子节点
					if (element.getParent() != null) {
						// Dom4JHelper.replaceNodeWithChildren(element);//要防止子节点在父亲节点被重复计算

						return element;
					} else {
						return (Element) list.get(0);
					}
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
			// 处理属性
			List list = element.attributes();
			for (int i = 0; i < list.size(); i++) {
				expandAttributeValue(element, context, (Node) list.get(i));
			}
			// 处理子节点
			for (int i = 0; i < element.nodeCount(); i++) {
				Node n = element.node(i);
				if (n instanceof Attribute) {
					expandAttributeValue(element, context, n);
				} else if (n instanceof Element) {
					Element newE = expandElement((Element) n, context);
					if (newE == null) {
						n.detach();
					} else if (newE != n) {
						newE.detach();
						Dom4JHelper.replaceNode((Element) n, newE);
					} else {
						// 如果返回的是模板，需要把模板的下级子项替换掉模板的那项
						if (templateElementName.equals(newE.getQName())) {
							i += Dom4JHelper.replaceNodeWithChildren(newE) - 1;
						}
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
	 * 将属性中的模板字符展开
	 * 
	 * @param element
	 * @param context
	 * @param n
	 */
	protected void expandAttributeValue(Element element, Map context, Node node) {
		Attribute attribute = (Attribute) node;
		String v = attribute.getValue();
		v = expandString(v, context);
		if (v != null) {
			Dom4JHelper.replaceAttribute(element, XmlDocument.creatXmlAttribute(attribute.getName(), v, attribute.getNamespace()));
		}
	}

	/**
	 * 如果有表达式且计算了，则返回新字符串，否则返回NULL
	 * 
	 * @param expression
	 * @param context
	 * @return
	 */
	public  String expandString(String expression, Map context) {
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
					String value = getExpressionValue(press, context);

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

	public  String getExpressionValue(String exp, Map context) {
		Object object = null;
		if (scriptEngine.isCompilable()) {
			object = scriptEngine.compile(exp);
			object = scriptEngine.runScript(object, context);
		} else {
			object = scriptEngine.runScript(exp, context);
		}

		return String.valueOf(object);
	}

}
