package com.pyramidframework.ci;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Namespace;

import com.pyramidframework.ci.impl.ConfigDamainTree;
import com.pyramidframework.ci.impl.ConfigServiceProvider;
import com.pyramidframework.script.CompilableScriptEngine;
import com.pyramidframework.script.MVELScriptEngine;

public class ConfigurationManager {
	protected Map instanceParsers = null;

	protected Namespace namespace = new Namespace(DEFAULT_NAMESPACE_PREFIX, DEFAULT_NAMESPACE_URI);
	protected ConfigServiceProvider _providerInstance;
	protected CompilableScriptEngine scriptEngine = new MVELScriptEngine();; // 执行脚本的引擎实现

	/**
	 * 设置默认的脚本引擎的实现，该实现应该是实现{@link CompilableScriptEngine}接口
	 * 
	 * @param scriptEngine
	 */
	public void setScriptEngine(CompilableScriptEngine scriptEngine) {

		if (scriptEngine != null) {
			this.scriptEngine = scriptEngine;
		} else {
			throw new IllegalArgumentException("scriptEngine can not be null!");
		}
	}

	/**
	 * 获取内部的脚本引擎的实现类，默认是{@link MVELScriptEngine}实现
	 * 
	 * @return
	 */
	public CompilableScriptEngine getScriptEngine() {
		return scriptEngine;
	}

	/**
	 * 得到配置信息的作用域
	 * 
	 * @param functionPath
	 * @param configType
	 * @return
	 */
	public ConfigDomain getConfigDomain(String functionPath, String configType) {

		if (configType == null) {
			throw new NullPointerException("The configType parameter can not be NULL!");
		}

		if (!isValidFunctionPath(functionPath)) {
			throw new IllegalArgumentException("The functionPath parameter is not valid!");
		}

		ConfigDocumentParser parser = getInstanceParser(configType);
		if (parser == null) {
			throw new NullPointerException("Can not fount Parser instance for configtype:" + configType);
		}

		ConfigDomain domain = getDataProvider().getDomain(functionPath, configType, parser);
		
		//在上级没有配置下级有配置时，需要返回NULL
		/*if (domain == null) {
			throw new NullPointerException("Can not find any configuration!");
		}*/
		
		return domain;
	}

	/**
	 * 获取配置数据获取服务的实现者
	 * 
	 * @return
	 */
	protected ConfigServiceProvider getDataProvider() {
		if (_providerInstance == null) {
			synchronized (this) {
				_providerInstance = new ConfigDamainTree(this);
			}
		}

		return _providerInstance;
	}

	/**
	 * 得到配置信息的具体数据
	 * 
	 * @param functionPath
	 * @param configType
	 * @return
	 */
	public Object getConfigData(String functionPath, String configType) {
		ConfigDomain domain = getConfigDomain(functionPath, configType);
		if (domain != null) {
			return domain.getConfigData();
		}
		return null;
	}

	/**
	 * 构建没有默认的解析器集合的配置管理器
	 */
	public ConfigurationManager() {
		// this.instanceParsers = new HashMap();
	}

	/**
	 * 构造包含一些在实例一级默认的配置解析器的实例
	 * 
	 * @param instanceParsers
	 */
	public ConfigurationManager(Map instanceParsers) {
		this.instanceParsers = instanceParsers;
	}

	/**
	 * 得到实例上的配置好解析器
	 * 
	 * @param configType
	 * @return
	 */
	public ConfigDocumentParser getInstanceParser(String configType) {

		if (instanceParsers == null) {
			return getGlobalParser(configType);
		}

		ConfigDocumentParser p = (ConfigDocumentParser) instanceParsers.get(configType);
		if (p == null) {
			return getGlobalParser(configType);
		} else {
			return p;
		}
	}

	/**
	 * 添加实例一级的解析器
	 * 
	 * @param configType
	 * @param documentParser
	 */
	public void addLocalParser(String configType, DefaultDocumentParser documentParser) {
		if (this.instanceParsers == null) {
			this.instanceParsers = new HashMap();
		}
		this.instanceParsers.put(configType, documentParser);

	}

	/**
	 * 设置全局的配置文件类型对应的解析器
	 * 
	 * @param type
	 *            配置信息的类型
	 * @param documentParser
	 *            对应的配置文件的解析器
	 */
	public void setGlobalParser(String type, DefaultDocumentParser documentParser) {
		type_parser_map.put(type, documentParser);
	}

	/**
	 * 获得已经设定的访问信息的全局解析器
	 * 
	 * @param configType
	 *            配置文件类型
	 * @return
	 */
	public ConfigDocumentParser getGlobalParser(String configType) {
		return (ConfigDocumentParser) type_parser_map.get(configType);
	}

	/**
	 * 返回解析器使用的命名空间，默认是前缀为ci,URI为{@link ConfigurationManager.DEFAULT_NAMESPACE_URI}
	 * 
	 * @return
	 */
	public Namespace getNamespace() {
		return namespace;
	}

	/**
	 * 设置新的配置文件中配置继承相关的命名空间
	 * 
	 * @param namespace
	 *            如果不使用命名空间，请设置null
	 */
	public void setNamespace(Namespace namespace) {
		this.namespace = namespace;
	}

	/* 内部设置的配置文件对应的文件解析器 */
	private static HashMap type_parser_map = new HashMap();

	/**
	 * ConfigurationInheritance模块的默认URL
	 */
	public static final String DEFAULT_NAMESPACE_URI = "http://www.pyramidframework.com/2008/ConfigurationInheritance";

	/**
	 * ConfigurationInheritance模块的在XML文档中的默认前缀
	 */
	public static final String DEFAULT_NAMESPACE_PREFIX = "ci";

	/**
	 * 在配置继承文件中指明的继承自的三个常量：parent-从上级目录继承；root-从系统继承即根目录，default-从软件模块的根中继承,none不从任何地方继承；默认为parent
	 */
	public static final String[] INHERITE_TYPE_CONSTANTS = { "parent", "root", "default", "none" };

	/**
	 * 功能路径的路径之间的分隔符，本系统的文件路径的分割也是采用这个
	 */
	public static final String FUNCTION_PATH_SEPARATOR = "/";

	/**
	 * 判断指定的功能路径是不是符合要求的路径的表达形式：
	 * 以'/'开始，不能以有两个'/'以上的连在一起，并且只能包含字符、数字和'/'，NULL表示默认配置
	 * 
	 * @param functionPath
	 * @return
	 */
	public static boolean isValidFunctionPath(String functionPath) {
		if (functionPath == null || ConfigurationManager.INHERITE_TYPE_CONSTANTS[3].equals(functionPath)) {// 表示默认配置
			return true;
		}

		if (!functionPath.startsWith(FUNCTION_PATH_SEPARATOR)) {// 必须以/开始
			return false;
		}

		String t = FUNCTION_PATH_SEPARATOR + FUNCTION_PATH_SEPARATOR;
		if (functionPath.indexOf(t) >= 0 || functionPath.indexOf('=') > 0) { // 暂时先限制不能包含“=”
			return false;
		}
		return true;

	}

	/**
	 * 按照默认的继承规则得到指定的路径的上级的功能路径，如果是在系统根目录级（'/'），则返回NULL
	 * 
	 * @param functionPath
	 *            符合函数isCorrectFunctionPath校验的功能路径
	 * @return NULL or parent path
	 */
	public static String getDefaultParentPath(String functionPath) {
		char c = FUNCTION_PATH_SEPARATOR.charAt(0);

		if (functionPath == null || functionPath.length() < 2) {
			return null;
		} else {
			char[] a = functionPath.toCharArray();
			for (int i = a.length - 2; i >= 0; i--) {
				if (a[i] == c) {
					return new String(a, 0, i + 1);
				}
			}
		}
		return null;
	}

}
