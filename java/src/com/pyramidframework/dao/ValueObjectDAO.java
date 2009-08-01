package com.pyramidframework.dao;

import java.util.Map;

import com.pyramidframework.dao.model.ModelProvider;

/**
 * ���ר�ŵ�ֵ�����DAO�ӿڣ����еĲ�������VO
 * 
 * @author Mikab Peng
 * 
 */
public interface ValueObjectDAO {
	/**
	 * �������
	 * 
	 * @param dataObject
	 * @return Object ִ����Ӻ�����ݣ�һ���dataObject��ͬһ������
	 */
	public Object add(Object dataObject) throws DAOException;

	/**
	 * �޸�����
	 * 
	 * @param dataObject
	 * @return Object ִ���޸ĺ�����ݣ�һ���dataObject��ͬһ������
	 */
	public int update(Object dataObject) throws DAOException;

	/**
	 * ɾ������
	 * 
	 * @param dataObject
	 * @return ɾ�������ݸ���
	 */
	public int delete(Object dataObject) throws DAOException;

	/**
	 * ����ָ���Ĳ�ѯ������ȡһ��
	 * 
	 * @param modelName
	 *            ��ѯ����������
	 * @param primaryKeyValues
	 *            ������ֵ�ļ���
	 * @return
	 * @throws DAOException
	 */
	public Object retrieve(String modelName, Map primaryKeyValues) throws DAOException;

	/**
	 * ����ָ���Ĳ�ѯ������ȡ����
	 * 
	 * @param modelName
	 *            ��ѯ����������
	 * @param queryValues
	 *            ��ѯ��ֵ�ļ���
	 * @param pageSize
	 *            ҳ��С�����Ϊ0�򲻷�ҳ
	 * @param page
	 *            ��ǰ����ҳ��
	 * @return
	 * @throws DAOException
	 */
	public PaginatedResult query(String modelName, Map queryValues, String orderBy, int pageSize, int page) throws DAOException;

	/**
	 * ����ڲ���VOFactoryʵ��
	 * @return
	 */
	public VOFactory getVOFactory();
	
	/**
	 * �õ�һ���ڲ�ʵ����ص�ModelProvider��ʵ��
	 * @return
	 */
	public ModelProvider getModelProvider();
}
