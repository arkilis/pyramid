/*
 * �����ڣ�2008-06-22
 */
package com.pyramidframework.sdi;

/**
 * �ڼ̳й���������Ĳ���������Ϊ�ɶ�ԭ�ļ��������ŵ�����
 * 
 * @author Mikab Peng
 * @version 0.01
 */
public abstract class NodeOperator {

	protected SDINode operhand = null;			//��������ֵ����
	protected String operatorName = null;		//������������
	protected String targetIdentifier = null;	//��Ҫ�滻���ľ�ֵ�Ķ���

	/**
	 * �õ��ò�������XML�ĵ�������
	 * 
	 * @return
	 */
	public String getOperatorName()  {
		return operatorName;
	}

	/**
	 * ��Ҫ����һ������֧�ֶ��ֲ�������
	 * 
	 * @param newName
	 * @return ����˲�������ǰ������ɵ����ƣ��򷵻ؾ����ƣ����򷵻�<code>NULL</code>
	 */
	public void setOperatorName(String newName) throws SDIException {
		this.operatorName = newName;
	}

	/**
	 * �����������Ķ��󣬿���ΪNULL
	 * 
	 * @return
	 */
	public SDINode getOperhand() {
		return operhand;
	}

	public void setOperhand(SDINode operhand) {
		this.operhand = operhand;
	}

	/**
	 * ���ļ����м̳й�ϵת����������Ӧ�����ڷ��ڼ̳ж����ĵ��ĸ��ڵ���ӽڵ�ʱʹ��
	 * 
	 * @param parent
	 * @param child
	 * @param identifier
	 *            Ӧ�ô˲������ĵ����ֵı�ʾ������Ӧ��XML�ļ�ʱͨ��Ӧ����XPath���ʽ
	 * @return <code>true<code>��ʾ�ɹ�
	 * @throws SDIException
	 */
	public abstract boolean operateWithDocument(SDIDocument document) throws SDIException;
	
	
	/**
	 * �Ƿ���Ҫ�����Ķ���
	 * @return true or false,�����ɾ���Ȳ�����ӦΪfalse
	 */
	public boolean needOperhand(){
		return true;
	}
	

	public String getTargetIdentifier() {
		return targetIdentifier;
	}

	public void setTargetIdentifier(String targetIdentifier) {
		this.targetIdentifier = targetIdentifier;
	}

}
