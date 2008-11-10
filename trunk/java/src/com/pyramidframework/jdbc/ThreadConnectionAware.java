package com.pyramidframework.jdbc;

import java.sql.Connection;

/**
 * 每个线程都会被注入一个连接的接口。即一个Bean的实例可同时响应多个线程调用，但是每个线程都使用单独的连接。
 * 其连接使用此接口被注入。要求其
 * @author Mikab Peng
 *
 */
public interface ThreadConnectionAware {
	
	/**
	 * 将本线程的连接注入
	 * @param connection
	 */
	public void setConnection(Connection connection);
	
	
}
