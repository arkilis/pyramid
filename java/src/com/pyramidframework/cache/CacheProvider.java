package com.pyramidframework.cache;

import java.util.List;

/**
 * ��ȡ����Ľӿ�
 * @author Mikab Peng
 *
 */
public interface CacheProvider {
	
	/**
	 * ��ȡ���������
	 * @param key
	 * @param params û�в����Ŀ���Ϊ��
	 * @return
	 */
	public CacheItem getCachedData(String key,List params);
	
}
