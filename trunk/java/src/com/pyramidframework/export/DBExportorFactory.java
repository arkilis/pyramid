package com.pyramidframework.export;

/**
 * 构建导出的程序
 * @author Mikab Peng
 *
 */
public interface DBExportorFactory {
	
	/**
	 * 
	 * @param fileName 不包含后缀名
	 * @param type 当前支持csv,可以为NULL
	 * @return
	 */
	public DBDataExportHandler getExpotor(String fileName,String type);

}
