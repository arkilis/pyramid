package com.pyramidframework.dao.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.pyramidframework.dao.DAOException;
import com.pyramidframework.dao.SqlDAO.ResultSetHandler;

/**
 * 只返回一个结果
 * 
 * @author Mikab Peng
 * 
 */
public class SingleObjectHandler implements ResultSetHandler {
	
	/**
	 * 直接返回第一行的第一列作为结果
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
