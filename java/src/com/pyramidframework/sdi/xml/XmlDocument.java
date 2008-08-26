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
 * 如果新加入的元素含有本文档未含有的namespace，则其默认在xpath中不支持，需要
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
	 * 获取一个节点
	 * 
	 * @return 如果没找到则返回NULL;;
	 */
	public SDINode getSingleNode(String identifier) throws SDIException {
		List list = getNodeList(identifier);
		if (list.size() > 0) {
			return (XmlNode) list.get(0);
		}
		return null;
	}

	/**
	 * 得到一组节点
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
	 * 设置节点为新的内容 如果沒有该节点，则添加
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

				// 先删掉旧有的属性，然后在合并新的属性
				Attribute newAttribute = null;
				
				//需要看看替换的对象是不是Attribute
				if(xn.rootNode instanceof Attribute){
					newAttribute = (Attribute) xn.rootNode;
				}else{
					newAttribute = XmlDocument.creatXmlAttribute(attribute.getName(), xn.getTextValue(),attribute.getNamespace());
				}
				
				Dom4JHelper.replaceAttribute(element,newAttribute);

			} else if (node.rootNode instanceof Element) {

				Element element = (Element) node.rootNode;// 如果目标都是节点时则直接替换
				if (xn.rootNode instanceof Element) {
					Dom4JHelper.replaceNode((Element) node.rootNode, (Element) xn.rootNode);

				} else if (xn.rootNode instanceof Attribute) {

					// 存在属性则修改，没有则添加
					Dom4JHelper.replaceAttribute((Element) node.rootNode, (Attribute) xn.rootNode);

				} else {
					// 找到该节点的第一个并且进行替换
					Dom4JHelper.replaceText(element, (Text) xn.rootNode);
				}
			} else {// 修改指定的文本
				node.rootNode.setText(xn.rootNode.getText());
			}

		}
	}

	/**
	 * 移除一个节点
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
	 * 添加一个节点 如果是属性，有则修改
	 */
	public void addNode(String parentidentifier, SDINode snode) throws SDIException {
		XmlNode sn = (XmlNode) snode;
		mergeNamespaces(this.namespaces, sn.namespaces);
		List list = getNodeList(parentidentifier);

		for (int i = 0; i < list.size(); i++) {

			XmlNode node = (XmlNode) list.get(i);

			// 如果目标是属性，则其表达式的最终结果必须为属性
			if (node.rootNode instanceof Element) {
				if (sn.rootNode instanceof Attribute) {//是属性时必须进行合并操作	
					Dom4JHelper.replaceAttribute((Element) node.rootNode, (Attribute) sn.rootNode);
					
				} else {
					((Element) node.rootNode).add(sn.rootNode);
				}
			}//忽略其他类型的节点，无法为其添加子节点
		}
	}

	/**
	 * 复制自己
	 */
	public SDINode cloneNode() throws SDIException {
		Document newDocument = (Document) innerDocument.clone();
		HashMap namespaces = (HashMap) this.namespaces.clone();
		return new XmlDocument(newDocument, namespaces);
	}

	/**
	 * 根据新的资源名创建新的文档对象
	 */
	public SDIDocument createDocumentFromResource(String resourcePath) throws SDIException {

		// 先在缓存中查找
		if (DOCUMENTS_CACHE == null) {
			DOCUMENTS_CACHE = new HashMap();
		}

		WeakReference ref = (WeakReference) DOCUMENTS_CACHE.get(resourcePath);

		if (ref == NULL_REF) {
			return null;
		}

		// 存在缓存，则找出并且返回
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
	 * 使用输入流构造XML对象
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
	 * 使用文件构造对象
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
	 * 根据满足W3C规范的DOM文档对象来构建xml对象
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
	 * 使用构建号的DOM树构建对象
	 * 
	 * @param document
	 */
	public XmlDocument(Document document, HashMap namespaces) {
		super(document.getRootElement(), namespaces);
		innerDocument = document;
	}

	/**
	 * 从输入流构建本对象
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
	 * 取得内部持有的DOM4j的document对象
	 * 
	 * @return
	 */
	public Document getDom4jDocument() {
		return innerDocument;
	}

	/**
	 * 取得符合W3C规范的DOM对象
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
	 * 添加prefix和uri的键值对，直接覆盖掉以前的
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
	 * @return 如果存在，返回其对应的uri
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
	 * 创建一个全新的对象
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
	 * 创建一个文本节点
	 * 
	 * @param textValue
	 * @return
	 */
	public static Text createXmlText(String textValue) {
		return DOCUMENT_FACTORY.createText(textValue);
	}

	/**
	 * 以传入的新的节点为根节点建立一个新的元素 注意此处无法复制对应的文档的命名空间
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
	 * 创建查询的Xpath
	 * 
	 * @param identifier
	 * @param namespaces
	 *            namespace的prefix和uri的键值对
	 * @return
	 */
	public static XPath createXmlXpath(String identifier, Map namespaces) {
		XPath path = new DefaultXPath(identifier);
		path.setNamespaceURIs(namespaces);
		return path;
	}

	/**
	 * 创建DOM分析器
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
	 * 得到上级的属性和数据
	 * 
	 * @param path
	 * @return
	 */
	protected String getParentXPath(String path) {
		int index = path.lastIndexOf("/");
		return path.substring(0, index);
	}

	/**
	 * 得到上级的属性和数据
	 * 
	 * @param path
	 * @return
	 */
	protected String getTargetXPath(String path, Attribute node) {

		Attribute attribute = (Attribute) node;// 需要判断有没有命名空间
		if (attribute.getNamespace() == null || "".equals(attribute.getNamespace().getPrefix())) {
			return path + "/@" + node.getName();
		} else {
			return path + "/@" + attribute.getNamespace().getPrefix() + ":" + node.getName();
		}

	}

	/**
	 * 根据传入的资源标志符，打开输入输出流
	 * 
	 * @param resourcePath
	 * @return
	 * @throws Exception
	 */
	protected InputStream openResource(String resourcePath) throws Exception {

		// 是不是网络资源，判断包含不包含://
		if (resourcePath.indexOf("://") > 0) {
			return (new URL(resourcePath)).openStream();
		}

		InputStream inputStream = this.getClass().getResourceAsStream(resourcePath);

		// 如果不是资源，则是文件系统系统
		if (inputStream == null) {
			return new FileInputStream(resourcePath);
		} else {
			return inputStream;
		}
	}

	/**
	 * 将两个MAP合并
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
