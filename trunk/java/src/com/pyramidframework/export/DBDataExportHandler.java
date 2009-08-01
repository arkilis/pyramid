package com.pyramidframework.export;

import javax.servlet.http.HttpServletResponse;

import com.pyramidframework.dao.SqlDAO.ResultSetHandler;

/**
 * ִ�����ݵ���������
 * 
 * @author Mikab Peng
 * 
 */
public interface DBDataExportHandler extends ResultSetHandler {

	/**
	 * ����������ļ���
	 * 
	 * @param fileName
	 */
	public void setFileName(String fileName);

	/**
	 * ���������response
	 * 
	 * @param response
	 */
	public void setResponse(HttpServletResponse response);

	/**
	 * 
	 */
	public void setHeaderNames(String[] names);
}
