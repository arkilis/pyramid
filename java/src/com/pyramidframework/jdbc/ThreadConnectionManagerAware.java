package com.pyramidframework.jdbc;

/**
 * 对基于线程的连接管理器敏感。获取数据库连接时采取拉的方式从ThreadConnectionManager的实例获取。
 * ThreadConnectionManager的实例通过实现此接口注入。
 * 由于接口的参数依赖ThreadConnectionManager，从性能考虑注册时不实现代理，类必须实现此接口。
 * 
 * @author Mikab Peng
 * @see ThreadConnectionInjectManager
 */
public interface ThreadConnectionManagerAware {

	/**
	 * 注入ThreadConnectionManager管理器实例
	 * 
	 * @see ThreadConnectionInjectManager.registerComponent
	 * @param connectionManager
	 */
	public void setThreadConnectionManager(ThreadConnectionManager connectionManager);
}
