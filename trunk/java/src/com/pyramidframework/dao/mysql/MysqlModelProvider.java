package com.pyramidframework.dao.mysql;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.pyramidframework.dao.DAOException;
import com.pyramidframework.dao.SqlDAO;
import com.pyramidframework.dao.model.DataModel;
import com.pyramidframework.dao.model.DataType;
import com.pyramidframework.dao.model.ModelField;
import com.pyramidframework.dao.model.ModelProvider;

/**
 * 从MYSQL数据库中获取对应的表结构
 * 
 * @author Mikab Peng
 * 
 */
public class MysqlModelProvider implements ModelProvider {

	private String databaseSchema = null;// 当前MYSQL用户的默认schema,
	private SqlDAO sqlDAO = null;

	public DataModel getModelByName(String name) {
		return getModelFromDB(name);
	}

	/**
	 * 使用MYSQL的show column语句获得表结构
	 * 
	 * @param name
	 * @return
	 */
	protected DataModel getModelFromDB(String name) {
		String sql = "show full columns from " + name;

		final DataModel model = new DataModel(name);

		return (DataModel) sqlDAO.queryData(sql, null, null, new SqlDAO.ResultSetHandler() {
			public Object handleResult(ResultSet resultSet) throws DAOException {
				try {
					while (resultSet.next()) {
						String fieldname = resultSet.getString(1);
						String typeName = resultSet.getString(2);
						String keyName = resultSet.getString(5);
						String extra = resultSet.getString(7);
						String comment = resultSet.getString(9);

						ModelField field = new ModelField(fieldname);
						if (typeName.indexOf('(') > 0) {

							String tName = typeName.substring(0, typeName.indexOf('('));
							// 需要对INT型进行处理
							if ("INT".equalsIgnoreCase(tName)) {
								int length = Integer.parseInt(typeName.substring(typeName.indexOf('(') + 1, typeName.indexOf(')')));
								if (length <= 10) {
									tName = "INTEGER";
								} else {
									tName = "BIGINT";
								}
							}
							typeName = tName;
						}
						field.setType(DataType.getDataType(typeName));

						if (keyName.indexOf("PRI") >= 0) {
							field.setPrimary(true);
						}

						if (extra.indexOf(MysqlVoDAO.AUTO_INCREMENT) >= 0) {
							field.setSequence(MysqlVoDAO.AUTO_INCREMENT);
						}
						if (comment != null) {
							field.setLabel(comment);
						}
						model.addModelFiled(field);
					}
				} catch (Exception e) {
					MysqlVoDAO.catchException(e);
				}
				return model;
			}
		});

	}

	/**
	 * 通知有模型产生，查找数据库中，如果没有对应的表则直接产生表<br>
	 * 注意：现在只支持整数、浮点数(double)以及日期字符串和blob5种类型
	 */
	public void setModel(DataModel model) {
		ArrayList params = new ArrayList();
		params.add(model.getModelName());
		params.add(getDatabaseSchema());
		String sql = "SELECT count(*) FROM information_schema.TABLES T where t.table_name=? and table_schema=?";

		Object object = sqlDAO.queryData(sql, params, null, new SingleObjectHandler());
		int cnt = Integer.parseInt(object.toString());

		if (cnt < 1) { // 如果没有表，则创建之
			StringBuilder builder = new StringBuilder();
			builder.append("create table ").append(model.getModelName()).append(" ( \r\n");

			List fields = model.getFiledList();
			for (int i = 0; i < fields.size(); i++) {
				ModelField field = (ModelField) fields.get(i);
				builder.append(field.getName());
				if (field.getType().getType() == DataType.INTEGER) {
					builder.append(field.getName()).append(" integer ");
				} else if (field.getType().getType() == DataType.BLOB) {
					builder.append(field.getName()).append(" blob ");
				} else if (field.getType().getType() == DataType.DOUBLE) {
					builder.append(field.getName()).append(" double ");
				} else if (field.getType().getType() == DataType.DATE) {
					builder.append(field.getName()).append(" date ");
				} else {// 定长字符串
					builder.append(field.getName()).append(" varchar(200) ");
				}

				if (field.isPrimary()) {
					builder.append(field.getName()).append(" not null ");
				}

				if (MysqlVoDAO.AUTO_INCREMENT.equals(field.getSequence())) {
					builder.append(field.getName()).append(MysqlVoDAO.AUTO_INCREMENT);
				}
				builder.append(",\r\n");
			}

			List pri = model.getPramaryKeys();
			if (pri.size() > 0) {
				builder.append(" primary key (");
				for (int i = 0; i < pri.size(); i++) {
					if (i > 0) {
						builder.append(",");
					}
					ModelField field = (ModelField) fields.get(i);
					builder.append(field.getName());
				}
				builder.append(")\r\n");
			} else {
				builder.replace(builder.length() - 3, builder.length(), "");
			}
			builder.append(")");
			sqlDAO.executeUpdate(builder.toString(), null, null);
		}

	}

	/**
	 * 得到当前的用户schema
	 * 
	 * @return
	 */
	public String getDatabaseSchema() {
		return databaseSchema;
	}

	/**
	 * 当前用户的默认schema,主要用于setmodel时创建表
	 * 
	 * @param databaseSchema
	 */
	public void setDatabaseSchema(String databaseSchema) {
		this.databaseSchema = databaseSchema;
	}

	public SqlDAO getSqlDAO() {
		return sqlDAO;
	}

	public void setSqlDAO(SqlDAO sqlDAO) {
		this.sqlDAO = sqlDAO;
	}
}
