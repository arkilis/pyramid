package com.pyramidframework.ci;

import java.util.List;

/**
 * ������Ϣ������,��Ҫ��¼��������Ϣ�̳���ص���Ϣ
 * 
 * @author Mikab Peng
 * 
 */
public abstract class ConfigDomain {

	// �Ƿ���Ҫ����ڵ�
	protected boolean cached = false;
	protected String targetPath = null; // ����ָ����������Ϣ��·��
	protected String configType = null; // ����ָ����������Ϣ������
	protected Object configData = null; // ����ľ������������

	public String getTargetPath() {
		return targetPath;
	}


	public Object getConfigData() {
		return configData;
	}
	
	/**
	 * �Ƿ񻺴�������
	 * 
	 * @return
	 */
	public boolean isCached() {
		return cached;
	}
	
	/**
	 * �õ��������ݵ�����
	 * @return
	 */
	public String getConfigType() {
		return configType;
	}


	/**
	 * �õ��ϼ�����������
	 * 
	 * @return �������㣬�򷵻�<code>NULL</code>,���򷵻��丸�������ʵ��
	 */
	public abstract ConfigDomain getParent();

	/**
	 * �õ�������������б����û���ӽڵ��򷵻�NULL
	 * 
	 * @return
	 */
	public abstract List getChildren();



}
