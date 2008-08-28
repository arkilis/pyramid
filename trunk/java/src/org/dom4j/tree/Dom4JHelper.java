package org.dom4j.tree;

import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Branch;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Text;
import org.dom4j.io.DOMReader;
import org.dom4j.io.SAXContentHandler;
import org.dom4j.io.SAXReader;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * 为DOM4提供的一些帮助方法
 * 
 * @author Mikab Peng
 * 
 */
public class Dom4JHelper {

	/**
	 * 将指定的节点替换新的节点
	 * 
	 * @param OldElement
	 * @param xn
	 */
	public static void replaceNode(Element OldElement, org.dom4j.Node newElement) {
		DefaultElement paElement = (DefaultElement) OldElement.getParent();
		int index = paElement.indexOf(OldElement);
		paElement.remove(OldElement);
		paElement.addNewNode(index, newElement);
	}
	
	/**
	 * 把其除了属性之外的子节点附加到本节点在父节点中的位置
	 * 主要用户模板
	 * @param element
	 * @return 有多个子对象被迁移
	 */
	public static int replaceNodeWithChildren(Element element){
		DefaultElement paElement = (DefaultElement) element.getParent();
		int index = paElement.indexOf(element);
		
		int cnt =0;
		for(int i =0; i < element.nodeCount();i++){
			org.dom4j.Node node = element.node(i);
			if (!(node instanceof Attribute)){
				node.detach();
				paElement.addNewNode(index+cnt,node);
				cnt ++;
				i--;
			}
		}
		element.detach();
		return cnt;
	}

	/**
	 * 将属性替换，如果是没有则添加
	 * 
	 * @param element
	 *            属性所拥有的节点
	 * @param attribute
	 *            新替换的属性
	 */
	public static void replaceAttribute(Element element, Attribute attribute) {
		Attribute oldAttribute = element.attribute(attribute.getQName());

		if (oldAttribute != null) {
			element.remove(oldAttribute);
		}
		element.add(attribute);
	}

	/**
	 * 如果有该节点有关联Text节点，则替换掉第一个，否则作为第一个添加
	 * 
	 * @param element
	 * @param text
	 */
	public static void replaceText(Element element, Text text) {
		DefaultElement defaultElement = (DefaultElement) element;

		List list = defaultElement.contentList();
		if (list.size() > 0) {
			int size = list.size();
			for (int i = 0; i < size; i++) {
				if (list.get(i) instanceof Text) {
					int index = defaultElement.indexOf((Text) list.get(i));
					defaultElement.remove((Text) list.get(i));
					defaultElement.addNewNode(index, text);
					return;
				}
			}
		}

		defaultElement.add(text);
	}

	/**
	 * 通过重载实在在声明namespace时记录该最新的声明
	 * 
	 * @param namespaceContainer
	 * @return
	 */
	public static SAXReader createSAXReader(final Map namespaceContainer) {
		SAXReader reader = new SAXReader() {

			protected SAXContentHandler createContentHandler(XMLReader reader) {

				return new SAXContentHandler(getDocumentFactory(), getDispatchHandler()) {

					public void startPrefixMapping(String prefix, String uri) throws SAXException {
						super.startPrefixMapping(prefix, uri);
						namespaceContainer.put(prefix, uri);

					}

				};
			}
		};

		return reader;
	}

	/**
	 * 需要在解析时得到文档所包含的全部的namespace
	 * 
	 * @param namespaceContainer
	 * @return
	 */
	public static DOMReader createDOMReader(final Map namespaceContainer) {

		DOMReader reader = new DOMReader() {

			/**
			 * 在临时缓存中记录
			 */
			protected void readElement(Node node, Branch current) {
				super.readElement(node, current);

				String elementPrefix = node.getPrefix();
				if (elementPrefix != null && !"".equals(elementPrefix)) {
					namespaceContainer.put(elementPrefix, node.getNamespaceURI());
				}

				org.w3c.dom.NamedNodeMap attributeList = node.getAttributes();
				int l = attributeList.getLength();
				for (int i = 0; i < l; i++) {
					Node node2 = attributeList.item(i);
					elementPrefix = node2.getPrefix();
					if (elementPrefix != null && !"".equals(elementPrefix)) {
						namespaceContainer.put(elementPrefix, node2.getNamespaceURI());
					}
				}

			}

			/**
			 * 不管怎么样必须进行解析
			 */
			public Document read(org.w3c.dom.Document domDocument) {
				Document document = createDocument();

				clearNamespaceStack();

				org.w3c.dom.NodeList nodeList = domDocument.getChildNodes();

				for (int i = 0, size = nodeList.getLength(); i < size; i++) {
					readTree(nodeList.item(i), document);
				}

				return document;
			}
		};

		return reader;

	}
}
