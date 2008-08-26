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
 * XML��ʾ���ļ����ӽڵ�
 * 
 * @author Mikab Peng
 */
public class XmlNode implements SDINode {

	/**
	 * �õ�Dom4jʵ�ֵĽڵ�
	 * 
	 * @return
	 */
	public Node getDom4JNode()  {
		return (Node) rootNode/*.clone()*/;
	}

	/**
	 * �õ�java��׼��DOmʵ�ֵĽڵ���
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
	 * �õ����ڵ��а����������ռ��ǰ׺��URL�ļ���
	 * @return
	 */
	public HashMap getNamespaces() {
		return (HashMap)namespaces.clone();
	}

	/**
	 * ��ȡ���ӽڵ�
	 */
	public List getChildren() throws SDIException {
		ArrayList result = new ArrayList();

		// ֻ��Element �����ӽڵ�
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
	 * ��ȡ�丸�ڵ�
	 */
	public SDINode getParentNode() throws SDIException {

		return encapsulation(rootNode.getParent());
	}

	/**
	 * ��ȡ�䱾��ĸ��ƣ�����clone
	 */
	public SDINode cloneNode() throws SDIException {

		return encapsulation((Node) this.rootNode.clone());
	}

	/**
	 * ��ȡ�ı��ַ�����ʾ��ǰ������
	 */
	public String getTextValue() throws SDIException {
		return rootNode.getStringValue().trim();
	}

	/**
	 * ����һ��XML�ڵ��װ��Ӧ��һ������
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
	 * �����µĽڵ�
	 * 
	 * @param rootNode
	 * @param namespaces
	 *            prefixΪkey�ģ�uriΪֵ��MAP
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
