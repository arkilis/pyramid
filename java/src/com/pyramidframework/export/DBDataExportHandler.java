package com.pyramidframework.export;

import javax.servlet.http.HttpServletResponse;

import com.pyramidframework.dao.SqlDAO.ResultSetHandler;

/**
 * 执行数据导出的任务
 * 
 * @author Mikab Peng
 * 
 */
public interface DBDataExportHandler extends ResultSetHandler {

	/**
	 * 设置输出的文件名
	 * 
	 * @param fileName
	 */
	public void setFileName(String fileName);

	/**
	 * 设置输出的response
	 * 
	 * @param response
	 */
	public void setResponse(HttpServletResponse response);

	/**
	 * 
	 */
	public void setHeaderNames(String[] names);
}
