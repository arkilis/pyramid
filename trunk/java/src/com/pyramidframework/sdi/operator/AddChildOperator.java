package com.pyramidframework.sdi.operator;

import com.pyramidframework.sdi.NodeOperator;
import com.pyramidframework.sdi.SDIDocument;
import com.pyramidframework.sdi.SDIException;

/**
 * 添加一个文档的节点
 * 
 * @author Mikab Peng
 * @version 2008-7-14
 */
public class AddChildOperator extends NodeOperator {

	/**
	 * 添加对象主要是借助文档对象的方法来添加
	 */
	public boolean operateWithDocument(SDIDocument document) throws SDIException {
		document.addNode(this.getTargetIdentifier(), this.getOperhand());
		return true;
	}
}
