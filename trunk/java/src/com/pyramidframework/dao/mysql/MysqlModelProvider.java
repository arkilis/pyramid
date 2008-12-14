package com.pyramidframework.dao.mysql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.pyramidframework.dao.DAOException;
import com.pyramidframework.dao.model.DataModel;
import com.pyramidframework.dao.model.DataType;
import com.pyramidframework.dao.model.ModelField;
import com.pyramidframework.dao.model.ModelProvider;

/**
 * ��MYSQL���ݿ��л�ȡ��Ӧ�ı�ṹ
 * 
 * @author Mikab Peng
 * 
 */
public class MysqlModelProvider implements ModelProvider{

	private ThreadLocal conn = new ThreadLocal();// ����ע��

	public DataModel getModelByName(String name) {
		return getModelFromDB(name);
	}
	
	/**
	 * ʹ��MYSQL��show column����ñ�ṹ
	 * @param name
	 * @return
	 */
	protected DataModel getModelFromDB(String name) {
		String sql = "show full columns from " + name;
		ResultSet resultSet = null;
		Statement statement = null;
		try {
			statement = getConnection().createStatement();
			resultSet = statement.executeQuery(sql);

			DataModel model = new DataModel(name);

			while (resultSet.next()) {
				String fieldname = resultSet.getString(1);
				String typeName = resultSet.getString(2);
				String keyName = resultSet.getString(5);
				String extra = resultSet.getString(7);
				String comment = resultSet.getString(9);

				ModelField field = new ModelField(fieldname);
				if (typeName.indexOf('(') > 0) {

					String tName = typeName.substring(0, typeName.indexOf('('));
					// ��Ҫ��INT�ͽ��д���
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
			return model;

		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		} finally {
			MysqlVoDAO.closeDBO(statement, resultSet);
		}
	}

	public Connection getConnection() {
		return (Connection)conn.get();
	}

	public void setConnection(Connection connection) {
		this.conn.set(connection);
	}

}
