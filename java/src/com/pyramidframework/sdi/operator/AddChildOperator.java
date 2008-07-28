package com.pyramidframework.sdi.operator;

import com.pyramidframework.sdi.NodeOperator;
import com.pyramidframework.sdi.SDIDocument;
import com.pyramidframework.sdi.SDIException;

/**
 * ���һ���ĵ��Ľڵ�
 * 
 * @author Mikab Peng
 * @version 2008-7-14
 */
public class AddChildOperator extends NodeOperator {

	/**
	 * ��Ӷ�����Ҫ�ǽ����ĵ�����ķ��������
	 */
	public boolean operateWithDocument(SDIDocument document) throws SDIException {
		document.addNode(this.getTargetIdentifier(), this.getOperhand());
		return true;
	}
}
