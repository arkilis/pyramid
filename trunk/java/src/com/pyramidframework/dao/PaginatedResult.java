package com.pyramidframework.dao;

import java.util.List;

/**
 * 专门用于记录分页后的结果
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

	/** 得到当前页内的数据 */
	public List getPageDataList() {
		return pageDataList;
	}

	public void setPageDataList(List pageDataList) {
		this.pageDataList = pageDataList;
	}

	/** 得到总的数据行数 */
	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
}
