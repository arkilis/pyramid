package com.pyramidframework.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pyramidframework.dao.model.datatype.DataType;
import com.pyramidframework.dao.model.datatype.StringDataType;
import com.pyramidframework.dao.model.datatype.SystemDataTypeFactory;

/**
 * 支持特定的SQL解析方式：<br>
 * select * from table where 1=1 #and field=[param]# #and
 * field=[name=param,type=string]# #and field=$value$#<br>
 * 
 * 其中#标示活动区域，[]标示可作为参数的传递，$$是做为文本替换，连续的## 表示实际的一个#
 * 
 * @author Administrator
 * 
 */
public class SqlTextUtil {

	/**
	 * 
	 * @param rawSql
	 * @param params
	 * @param dataTypes
	 * @param paramValues
	 * @return
	 */
	public static String parseSQL(String rawSql, Map params, List dataTypes, List paramValues) {
		StringBuilder sql = new StringBuilder(rawSql);
		int length = sql.length();
		for (int start = 0; start < length; start++) {
			if (sql.charAt(start) == '#') {
				for (int end = start + 1; end < length; end++) {

					if (sql.charAt(end) == '#') {
						int old = length;
						if (end == start + 1) {
							sql.replace(start, end + 1, "#");
							length--;
						} else {
							String express = sql.substring(start + 1, end);
							express = parseExpression(express, params, dataTypes, paramValues);
							sql.replace(start, end + 1, express);
							length = sql.length();
						}
						start = end - old + length;

						break;
					}
				}

			}
		}

		return sql.toString();
	}

	protected static String parseExpression(String express, Map params, List dataTypes, List paramValues) {

		StringBuilder builder = new StringBuilder(express);
		int length = builder.length();
		for (int start = 0; start < length; start++) {
			if (builder.charAt(start) == '[') {
				for (int end = start + 1; end < length; end++) {
					if (builder.charAt(end) == ']') {
						int old = length;
						String name = builder.substring(start + 1, end);
						String[] porps = name.split(",");
						Map ps = new HashMap();
						for (int i = 0; i < porps.length; i++) {
							String[] pair = porps[i].split("=");
							if (pair.length > 1) ps.put(pair[0], pair[1]);
							else
								ps.put("name", pair[1]);
						}
						name = (String) ps.get(name);
						Object value = params.get(name);
						if (value == null) return "";

						String type = (String) ps.get("type");
						DataType dataType = type == null ? new StringDataType() : SystemDataTypeFactory.getInstance().fromString(type, ps);
						dataTypes.add(dataType);
						paramValues.add(value);

						builder.replace(start, end + 1, "?");
						length = builder.length();
						start = end - old + length;
					}
				}
			}
			if (builder.charAt(start) == '$') {
				for (int end = start + 1; end < length; end++) {
					if (builder.charAt(end) == '$') {
						int old = length;
						String name = builder.substring(start + 1, end);
						Object value = params.get(name);
						if (value == null) return "";
						String str = String.valueOf(value);
						builder.replace(start, end + 1, str);
						length = builder.length();
						start = end - old + length;
					}
				}
			}
		}
		return builder.toString();
	}

}
