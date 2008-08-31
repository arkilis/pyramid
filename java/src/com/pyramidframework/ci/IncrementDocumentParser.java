package com.pyramidframework.ci;

import com.pyramidframework.sdi.NodeOperator;
import com.pyramidframework.sdi.xml.XmlNode;

/**
 * ִ������ʽ��ʵ�������Ľ�������Ľӿ�
 * 
 * @author Mikab Peng
 * 
 */
public interface IncrementDocumentParser extends ConfigDocumentParser {

	/**
	 * ��������Ϣ�ĸ�Ԫ�ؽ��н��� �˴���Ϊÿ����Ԫ�ؼ���һ�����Ա������������������õ�Ԫ
	 * 
	 * @param thisConfigData
	 * @param childOfRoot
	 * @param operator ָ���������͵Ĳ��������ǿ���ΪNULL
	 * @return
	 */
	public  Object parseIncrementElement(Object thisConfigData,XmlNode childOfRoot,NodeOperator operator);
	
	

	/**
	 * �õ�Ĭ�ϵ����ö�������һ�������������ڽ���������ʹ�����������������ݵ�������
	 * ���ʵ���˴˷���������ʵ��ConfigDocumentParser.getDefauDocument��������ʵ�ֽ����ᱻ����
	 * @param domain ��������趨���������������Ϣʱ��������ϵͳ��Ĭ���������ݣ�domainΪNULL,����Ϊ�丸�ڵ��domain
	 * @param parentDataNode �ýڵ�ָ�����ϼ��ڵ������
	 * @return
	 */
	public  Object getDefaultConfigData(ConfigDomain domain,Object parentDataNode);
}
