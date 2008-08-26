package com.pyramidframework.ci;

import com.pyramidframework.sdi.xml.XmlDocument;

/**
 * �������ļ��е����ݽ������������ݵķ�ʽ
 * @author Mikab Peng
 *
 */
public interface ConfigDocumentParser {
	
	/**
	 * ���ݵõ��������ĵ��õ�������Ϣ���ڴ������ʽ
	 * @param configDocument ֱ�Ӱ���������Ϣ�Ľ������
	 * @param thisDomain ���ڵ㲻����domain��Ϣ������configdata����NULL
	 * @return
	 */
	public Object parseConfigDocument(ConfigDomain thisDomain,XmlDocument configDocument);
	
	
	
	/**
	 * �õ�Ĭ�ϵ������ĵ��������ǰ���ģ����ĵ�
	 * @return
	 */
	public XmlDocument getDefauDocument();
	
	/**
	 * �ҵ����ܶ�Ӧ�������ļ�·����
	 * �ù������ڵ�ģ�鼫���ϼ�ģ��������ļ�����������Ǳ��·����������������ܵ�·���������ļ��ڵ���ϢΪ׼
	 * @param functionPath ���������·��������Ŀ¼����ʽ
	 * @param configType ������Ϣ������
	 */
	public String getConfigFileName(String functionPath,String configType);
	
}
