package com.pyramidframework.dao.mysql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pyramidframework.dao.DAOException;
import com.pyramidframework.dao.SqlDAO.ResultSetHandler;

/**
 * 处理查询结果的hanlder
 * 
 * @author Mikab Peng
 * 
 */
public class QueryResultSetHandler implements ResultSetHandler {

	/**
	 * 直接返回每行一个HashMapMap&lt;String,OBject&gt;形式的List数据，
	 */
	public Object handleResult(ResultSet resultSet) throws DAOException {
		List datas = new ArrayList();
		try {
			ResultSetMetaData metaData = resultSet.getMetaData();
			int columns = metaData.getColumnCount();

			while (resultSet.next()) {
				Map row = createValueMap(datas);
				for (int i = 1; i <= columns; i++) {
					row.put(metaData.getColumnName(i), resultSet.getObject(i));
				}
			}
		} catch (SQLException e) {
			MysqlVoDAO.catchException(e);
		}
		return datas;
	}

	/**
	 * @param datas
	 * @return
	 */
	protected Map createValueMap(List datas) {
		Map row = new HashMap();
		datas.add(row);
		return row;
	}

}
