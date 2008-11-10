package com.pyramidframework.dao.model.datatype;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.pyramidframework.dao.DAOException;
import com.pyramidframework.dao.model.DataType;

/**
 * 字符串相关的类型
 * 
 * @author Mikab Peng
 * 
 */
public class StringDataType extends DataType {

	public StringDataType(int type) {
		this.dataType = type;
	}

	int dataType;

	public Object getData(ResultSet resultSet, int index) {
		try {
			return resultSet.getString(index);
		} catch (SQLException e) {
			throw new DAOException(e);
		}
	}

	public Object getData(ResultSet resultSet, String name) {
		try {
			return resultSet.getString(name);
		} catch (SQLException e) {
			throw new DAOException(e);
		}
	}

	public int getType() {
		return dataType;
	}

	public void setParameter(PreparedStatement statement, int index, Object data) {
		try {
			if (data != null) {
				statement.setString(index, data.toString());
			} else {
				statement.setString(index, null);
			}
		} catch (SQLException e) {
			throw new DAOException(e);
		}
	}

	public int hashCode() {
		return this.dataType;
	}

	public boolean equals(Object obj) {
		if (obj != null) {
			if (obj.getClass().equals(StringDataType.class)) {
				return this.dataType == ((StringDataType) obj).dataType;
			}
		}
		return false;
	}

}
