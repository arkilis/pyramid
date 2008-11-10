package com.pyramidframework.jdbc;

import java.sql.Connection;

/**
 * �̰߳󶨵����ݿ����ӵĹ�����.�ù�������һ��ʵ��������ʵ���ġ�
 * ��ÿ���̶߳����ø�ʵ����openConnection���ɣ��������ɶ��ʵ����
 * @author Mikab Peng
 *
 */
public interface ThreadConnectionManager {
	
	/**
	 * Ϊ���̴߳�һ������,���û�д����������½�һ�����ӣ����ؾɵ�����
	 */
	public Connection openConnection(); 
	
	
	/**
	 * �õ���ǰ�߳��ڵ����ӡ����û�е��ù�openConnection����Ӧ�÷���NULL
	 */
	public Connection getCurrent();
	
	/**
	 * ʹ��openConnection�򿪵����ӣ�����ʹ��closeConnection�رգ�������ʹ��Connection.close�����ر�
	 */
	public void closeConnection();
}
