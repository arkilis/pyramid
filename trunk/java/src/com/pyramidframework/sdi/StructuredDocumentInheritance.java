/*
 * 创建于：2008-06-22
 */
package com.pyramidframework.sdi;

import java.util.Iterator;
import java.util.List;

/**
 * 对结构化的文档进行继承的接口声明，暂且只考虑单继承，即只有一个父(PARENT)文档。
 * 结构化文档继承是指在父文档的结构中增加、变更、删除其中的一部分的内容。 按照OO的特性，在新增和变更的过程中，可直接引用父文档的部分内容。
 * 
 * @author Mikab Peng
 * @version 0.01 2008-06-22
 */
public abstract class StructuredDocumentInheritance {

	protected OperatorConvter DEFAULT_OPERATOR_CONVTER = null;

	/**
	 * 将记录变化的XML文档转换成操作的数组
	 * 
	 * @param context
	 * @param node
	 * @return
	 * @throws SDIException
	 */
	public NodeOperator parseOperate(SDIContext context, SDINode node) throws SDIException {

		return getConvter().parseOperate(context, node);
	}


	/**
	 * 根据记录的变化，进行所需要的转换
	 * 
	 * @param parent
	 * @param changes
	 * @return
	 * @throws SDIException
	 */
	public SDIDocument getTargetDocument(SDIDocument parent, SDIDocument changes) throws SDIException {

		SDIDocument child = (SDIDocument) parent.cloneNode();
		SDIContext context = initContext(parent, changes, child);

		List changeDetails = getConvter().getRootOperatorsList(changes);

		Iterator iterator = changeDetails.iterator();

		while (iterator.hasNext()) {
			SDINode node = (SDINode) iterator.next();
			NodeOperator operator = parseOperate(context, node);

			operator.operateWithDocument(context.getChild());
		}

		return child;
	}

	/**
	 * 构建上下文
	 * 
	 * @param parent
	 * @param changes
	 * @param child
	 * @return
	 */
	protected SDIContext initContext(SDIDocument parent, SDIDocument changes, SDIDocument child) {
		SDIContext context = new SDIContext();
		context.setRule(changes);
		context.setChild(child);
		context.setParent(parent);

		return context;
	}

	/**
	 * 创建转换器
	 */
	protected abstract OperatorConvter createOperatorConvter();
	
	/**
	 * 得到转换器
	 * @return
	 */
	protected OperatorConvter getConvter() {
		if (DEFAULT_OPERATOR_CONVTER == null) {
			DEFAULT_OPERATOR_CONVTER = createOperatorConvter();
		}
		return DEFAULT_OPERATOR_CONVTER;
	}

}
