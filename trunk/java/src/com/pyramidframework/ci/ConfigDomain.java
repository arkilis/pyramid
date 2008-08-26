package com.pyramidframework.ci;

import java.util.List;

/**
 * 配置信息作用域,主要记录与配置信息继承相关的信息
 * 
 * @author Mikab Peng
 * 
 */
public abstract class ConfigDomain {

	// 是否需要缓存节点
	protected boolean cached = false;
	protected String targetPath = null; // 具体指代的配置信息的路径
	protected String configType = null; // 具体指代的配置信息的类型
	protected Object configData = null; // 缓存的具体的配置数据

	public String getTargetPath() {
		return targetPath;
	}


	public Object getConfigData() {
		return configData;
	}
	
	/**
	 * 是否缓存了数据
	 * 
	 * @return
	 */
	public boolean isCached() {
		return cached;
	}
	
	/**
	 * 得到配置数据的类型
	 * @return
	 */
	public String getConfigType() {
		return configType;
	}


	/**
	 * 得到上级配置作用域
	 * 
	 * @return 如果是最顶层，则返回<code>NULL</code>,否则返回其父作用域的实例
	 */
	public abstract ConfigDomain getParent();

	/**
	 * 得到其子作用域的列表，如果没有子节点则返回NULL
	 * 
	 * @return
	 */
	public abstract List getChildren();



}
