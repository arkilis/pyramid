package com.pyramidframework.jdbc;

import java.sql.Connection;

/**
 * ÿ���̶߳��ᱻע��һ�����ӵĽӿڡ���һ��Bean��ʵ����ͬʱ��Ӧ����̵߳��ã�����ÿ���̶߳�ʹ�õ��������ӡ�
 * ������ʹ�ô˽ӿڱ�ע�롣Ҫ����
 * @author Mikab Peng
 *
 */
public interface ThreadConnectionAware {
	
	/**
	 * �����̵߳�����ע��
	 * @param connection
	 */
	public void setConnection(Connection connection);
	
	
}
