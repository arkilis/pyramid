package com.pyramidframework.sdi.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.CDATA;
import org.dom4j.Comment;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Entity;
import org.dom4j.Node;
import org.dom4j.ProcessingInstruction;
import org.dom4j.Text;
import org.dom4j.dom.DOMAttribute;
import org.dom4j.dom.DOMCDATA;
import org.dom4j.dom.DOMComment;
import org.dom4j.dom.DOMEntityReference;
import org.dom4j.dom.DOMProcessingInstruction;
import org.dom4j.dom.DOMText;
import org.dom4j.io.DOMWriter;
import org.dom4j.tree.DefaultDocument;

import com.pyramidframework.sdi.SDIException;
import com.pyramidframework.sdi.SDINode;

/**
 * XML表示的文件的子节点
 * 
 * @author Mikab Peng
 */
public class XmlNode implements SDINode {

	/**
	 * 得到Dom4j实现的节点
	 * 
	 * @return
	 */
	public Node getDom4JNode()  {
		return (Node) rootNode/*.clone()*/;
	}

	/**
	 * 得到java标准的DOm实现的节点表达
	 * 
	 * @return
	 */
	public org.w3c.dom.Node getDomNode() throws SDIException {
		Node object = (Node) this.rootNode.clone();

		if (object instanceof Element) {
			Document document = new DefaultDocument((Element) object);
			DOMWriter writer = new DOMWriter();
			try {
				return writer.write(document).getDocumentElement().cloneNode(true);
			} catch (DocumentException e) {
				throw new SDIException(e);
			}

		} else if (object instanceof Text) {
			Text text = (Text) object;
			return new DOMText(text.getText());
		} else if (object instanceof CDATA) {
			return new DOMCDATA(((CDATA) object).getText());
		} else if (object instanceof Comment) {
			return new DOMComment(((Comment) object).getText());
		} else if (object instanceof Entity) {
			Entity entity = (Entity) object;
			return new DOMEntityReference(entity.getName(), entity.getText());
		} else if (object instanceof ProcessingInstruction) {
			ProcessingInstruction p = (ProcessingInstruction) object;
			return new DOMProcessingInstruction(p.getTarget(), p.getValues());
		} else { // attribute
			Attribute attribute = (Attribute) object;
			return new DOMAttribute(attribute.getQName(), attribute.getValue());
		}

	}

	protected Node rootNode = null;
	HashMap namespaces = null;
	
	/**
	 * 得到本节点中包含的命名空间的前缀和URL的集合
	 * @return
	 */
	public HashMap getNamespaces() {
		return (HashMap)namespaces.clone();
	}

	/**
	 * 获取其子节点
	 */
	public List getChildren() throws SDIException {
		ArrayList result = new ArrayList();

		// 只有Element 才有子节点
		if (rootNode != null && rootNode instanceof Element) {
			List elements = ((Element) rootNode).elements();
			result.ensureCapacity(elements.size());

			for (int i = 0; i < elements.size(); i++) {
				result.add(encapsulation((Element) elements.get(i)));
			}
		}
		return result;
	}

	/**
	 * 获取其父节点
	 */
	public SDINode getParentNode() throws SDIException {

		return encapsulation(rootNode.getParent());
	}

	/**
	 * 获取其本身的复制，类似clone
	 */
	public SDINode cloneNode() throws SDIException {

		return encapsulation((Node) this.rootNode.clone());
	}

	/**
	 * 获取文本字符串表示当前的内容
	 */
	public String getTextValue() throws SDIException {
		return rootNode.getStringValue().trim();
	}

	/**
	 * 根据一个XML节点封装对应的一个对象
	 * 
	 * @param node
	 * @return
	 */
	protected XmlNode encapsulation(Node node) {
		if (node == null) {
			return null;
		} else {
			HashMap newName = null;
			if (this.namespaces != null) {
				newName = (HashMap) this.namespaces.clone();
			} else {
				newName = new HashMap();
			}
			return new XmlNode(node, newName);
		}
	}

	/**
	 * 创建新的节点
	 * 
	 * @param rootNode
	 * @param namespaces
	 *            prefix为key的，uri为值的MAP
	 */
	public XmlNode(Node rootNode, HashMap namespaces) {

		this.rootNode = rootNode;

		if (namespaces == null) {
			this.namespaces = new HashMap();
		} else {
			this.namespaces = (HashMap) namespaces.clone();
		}
	}

}
