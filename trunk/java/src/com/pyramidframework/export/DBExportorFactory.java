package com.pyramidframework.export;

/**
 * ���������ĳ���
 * @author Mikab Peng
 *
 */
public interface DBExportorFactory {
	
	/**
	 * 
	 * @param fileName ��������׺��
	 * @param type ��ǰ֧��csv,����ΪNULL
	 * @return
	 */
	public DBDataExportHandler getExpotor(String fileName,String type);

}
