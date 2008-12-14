package com.pyramidframework.jdbc;

import java.lang.reflect.InvocationHandler;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Logger;

import com.pyramidframework.proxy.ProxyHelper;
import com.pyramidframework.proxy.SingleMethodProxyHandler;

/**
 * 使用ThreadLocal管理生成的数据库连接，在spring中最好是singleton的.默认按照基于连接的事务管理
 * 
 * @author Mikab Peng
 * 
 */
public class ThreadConnectionInjectManager implements ThreadConnectionManager {

	private ThreadLocal conn = new ThreadLocal();
	private Map components = new WeakHashMap();
	private ConnectionProvider connectionProvider = null;// 连接生成器
	boolean autoCommit = false;
	
	private static final Logger logger = Logger.getLogger(ThreadConnectionInjectManager.class.getName());

	/**
	 * 通过connectionProvider获取连接，并且注入连接到所有注册过的连接
	 */
	public Connection openConnection() {
		Connection connection = getCurrent();
		if (connection == null) {
			connection = openNewConnection();
			conn.set(connection);

			// 给所有注册过的注入连接
			Iterator iterator = components.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry entry = (Map.Entry) iterator.next();
				ThreadConnectionAware aware = (ThreadConnectionAware) entry.getValue();
				aware.setConnection(connection);
			}
		}
		
		logger.info(connection.toString());

		return connection;
	}

	/**
	 * @return
	 */
	protected Connection openNewConnection() {
		Connection connection = connectionProvider.getConnection();
		try {
			connection.setAutoCommit(autoCommit);// 是否自动提交
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
		logger.info(connection.toString());
		
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
		
		logger.info(connection.toString());
		
		try {
			if (connection != null) {
				Iterator iterator = components.entrySet().iterator();
				// 给所有注册过的清除连接
				while (iterator.hasNext()) {
					Map.Entry entry = (Map.Entry) iterator.next();
					ThreadConnectionAware aware = (ThreadConnectionAware) entry.getValue();
					aware.setConnection(null);
				}

				if (!autoCommit) {
					connection.commit();
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			// 关闭连接并且清空
			connectionProvider.closeConnection(connection);
			conn.set(null);
		}
	}

	/**
	 * 注册需要注入连接的bean。有两种方式，一是manager主动将数据库连接注入到组件中，需要实现{@link ThreadConnectionAware}接口，
	 * 或者实现同名函数通过注册时使用代理对象；二是实现ThreadConnectionManagerAware接口，主动从ThreadConnectionManager获取当前的数据库连接，
	 * 使用这种方式必须要实现ThreadConnectionManagerAware接口
	 * 
	 * @param component
	 * @return 返回自己，可用在Spring中做工厂方法
	 */
	public Object registerComponent(Object component) {
		if (component == null) {
			throw new NullPointerException("component parameter can not be null !");
		}
		
		logger.info(component.toString());

		if (ProxyHelper.canTranslate(ThreadConnectionManagerAware.class, component)) {
			Object aware = ProxyHelper.translateObject(ThreadConnectionManagerAware.class, component);
			((ThreadConnectionManagerAware) aware).setThreadConnectionManager(this);

		} else {
			ThreadConnectionAware aware = translateToAware(component);
			if (!components.containsKey(component)) {
				components.put(component, aware);

				// 有打开的连接则关闭
				aware.setConnection(getCurrent());
			}
		}
		return component;
	}

	/**
	 * 将注册的对象转换为ThreadConnectionAware的类型
	 * 
	 * @param component
	 * @return
	 */
	protected ThreadConnectionAware translateToAware(Object component) {
		if (component instanceof ThreadConnectionAware) {
			return (ThreadConnectionAware) component;
		}

		InvocationHandler handler = new SingleMethodProxyHandler(component);
		return (ThreadConnectionAware) ProxyHelper.translateObject(ThreadConnectionAware.class, handler, component);
	}

	/**
	 * 清除掉以前注册过的组件
	 * 
	 * @param component
	 * @return 如果成功返回true，否则false
	 */
	public boolean unRegisterComponent(Object component) {
		
		logger.info(component.toString());
		
		if (component != null && components.containsKey(component)) {

			ThreadConnectionAware aware = (ThreadConnectionAware) components.get(component);
			aware.setConnection(null);

			components.remove(component);
			return true;
		}
		return false;
	}

	public ConnectionProvider getConnectionProvider() {
		return connectionProvider;
	}

	public void setConnectionProvider(ConnectionProvider connectionProvider) {
		this.connectionProvider = connectionProvider;
	}

	/**
	 * 连接的事务是否自动提交
	 * 
	 * @return
	 */
	public boolean isAutoCommit() {
		return autoCommit;
	}

	/**
	 * 连接的事务是否自动提交
	 * 
	 * @return
	 */
	public void setAutoCommit(boolean autoCommit) {
		this.autoCommit = autoCommit;
	}

}
