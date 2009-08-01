package com.pyramidframework.struts1;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import com.pyramidframework.cache.CacheItem;
import com.pyramidframework.cache.CacheProvider;

/**
 * 系统一级的资源访问
 * 
 * @author Administrator
 * 
 */
public class SystemResourceAccessService extends AbstractService {

	protected CacheProvider cacheProvider;

	/**
	 * 查询单个缓存，支持带参数:cache:缓存code,cacheParam缓存参数，可以是多个参数
	 * 
	 * @return
	 */
	public String getCache() {
		HttpServletRequest request = ActionContext.getCurrent().getRequest();
		String cachecode = request.getParameter("cache");

		if (cachecode != null && cachecode.length() > 0) {
			CacheItem item = getCacheProvider().getCachedData(cachecode, getParams("cache", request));
			request.setAttribute(RequestProxyProcessor.FORM_BEAN_KEY, item);
		} else {
			request.setAttribute(RequestProxyProcessor.FORM_BEAN_KEY, null);
		}
		return ActionSupport.JSON_FORWARD;
	}

	/**
	 * 一次性获取多个参数,cache参数code的集合
	 * 
	 * @return
	 */
	public String getCaches() {
		HttpServletRequest request = ActionContext.getCurrent().getRequest();
		String cachecodes = request.getParameter("cache");

		List items = new ArrayList();
		int index = 0;
		if (cachecodes != null && cachecodes.length() > 0) {
			StringTokenizer tokenizer = new StringTokenizer(cachecodes, ",");
			while (tokenizer.hasMoreElements()) {
				String c = (String) tokenizer.nextElement();
				items.add(getCacheProvider().getCachedData(c, getParams("cache" + index, request)));
				index++;
			}
		}
		request.setAttribute(RequestProxyProcessor.FORM_BEAN_KEY, items);
		return ActionSupport.JSON_FORWARD;
	}

	/**
	 * 获取参数列表：name + "ParamCount"=参数个数，name + "Param" + i具体参数值
	 * @param name
	 * @param request
	 * @return
	 */
	public List getParams(String name, HttpServletRequest request) {
		String cnt = request.getParameter(name + "ParamCount");
		List list = new ArrayList();
		if (cnt == null || cnt.length() < 1) {
			return list;
		}
		
		int c = Integer.parseInt(cnt);
		for (int i = 0; i < c; i++) {
			list.add(request.getParameter(name + "Param" + i));
		}
		return list;
	}

	public CacheProvider getCacheProvider() {
		return cacheProvider;
	}

	public void setCacheProvider(CacheProvider cacheProvider) {
		this.cacheProvider = cacheProvider;
	}
}
