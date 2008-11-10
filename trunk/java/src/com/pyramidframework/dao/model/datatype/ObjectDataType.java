package com.pyramidframework.dao.model.datatype;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.pyramidframework.dao.DAOException;
import com.pyramidframework.dao.model.DataType;

public class ObjectDataType extends DataType {

	public ObjectDataType(int type) {
		this.dataType = type;
	}

	int dataType;

	public Object getData(ResultSet resultSet, int index) {
		try {
			return resultSet.getObject(index);
		} catch (SQLException e) {
			throw new DAOException(e);
		}
	}

	public Object getData(ResultSet resultSet, String name) {
		try {
			return resultSet.getObject(name);
		} catch (SQLException e) {
			throw new DAOException(e);
		}
	}

	public int getType() {
		return dataType;
	}

	public void setParameter(PreparedStatement statement, int index, Object data) {
		try {
			statement.setObject(index, data);
		} catch (SQLException e) {
			throw new DAOException(e);
		}

	}
	
	public int hashCode() {
		return this.dataType;
	}
	
	public boolean equals(Object obj) {
		if (obj != null){
			if (obj.getClass().equals(ObjectDataType.class)){
				return this.dataType == ((ObjectDataType)obj).dataType;
			}
		}
		return false;
	}

}
