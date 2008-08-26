package com.pyramidframework.sdi.xml;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.tree.Dom4JHelper;

import com.pyramidframework.sdi.NodeOperator;
import com.pyramidframework.sdi.OperatorConvter;
import com.pyramidframework.sdi.SDIContext;
import com.pyramidframework.sdi.SDIDocument;
import com.pyramidframework.sdi.SDIException;
import com.pyramidframework.sdi.SDINode;
import com.pyramidframework.sdi.xml.reference.NodeReference;

/**
 * 将记录变量的XML信息转换对应的operator极其替换值
 * 
 * @author Mikab Peng
 * @version 2008-7-14
 */
public class XmlConvter implements OperatorConvter {

	/**
	 * 得到根元素下的操作的列表
	 */
	public List getRootOperatorsList(SDIDocument changesDocument) throws SDIException {
		XmlDocument document = (XmlDocument) changesDocument;

		Element element = document.getDom4jDocument().getRootElement();

		if (element.getQName().equals(rootElementName)) {

			List r = element.elements(operatorElementName);
			ArrayList result = new ArrayList(r.size());
			for (int i = 0; i < r.size(); i++) {
				result.add(new XmlNode((Element) r.get(i), document.namespaces));
			}
			return result;
		}

		return null;
	}

	/**
	 * 根据XML配置文件解析记录的操作符 如果考虑两种状态，即使用了NameSpace和NameSpace不使用的方法
	 */
	public NodeOperator parseOperate(SDIContext context, SDINode node) throws SDIException {
		return parseInternOperator(context, (XmlNode) node);
	}

	/**
	 * 对于嵌套表达式解答
	 * 
	 * @param context
	 * @param operatorNode
	 * @param refParent
	 * @return
	 * @throws SDIException
	 */
	public NodeOperator parseInternOperator(SDIContext context, XmlNode operatorNode) throws SDIException {

		Element element = (Element) operatorNode.rootNode;

		String type = element.attributeValue(operatorTypeAttributeName);
		String path = element.attributeValue(operatorTargetPathAttributeName);

		// 构建结构
		NodeOperator operator = createOperator(type);
		operator.setOperatorName(type);
		operator.setTargetIdentifier(path);

		if (operator.needOperhand()) {
			XmlNode node = null;
			List list = operatorNode.getChildren();
			if (list.size() > 0) {

				// 拷贝出一个副本作为计算结果
				node = (XmlNode) list.get(0);

				Element nodeElement = (Element) node.rootNode;
				QName nodeName = nodeElement.getQName();
				if (nodeName.equals(referenceElementName)) { // 解析引用
					node = parseInternReference(context, node);

				} else {// 其他的节点都作为数据节点来解析
					node = (XmlNode) parseInternDataNode(context, node).cloneNode();
				}
			} else {
				node = new XmlNode(XmlDocument.createXmlText(operatorNode.getTextValue()), operatorNode.namespaces);
			}
			operator.setOperhand(node);
		}

		return operator;
	}

	/**
	 * 对设置的结果表达式进行解析,
	 * 
	 * @param dataNode
	 * @param type
	 * @param path
	 * @return
	 */
	public XmlNode parseInternDataNode(SDIContext context, XmlNode dataNode) throws SDIException {

		List children = dataNode.getChildren();
		if (children.size() > 0) {
			int size = children.size();
			for (int i = 0; i < size; i++) {
				XmlNode xn = (XmlNode) children.get(i);

				Element element = ((Element) xn.rootNode);
				QName nodeName = element.getQName();

				if (nodeName.equals(referenceElementName)) {
					// 只取出第一个节点的值作为操作对象
					XmlNode node = parseInternReference(context, xn);
					if (node.rootNode instanceof Attribute) { // 如果索引到属性，则直接修改父节点的属性

						((Element) dataNode.rootNode).remove((Element) xn.rootNode);
						Dom4JHelper.replaceAttribute((Element) dataNode.rootNode, (Attribute) node.rootNode);
					} else {// 直接替换掉原节点
						Dom4JHelper.replaceNode((Element) xn.rootNode, node.rootNode);
					}
				} else if (nodeName.equals(operatorElementName)) {
					NodeOperator operator = parseInternOperator(context, xn);

					// 需要先把节点删除掉
					Element tempRoot = (Element) dataNode.rootNode;
					tempRoot.remove((Element) xn.rootNode);

					// 临时构建一个文档对象用于计算,注意，此处是以运算器的父节点为文档的根节点，其运算的表达式也应该是以其作为根节点的XPath表达式
					XmlDocument document = XmlDocument.createXmlDocumentFromNode(dataNode);

					operator.operateWithDocument(document);
					XmlNode node = new XmlNode(document.rootNode, document.namespaces);
					Dom4JHelper.replaceNode((Element) dataNode.rootNode, document.rootNode);
					dataNode = node;

					// 需要重设子数组，否则数据会无法删除
					children = dataNode.getChildren();
					int temp = (children.size() - size);
					size += temp;

					// 调整下一步执行到的位置
					i += temp;

				} else {
					parseInternDataNode(context, xn);
				}
			}
		}

		return dataNode;
	}

	/**
	 * 根据指定的参数解析得到对应的值索引类型
	 * 
	 * @return
	 * @throws SDIException
	 */
	public XmlNode parseInternReference(SDIContext context, XmlNode node) throws SDIException {
		Element element = (Element) node.rootNode;

		String referType = element.attributeValue(referenceTypeName);

		String parameter = null;
		if (referType != null) {
			if (referType.indexOf('(') > 0) {
				parameter = referType.substring(referType.indexOf('(') + 1, referType.indexOf(')'));
				referType = referType.substring(0, referType.indexOf('('));
			}

			// 首字母必须大写，以免出现类解析错误
			referType = referType.substring(0, 1).toUpperCase() + referType.substring(1);
		}

		NodeReference reference = createValueReference(referType);
		reference.setParameter(parameter);

		reference.setIdentifier(element.attributeValue(referencePathAttributeName));
		reference.setAttributeName(element.attributeValue(referenceTargetAttributeName));
		reference.setNamespace(element.attributeValue(referencePathAttributeNamespace));

		XmlNode xmlNode = (XmlNode) reference.getReferenceValue(context, node);

		// 如果值索引有子对象，则必须为操作对象
		List list = node.getChildren();
		int size = list.size();
		XmlDocument document = null;
		
		//只有是Element时才能修改
		if (size > 0 && xmlNode.rootNode instanceof Element) {
			document = XmlDocument.createXmlDocumentFromNode(xmlNode);
		} else {
			return xmlNode;
		}

		for (int i = 0; i < size; i++) {
			XmlNode operatNode = (XmlNode) list.get(i);
			Element pElement = (Element) operatNode.rootNode;
			if (pElement.getQName().equals(operatorElementName)) {
				NodeOperator operator = parseInternOperator(context, operatNode);
				operator.operateWithDocument(document);
			}
			// 其他的类型忽略
		}
		return new XmlNode(document.getDom4jDocument().getRootElement().detach(), document.namespaces);
	}

	protected XmlInhertance inheriInstance = null;
	public Namespace nameSpace = null;
	public QName referenceElementName = null;
	public QName referenceTargetAttributeName = null;
	public QName referencePathAttributeName = null;
	public QName referencePathAttributeNamespace = null;
	public QName referenceTypeName = null;

	public QName rootElementName = null;
	public QName operatorElementName = null;
	public QName operatorTypeAttributeName = null;
	public QName operatorTargetPathAttributeName = null;

	/**
	 * 构建一个转换对象
	 * 
	 * @param inheriInstance
	 */
	public XmlConvter(XmlInhertance inheriInstance) {
		this.inheriInstance = inheriInstance;

		// 记录变化量文档的元素名
		rootElementName = new QName("operates", getNameSpace());
		operatorElementName = new QName("operate", getNameSpace());
		operatorTypeAttributeName = new QName("type", getNameSpace());
		operatorTargetPathAttributeName = new QName("path", getNameSpace());

		referenceElementName = new QName("reference", getNameSpace());
		referencePathAttributeName = new QName("path", getNameSpace());
		referenceTargetAttributeName = new QName("attribute", getNameSpace());
		referencePathAttributeNamespace = new QName("namespace", getNameSpace());
		referenceTypeName = new QName("type", getNameSpace());
	}

	/**
	 * 得到当前文档使用的NameSpace
	 * 
	 * @return
	 */
	protected Namespace getNameSpace() {
		if (inheriInstance.useNamespace && nameSpace == null) {
			nameSpace = inheriInstance.createDefaultNamespace();
		}
		return nameSpace;
	}

	/**
	 * 创建新的
	 * 
	 * @param operateType
	 * @return
	 * @throws SDIException
	 */
	protected NodeOperator createOperator(String operateType) throws SDIException {
		return (NodeOperator) ClassResolver.OPERATOR_RESOLVER.newInstance(operateType);
	}

	/**
	 * 创建值索引对象
	 * 
	 * @param referType
	 * @return
	 * @throws SDIException
	 */
	protected NodeReference createValueReference(String referType) throws SDIException {
		return (NodeReference) ClassResolver.REFERENCE_RESOLVER.newInstance(referType);
	}
}
