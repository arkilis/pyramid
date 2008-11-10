package com.pyramidframework.dao.mysql;

import java.util.List;
import java.util.Map;

import com.pyramidframework.dao.DAOException;
import com.pyramidframework.dao.SqlDAO;

public class MysqlDAO extends MysqlVoDAO implements SqlDAO {

	public Object[][] queryData(String sql, int pageSize, int page) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	public void queryData(String sql, int pageSize, int page, ResultSetHandler handler) throws DAOException {
		// TODO Auto-generated method stub

	}

	public List query(String queryName, Map queryParameters, int pageSize, int page) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

}
