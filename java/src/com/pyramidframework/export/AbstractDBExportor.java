package com.pyramidframework.export;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import com.pyramidframework.dao.DAOException;

/**
 * 输出导出的抽象基类
 * 
 * @author Mikab Peng
 * 
 */
public abstract class AbstractDBExportor implements DBDataExportHandler {

	protected String fileName = null;
	protected HttpServletResponse response = null;
	protected String[] dataNames = null;
	protected OutputStream stream = null;
	protected String TIME_FORMATTER = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 写出一个单元格的数据,第一行从0开始
	 * 
	 * @param row
	 * @param column
	 * @param data
	 * @return
	 */
	protected abstract void writeCell(int row, int column, String data)throws Exception;

	/**
	 * 写数据前被调用
	 */
	protected void beforeWriteData() {

	}

	/**
	 * 写数据被调用后,此函数会使用finally确保被执行
	 */
	protected void afterWriteData() {

	}

	public void setFileName(String fileName) {
		this.fileName = fileName;

	}

	public void setHeaderNames(String[] names) {
		this.dataNames = names;

	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;

	}

	public Object handleResult(ResultSet resultSet) throws DAOException {

		try {
			String downFileName = new String(fileName.getBytes(), "ISO8859-1");
			response.setContentType("application/x-msdownload");
			response.setHeader("Content-Disposition", "attachment;filename=" + downFileName);

			stream = new BufferedOutputStream(response.getOutputStream());

			beforeWriteData();
			int j = 0;
			if (dataNames != null) {

				for (int i = 0; i < dataNames.length; i++) {
					writeCell(j, 1, dataNames[i]);
				}
				j++;
			}

			int columnSize = resultSet.getMetaData().getColumnCount();
			SimpleDateFormat formatter = new SimpleDateFormat(TIME_FORMATTER);
			while (resultSet.next()) {

				for (int i = 0; i < columnSize; i++) {
					Object o = resultSet.getObject(i + 1);
					if (o instanceof Date) {
						writeCell(j, i, formatter.format(o));
					} else {
						writeCell(j, i, o ==null ? null:o.toString());
					}
				}
				j++;
			}
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			afterWriteData();
		}
		return null;
	}
}
