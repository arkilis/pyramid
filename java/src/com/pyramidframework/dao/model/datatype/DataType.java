package com.pyramidframework.dao.model.datatype;

import java.lang.reflect.Field;
import java.sql.Types;

import com.pyramidframework.dao.DAOException;
import com.pyramidframework.dao.model.JDBCHandler;

/**
 * ����������ΪJDBC���������͵�һ���� ��Ҫ������SQL��ص����ݴ����С�
 * 
 * @author Mikab Peng
 * 
 */
public abstract class DataType implements JDBCHandler {

	public abstract String getTypeName();

	public abstract String toDBSchema();

	/**
	 * ���������������ֵõ���������
	 * 
	 * @param typeName
	 * @return
	 */
	public static int getJDBCTypeByName(String typeName) {
		if (typeName != null) {
			typeName = typeName.toUpperCase();

			try {
				Field field = DataType.class.getDeclaredField(typeName);
				if (!field.isAccessible()) field.setAccessible(true);

				return field.getInt(null);
			} catch (Throwable e) {
				throw new DAOException("Error to get datatype:" + typeName, e);
			}
		}
		throw new DAOException("Unknown datatype:" + typeName);
	}

	public static final int ARRAY = Types.ARRAY;
	// ��ʶһ�� SQL ���� ARRAY �� Java ��������еĳ�������ʱ��Ϊ���ʹ��룩��
	public static final int BIGINT = Types.BIGINT;
	// ��ʶһ�� SQL ���� BIGINT �� Java ��������еĳ�������ʱ��Ϊ���ʹ��룩��
	public static final int BINARY = Types.BINARY;
	// ��ʶһ�� SQL ���� BINARY �� Java ��������еĳ�������ʱ��Ϊ���ʹ��룩��
	public static final int BIT = Types.BIT;
	// ��ʶһ�� SQL ���� BIT �� Java ��������еĳ�������ʱ��Ϊ���ʹ��룩��
	public static final int BLOB = Types.BLOB;
	// ��ʶһ�� SQL ���� BLOB �� Java ��������еĳ�������ʱ��Ϊ���ʹ��룩��
	public static final int BOOLEAN = Types.BOOLEAN;
	// ��ʶһ�� SQL ���� BOOLEAN �� Java ��������еĳ�������ʱ��Ϊ���ʹ��룩��
	public static final int CHAR = Types.CHAR;
	// ��ʶһ�� SQL ���� CHAR �� Java ��������еĳ�������ʱ��Ϊ���ʹ��룩��
	public static final int CLOB = Types.CLOB;
	// ��ʶһ�� SQL ���� CLOB �� Java ��������еĳ�������ʱ��Ϊ���ʹ��룩��
	public static final int DATALINK = Types.DATALINK;
	// ��ʶһ�� SQL ���� DATALINK �� Java ��������еĳ�������ʱ��Ϊ���ʹ��룩��
	public static final int DATE = Types.DATE;
	// ��ʶһ�� SQL ���� DATE �� Java ��������еĳ�������ʱ��Ϊ���ʹ��룩��
	public static final int DECIMAL = Types.DECIMAL;
	// ��ʶһ�� SQL ���� DECIMAL �� Java ��������еĳ�������ʱ��Ϊ���ʹ��룩��
	public static final int DISTINCT = Types.DISTINCT;
	// ��ʶһ�� SQL ���� DISTINCT �� Java ��������еĳ�������ʱ��Ϊ���ʹ��룩��
	public static final int DOUBLE = Types.DOUBLE;
	// ��ʶһ�� SQL ���� DOUBLE �� Java ��������еĳ�������ʱ��Ϊ���ʹ��룩��
	public static final int FLOAT = Types.FLOAT;
	// ��ʶһ�� SQL ����; FLOAT �� Java ��������еĳ�������ʱ��Ϊ���ʹ��룩��
	public static final int INTEGER = Types.INTEGER;
	// ��ʶһ�� SQL ���� INTEGER �� Java ��������еĳ�������ʱ��Ϊ���ʹ��룩��
	public static final int JAVA_OBJECT = Types.JAVA_OBJECT;
	// ��ʶһ�� SQL ���� JAVA_OBJECT �� Java ��������еĳ�������ʱ��Ϊ���ʹ��룩��
	public static final int LONGVARBINARY = Types.LONGVARBINARY;
	// ��ʶһ�� SQL ���� LONGVARBINARY �� Java ��������еĳ�������ʱ��Ϊ���ʹ��룩��
	public static final int LONGVARCHAR = Types.LONGVARCHAR;
	// ��ʶһ�� SQL ���� LONGVARCHAR �� Java ��������еĳ�������ʱ��Ϊ���ʹ��룩��
	public static final int NULL = Types.NULL;
	// ��ʶһ�� SQL ���� NULL �� Java ��������еĳ�������ʱ��Ϊ���ʹ��룩��
	public static final int NUMERIC = Types.NUMERIC;
	// ��ʶһ�� SQL ���� NUMERIC �� Java ��������еĳ�������ʱ��Ϊ���ʹ��룩��
	public static final int OTHER = Types.OTHER;
	// Java ��������еĳ������ó���ָʾ SQL �������ض������ݿ�Ĳ��ұ�ӳ�䵽��ͨ�� getObject �� setObject �������ʵ�
	// Java ����
	public static final int REAL = Types.REAL;
	// ��ʶһ�� SQL ���� REAL �� Java ��������еĳ�������ʱ��Ϊ���ʹ��룩��
	public static final int REF = Types.REF;
	// ��ʶһ�� SQL ���� REF �� Java ��������еĳ�������ʱ��Ϊ���ʹ��룩��
	public static final int SMALLINT = Types.SMALLINT;
	// ��ʶһ�� SQL ���� SMALLINT �� Java ��������еĳ�������ʱ��Ϊ���ʹ��룩��
	public static final int STRUCT = Types.STRUCT;
	// ��ʶһ�� SQL ���� STRUCT �� Java ��������еĳ�������ʱ��Ϊ���ʹ��룩��
	public static final int TIME = Types.TIME;
	// ��ʶһ�� SQL ���� TIME �� Java ��������еĳ�������ʱ��Ϊ���ʹ��룩��
	public static final int TIMESTAMP = Types.TIMESTAMP;
	// ��ʶһ�� SQL ���� TIMESTAMP �� Java ��������еĳ�������ʱ��Ϊ���ʹ��룩��
	public static final int TINYINT = Types.TINYINT;
	// ��ʶһ�� SQL ���� TINYINT �� Java ��������еĳ�������ʱ��Ϊ���ʹ��룩��
	public static final int VARBINARY = Types.VARBINARY;
	// ��ʶһ�� SQL ���� VARBINARY �� Java ��������еĳ�������ʱ��Ϊ���ʹ��룩��
	public static final int VARCHAR = Types.VARCHAR;
	// ��ʶһ�� SQL ���� VARCHAR �� Java ��������еĳ�������ʱ��Ϊ���ʹ��룩��
	public static final int DATETIME = 1;
}
