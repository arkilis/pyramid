package com.pyramidframework.dao.model.datatype;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pyramidframework.dao.DAOException;

/**
 * 使用二进制数组代替
 * 
 * @author Mikab Peng
 * 
 */
public class BlobDataType extends DataType {
	public static final String TYPE_NAME = "Blob";
	private int jdbcType;

	public String toDBSchema() {
		return "LONGBLOB";
	}

	public int getJDBCType() {
		return jdbcType;
	}

	public String getTypeName() {
		return TYPE_NAME;
	}

	public Object getValueFromResultSet(ResultSet resultSet, int index) throws SQLException {
		Blob blob = resultSet.getBlob(index);
		return blob == null ? null : blob2ByteArray(blob);
	}

	public void setParameter(PreparedStatement statement, int index, Object data) {
		try {
			if (data == null) {
				statement.setNull(index, getJDBCType());
			}else if (data instanceof byte[]) {
				byte[] bs = (byte[]) data;
				statement.setBytes(index, bs);
			} else if (data instanceof String) {
				statement.setBytes(index, ((String) data).getBytes());
			} else if (data instanceof InputStream) {
				statement.setBinaryStream(index, (InputStream) data, ((InputStream) data).available());
			} else if (data instanceof Blob) {
				statement.setBlob(index, (Blob) data);
			} else {
				throw new UnsupportedOperationException("blob don't support this data!" + data);
			}
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	protected Object blob2ByteArray(Blob b) throws SQLException {
		InputStream stream = b.getBinaryStream();
		BufferedInputStream bom = new BufferedInputStream(stream);
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		byte[] temp = new byte[9152];
		int cnt = 0;
		try {
			while ((cnt = bom.read(temp)) > 0) {
				arrayOutputStream.write(temp, 0, cnt);
			}
		} catch (IOException e) {// should never happen
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return arrayOutputStream.toByteArray();
	}

	public BlobDataType(int type) {
		this.jdbcType = type;
	}

	public BlobDataType() {
		this.jdbcType = DataType.BLOB;
	}

	public int getJdbcType() {
		return jdbcType;
	}

	public void setJdbcType(int jdbcType) {
		this.jdbcType = jdbcType;
	}
	
	public static DataTypeFactory getFactory(){
		Pattern pattern = Pattern.compile(".*(?i)BLOB.*");
		return new AbstractDatatypeFactory(pattern){
			protected DataType createDataType(Matcher matcher) {
				BlobDataType dataType = new BlobDataType();
				return dataType;
			}
			
			public DataType fromString(String typeDescription, Map props) {
				if (TYPE_NAME.equalsIgnoreCase(typeDescription)){
					BlobDataType dataType = new BlobDataType();
					return dataType;
				}
				return null;
			}
		};
	}

}
