package com.pyramidframework.struts2;

import java.sql.SQLException;
import java.util.Map;

import org.springframework.beans.factory.BeanFactory;

import com.pyramidframework.jdbc.ThreadConnectionInjectManager;
import com.pyramidframework.jdbc.ThreadConnectionManager;
import com.pyramidframework.script.CompilableScriptEngine;

/**
 * ʹ��pyramid�ṩ��jdbc���������ݿ����ӵĺ������action
 * 
 * @author Mikab Peng
 * 
 * @see ThreadConnectionManager
 * @see ThreadConnectionInjectManager
 */
public class ConnectionManagerAction extends Struts2ScriptableAction {
	String connectionManagerBean = "connectionManager";
	
	/**
	 * ʹ��try-finally��������ݿ����ӵȹؼ���Դ
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
