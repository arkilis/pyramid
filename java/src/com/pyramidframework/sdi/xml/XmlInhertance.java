package com.pyramidframework.sdi.xml;

import java.io.FileOutputStream;

import org.dom4j.io.XMLWriter;

import com.pyramidframework.sdi.OperatorConvter;
import com.pyramidframework.sdi.SDIDocument;
import com.pyramidframework.sdi.SDIException;
import com.pyramidframework.sdi.StructuredDocumentInheritance;

/**
 * ����XML�ļ�ת���ı�׼ʵ��
 * 
 * @author Mikab Peng
 * @version 2008-7-27
 */
public class XmlInhertance extends StructuredDocumentInheritance {

	/**
	 * StructuredDocumentInheritanceģ���Ĭ��URL
	 */
	public static final String DEFAULT_NAMESPACE_URI = "http://www.pyramidframework.com/2008/StructuredDocumentInheritance";

	/**
	 * StructuredDocumentInheritanceģ�����XML�ĵ��е�Ĭ��ǰ׺
	 */
	public static final String DEFAULT_NAMESPACE_PREFIX = "sdi";

	/** �Ƿ�ʹ�������ռ䣬Ĭ��Ϊfalse(��ʹ��) */
	protected boolean useNamespace = true;

	public boolean isUseNamespace() {
		return useNamespace;
	}

	public void setUseNamespace(boolean useNamespace) {
		this.useNamespace = useNamespace;
	}

	/**
	 * ����Ĭ�ϵ�ת���������Զ�̽���Ƿ����������ռ��ʾ
	 */
	public SDIDocument getTargetDocument(SDIDocument parent, SDIDocument rule) throws SDIException {

		if (this.DEFAULT_OPERATOR_CONVTER == null) {
			XmlDocument document = (XmlDocument) rule;

			// �ж��Ƿ���ҪĬ�������ռ�
			useNamespace = DEFAULT_NAMESPACE_URI.equals(document.namespaces.get(DEFAULT_NAMESPACE_PREFIX));

			createOperatorConvter();
		}
		
		//ע���ַ���������
		XmlDocument document = (XmlDocument)super.getTargetDocument(parent, rule);
		document.getDom4jDocument().setXMLEncoding(((XmlDocument)parent).getDom4jDocument().getXMLEncoding());
		return document;
	}

	/**
	 * ����ԭ�ļ��ͼ̳ж����ļ��õ����ת������ļ�
	 * 
	 * @param source
	 *            ԭ�ļ�
	 * @param rule
	 *            �̳ж����ļ�
	 * @return
	 * @throws SDIException
	 */
	public static XmlDocument doInheritance(XmlDocument source, XmlDocument rule) throws SDIException {
		return (XmlDocument) (new XmlInhertance()).getTargetDocument(source, rule);
	}

	/**
	 * ����������������ת����
	 */
	protected OperatorConvter createOperatorConvter() {
		if (this.DEFAULT_OPERATOR_CONVTER == null) {
			this.DEFAULT_OPERATOR_CONVTER = new XmlConvter(this);
		}
		return this.DEFAULT_OPERATOR_CONVTER;
	}

	/**
	 * ִ�У�������Ҫ���������������ļ����̳ж����ļ�������ļ�
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 3) {
			System.out.println("Usage: java com.pyramidframework.sdi.xml.XmlInhertance inFile ruleFile outFile");
		} else {
			try {
				XmlDocument parent = new XmlDocument(args[0]);
				XmlDocument rule = new XmlDocument(args[1]);
				XmlDocument target = doInheritance(parent, rule);
				FileOutputStream ou = null;
				try {
					ou = new FileOutputStream(args[2]);
					XMLWriter writer = new XMLWriter();
					writer.write(target.getDom4jDocument());
				} finally {
					if (ou != null)
						ou.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
