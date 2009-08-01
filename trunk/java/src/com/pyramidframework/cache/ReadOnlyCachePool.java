package com.pyramidframework.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.beans.factory.support.AbstractBeanFactory;

import com.pyramidframework.spring.SpringFactory;

/**
 * 本类负责对各类scope进行管理，即所有的spring中支持的scope都支持
 * 
 * @author Mikab Peng
 * 
 */
public class ReadOnlyCachePool implements CacheProvider {

	Map globalCaches = new HashMap();// 用户替换掉singleton的缓存

	/**
	 * 获取数据，
	 */
	public CacheItem getCachedData(String key, List params) {

		AbstractBeanFactory factory = (AbstractBeanFactory) SpringFactory.getDefaultInstance().getBeanFactory("/cache/" + key);
		String[] scopeNames = factory.getRegisteredScopeNames();
		for (int i = 0; i < scopeNames.length; i++) {
			// System.err.println("scopeNames[i]"+scopeNames[i]);
			Map container = lookupCacheContainer(factory, scopeNames[i]);
			CacheWithParam item = (CacheWithParam) container.get(key);
			if (item != null && item.parameterEquals(params)) {
				return item.item;
			}
		}

		for (int i = 0; i < cacheProviders.size(); i++) {
			CacheProvider provider = (CacheProvider) cacheProviders.get(i);
			CacheItem item = provider.getCachedData(key, params);
			if (item != null) {
				// System.err.println("item.getScope()"+item.getScope());
				Map container = lookupCacheContainer(factory, item.getScope());
				if (container != null) {
					container.put(key, new CacheWithParam(item,params));
				}
				return item;
			}
		}
		
		return null;
	}

	/**
	 * @param factory
	 * @param scopeNames
	 * @param i
	 * @param item
	 * @return
	 */
	protected Map lookupCacheContainer(AbstractBeanFactory factory, String scopeName) {
		Scope scope = factory.getRegisteredScope(scopeName);

		if (scope == null) {
			if (AbstractBeanFactory.SCOPE_SINGLETON.equals(scopeName)) {
				return globalCaches;
			} else if (AbstractBeanFactory.SCOPE_PROTOTYPE.equals(scopeName)) {
				return new HashMap();
			}
		}
		Map container = (Map) scope.get(scopeName, containerFactory);
		return container;
	}

	protected List cacheProviders = new ArrayList();
	public static final String CACHE_CONTAINER_KEY = ReadOnlyCachePool.class.getName() + ".cacheContainer";
	private ObjectFactory containerFactory = new ObjectFactory() {
		public Object getObject() throws BeansException {
			return new HashMap();
		}
	};

	/**
	 * 添加内部的缓存提供商，如果已经存在返回true，否则返回false
	 * 
	 * @param provider
	 * @return 如果已经存在返回true，否则返回false
	 */
	public boolean addCacheProvider(CacheProvider provider) {
		if (cacheProviders.contains(provider)) {
			return false;
		} else {
			cacheProviders.add(provider);
			return true;
		}
	}

	public void setCacheProvider(CacheProvider provider) {
		addCacheProvider(provider);
	}

	public List getCacheProviders() {
		return cacheProviders;
	}

	public void setCacheProviders(List cacheProviders) {
		this.cacheProviders = cacheProviders;
	}
	
	private class CacheWithParam{
		CacheItem item = null;
		List param = null;
		
		private CacheWithParam(CacheItem item,List params) {
			this.item = item;
			this.param = params;
		}
		
		private boolean parameterEquals(List params){
			if (this.param == null  ){
				return params == null;
			}else{
				return this.param.equals(params);
			}
		}
		 
	}

}
