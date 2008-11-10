package com.pyramidframework.dao.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.pyramidframework.dao.model.datatype.BlobDataType;
import com.pyramidframework.dao.model.datatype.ObjectDataType;
import com.pyramidframework.dao.model.datatype.StringDataType;

/**
 * 该数据类型为JDBC的数据类型的一部分
 * 主要是用在SQL相关的数据处理中。
 * @author Mikab Peng
 *
 */
public abstract class DataType {
	
	public abstract int getType();
	
	public abstract Object getData(ResultSet resultSet,int index);
	public abstract Object getData(ResultSet resultSet,String name);
	
	public abstract void setParameter(PreparedStatement statement,int index,Object data);
	
	/**
	 * 根据数据类型名字得到数据类型
	 * @param typeName
	 * @return
	 */
	public static DataType getDataType(String typeName){
		if(typeName != null ){
			return (DataType)name2types.get(typeName.toUpperCase());
		}
		return null;
	}
	
	/**
	 * 根据数据类型获得
	 * @param type
	 * @return
	 */
	public static DataType getDataType(int type){
		Iterator iterator = name2types.entrySet().iterator();
		while (iterator.hasNext()) {
			DataType elem = (DataType) ((Map.Entry)iterator.next()).getValue();
			if (elem.getType() ==type){
				return elem;
			}
		}
		throw new UnsupportedOperationException("unknown datatype " + type);
	}
	

	public static final int ARRAY = Types.ARRAY;
	// 标识一般 SQL 类型 ARRAY 的 Java 编程语言中的常量（有时称为类型代码）。
	public static final int BIGINT = Types.BIGINT;
	// 标识一般 SQL 类型 BIGINT 的 Java 编程语言中的常量（有时称为类型代码）。
	public static final int BINARY = Types.BINARY;
	// 标识一般 SQL 类型 BINARY 的 Java 编程语言中的常量（有时称为类型代码）。
	public  static final int BIT = Types.BIT;
	// 标识一般 SQL 类型 BIT 的 Java 编程语言中的常量（有时称为类型代码）。
	public static final int BLOB = Types.BLOB;
	// 标识一般 SQL 类型 BLOB 的 Java 编程语言中的常量（有时称为类型代码）。
	public static final int BOOLEAN = Types.BOOLEAN;
	// 标识一般 SQL 类型 BOOLEAN 的 Java 编程语言中的常量（有时称为类型代码）。
	public static final int CHAR = Types.CHAR;
	// 标识一般 SQL 类型 CHAR 的 Java 编程语言中的常量（有时称为类型代码）。
	public static final int CLOB = Types.CLOB;
	// 标识一般 SQL 类型 CLOB 的 Java 编程语言中的常量（有时称为类型代码）。
	public static final int DATALINK = Types.DATALINK;
	// 标识一般 SQL 类型 DATALINK 的 Java 编程语言中的常量（有时称为类型代码）。
	public static final int DATE = Types.DATE;
	// 标识一般 SQL 类型 DATE 的 Java 编程语言中的常量（有时称为类型代码）。
	public static final int DECIMAL = Types.DECIMAL;
	// 标识一般 SQL 类型 DECIMAL 的 Java 编程语言中的常量（有时称为类型代码）。
	public static final int DISTINCT= Types.DISTINCT;
	// 标识一般 SQL 类型 DISTINCT 的 Java 编程语言中的常量（有时称为类型代码）。
	public static final int DOUBLE= Types.DOUBLE;
	// 标识一般 SQL 类型 DOUBLE 的 Java 编程语言中的常量（有时称为类型代码）。
	public static final int FLOAT= Types.FLOAT;
	// 标识一般 SQL 类型; FLOAT 的 Java 编程语言中的常量（有时称为类型代码）。
	public static final int INTEGER= Types.INTEGER;
	// 标识一般 SQL 类型 INTEGER 的 Java 编程语言中的常量（有时称为类型代码）。
	public static final int JAVA_OBJECT= Types.JAVA_OBJECT;
	// 标识一般 SQL 类型 JAVA_OBJECT 的 Java 编程语言中的常量（有时称为类型代码）。
	public static final int LONGVARBINARY= Types.LONGVARBINARY;
	// 标识一般 SQL 类型 LONGVARBINARY 的 Java 编程语言中的常量（有时称为类型代码）。
	public static final int LONGVARCHAR= Types.LONGVARCHAR;
	// 标识一般 SQL 类型 LONGVARCHAR 的 Java 编程语言中的常量（有时称为类型代码）。
	public static final int NULL= Types.NULL;
	// 标识一般 SQL 类型 NULL 的 Java 编程语言中的常量（有时称为类型代码）。
	public static final int NUMERIC= Types.NUMERIC;
	// 标识一般 SQL 类型 NUMERIC 的 Java 编程语言中的常量（有时称为类型代码）。
	public static final int OTHER= Types.OTHER;
	// Java 编程语言中的常量，该常量指示 SQL 类型是特定于数据库的并且被映射到可通过 getObject 和 setObject 方法访问的
	// Java 对象。
	public static final int REAL= Types.REAL;
	// 标识一般 SQL 类型 REAL 的 Java 编程语言中的常量（有时称为类型代码）。
	public static final int REF= Types.REF;
	// 标识一般 SQL 类型 REF 的 Java 编程语言中的常量（有时称为类型代码）。
	public  static final int SMALLINT= Types.SMALLINT;
	// 标识一般 SQL 类型 SMALLINT 的 Java 编程语言中的常量（有时称为类型代码）。
	public  static final int STRUCT= Types.STRUCT;
	// 标识一般 SQL 类型 STRUCT 的 Java 编程语言中的常量（有时称为类型代码）。
	public  static final int TIME= Types.TIME;
	// 标识一般 SQL 类型 TIME 的 Java 编程语言中的常量（有时称为类型代码）。
	public static final int TIMESTAMP= Types.TIMESTAMP;
	// 标识一般 SQL 类型 TIMESTAMP 的 Java 编程语言中的常量（有时称为类型代码）。
	public static final int TINYINT= Types.TINYINT;
	// 标识一般 SQL 类型 TINYINT 的 Java 编程语言中的常量（有时称为类型代码）。
	public static final int VARBINARY= Types.VARBINARY;
	// 标识一般 SQL 类型 VARBINARY 的 Java 编程语言中的常量（有时称为类型代码）。
	public static  final int VARCHAR= Types.VARCHAR;
	// 标识一般 SQL 类型 VARCHAR 的 Java 编程语言中的常量（有时称为类型代码）。
	
	static HashMap name2types = new HashMap();
	static{
		//字符串
		name2types.put("VARCHAR", new StringDataType(VARCHAR));
		
		name2types.put("CHAR", new StringDataType(CHAR));
		
		//数字
		name2types.put("BIGINT", new ObjectDataType(BIGINT));
		
		name2types.put("DOUBLE", new ObjectDataType(DOUBLE));
		
		name2types.put("INTEGER", new ObjectDataType(INTEGER));
		
		name2types.put("FLOAT", new ObjectDataType(FLOAT));
		
		//日期
		name2types.put("DATE", new ObjectDataType(DATE));
		
		//blob
		name2types.put("BLOB", new BlobDataType(BLOB));
		name2types.put("LONGVARBINARY", new BlobDataType(LONGVARBINARY));
	}
	

}
