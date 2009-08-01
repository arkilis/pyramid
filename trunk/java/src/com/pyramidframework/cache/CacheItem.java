package com.pyramidframework.cache;

import java.io.Serializable;
import java.util.Date;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonBeanProcessor;

import com.pyramidframework.ajax.JsonBeanConverterUtil;

/**
 * һ����������ȡֵ
 * 
 * @author Mikab Peng
 * 
 */
public class CacheItem implements Serializable {

	private static final long serialVersionUID = 7672773242794013055L;

	protected Object data = null;
	protected Date lastModifyTime = null;
	protected String key = null;
	protected String scope = null;

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public Date getLastModifyTime() {
		return lastModifyTime;
	}

	public void setLastModifyTime(Date lastModifyTime) {
		this.lastModifyTime = lastModifyTime;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * ��Ӧ��Spring��Bean��scope��ȡֵ
	 * 
	 * @return
	 */
	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String toString() {
		return "{key:" + key + ",lastModifyTime:" + lastModifyTime + ",scope:" + scope + ",data:" + data + "}";
	}

	//ע��CacheItem��ת����
	static {
		JsonBeanConverterUtil.config.registerJsonBeanProcessor(CacheItem.class, new JsonBeanProcessor() {
			public JSONObject processBean(Object arg0, JsonConfig config) {
				CacheItem form = (CacheItem) arg0;
				JSONObject object = new JSONObject();

				object.accumulate("lastModifyTime", form.getLastModifyTime(), config);
				object.accumulate("key", form.getKey());
				object.accumulate("scope", form.getScope());
				object.accumulate("data", form.getData(), config);

				return object;
			}
		});
	}

}
