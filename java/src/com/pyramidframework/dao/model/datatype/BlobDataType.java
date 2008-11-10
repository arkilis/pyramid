package com.pyramidframework.dao.model.datatype;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.pyramidframework.dao.DAOException;
import com.pyramidframework.dao.model.DataType;

/**
 * 使用二进制数组代替
 * 
 * @author Mikab Peng
 * 
 */
public class BlobDataType extends DataType {
	
	public BlobDataType(int type) {
		this.dataType = type;
	}

	int dataType;
	
	/**
	 * 从结果集中获取参数
	 */
	public Object getData(ResultSet resultSet, int index) {

		try {
			Blob b = resultSet.getBlob(index);
			return blob2ByteArray(b);

		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	public Object getData(ResultSet resultSet, String name) {
		try {
			Blob b = resultSet.getBlob(name);
			return blob2ByteArray(b);

		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	public int getType() {
		return dataType;
	}

	public void setParameter(PreparedStatement statement, int index, Object data) {
		try {
			if (data instanceof byte[]) {
				statement.setBytes(index, (byte[]) data);
			} else if (data instanceof String) {
				statement.setBytes(index, ((String) data).getBytes());
			} else if (data instanceof InputStream) {
				statement.setBinaryStream(index, (InputStream) data, ((InputStream) data).available());
			} else if (data instanceof Blob) {
				statement.setBlob(index, (Blob) data);
			} else {
				throw new UnsupportedOperationException("blob don't support this data!");
			}
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * @param b
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	public Object blob2ByteArray(Blob b) throws SQLException, IOException {
		InputStream stream = b.getBinaryStream();
		BufferedInputStream bom = new BufferedInputStream(stream);
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		byte[] temp = new byte[9152];
		int cnt = 0;
		while ((cnt = bom.read(temp)) > 0) {
			arrayOutputStream.write(temp, 0, cnt);
		}
		return arrayOutputStream.toByteArray();
	}
	
	public int hashCode() {
		return this.dataType;
	}
	
	public boolean equals(Object obj) {
		if (obj != null){
			if (obj.getClass().equals(BlobDataType.class)){
				return this.dataType == ((BlobDataType)obj).dataType;
			}
		}
		return false;
	}
}
