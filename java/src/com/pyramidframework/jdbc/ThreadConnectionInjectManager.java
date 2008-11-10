package com.pyramidframework.jdbc;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.pyramidframework.proxy.ProxyHelper;

/**
 * ʹ��ThreadLocal�������ɵ����ݿ�����
 * 
 * @author Mikab Peng
 * 
 */
public class ThreadConnectionInjectManager implements ThreadConnectionManager {

	private ThreadLocal conn = new ThreadLocal();
	private List components = new ArrayList();
	private ConnectionProvider connectionProvider = null;// ����������

	/**
	 * ͨ��connectionProvider��ȡ���ӣ�����ע�����ӵ�����ע���������
	 */
	public Connection openConnection() {
		Connection connection = getCurrent();
		if (connection == null) {
			connection = connectionProvider.getConnection();
			conn.set(connection);
			Iterator iterator = components.iterator();

			// ������ע�����ע������
			while (iterator.hasNext()) {
				ThreadConnectionAware aware = (ThreadConnectionAware) iterator.next();
				aware.setConnection(connection);
			}
		}

		return connection;
	}

	/**
	 * ��ȡ�Ѿ�Ϊ���̴߳򿪵�����
	 */
	public Connection getCurrent() {

		return (Connection) conn.get();
	}
	/**
	 * ����д򿪵�������رգ������ע������������������
	 */
	public void closeConnection() {
		Connection connection = getCurrent();
		if (connection != null) {
			
			Iterator iterator = components.iterator();
			// ������ע������������
			while (iterator.hasNext()) {
				ThreadConnectionAware aware = (ThreadConnectionAware) iterator.next();
				aware.setConnection(null);
			}
			
			//�ر����Ӳ������
			connectionProvider.closeConnection(connection);
			conn.set(null);
		}

	}

	/**
	 * ע���ڹ������ж��ٸ�bean����Ҫע������
	 * 
	 * @param component
	 * @return �����Լ���������Spring������������
	 */
	public Object registerComponent(Object component) {
		if (component == null) {
			throw new NullPointerException("component parameter can not be null !");
		}

		ThreadConnectionAware aware = (ThreadConnectionAware) ProxyHelper.translateObject(ThreadConnectionAware.class, component);
		if (!components.contains(aware)) {
			components.add(aware);
			
			//�д򿪵�������ر�
			aware.setConnection(getCurrent());
		}
		return component;
	}

	public ConnectionProvider getConnectionProvider() {
		return connectionProvider;
	}

	public void setConnectionProvider(ConnectionProvider connectionProvider) {
		this.connectionProvider = connectionProvider;
	}

}
