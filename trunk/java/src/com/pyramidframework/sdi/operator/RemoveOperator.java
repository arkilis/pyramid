package com.pyramidframework.sdi.operator;

import com.pyramidframework.sdi.NodeOperator;
import com.pyramidframework.sdi.SDIDocument;
import com.pyramidframework.sdi.SDIException;

/**
 * @author Mikab Peng
 */
public class RemoveOperator extends NodeOperator {

	public boolean operateWithDocument(SDIDocument document) throws SDIException {

		document.removeNode(this.getTargetIdentifier());

		return true;
	}

	/**
	 * ����Ҫ�������滻ֵ����
	 */
	public boolean needOperhand() {
		return false;
	}

}
