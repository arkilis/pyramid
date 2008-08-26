package com.pyramidframework.sdi.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.dom4j.QName;
import org.dom4j.Text;
import org.dom4j.XPath;
import org.dom4j.io.DOMReader;
import org.dom4j.io.DOMWriter;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.Dom4JHelper;
import org.dom4j.xpath.DefaultXPath;

import com.pyramidframework.sdi.SDIDocument;
import com.pyramidframework.sdi.SDIException;
import com.pyramidframework.sdi.SDINode;

/**
 * ����¼����Ԫ�غ��б��ĵ�δ���е�namespace������Ĭ����xpath�в�֧�֣���Ҫ
 * 
 * @author Mikab Peng
 * 
 */
public class XmlDocument extends XmlNode implements SDIDocument {
	private Document innerDocument = null;
	protected HashMap DOCUMENTS_CACHE = null;
	private static final WeakReference NULL_REF = new WeakReference(new Object());
	static final DocumentFactory DOCUMENT_FACTORY = DocumentFactory.getInstance();

	/**
	 * ��ȡһ���ڵ�
	 * 
	 * @return ���û�ҵ��򷵻�NULL;;
	 */
	public SDINode getSingleNode(String identifier) throws SDIException {
		List list = getNodeList(identifier);
		if (list.size() > 0) {
			return (XmlNode) list.get(0);
		}
		return null;
	}

	/**
	 * �õ�һ��ڵ�
	 */
	public List getNodeList(String identifier) throws SDIException {
		XPath path = createXmlXpath(identifier, namespaces);
		List list = path.selectNodes(innerDocument);

		ArrayList nodeList = new ArrayList(list.size());
		for (int i = 0; i < list.size(); i++) {
			nodeList.add(new XmlNode((Node) list.get(i), this.namespaces));
		}

		return nodeList;
	}

	/**
	 * ���ýڵ�Ϊ�µ����� ����]�иýڵ㣬�����
	 */
	public void setNode(String identifier, SDINode xnode) throws SDIException {
		List nodeList = getNodeList(identifier);
		XmlNode xn = (XmlNode) xnode;
		mergeNamespaces(this.namespaces, xn.namespaces);

		for (int i = 0; i < nodeList.size(); i++) {
			XmlNode node = (XmlNode) nodeList.get(i);

			if (node.rootNode instanceof Attribute) {
				Attribute attribute = (Attribute) node.rootNode;
				Element element = attribute.getParent();

				// ��ɾ�����е����ԣ�Ȼ���ںϲ��µ�����
				Attribute newAttribute = null;
				
				//��Ҫ�����滻�Ķ����ǲ���Attribute
				if(xn.rootNode instanceof Attribute){
					newAttribute = (Attribute) xn.rootNode;
				}else{
					newAttribute = XmlDocument.creatXmlAttribute(attribute.getName(), xn.getTextValue(),attribute.getNamespace());
				}
				
				Dom4JHelper.replaceAttribute(element,newAttribute);

			} else if (node.rootNode instanceof Element) {

				Element element = (Element) node.rootNode;// ���Ŀ�궼�ǽڵ�ʱ��ֱ���滻
				if (xn.rootNode instanceof Element) {
					Dom4JHelper.replaceNode((Element) node.rootNode, (Element) xn.rootNode);

				} else if (xn.rootNode instanceof Attribute) {

					// �����������޸ģ�û�������
					Dom4JHelper.replaceAttribute((Element) node.rootNode, (Attribute) xn.rootNode);

				} else {
					// �ҵ��ýڵ�ĵ�һ�����ҽ����滻
					Dom4JHelper.replaceText(element, (Text) xn.rootNode);
				}
			} else {// �޸�ָ�����ı�
				node.rootNode.setText(xn.rootNode.getText());
			}

		}
	}

	/**
	 * �Ƴ�һ���ڵ�
	 */
	public void removeNode(String identifier) throws SDIException {
		List list = getNodeList(identifier);

		for (int i = 0; i < list.size(); i++) {
			Node node = ((XmlNode) list.get(i)).rootNode;
			if (node instanceof Element) {
				node = node.detach();
				innerDocument.remove(node);
			} else {
				node = node.detach();
			}

		}
	}

	/**
	 * ���һ���ڵ� ��������ԣ������޸�
	 */
	public void addNode(String parentidentifier, SDINode snode) throws SDIException {
		XmlNode sn = (XmlNode) snode;
		mergeNamespaces(this.namespaces, sn.namespaces);
		List list = getNodeList(parentidentifier);

		for (int i = 0; i < list.size(); i++) {

			XmlNode node = (XmlNode) list.get(i);

			// ���Ŀ�������ԣ�������ʽ�����ս������Ϊ����
			if (node.rootNode instanceof Element) {
				if (sn.rootNode instanceof Attribute) {//������ʱ������кϲ�����	
					Dom4JHelper.replaceAttribute((Element) node.rootNode, (Attribute) sn.rootNode);
					
				} else {
					((Element) node.rootNode).add(sn.rootNode);
				}
			}//�����������͵Ľڵ㣬�޷�Ϊ������ӽڵ�
		}
	}

	/**
	 * �����Լ�
	 */
	public SDINode cloneNode() throws SDIException {
		Document newDocument = (Document) innerDocument.clone();
		HashMap namespaces = (HashMap) this.namespaces.clone();
		return new XmlDocument(newDocument, namespaces);
	}

	/**
	 * �����µ���Դ�������µ��ĵ�����
	 */
	public SDIDocument createDocumentFromResource(String resourcePath) throws SDIException {

		// ���ڻ����в���
		if (DOCUMENTS_CACHE == null) {
			DOCUMENTS_CACHE = new HashMap();
		}

		WeakReference ref = (WeakReference) DOCUMENTS_CACHE.get(resourcePath);

		if (ref == NULL_REF) {
			return null;
		}

		// ���ڻ��棬���ҳ����ҷ���
		if (ref != null && ref.get() != null) {
			return (SDIDocument) ref.get();
		}

		InputStream inputStream = null;
		try {
			inputStream = openResource(resourcePath);
			if (inputStream != null) {
				XmlDocument newdoc = new XmlDocument(inputStream);
				WeakReference reference = new WeakReference(newdoc);

				DOCUMENTS_CACHE.put(resourcePath, reference);
				return newdoc;
			}
		} catch (Exception e) {
			throw new SDIException(e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (Exception e) {
					// do Nothing
				}
			}
		}

		DOCUMENTS_CACHE.put(resourcePath, NULL_REF);
		return null;
	}

	/**
	 * ʹ������������XML����
	 * 
	 * @param inputStream
	 * @throws SDIException
	 */
	public XmlDocument(InputStream inputStream) throws SDIException {

		super(null, null);
		try {
			initFromStream(inputStream);
		} catch (DocumentException e) {
			throw new SDIException(e);
		}
	}

	/**
	 * ʹ���ļ��������
	 * 
	 * @param infile
	 * @throws SDIException
	 */
	public XmlDocument(File infile) throws SDIException {

		super(null, null);
		try {
			SAXReader reader = createSAXReader();

			reader.setIgnoreComments(true);
			reader.setStripWhitespaceText(true);
			innerDocument = reader.read(infile);
			super.rootNode = innerDocument.getRootElement();
		} catch (DocumentException e) {
			throw new SDIException(e);
		}
	}

	/**
	 * 
	 * @param uri
	 * @throws SDIException
	 */
	public XmlDocument(String reourcePath) throws SDIException {
		super(null, null);
		InputStream inputStream = null;
		try {
			inputStream = openResource(reourcePath);
			if (inputStream != null) {
				initFromStream(inputStream);
			}
		} catch (Exception e) {
			throw new SDIException(e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * ��������W3C�淶��DOM�ĵ�����������xml����
	 * 
	 * @param domDocument
	 * @throws SDIException
	 */
	public XmlDocument(org.w3c.dom.Document domDocument) throws SDIException {
		super(null, null);

		innerDocument = createDomReader().read(domDocument);
		super.rootNode = innerDocument.getRootElement();
	}

	/**
	 * ʹ�ù����ŵ�DOM����������
	 * 
	 * @param document
	 */
	public XmlDocument(Document document, HashMap namespaces) {
		super(document.getRootElement(), namespaces);
		innerDocument = document;
	}

	/**
	 * ������������������
	 * 
	 * @param inputStream
	 * @throws DocumentException
	 */
	private void initFromStream(InputStream inputStream) throws DocumentException {
		SAXReader reader = createSAXReader();
		reader.setIgnoreComments(true);
		reader.setStripWhitespaceText(true);
		innerDocument = reader.read(inputStream);
		super.rootNode = innerDocument.getRootElement();
	}

	/**
	 * ȡ���ڲ����е�DOM4j��document����
	 * 
	 * @return
	 */
	public Document getDom4jDocument() {
		return innerDocument;
	}

	/**
	 * ȡ�÷���W3C�淶��DOM����
	 * 
	 * @return
	 */
	public org.w3c.dom.Document getDomDocument() throws SDIException {
		DOMWriter writer = new DOMWriter();
		try {
			return writer.write(innerDocument);
		} catch (Exception e) {
			throw new SDIException(e);
		}
	}

	/**
	 * ���prefix��uri�ļ�ֵ�ԣ�ֱ�Ӹ��ǵ���ǰ��
	 * 
	 * @param prefix
	 * @param uri
	 */
	public void addNamespace(String prefix, String uri) {
		namespaces.put(prefix, uri);
	}

	/**
	 * 
	 * @param prefix
	 * @param uri
	 */
	public void addNamespace(Namespace namespace) {
		addNamespace(namespace.getPrefix(), namespace.getURI());
	}

	public String getNamespace(String prefix) {
		return (String) namespaces.get(prefix);
	}

	/**
	 * 
	 * @param prefix
	 * @return ������ڣ��������Ӧ��uri
	 */
	public String removeNamespace(String prefix) {
		String p = getNamespace(prefix);
		namespaces.remove(prefix);
		return p;
	}

	public boolean equals(Object obj) {
		if (obj instanceof XmlDocument) {
			XmlDocument newDoc = (XmlDocument) obj;
			return this.innerDocument.equals(newDoc.innerDocument);
		}
		return false;
	}

	/**
	 * ����һ��ȫ�µĶ���
	 * 
	 * @param name
	 * @param value
	 * @return
	 */
	public static Attribute creatXmlAttribute(String name, String value, Namespace namespace) {
		if (namespace != null) {
			QName qAName = new QName(name, namespace);
			return DOCUMENT_FACTORY.createAttribute(null, qAName, value);
		}
		return DOCUMENT_FACTORY.createAttribute(null, name, value);
	}

	/**
	 * ����һ���ı��ڵ�
	 * 
	 * @param textValue
	 * @return
	 */
	public static Text createXmlText(String textValue) {
		return DOCUMENT_FACTORY.createText(textValue);
	}

	/**
	 * �Դ�����µĽڵ�Ϊ���ڵ㽨��һ���µ�Ԫ�� ע��˴��޷����ƶ�Ӧ���ĵ��������ռ�
	 * 
	 * @param node
	 * @return
	 */
	public static XmlDocument createXmlDocumentFromNode(XmlNode node) {

		if (node.rootNode instanceof Element) {
			return new XmlDocument(DOCUMENT_FACTORY.createDocument((Element) node.rootNode.clone()), node.namespaces);
		} else {
			throw new ClassCastException("can not create document without element !");
		}
	}

	/**
	 * ������ѯ��Xpath
	 * 
	 * @param identifier
	 * @param namespaces
	 *            namespace��prefix��uri�ļ�ֵ��
	 * @return
	 */
	public static XPath createXmlXpath(String identifier, Map namespaces) {
		XPath path = new DefaultXPath(identifier);
		path.setNamespaceURIs(namespaces);
		return path;
	}

	/**
	 * ����DOM������
	 * 
	 * @return
	 */
	protected SAXReader createSAXReader() {
		return Dom4JHelper.createSAXReader(this.namespaces);
	}

	protected DOMReader createDomReader() {
		return Dom4JHelper.createDOMReader(namespaces);
	}

	/**
	 * �õ��ϼ������Ժ�����
	 * 
	 * @param path
	 * @return
	 */
	protected String getParentXPath(String path) {
		int index = path.lastIndexOf("/");
		return path.substring(0, index);
	}

	/**
	 * �õ��ϼ������Ժ�����
	 * 
	 * @param path
	 * @return
	 */
	protected String getTargetXPath(String path, Attribute node) {

		Attribute attribute = (Attribute) node;// ��Ҫ�ж���û�������ռ�
		if (attribute.getNamespace() == null || "".equals(attribute.getNamespace().getPrefix())) {
			return path + "/@" + node.getName();
		} else {
			return path + "/@" + attribute.getNamespace().getPrefix() + ":" + node.getName();
		}

	}

	/**
	 * ���ݴ������Դ��־���������������
	 * 
	 * @param resourcePath
	 * @return
	 * @throws Exception
	 */
	protected InputStream openResource(String resourcePath) throws Exception {

		// �ǲ���������Դ���жϰ���������://
		if (resourcePath.indexOf("://") > 0) {
			return (new URL(resourcePath)).openStream();
		}

		InputStream inputStream = this.getClass().getResourceAsStream(resourcePath);

		// ���������Դ�������ļ�ϵͳϵͳ
		if (inputStream == null) {
			return new FileInputStream(resourcePath);
		} else {
			return inputStream;
		}
	}

	/**
	 * ������MAP�ϲ�
	 * 
	 * @param source
	 * @param target
	 */
	private void mergeNamespaces(HashMap target, HashMap source) {
		if (source != null) {

			Iterator iterator = source.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry entry = (Map.Entry) iterator.next();

				if (!target.containsKey(entry.getKey())) {
					target.put(entry.getKey(), entry.getValue());
				}
			}
		}
	}

}
