package com.pyramidframework.cache;

import java.util.List;

/**
 * 获取缓存的接口
 * @author Mikab Peng
 *
 */
public interface CacheProvider {
	
	/**
	 * 获取缓存的数据
	 * @param key
	 * @param params 没有参数的可以为空
	 * @return
	 */
	public CacheItem getCachedData(String key,List params);
	
}
