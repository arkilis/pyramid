package com.pyramidframework.ci.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ognl.Ognl;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.dom4j.QName;
import org.dom4j.tree.Dom4JHelper;

import com.pyramidframework.sdi.xml.XmlDocument;
import com.pyramidframework.sdi.xml.XmlNode;

/**
 * ʹ��OGNL���ʽ������Ϊ���ʽ
 * 
 * @author Mikab Peng
 * 
 */
public class OGNLExpressionInterpreter {
	Namespace namespace = null;
	String source;
	XmlNode xmlElement = null;

	QName templateElementName = null;
	QName templatePatternAttributeName = null;
	QName templateIDAttributeName = null;

	public OGNLExpressionInterpreter(Namespace namespace, XmlNode element, String functionPath) {
		this.namespace = namespace;

		templateElementName = new QName("template", namespace);
		templatePatternAttributeName = new QName("pattern", namespace);
		templateIDAttributeName = new QName("id", namespace);
		this.source = functionPath;
		this.xmlElement = element;
	}

	/**
	 * ����ģ��Ľ��
	 */
	public static XmlNode expandTemplateExpression(XmlNode element, String targetPath, Namespace namespace) {
		OGNLExpressionInterpreter interpreter = new OGNLExpressionInterpreter(namespace, element, targetPath);
		return interpreter.expandExpression();
	}

	public XmlNode expandExpression() {
		Map context = new HashMap();
		Element rootElement = (Element) xmlElement.getDom4JNode();

		Element element = expandElement(rootElement, context);
		if (element == null) {
			return null;
		} else if (element == rootElement) {
			// �ڵ�δ�仯�������޸�
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
				context.put(id, matcher);
				for (int i = 0; i < element.nodeCount(); i++) {
					Node n = element.node(i);
					if (n instanceof Attribute) {
						// ģ������Բ�������
					} else if (n instanceof Element) {
						Element newE = expandElement((Element) n, context);
						if (newE == null) {
							n.detach();
						} else if (newE != n) {
							newE.detach();
							Dom4JHelper.replaceNode((Element) n, newE);
						} else{
							//������ص���ģ�壬��Ҫ��ģ����¼������滻��ģ�������
							if (templateElementName.equals(newE.getQName())) {
								i += Dom4JHelper.replaceNodeWithChildren(newE)-1;
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
				List list = element.elements();
				if (list.size() > 0) {

					// ����и��׽ڵ㣬��ѱ��ڵ��ȫ���ӽڵ���ӵ����У�����ȡ��һ���ӽڵ�
					if (element.getParent() != null) {
						//Dom4JHelper.replaceNodeWithChildren(element);//Ҫ��ֹ�ӽڵ��ڸ��׽ڵ㱻�ظ�����

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
						n.detach();
					} else if (newE != n) {
						newE.detach();
						Dom4JHelper.replaceNode((Element) n, newE);
					}else{
						//������ص���ģ�壬��Ҫ��ģ����¼������滻��ģ�������
						if (templateElementName.equals(newE.getQName())) {
							i += Dom4JHelper.replaceNodeWithChildren(newE)-1;
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
	 * ����б��ʽ�Ҽ����ˣ��򷵻����ַ��������򷵻�NULL
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
						value = Ognl.getValue(press, context).toString();
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
