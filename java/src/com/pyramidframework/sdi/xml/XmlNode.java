package com.pyramidframework.sdi.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Element;
import org.dom4j.Node;

import com.pyramidframework.sdi.SDIException;
import com.pyramidframework.sdi.SDINode;

/**
 * XML��ʾ���ļ����ӽڵ�
 * 
 * @author Mikab Peng
 */
public class XmlNode implements SDINode {
	protected Node rootNode = null;
	HashMap namespaces = null;

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
