package com.pyramidframework.ajax;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonBeanProcessor;

import com.pyramidframework.dao.VOSupport;
import com.pyramidframework.dao.ValueObject;
import com.pyramidframework.struts1.ActionFormBean;

/**
 * JSON LIb的实现辅助类
 * 
 * @author Mikab Peng
 * 
 */
public class JsonBeanConverterUtil {
	public static JsonConfig config = new JsonConfig();

	/**
	 * 转化bean为JSON表达式
	 * 
	 * @param object
	 * @return
	 */
	public static String convertBean(Object object) {

		String json = null;
		if (object instanceof List) {
			json = JSONArray.fromObject(object, config).toString();
		} else {
			json = JSONObject.fromObject(object, config).toString();
		}
		String result = escapeJavaScript(json);

		return result;
	}

	/**
	 * 注册全局的bean处理器
	 * 
	 * @param clazz
	 * @param beanProcessor
	 */
	public void registerJsonBeanProcessor(Class clazz, JsonBeanProcessor beanProcessor) {
		config.registerJsonBeanProcessor(clazz, beanProcessor);
	}

	/**
	 * 过滤掉javascript中的特殊字符
	 * 
	 * @param str
	 * @return
	 */
	public static String escapeJavaScript(String str) {
		if (str == null) {
			return null;
		}

		StringBuffer writer = new StringBuffer(str.length() * 2);
		int sz;
		sz = str.length();
		for (int i = 0; i < sz; i++) {
			char ch = str.charAt(i);

			// handle unicode
			if (ch > 0xfff) {
				writer.append("\\u" + hex(ch)); //$NON-NLS-1$
			} else if (ch > 0xff) {
				writer.append("\\u0" + hex(ch)); //$NON-NLS-1$
			} else if (ch > 0x7f) {
				writer.append("\\u00" + hex(ch)); //$NON-NLS-1$
			} else if (ch < 32) {
				switch (ch) {
				case '\b':
					writer.append('\\');
					writer.append('b');
					break;
				case '\n':
					writer.append('\\');
					writer.append('n');
					break;
				case '\t':
					writer.append('\\');
					writer.append('t');
					break;
				case '\f':
					writer.append('\\');
					writer.append('f');
					break;
				case '\r':
					writer.append('\\');
					writer.append('r');
					break;
				default:
					if (ch > 0xf) {
						writer.append("\\u00" + hex(ch)); //$NON-NLS-1$
					} else {
						writer.append("\\u000" + hex(ch)); //$NON-NLS-1$
					}
					break;
				}
			} else {
				writer.append(ch);
			}
		}
		return writer.toString();
	}

	static {
		config.registerJsonBeanProcessor(ActionFormBean.class, new FromBeanProcessor());
		config.registerJsonBeanProcessor(VOSupport.class, new VOSupportProcessor());
		config.registerJsonBeanProcessor(ValueObject.class, new VOSupportProcessor());
		config.registerJsonBeanProcessor(Date.class, new DateProcessor());
		config.registerJsonBeanProcessor(java.sql.Date.class, new DateProcessor());
		config.registerJsonBeanProcessor(java.sql.Time.class, new DateProcessor());
		config.registerJsonBeanProcessor(java.sql.Timestamp.class, new DateProcessor());
	}

	static class FromBeanProcessor implements JsonBeanProcessor {
		public JSONObject processBean(Object arg0, JsonConfig config) {
			ActionFormBean form = (ActionFormBean) arg0;
			JSONObject object = new JSONObject();

			object.accumulate("totalCount", form.getTotalCount());
			object.accumulate("pageSize", form.getPageSize());
			object.accumulate("currentPage", form.getCurrentPage());
			object.accumulate("queryResult", form.getQueryResult(), config);
			object.accumulate("models", form.getModelMap());

			return object;
		}
	}

	static class VOSupportProcessor implements JsonBeanProcessor {
		public JSONObject processBean(Object arg0, JsonConfig arg1) {
			VOSupport support = (VOSupport) arg0;
			JSONObject object = new JSONObject();
			object.accumulateAll(support.getValues(), arg1);

			return object;
		}
	}

	static class DateProcessor implements JsonBeanProcessor {
		public JSONObject processBean(Object arg0, JsonConfig arg1) {
			if (arg0 != null) {
				String TIME_FORMATTER = "yyyy-MM-dd HH:mm:ss";
				SimpleDateFormat formatter = new SimpleDateFormat(TIME_FORMATTER);
				return JSONObject.fromObject("{toString:function(){ return '" + formatter.format((Date) arg0) + "';}}");
			}

			return JSONObject.fromObject("{toString:function(){ return '';}}");
		}
	}

	private static String hex(char ch) {
		return Integer.toHexString(ch).toUpperCase();
	}

}
