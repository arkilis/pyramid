/*
 * �����ڣ�2008-06-22
 */
package com.pyramidframework.sdi;

import java.util.Iterator;
import java.util.List;

/**
 * �Խṹ�����ĵ����м̳еĽӿ�����������ֻ���ǵ��̳У���ֻ��һ����(PARENT)�ĵ���
 * �ṹ���ĵ��̳���ָ�ڸ��ĵ��Ľṹ�����ӡ������ɾ�����е�һ���ֵ����ݡ� ����OO�����ԣ��������ͱ���Ĺ����У���ֱ�����ø��ĵ��Ĳ������ݡ�
 * 
 * @author Mikab Peng
 * @version 0.01 2008-06-22
 */
public abstract class StructuredDocumentInheritance {

	protected OperatorConvter DEFAULT_OPERATOR_CONVTER = null;

	/**
	 * ����¼�仯��XML�ĵ�ת���ɲ���������
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
	 * ���ݼ�¼�ı仯����������Ҫ��ת��
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
	 * ����������
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
	 * ����ת����
	 */
	protected abstract OperatorConvter createOperatorConvter();
	
	/**
	 * �õ�ת����
	 * @return
	 */
	protected OperatorConvter getConvter() {
		if (DEFAULT_OPERATOR_CONVTER == null) {
			DEFAULT_OPERATOR_CONVTER = createOperatorConvter();
		}
		return DEFAULT_OPERATOR_CONVTER;
	}

}
