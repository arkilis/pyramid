package com.pyramidframework.ci;

import java.util.Map;

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
	 * �ҵ����ܶ�Ӧ�������ļ�·����
	 * �ù������ڵ�ģ�鼫���ϼ�ģ��������ļ�����������Ǳ��·����������������ܵ�·���������ļ��ڵ���ϢΪ׼
	 * @param functionPath ���������·��������Ŀ¼����ʽ
	 * @param configType ������Ϣ������
	 */
	public String getConfigFileName(String functionPath,String configType);
	
	
	/**
	 * ��ʼ��ģ�����Ե�ִ�е�������.���Խ�һЩ�������������棬�Ա���ģ����ֱ������.ģ���п���ʹ��{{��}}����Ҫִ�еĸ�ֵ�ű���������
	 * �磺<br>
	 * <code>templateContext.put("programe_name","simpleconfig");</code><br>
	 * ���������ļ��п�������ֱ��ʹ�ô˱�����<br>
	 * &lt;item name="programe_name"&gt;the programe'name is {{programe_name}} !&lt;/item&gt;</item>
	 * @param templateContext �ű������ĸ�������
	 */
	public void InitTemplateContext(Map templateContext);
	
}
