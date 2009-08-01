package com.pyramidframework.cache;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.springframework.beans.factory.support.AbstractBeanFactory;

import com.pyramidframework.ci.ConfigDomain;
import com.pyramidframework.ci.IncrementDocumentParser;
import com.pyramidframework.ci.TypedManager;
import com.pyramidframework.dao.SqlDAO;
import com.pyramidframework.dao.SqlTextUtil;
import com.pyramidframework.sdi.NodeOperator;
import com.pyramidframework.sdi.xml.XmlDocument;
import com.pyramidframework.sdi.xml.XmlNode;
import com.pyramidframework.simpleconfig.ConfigContainer;
import com.pyramidframework.spring.SpringFactory;

/**
 * 配置文件的格式为：<br>
 * &lt;caches&gt;<br>
 * &lt;cache code="XXX" scope="XXX"&gt;select * from test where a
 * =?&lt;/cache&gt;<br>
 * &lt;/caches&gt;<br>
 * 
 * @author Mikab Peng
 * 
 */
public class DBCacheAccessor extends TypedManager implements CacheProvider {
	String rootFileDirectory = SpringFactory.DEFAULT_CONFIG_LOCATION_PREFIX;

	public DBCacheAccessor() {
		super("cache");
		this.parser = new DBCacheConfigParser();
	}

	public CacheItem getCachedData(String key, List params) {

		CacheItem item = new CacheItem();
		String sql = getCachedSql(key);
		List datatypes = new ArrayList(params.size());

		Map param = new HashMap();
		if (params != null) {
			for (int i = 0; i < params.size(); i++) {
				param.put(String.valueOf(i), params.get(i));
			}
		}
		
		params.clear();
		sql = SqlTextUtil.parseSQL(sql, param, datatypes, params);

		item.setData(dao.queryData(sql, params, datatypes));
		item.setKey(key);
		item.setScope(getCachedScope(key));
		item.setLastModifyTime(new Date(System.currentTimeMillis()));

		return item;
	}

	/**
	 * @param key
	 * @param container
	 * @return
	 */
	public String getCachedSql(String key) {
		ConfigContainer container = getConfigContainer(key);
		CacheSql sql = (CacheSql) container.getData(key);
		return sql.sql;
	}

	/**
	 * @param key
	 * @return
	 */
	protected ConfigContainer getConfigContainer(String key) {
		ConfigContainer container = (ConfigContainer) getConfigData("/");
		return container;
	}

	protected String getCachedScope(String key) {
		ConfigContainer container = getConfigContainer(key);
		CacheSql sql = (CacheSql) container.getData(key);
		return sql.scope;
	}

	private SqlDAO dao = null;

	public SqlDAO getDao() {
		return dao;
	}

	public void setDao(SqlDAO dao) {
		this.dao = dao;
	}

	/**
	 * 针对数据访问的缓存的配置文件的解析器,配置文件的格式为：<br>
	 * &lt;caches&gt;<br>
	 * &lt;cache code="XXX" scope="XXX"&gt;select * from test where a
	 * =?&lt;/cache&gt;<br>
	 * &lt;/caches&gt;<br>
	 * 
	 * @author Mikab Peng
	 * 
	 */
	protected class DBCacheConfigParser implements IncrementDocumentParser {

		/**
		 * 配置的文件名
		 */
		public String getConfigFileName(String functionPath, String configType) {
			return rootFileDirectory + functionPath + configType + ".xml";
		}

		public Object getDefaultConfigData(ConfigDomain domain, Object parentDataNode) {
			// 软件没有默认设定配置信息
			if (domain == null) {
				return null;
			}

			if (parentDataNode != null) {
				ConfigContainer container = (ConfigContainer) parentDataNode;
				try {
					return container.clone();
				} catch (Exception e) {
					return new ConfigContainer();
				}
			} else {
				// 其他情况下默认生成一下新的实例
				return new ConfigContainer();
			}
		}

		public void InitTemplateContext(Map templateContext) {
			templateContext.put("SimpleConfigManager", DBCacheAccessor.this);

		}

		public Object parseConfigDocument(ConfigDomain thisDomain, XmlDocument configDocument) {
			ConfigContainer data = (ConfigContainer) thisDomain.getConfigData();
			if (data == null) {
				data = new ConfigContainer();
			}
			Element element = configDocument.getDom4jDocument().getRootElement();
			List d = element.elements();
			Iterator iterator = d.iterator();
			while (iterator.hasNext()) {
				Element e = (Element) iterator.next();
				String code = e.attributeValue("code");
				String scope = e.attributeValue("scope");
				scope = (scope == null || "".equals(scope)) ? AbstractBeanFactory.SCOPE_PROTOTYPE : scope;
				String sql = e.getTextTrim();// System.err.println(sql);
				data.setData(code, new CacheSql(code, scope, sql));
			}
			return data;
		}

		public Object parseIncrementElement(Object thisConfigData, XmlNode childOfRoot, NodeOperator operator) {
			ConfigContainer data = (ConfigContainer) thisConfigData;
			if (data == null) {
				data = new ConfigContainer();
			}

			Element e = (Element) childOfRoot.getDom4JNode();
			String code = e.attributeValue("code");
			String scope = e.attributeValue("scope");
			String sql = e.getTextTrim();
			data.setData(code, new CacheSql(code, scope, sql));

			return data;
		}
	}

	protected static class CacheSql {
		public String sql;
		public String code;
		public String scope;

		public CacheSql(String code, String scope, String Sql) {
			this.code = code;
			this.scope = scope;
			this.sql = Sql;
		}

		public CacheSql() {
		}
	}

}
