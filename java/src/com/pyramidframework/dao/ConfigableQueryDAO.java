package com.pyramidframework.dao;

import java.util.List;
import java.util.Map;

/**
 * �����ò�ѯ�����ݷ���
 * @author Mikab Peng
 *
 */
public interface ConfigableQueryDAO {

	/**
	 * ����ϵͳ�ڵĲ�ѯ���ƺͲ����õ���ѯ�Ľ��
	 * @param queryName ��ѯ����
	 * @param queryParameters ��ѯ�Ĳ���
	 * @return
	 * @throws DAOException
	 */
	List query(String queryName,Map queryParameters,int pageSize,int page) throws DAOException;
}
