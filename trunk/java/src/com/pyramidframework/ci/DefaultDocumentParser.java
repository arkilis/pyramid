package com.pyramidframework.ci;

import com.pyramidframework.sdi.xml.XmlDocument;

/**
 * ���ӻ�ȡ����Լ�Ĭ�ϵ�������Ϣ���ĵ��Ľӿ�
 * 
 * @author Mikab Peng
 *
 */
public interface DefaultDocumentParser extends ConfigDocumentParser {

	/**
	 * �õ����Ĭ�ϵ������ĵ��������ǰ���ģ����ĵ�
	 * @return
	 */
	public abstract XmlDocument getDefaultDocument();

}