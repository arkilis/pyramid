package com.pyramidframework.jdbc;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.pyramidframework.proxy.ProxyHelper;

/**
 * 使用ThreadLocal管理生成的数据库连接
 * 
 * @author Mikab Peng
 * 
 */
public class ThreadConnectionInjectManager implements ThreadConnectionManager {

	private ThreadLocal conn = new ThreadLocal();
	private List components = new ArrayList();
	private ConnectionProvider connectionProvider = null;// 连接生成器

	/**
	 * 通过connectionProvider获取连接，并且注入连接到所有注册过的连接
	 */
	public Connection openConnection() {
		Connection connection = getCurrent();
		if (connection == null) {
			connection = connectionProvider.getConnection();
			conn.set(connection);
			Iterator iterator = components.iterator();

			// 给所有注册过的注入连接
			while (iterator.hasNext()) {
				ThreadConnectionAware aware = (ThreadConnectionAware) iterator.next();
				aware.setConnection(connection);
			}
		}

		return connection;
	}

	/**
	 * 获取已经为本线程打开的连接
	 */
	public Connection getCurrent() {

		return (Connection) conn.get();
	}
	/**
	 * 如果有打开的连接则关闭，并清除注册过的组件的所有连接
	 */
	public void closeConnection() {
		Connection connection = getCurrent();
		if (connection != null) {
			
			Iterator iterator = components.iterator();
			// 给所有注册过的清除连接
			while (iterator.hasNext()) {
				ThreadConnectionAware aware = (ThreadConnectionAware) iterator.next();
				aware.setConnection(null);
			}
			
			//关闭连接并且清空
			connectionProvider.closeConnection(connection);
			conn.set(null);
		}

	}

	/**
	 * 注册在过程中有多少个bean会需要注入连接
	 * 
	 * @param component
	 * @return 返回自己，可用在Spring中做工厂方法
	 */
	public Object registerComponent(Object component) {
		if (component == null) {
			throw new NullPointerException("component parameter can not be null !");
		}

		ThreadConnectionAware aware = (ThreadConnectionAware) ProxyHelper.translateObject(ThreadConnectionAware.class, component);
		if (!components.contains(aware)) {
			components.add(aware);
			
			//有打开的连接则关闭
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
