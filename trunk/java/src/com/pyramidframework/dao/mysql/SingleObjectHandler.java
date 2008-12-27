package com.pyramidframework.dao.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.pyramidframework.dao.DAOException;
import com.pyramidframework.dao.SqlDAO.ResultSetHandler;

/**
 * ֻ����һ�����
 * 
 * @author Mikab Peng
 * 
 */
public class SingleObjectHandler implements ResultSetHandler {
	
	/**
	 * ֱ�ӷ��ص�һ�еĵ�һ����Ϊ���
	 */
	public Object handleResult(ResultSet resultSet) throws DAOException {
		try {
			if (resultSet.next()) {
				return resultSet.getObject(1);
			}
		} catch (SQLException e) {
			MysqlVoDAO.catchException(e);
		}
		return null;
	}

}
