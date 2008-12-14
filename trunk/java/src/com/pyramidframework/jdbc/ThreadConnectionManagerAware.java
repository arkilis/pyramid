package com.pyramidframework.jdbc;

/**
 * �Ի����̵߳����ӹ��������С���ȡ���ݿ�����ʱ��ȡ���ķ�ʽ��ThreadConnectionManager��ʵ����ȡ��
 * ThreadConnectionManager��ʵ��ͨ��ʵ�ִ˽ӿ�ע�롣
 * ���ڽӿڵĲ�������ThreadConnectionManager�������ܿ���ע��ʱ��ʵ�ִ��������ʵ�ִ˽ӿڡ�
 * 
 * @author Mikab Peng
 * @see ThreadConnectionInjectManager
 */
public interface ThreadConnectionManagerAware {

	/**
	 * ע��ThreadConnectionManager������ʵ��
	 * 
	 * @see ThreadConnectionInjectManager.registerComponent
	 * @param connectionManager
	 */
	public void setThreadConnectionManager(ThreadConnectionManager connectionManager);
}
