package com.pyramidframework.jdbc;

import java.sql.Connection;

/**
 * 线程绑定的数据库连接的管理器.该管理器是一个实例负责多个实例的。
 * 即每个线程都调用该实例的openConnection即可，无需生成多个实例。
 * @author Mikab Peng
 *
 */
public interface ThreadConnectionManager {
	
	/**
	 * 为本线程打开一个连接,如果没有创建过，会新建一个连接，返回旧的连接
	 */
	public Connection openConnection(); 
	
	
	/**
	 * 得到当前线程内的连接。如果没有调用过openConnection，则应该返回NULL
	 */
	public Connection getCurrent();
	
	/**
	 * 使用openConnection打开的连接，必须使用closeConnection关闭，而不能使用Connection.close方法关闭
	 */
	public void closeConnection();
}
