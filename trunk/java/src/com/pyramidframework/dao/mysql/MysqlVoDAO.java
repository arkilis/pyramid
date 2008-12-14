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
import java.util.logging.Logger;

import com.pyramidframework.dao.DAOException;
import com.pyramidframework.dao.VOFactory;
import com.pyramidframework.dao.VOSupport;
import com.pyramidframework.dao.ValueObjectDAO;
import com.pyramidframework.dao.model.DataModel;
import com.pyramidframework.dao.model.DataType;
import com.pyramidframework.dao.model.ModelField;
import com.pyramidframework.dao.model.ModelProvider;

public class MysqlVoDAO implements ValueObjectDAO {

	private static Logger logger = Logger.getLogger(MysqlVoDAO.class.getName());

	/** MYSQL中自增长列的标示 */
	public static final String AUTO_INCREMENT = "auto_increment";

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
			if (field == null && field.getSequence() != null) {
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

		ResultSet rst = null;
		PreparedStatement statement = null;

		try {
			String sql = insertSql.toString();
			statement = getConnection().prepareStatement(sql);

			// TODO:LOG
			logger.info(sql + params);

			setParameter(params, datatype, statement);
			statement.execute();

			if (autoColumn != null) {

				rst = statement.getGeneratedKeys();
				if (rst.next()) {
					values.put(autoColumn, rst.getObject(1));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DAOException(e);
		} finally {
			closeDBO(statement, rst);
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
		String sql = updateSql.toString();
		// TODO:LOG
		logger.info(sql + params);

		executeUpdate(sql, params, datatype);

		return dataObject;
	}

	public Object delete(Object dataObject) throws DAOException {
		VOSupport vo = voFactory.getVOSupport(dataObject);
		Map values = vo.getValues();
		DataModel model = modelProvider.getModelByName(vo.getName());

		int primarySize = model.getPramaryKeys().size();
		int deleteSize = model.getPrimaryLength() + primarySize * 7 + 20;
		StringBuffer deleBuffer = new StringBuffer(deleteSize);

		deleBuffer.append("delete from ").append(vo.getName()).append(" where ");

		// 根据主键生产查询条件
		appendPrimarycondition(model.getPramaryKeys(), deleBuffer);
		PreparedStatement statement = null;
		try {

			String sql = deleBuffer.toString();
			// TODO:LOG
			logger.info(sql + values);

			statement = getConnection().prepareStatement(sql);
			setPrimaryParameter(values, model.getPramaryKeys(), statement);
			statement.execute();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new DAOException(e);
		} finally {
			closeDBO(statement, null);
		}

		return dataObject;
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

		// 根据主键生产查询条件
		appendPrimarycondition(model.getPramaryKeys(), selectSql);

		String sql = selectSql.toString();
		// TODO:LOG
		logger.info(sql + primaryKeyValues);

		// 设置查询结果
		PreparedStatement statement = null;
		ResultSet rst = null;
		try {
			statement = getConnection().prepareStatement(sql);
			setPrimaryParameter(primaryKeyValues, model.getPramaryKeys(), statement);
			rst = statement.executeQuery();

			if (rst.next()) {// 如果有查到，则取第一个查询结果
				VOSupport vo = voFactory.getVOSupport(modelName);
				Map map = vo.getValues();
				for (int i = 0; i < fields.size(); i++) {
					ModelField field = (ModelField) fields.get(i);
					map.put(field.getName(), field.getType().getData(rst, i + 1));
				}
				return vo;
			} else {
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DAOException(e);
		} finally {
			closeDBO(statement, rst);
		}
	}

	/**
	 * TODO：高级表达式
	 */
	public List query(String modelName, Map queryValues,String orderBy, int pageSize, int page) throws DAOException {
		DataModel model = modelProvider.getModelByName(modelName);
		List fields = model.getFiledList();
		int prisize = queryValues == null ? 0 : queryValues.size();

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
		
		if (orderBy != null && !"".equals(orderBy)){
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
		// TODO:LOG
		logger.info(sql + params);

		// 设置查询结果
		PreparedStatement statement = null;
		ResultSet rst = null;
		try {

			statement = getConnection().prepareStatement(sql);
			setParameter(params, datatype, statement);
			rst = statement.executeQuery();

			List datas = new ArrayList();
			while (rst.next()) {// 如果有查到，则取第一个查询结果
				VOSupport vo = voFactory.getVOSupport(modelName);
				Map map = vo.getValues();
				for (int i = 0; i < fields.size(); i++) {
					ModelField field = (ModelField) fields.get(i);
					map.put(field.getName(), field.getType().getData(rst, i + 1));
				}
				datas.add(vo);
			}
			return datas;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DAOException(e);
		} finally {
			closeDBO(statement, rst);
		}
	}

	protected ThreadLocal conn = new ThreadLocal(); // 数据库连接
	protected ModelProvider modelProvider = null; // 模型访问程序
	protected VOFactory voFactory = null; // 模型构造工厂
	protected static final Object NULL_OBJECT = new Object(); // 用作NULL值的内部标示

	public Connection getConnection() {
		return (Connection) conn.get();
	}

	public void setConnection(Connection connection) {
		this.conn.set(connection);
	}

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
	 * @param sql
	 * @param params
	 * @param datatype
	 */
	protected void executeUpdate(String sql, ArrayList params, ArrayList datatype) {
		PreparedStatement statement = null;
		try {
			statement = getConnection().prepareStatement(sql);
			setParameter(params, datatype, statement);
			statement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DAOException(e);
		} finally {
			closeDBO(statement, null);
		}
	}

	/**
	 * 设置主键的查询条件值
	 * 
	 * @param values
	 * @param fields
	 * @param prisize
	 * @param statement
	 */
	protected void setPrimaryParameter(Map values, List fields, PreparedStatement statement) {
		int prisize = fields.size();
		for (int i = 0; i < prisize; i++) {
			ModelField field = (ModelField) fields.get(i);
			Object j = values.get(field.getName());
			DataType type = field.getType();
			type.setParameter(statement, i + 1, j);
		}
	}

	/**
	 * 添加针对主键的查询条件
	 * 
	 * @param fields
	 * @param prisize
	 * @param sql
	 */
	protected void appendPrimarycondition(List fields, StringBuffer sql) {

		int prisize = fields.size();

		for (int i = 0; i < prisize; i++) {
			ModelField field = (ModelField) fields.get(i);
			if (i > 0) {
				sql.append(" and ");
			}
			sql.append(field.getName()).append("=?");
		}
	}

	public VOFactory getVoFactory() {
		return voFactory;
	}

	public void setVoFactory(VOFactory voFactory) {
		this.voFactory = voFactory;
	}

}
