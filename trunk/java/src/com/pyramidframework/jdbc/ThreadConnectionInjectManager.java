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
 * ʹ��ThreadLocal�������ɵ����ݿ����ӣ���spring�������singleton��.Ĭ�ϰ��ջ������ӵ��������
 * 
 * @author Mikab Peng
 * 
 */
public class ThreadConnectionInjectManager implements ThreadConnectionManager {

	private ThreadLocal conn = new ThreadLocal();
	private Map components = new WeakHashMap();
	private ConnectionProvider connectionProvider = null;// ����������
	boolean autoCommit = false;
	
	private static final Logger logger = Logger.getLogger(ThreadConnectionInjectManager.class.getName());

	/**
	 * ͨ��connectionProvider��ȡ���ӣ�����ע�����ӵ�����ע���������
	 */
	public Connection openConnection() {
		Connection connection = getCurrent();
		if (connection == null) {
			connection = openNewConnection();
			conn.set(connection);

			// ������ע�����ע������
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
			connection.setAutoCommit(autoCommit);// �Ƿ��Զ��ύ
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
		logger.info(connection.toString());
		
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
		
		logger.info(connection.toString());
		
		try {
			if (connection != null) {
				Iterator iterator = components.entrySet().iterator();
				// ������ע������������
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
			// �ر����Ӳ������
			connectionProvider.closeConnection(connection);
			conn.set(null);
		}
	}

	/**
	 * ע����Ҫע�����ӵ�bean�������ַ�ʽ��һ��manager���������ݿ�����ע�뵽����У���Ҫʵ��{@link ThreadConnectionAware}�ӿڣ�
	 * ����ʵ��ͬ������ͨ��ע��ʱʹ�ô�����󣻶���ʵ��ThreadConnectionManagerAware�ӿڣ�������ThreadConnectionManager��ȡ��ǰ�����ݿ����ӣ�
	 * ʹ�����ַ�ʽ����Ҫʵ��ThreadConnectionManagerAware�ӿ�
	 * 
	 * @param component
	 * @return �����Լ���������Spring������������
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

				// �д򿪵�������ر�
				aware.setConnection(getCurrent());
			}
		}
		return component;
	}

	/**
	 * ��ע��Ķ���ת��ΪThreadConnectionAware������
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
	 * �������ǰע��������
	 * 
	 * @param component
	 * @return ����ɹ�����true������false
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
	 * ���ӵ������Ƿ��Զ��ύ
	 * 
	 * @return
	 */
	public boolean isAutoCommit() {
		return autoCommit;
	}

	/**
	 * ���ӵ������Ƿ��Զ��ύ
	 * 
	 * @return
	 */
	public void setAutoCommit(boolean autoCommit) {
		this.autoCommit = autoCommit;
	}

}
