package com.pyramidframework.sdi;

import java.util.List;

/**
 * �����ĵ������ɾ���Ĳ�������ָ��
 * @author Mikab Peng
 *
 */
public interface OperatorConvter {
	
	/**
	 * �õ��ĵ��п��õĲ����Ľڵ���б�
	 * @param changesDocument
	 * @return <code>List&lt;SDIDocumentNode&gt;</code>
	 * @throws SDIException
	 */
	public List getRootOperatorsList(SDIDocument changesDocument)throws SDIException;
	
	/**
	 * ����һ���ڵ�ת���ɾ���Ĳ�������
	 * @param context
	 * @param node
	 * @return
	 * @throws SDIException
	 */
	public  NodeOperator parseOperate(SDIContext context, SDINode node)throws SDIException;
}
