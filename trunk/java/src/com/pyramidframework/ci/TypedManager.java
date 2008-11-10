package com.pyramidframework.ci;

import java.util.Map;

import com.pyramidframework.ci.impl.ConfigDamainTree;
import com.pyramidframework.ci.impl.ConfigServiceProvider;

/**
 * 指定了类型的配置信息類型配置信息管理器 一个配置管理只支持一个配置类型,在构造函数中指定。
 * 
 * @author Mikab Peng
 * 
 */
public class TypedManager extends ConfigurationManager {
	private String configType = null;
	protected ConfigDocumentParser parser = null;

	/**
	 * 得到配置信息转换后的配置信息域
	 * 
	 * @param functionPath
	 *            功能路径
	 * @return
	 */
	public ConfigDomain getConfigDomain(String functionPath) {
		return super.getConfigDomain(functionPath, configType);
	}

	/**
	 * 得到配置信息转化后的数据
	 * 
	 * @param functionPath
	 *            功能路径
	 * @return
	 */
	public Object getConfigData(String functionPath) {

		return super.getConfigData(functionPath, configType);

	}

	/**
	 * 限定configType参数只能和构造函数里的一样
	 * 
	 * @param functionPath
	 * @param configType
	 *            只能和构造函数的configType一样，否则抛出IllegalArgumentException
	 */
	public ConfigDomain getConfigDomain(String functionPath, String configType) {
		if (this.configType.equals(configType)) {
			return super.getConfigDomain(functionPath, configType);
		} else {
			throw new IllegalArgumentException("Currrent TypedManager instance only support " + this.configType + " configurations !");
		}
	}

	/**
	 * 构造函数
	 * 
	 * @param configType
	 *            指定的配置信息类型
	 */
	public TypedManager(String configType) {
		super();
		if (configType == null) {
			throw new NullPointerException("configType parameter is null!");
		}
		this.configType = configType;
	}

	/**
	 * 根据指定的类型和解析器构造管理器
	 * 
	 * @param configType
	 *            配置信息类型
	 * @param typeParser
	 *            配置文件解析器
	 */
	public TypedManager(String configType, ConfigDocumentParser typeParser) {
		super();

		if (typeParser == null) {
			throw new NullPointerException("typeParser parameter is null!");
		}
		this.parser = typeParser;

		if (configType == null) {
			throw new NullPointerException("configType parameter is null!");
		}
		this.configType = configType;
	}

	/**
	 * 如果构造时传递了解析器，则直接返回解析器
	 * 
	 * @param configType
	 *            配置信息类型
	 */
	public ConfigDocumentParser getInstanceParser(String configType) {
		if (this.configType.equals(configType) && parser != null) {
			return parser;
		}
		return super.getGlobalParser(configType);
	}

	/**
	 * 获取内幕的配置信息的类型
	 * 
	 * @return
	 */
	public String getConfigType() {
		return configType;
	}

	/**
	 * 设定配置信息的类型 不对外暴露
	 * 
	 * @param configType
	 *            新的配置信息类型
	 */
	protected void setConfigType(String configType) {
		if (configType == null) {
			throw new NullPointerException("configType parameter is null!");
		}
		this.configType = configType;
	}

	/**
	 * 获取内部持有的解析器的实例
	 * 
	 * @return 解析器的实例
	 */
	public ConfigDocumentParser getParser() {
		return parser;
	}

	/**
	 * 指定新的配置信息解析器
	 * 
	 * @param TypeParser
	 */
	public void setParser(ConfigDocumentParser TypeParser) {
		if (TypeParser == null) {
			throw new NullPointerException("typeParser parameter is null!");
		}
		this.parser = TypeParser;
	}

	protected ConfigServiceProvider getDataProvider() {
		if (_providerInstance == null) {
			synchronized (this) {
				if (_providerInstance == null) {
					_providerInstance = new ConfigDamainTree(this) {
						protected Map getTypedContainer(String type) {
							return this.rootContainerMap;
						}
					};
				}
			}
		}

		return _providerInstance;
	}
}
