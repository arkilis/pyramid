package com.pyramidframework.dao;

import java.util.Map;

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
	public Object modify(Object dataObject) throws DAOException;

	/**
	 * ɾ������
	 * 
	 * @param dataObject
	 * @return Object ִ����Ӻ�����ݣ�һ���dataObject��ͬһ������
	 */
	public Object remove(Object dataObject) throws DAOException;

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
	public Object retrieveData(String modelName, Map primaryKeyValues) throws DAOException;

}
