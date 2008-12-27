package com.pyramidframework.dao.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pyramidframework.dao.DAOException;
import com.pyramidframework.dao.PaginatedResult;
import com.pyramidframework.dao.SqlDAO;
import com.pyramidframework.dao.VOFactory;
import com.pyramidframework.dao.VOSupport;
import com.pyramidframework.dao.model.DataModel;
import com.pyramidframework.dao.model.DataType;
import com.pyramidframework.dao.model.ModelField;
import com.pyramidframework.dao.model.ModelProvider;
import com.pyramidframework.dao.model.datatype.ObjectDataType;
import com.pyramidframework.dao.model.datatype.StringDataType;
import com.pyramidframework.jdbc.ThreadConnectionManager;
import com.pyramidframework.jdbc.ThreadConnectionManagerAware;

public class MysqlVoDAO implements SqlDAO, ThreadConnectionManagerAware {

	private static Logger logger = Logger.getLogger(MysqlVoDAO.class.getName());

	/** MYSQL中自增长列的标示 */
	public static final String AUTO_INCREMENT = "auto_increment";

	/**
	 * 执行SQL的更新
	 */
	public int executeUpdate(String sql, List paramters, List datatypes) throws DAOException {

		logger.info(sql);

		PreparedStatement statement = null;
		int result = 0;
		
		// 如果不是在外面打开的连接，则直接打开，并记得关闭
		boolean out = true;
		try {
			Connection conn = manager.getCurrent();
			if (conn == null) {
				conn = manager.openConnection();
				out = false;
			}
			statement = conn.prepareStatement(sql);
			setParameters(paramters, datatypes, statement);
			result = statement.executeUpdate();

		} catch (SQLException e) {
			catchException(e);
		} finally {
			if (!out) {
				manager.closeConnection();
			}
			closeDBO(statement, null);
		}
		return result;
	}

	/**
	 * 直接返回2维数据的形式，数据都是以getObject的形式返回的
	 * 
	 * @param sql
	 * @param pageSize
	 * @param page
	 * @return
	 * @throws DAOException
	 */
	public List queryData(String sql, List paramters, List datatypes) throws DAOException {
		Object object = queryData(sql, paramters, datatypes, QUERY_HANDLER);
		return (List) object;
	}

	/**
	 * 查询数据，数据的处理交给handler完成
	 * 
	 * @param sql
	 * @param handler
	 * @throws DAOException
	 * @return ResultSetHandler的返回值
	 */
	public Object queryData(String sql, List paramters, List datatypes, ResultSetHandler handler) throws DAOException {
		logger.info(sql);

		PreparedStatement statement = null;
		ResultSet result = null;

		// 如果不是在外面打开的连接，则直接打开，并记得关闭
		boolean out = true;
		try {
			Connection conn = manager.getCurrent();
			if (conn == null) {
				conn = manager.openConnection();
				out = false;
			}

			statement = conn.prepareStatement(sql);
			setParameters(paramters, datatypes, statement);
			result = statement.executeQuery();
			return handler.handleResult(result);
		} catch (SQLException e) {
			catchException(e);
			return null;
		} finally {
			if (!out) {
				manager.closeConnection();
			}
			closeDBO(statement, result);
		}
	}

	public Object add(Object dataObject) throws DAOException {
		VOSupport vo = voFactory.getVOSupport(dataObject);
		Map values = vo.getValues();
		DataModel model = modelProvider.getModelByName(vo.getName());

		int stringSize = model.getColumnLength() + model.getFiledList().size() * 3 + 30;
		String autoColumn = null;

		// 先处理各个sequence的值办法
		int s = model.getFiledList().size();
		for (int i = 0; i < s; i++) {
			ModelField field = (ModelField) model.getFiledList().get(i);
			if (AUTO_INCREMENT.equalsIgnoreCase(field.getSequence())) {
				autoColumn = field.getName();
			} else if (field.getSequence() != null) {
				// TODO:实现各种sequence算法
			}
		}

		StringBuffer insertSql = new StringBuffer(stringSize);
		insertSql.append("insert into ").append(vo.getName()).append("(");

		ArrayList params = new ArrayList(model.getFiledList().size());
		ArrayList datatype = new ArrayList(model.getFiledList().size());

		Iterator iterator = values.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry) iterator.next();

			ModelField field = model.getFieldByName((String) entry.getKey());
			// 没有的列跳过
			if (field == null && AUTO_INCREMENT.equalsIgnoreCase(field.getSequence())) {
				continue;
			}

			if (params.size() > 0) {
				insertSql.append(",");
			}
			insertSql.append(entry.getKey());

			Object v = entry.getValue();
			if (v == null) {
				v = NULL_OBJECT;
			}
			params.add(v);
			datatype.add(field.getType());
		}

		// 设置参数
		insertSql.append(") values(");
		for (int i = 0; i < params.size(); i++) {
			if (i > 0) {
				insertSql.append(",");
			}
			insertSql.append("?");
		}
		insertSql.append(")");

		executeUpdate(insertSql.toString(), params, datatype);

		// 自增长列需要将值返回
		if (autoColumn != null) {
			Object object = queryData("select LAST_INSERT_ID() ", null, null, IDROW_HANDLER);
			values.put(autoColumn, object);
		}

		return dataObject;
	}

	public Object update(Object dataObject) throws DAOException {
		VOSupport vo = voFactory.getVOSupport(dataObject);
		Map values = vo.getValues();
		DataModel model = modelProvider.getModelByName(vo.getName());

		int fieldCount = model.getFiledList().size();
		int prisize = model.getPramaryKeys().size();

		int stringSize = model.getColumnLength() + fieldCount * 3 + 6 * prisize + 30;
		StringBuffer updateSql = new StringBuffer(stringSize);
		updateSql.append("update ").append(vo.getName()).append(" set ");

		ArrayList params = new ArrayList(fieldCount);
		ArrayList datatype = new ArrayList(fieldCount);

		Iterator iterator = values.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry) iterator.next();

			ModelField field = model.getFieldByName((String) entry.getKey());
			// 没有的列跳过
			if (field == null || field.isPrimary()) {
				continue;
			}

			if (params.size() > 0) {
				updateSql.append(",");
			}
			updateSql.append(entry.getKey()).append("=?");

			Object v = entry.getValue();
			if (v == null) {
				v = NULL_OBJECT;
			}
			params.add(v);
			datatype.add(field.getType());
		}

		updateSql.append(" where 1=1");

		iterator = model.getPramaryKeys().iterator();
		while (iterator.hasNext()) {
			ModelField field = (ModelField) iterator.next();

			updateSql.append(" and ").append(field.getName()).append("=?");

			Object v = values.get(field.getName());
			if (v == null) {
				v = NULL_OBJECT;
			}
			params.add(v);
			datatype.add(field.getType());
		}

		executeUpdate(updateSql.toString(), params, datatype);

		return dataObject;
	}

	public int delete(Object dataObject) throws DAOException {
		VOSupport vo = voFactory.getVOSupport(dataObject);
		Map values = vo.getValues();
		DataModel model = modelProvider.getModelByName(vo.getName());

		int primarySize = model.getPramaryKeys().size();
		int deleteSize = model.getPrimaryLength() + primarySize * 7 + 20;
		StringBuffer deleBuffer = new StringBuffer(deleteSize);

		deleBuffer.append("delete from ").append(vo.getName()).append(" where ");

		ArrayList params = new ArrayList(primarySize);
		ArrayList datatype = new ArrayList(primarySize);

		// 根据主键生产查询条件
		appendPrimarycondition(model.getPramaryKeys(), deleBuffer, params, datatype, values);

		return executeUpdate(deleBuffer.toString(), params, datatype);
	}

	/**
	 * 提取数据
	 */
	public Object retrieve(String modelName, Map primaryKeyValues) throws DAOException {
		DataModel model = modelProvider.getModelByName(modelName);
		List fields = model.getFiledList();
		int prisize = model.getPramaryKeys().size();

		int preferLength = model.getColumnLength() + model.getPrimaryLength() + 6 * prisize + 2 * fields.size() + 20;
		StringBuffer selectSql = new StringBuffer(preferLength);
		selectSql.append("select ");
		for (int i = 0; i < fields.size(); i++) {
			ModelField field = (ModelField) fields.get(i);
			if (i > 0) {
				selectSql.append(",");
			}
			selectSql.append(field.getName());
		}

		selectSql.append(" from ").append(model.getModelName()).append(" where ");

		ArrayList params = new ArrayList(prisize);
		ArrayList datatype = new ArrayList(prisize);

		// 根据主键生产查询条件
		appendPrimarycondition(model.getPramaryKeys(), selectSql, params, datatype, primaryKeyValues);
		VOResultSetHandler handler = new VOResultSetHandler(voFactory, modelName);
		List vos = (List) queryData(selectSql.toString(), params, datatype, handler);
		if (vos.size() > 0) {
			return vos.get(0);
		}
		return null;
	}

	/**
	 * TODO：高级表达式
	 */
	public PaginatedResult query(String modelName, Map queryValues, String orderBy, int pageSize, int page) throws DAOException {
		DataModel model = modelProvider.getModelByName(modelName);
		List fields = model.getFiledList();
		int prisize = queryValues == null ? 0 : queryValues.size();

		int preferLength = model.getColumnLength() + model.getPrimaryLength() + 6 * prisize + 2 * fields.size() + 20;
		StringBuffer selectSql = new StringBuffer(preferLength);
		selectSql.append("select SQL_CALC_FOUND_ROWS ");
		for (int i = 0; i < fields.size(); i++) {
			ModelField field = (ModelField) fields.get(i);
			if (i > 0) {
				selectSql.append(",");
			}
			selectSql.append(field.getName());
		}

		selectSql.append(" from ").append(model.getModelName());
		ArrayList params = new ArrayList(prisize);
		ArrayList datatype = new ArrayList(prisize);

		if (prisize > 0) {
			selectSql.append(" where ");

			// 根据主键生产查询条件
			Iterator iterator = queryValues.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry entry = (Map.Entry) iterator.next();
				String name = entry.getKey().toString();

				if (model.getFieldByName(name) == null) {
					continue;
				}

				if (params.size() > 0) {
					selectSql.append(" and ");
				}
				selectSql.append(name).append("=?");

				Object v = entry.getValue();
				if (v == null) {
					v = NULL_OBJECT;
				}
				params.add(v);
				datatype.add(model.getFieldByName(name).getType());
			}
		}

		if (orderBy != null && !"".equals(orderBy)) {
			selectSql.append(" order by ").append(orderBy);
		}

		if (pageSize > 0 && page > 0) {
			selectSql.append(" limit ? ,? ");
			params.add(new Integer((page - 1) * pageSize));
			params.add(new Integer(pageSize));
			datatype.add(DataType.getDataType(DataType.INTEGER));
			datatype.add(DataType.getDataType(DataType.INTEGER));
		}

		String sql = selectSql.toString();

		VOResultSetHandler handler = new VOResultSetHandler(voFactory, modelName);
		List pageData = (List) queryData(sql, params, datatype, handler);

		int total = Integer.parseInt(queryData("SELECT FOUND_ROWS() ", null, null, IDROW_HANDLER).toString());

		return new PaginatedResult(total, pageData);
	}

	protected ThreadLocal conn = new ThreadLocal(); // 数据库连接
	protected ModelProvider modelProvider = null; // 模型访问程序
	protected VOFactory voFactory = null; // 模型构造工厂
	protected DataType STRINGTYPE = new StringDataType(DataType.VARCHAR);
	protected DataType OBJECTTYPE = new ObjectDataType(DataType.JAVA_OBJECT);
	protected ResultSetHandler IDROW_HANDLER = new SingleObjectHandler();
	protected ResultSetHandler QUERY_HANDLER = new QueryResultSetHandler();
	protected ThreadConnectionManager manager = null;

	/** 用作NULL值的内部标示,用于List等容器中占位 */
	public static final Object NULL_OBJECT = new Object(); // 

	/**
	 * 直接关闭数据库对象
	 * 
	 * @param statement
	 * @param resultSet
	 */
	public static void closeDBO(Statement statement, ResultSet resultSet) {
		try {
			if (statement != null) {
				statement.close();
			}
		} catch (SQLException e) {
		}

		try {
			if (resultSet != null) {
				resultSet.close();
			}
		} catch (SQLException e) {
		}
	}

	public ModelProvider getModelProvider() {
		return modelProvider;
	}

	public void setModelProvider(ModelProvider modelProvider) {
		this.modelProvider = modelProvider;
	}

	/**
	 * @param params
	 * @param datatype
	 * @param statement
	 */
	protected void setParameter(ArrayList params, ArrayList datatype, PreparedStatement statement) {
		for (int i = 1; i <= params.size(); i++) {
			Object j = params.get(i - 1);
			if (j == NULL_OBJECT) {
				j = null;
			}
			DataType type = (DataType) datatype.get(i - 1);
			type.setParameter(statement, i, j);
		}
	}

	/**
	 * 添加针对主键的查询条件
	 * 
	 * @param fields
	 * @param prisize
	 * @param sql
	 */
	protected void appendPrimarycondition(List fields, StringBuffer sql, ArrayList params, ArrayList datatypes, Map values) {

		int prisize = fields.size();

		for (int i = 0; i < prisize; i++) {
			ModelField field = (ModelField) fields.get(i);
			if (i > 0) {
				sql.append(" and ");
			}
			sql.append(field.getName()).append("=?");
			Object value = values.get(field.getName());
			if (value == null) {
				value = NULL_OBJECT;
			}
			params.add(value);
			datatypes.add(field.getType());
		}
	}

	/**
	 * 捕捉并处理异常
	 * 
	 * @param e
	 */
	public static void catchException(Exception e) {
		logger.log(Level.SEVERE, "error", e);
		throw new DAOException(e);
	}

	public VOFactory getVoFactory() {
		return voFactory;
	}

	public void setVoFactory(VOFactory voFactory) {
		this.voFactory = voFactory;
	}

	public void setThreadConnectionManager(ThreadConnectionManager connectionManager) {
		this.manager = connectionManager;
	}

	public ThreadConnectionManager getThreadConnectionManager() {
		return manager;
	}

	/**
	 * 设置执行的参数
	 * 
	 * @param paramters
	 * @param datatypes
	 * @param statement
	 */
	protected void setParameters(List paramters, List datatypes, PreparedStatement statement) {
		if (paramters != null) {
			int psize = paramters.size();
			int dtSize = -1;
			if (datatypes != null) {
				dtSize = datatypes.size();
			}

			for (int i = 1, f = 0; i <= psize; i++) {
				DataType dataType = null;
				Object value = paramters.get(f);
				if (value == NULL_OBJECT) {
					value = null;
				}

				if (i <= dtSize) {
					dataType = (DataType) datatypes.get(f);
				} else {
					if (value instanceof String) {
						dataType = STRINGTYPE;
					} else {
						dataType = OBJECTTYPE;
					}
				}
				dataType.setParameter(statement, i, value);
				f = i;
			}
		}
	}

}
