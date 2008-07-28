package com.pyramidframework.sdi;

import java.util.List;

/**
 * 根据文档解析成具体的操作符的指令
 * @author Mikab Peng
 *
 */
public interface OperatorConvter {
	
	/**
	 * 得到文档中可用的操作的节点的列表
	 * @param changesDocument
	 * @return <code>List&lt;SDIDocumentNode&gt;</code>
	 * @throws SDIException
	 */
	public List getRootOperatorsList(SDIDocument changesDocument)throws SDIException;
	
	/**
	 * 根据一个节点转换成具体的操作对象
	 * @param context
	 * @param node
	 * @return
	 * @throws SDIException
	 */
	public  NodeOperator parseOperate(SDIContext context, SDINode node)throws SDIException;
}
