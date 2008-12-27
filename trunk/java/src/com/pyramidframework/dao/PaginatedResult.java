package com.pyramidframework.dao;

import java.util.List;

/**
 * ר�����ڼ�¼��ҳ��Ľ��
 * 
 * @author Mikab Peng
 * 
 */
public class PaginatedResult {
	List pageDataList = null;
	int totalCount = 0;

	public PaginatedResult() {

	}

	public PaginatedResult(int total, List pageList) {
		this.totalCount = total;
		this.pageDataList = pageList;
	}

	/** �õ���ǰҳ�ڵ����� */
	public List getPageDataList() {
		return pageDataList;
	}

	public void setPageDataList(List pageDataList) {
		this.pageDataList = pageDataList;
	}

	/** �õ��ܵ��������� */
	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
}
