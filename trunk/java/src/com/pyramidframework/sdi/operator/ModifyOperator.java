package com.pyramidframework.sdi.operator;

import com.pyramidframework.sdi.NodeOperator;
import com.pyramidframework.sdi.SDIDocument;
import com.pyramidframework.sdi.SDIException;

public class ModifyOperator extends NodeOperator {

	public boolean operateWithDocument(SDIDocument document) throws SDIException {
		
		document.setNode(this.getTargetIdentifier(),this.getOperhand());
		
		return true;
	}

}
