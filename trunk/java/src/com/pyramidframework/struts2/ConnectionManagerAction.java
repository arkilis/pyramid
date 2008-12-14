package com.pyramidframework.struts2;

import java.sql.SQLException;
import java.util.Map;

import org.springframework.beans.factory.BeanFactory;

import com.pyramidframework.jdbc.ThreadConnectionInjectManager;
import com.pyramidframework.jdbc.ThreadConnectionManager;
import com.pyramidframework.script.CompilableScriptEngine;

/**
 * 使用pyramid提供的jdbc包管理数据库连接的和事务的action
 * 
 * @author Mikab Peng
 * 
 * @see ThreadConnectionManager
 * @see ThreadConnectionInjectManager
 */
public class ConnectionManagerAction extends Struts2ScriptableAction {
	String connectionManagerBean = "connectionManager";
	
	/**
	 * 使用try-finally块管理数据库连接等关键资源
	 */
	protected Object runScript(BeanFactory beanFactory, Map scriptContext, Object script, CompilableScriptEngine scriptEngine) {
		
		ThreadConnectionManager manager = (ThreadConnectionManager)beanFactory.getBean(connectionManagerBean);
		try {
			manager.openConnection();
			
			return super.runScript(beanFactory, scriptContext, script, scriptEngine);
		}catch (RuntimeException e) {
			try {
				manager.getCurrent().rollback();
			} catch (SQLException e1) {
			}
			throw e;
		} finally{
			manager.closeConnection();
		}
		
		
	}
}
