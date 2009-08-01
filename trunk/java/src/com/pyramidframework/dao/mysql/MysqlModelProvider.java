package com.pyramidframework.dao.mysql;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.pyramidframework.dao.DAOException;
import com.pyramidframework.dao.SqlDAO;
import com.pyramidframework.dao.model.DataModel;
import com.pyramidframework.dao.model.ModelField;
import com.pyramidframework.dao.model.ModelProvider;
import com.pyramidframework.dao.model.datatype.DataType;
import com.pyramidframework.dao.model.datatype.DataTypeFactory;
import com.pyramidframework.dao.model.datatype.SystemDataTypeFactory;

/**
 * ��MYSQL���ݿ��л�ȡ��Ӧ�ı�ṹ
 * 
 * @author Mikab Peng
 * 
 */
public class MysqlModelProvider implements ModelProvider, DataTypeFactory {

	private String databaseSchema = null;// ��ǰMYSQL�û���Ĭ��schema,
	private SqlDAO sqlDAO = null;
	private DataTypeFactory dataTypeFactory = this;

	public DataModel getModelByName(String name) {
		return getModelFromDB(name);
	}

	/**
	 * ʹ��MYSQL��show column����ñ�ṹ
	 * 
	 * @param name
	 * @return
	 */
	protected DataModel getModelFromDB(String name) {
		String sql = "show full columns from " + name;
		ArrayList params = new ArrayList();
		params.add(name);
		params.add(getDatabaseSchema());
		String cntSql = "SELECT count(*) FROM information_schema.TABLES T where t.table_name=? and table_schema=?";

		Object object = sqlDAO.queryData(cntSql, params, null, new SingleObjectHandler());
		int cnt = Integer.parseInt(object.toString());
		if (cnt <=0 ){
			return null;
		}

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
						field.setType(dataTypeFactory.fromDBSchema(typeName));
						if (keyName.indexOf("PRI") >= 0) field.setPrimary(true);
						if (extra.indexOf(MysqlVoDAO.AUTO_INCREMENT) >= 0) field.setSequence(MysqlVoDAO.AUTO_INCREMENT);
						if (comment != null) field.setLabel(comment);
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
	 * ֪ͨ��ģ�Ͳ������������ݿ��У����û�ж�Ӧ�ı���ֱ�Ӳ�����<br>
	 * ע�⣺����ֻ֧��������������(double)�Լ������ַ�����blob5������
	 */
	public void setModel(DataModel model) {
		ArrayList params = new ArrayList();
		params.add(model.getModelName());
		params.add(getDatabaseSchema());
		String sql = "SELECT count(*) FROM information_schema.TABLES T where t.table_name=? and table_schema=?";

		Object object = sqlDAO.queryData(sql, params, null, new SingleObjectHandler());
		int cnt = Integer.parseInt(object.toString());

		if (cnt < 1) { // ���û�б��򴴽�֮
			StringBuilder builder = new StringBuilder();
			builder.append("create table ").append(model.getModelName()).append(" ( \r\n");

			List fields = model.getFiledList();
			for (int i = 0; i < fields.size(); i++) {
				ModelField field = (ModelField) fields.get(i);
				builder.append(field.getName()).append(" ");
				builder.append(field.getType().toDBSchema()).append(" ");

				if (field.isPrimary()) builder.append(" not null ");
				if (MysqlVoDAO.AUTO_INCREMENT.equals(field.getSequence())) builder.append(MysqlVoDAO.AUTO_INCREMENT);
				String l = field.getLabel();
				if (l != null && l.length() > 0) builder.append(" COMMENT '").append(l.replace("'", "''")).append("' ");

				builder.append(",\r\n");
			}

			List pri = model.getPramaryKeys();
			if (pri.size() > 0) {
				builder.append(" primary key (");
				for (int i = 0; i < pri.size(); i++) {
					if (i > 0) builder.append(",");

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

	public DataType fromDBSchema(String columnDescription) {
		return SystemDataTypeFactory.getInstance().fromDBSchema(columnDescription);
	}

	public DataType fromString(String typeDescription, Map props) {
		return SystemDataTypeFactory.getInstance().fromString(typeDescription, props);
	}

	public DataTypeFactory getDataTypeFactory() {
		return dataTypeFactory;
	}

	public void setDataTypeFactory(DataTypeFactory dataTypeFactory) {
		this.dataTypeFactory = dataTypeFactory;
	}

	/**
	 * �õ���ǰ���û�schema
	 * 
	 * @return
	 */
	public String getDatabaseSchema() {
		return databaseSchema;
	}

	/**
	 * ��ǰ�û���Ĭ��schema,��Ҫ����setmodelʱ������
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
