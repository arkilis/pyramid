package com.pyramidframework.sdi.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Element;
import org.dom4j.Node;

import com.pyramidframework.sdi.SDIException;
import com.pyramidframework.sdi.SDINode;

/**
 * XML表示的文件的子节点
 * 
 * @author Mikab Peng
 */
public class XmlNode implements SDINode {
	protected Node rootNode = null;
	HashMap namespaces = null;

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
