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
 * ΪDOM4�ṩ��һЩ��������
 * 
 * @author Mikab Peng
 * 
 */
public class Dom4JHelper {

	/**
	 * ��ָ���Ľڵ��滻�µĽڵ�
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
	 * �����������֮����ӽڵ㸽�ӵ����ڵ��ڸ��ڵ��е�λ��
	 * ��Ҫ�û�ģ��
	 * @param element
	 * @return �ж���Ӷ���Ǩ��
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
	 * �������滻�������û�������
	 * 
	 * @param element
	 *            ������ӵ�еĽڵ�
	 * @param attribute
	 *            ���滻������
	 */
	public static void replaceAttribute(Element element, Attribute attribute) {
		Attribute oldAttribute = element.attribute(attribute.getQName());

		if (oldAttribute != null) {
			element.remove(oldAttribute);
		}
		element.add(attribute);
	}

	/**
	 * ����иýڵ��й���Text�ڵ㣬���滻����һ����������Ϊ��һ�����
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
	 * ͨ������ʵ��������namespaceʱ��¼�����µ�����
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
	 * ��Ҫ�ڽ���ʱ�õ��ĵ���������ȫ����namespace
	 * 
	 * @param namespaceContainer
	 * @return
	 */
	public static DOMReader createDOMReader(final Map namespaceContainer) {

		DOMReader reader = new DOMReader() {

			/**
			 * ����ʱ�����м�¼
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
			 * ������ô��������н���
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
