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
	 * 不需要操作的替换值对象
	 */
	public boolean needOperhand() {
		return false;
	}

}
