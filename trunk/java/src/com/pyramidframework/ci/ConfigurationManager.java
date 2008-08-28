package com.pyramidframework.ci;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Namespace;

import com.pyramidframework.ci.impl.ConfigDamainTree;
import com.pyramidframework.ci.impl.ConfigServiceProvider;

public class ConfigurationManager {
	private Map instanceParsers = null;

	private Namespace namespace = new Namespace(DEFAULT_NAMESPACE_PREFIX, DEFAULT_NAMESPACE_URI);

	/**
	 * �õ�������Ϣ��������
	 * 
	 * @param functionPath
	 * @param configType
	 * @return
	 */
	public ConfigDomain getConfigDomain(String functionPath, String configType) {

		if (configType == null) {
			throw new NullPointerException("The configType parameter can not be NULL!");
		}

		if (!isCorrectFunctionPath(functionPath)) {
			throw new IllegalArgumentException("The functionPath parameter is not validate!");
		}

		ConfigDocumentParser parser = getInstanceParser(configType);
		if (parser == null) {
			throw new NullPointerException("Can not fount Parser instance for configtype:" + configType);
		}

		ConfigDomain domain = getDataProvider().getDomain(functionPath, configType, parser);

		if (domain == null) {
			throw new NullPointerException("Can not find any configuration!");
		}
		return domain;
	}

	/**
	 * ��ȡ�������ݻ�ȡ�����ʵ����
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
	 * �õ�������Ϣ�ľ�������
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
	 * ����û��Ĭ�ϵĽ��������ϵ����ù�����
	 */
	public ConfigurationManager() {
		// this.instanceParsers = new HashMap();
	}

	/**
	 * �������һЩ��ʵ��һ��Ĭ�ϵ����ý�������ʵ��
	 * 
	 * @param instanceParsers
	 */
	public ConfigurationManager(Map instanceParsers) {
		this.instanceParsers = instanceParsers;
	}

	/**
	 * �õ�ʵ���ϵ����úý�����
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
	 * ����ʵ��һ���Ľ�����
	 * 
	 * @param configType
	 * @param documentParser
	 */
	public void addLocalParser(String configType, DefaultDocumentParser documentParser) {
		this.instanceParsers.put(configType, documentParser);

	}

	/**
	 * 
	 * @param functionPath
	 * @param configType
	 * @return
	 */
	public static ConfigDomain getDomainByPath(String functionPath, String configType) {
		return _managerInstance.getConfigDomain(functionPath, configType);
	}

	/**
	 * �õ������ھ���Ĺ���·���ϵ���������
	 * 
	 * @param functionPath
	 *            ���õĹ���·��
	 * @param configType
	 *            ���õ�����·��
	 * @return
	 */
	public static Object getConfigDataByPath(String functionPath, String configType) {
		ConfigDomain domain = _managerInstance.getConfigDomain(functionPath, configType);
		if (domain != null) {
			return domain.getConfigData();
		}
		return null;
	}

	/**
	 * ����ȫ�ֵ������ļ����Ͷ�Ӧ�Ľ�����
	 * 
	 * @param type
	 *            ������Ϣ������
	 * @param documentParser
	 *            ��Ӧ�������ļ��Ľ�����
	 */
	public void setGlobalParser(String type, DefaultDocumentParser documentParser) {
		type_parser_map.put(type, documentParser);
	}

	/**
	 * ����Ѿ��趨�ķ�����Ϣ��ȫ�ֽ�����
	 * 
	 * @param configType
	 *            �����ļ�����
	 * @return
	 */
	public ConfigDocumentParser getGlobalParser(String configType) {
		return (ConfigDocumentParser) type_parser_map.get(configType);
	}

	/**
	 * ���ؽ�����ʹ�õ������ռ䣬Ĭ����ǰ׺Ϊci,URIΪ{@link ConfigurationManager.DEFAULT_NAMESPACE_URI}
	 * 
	 * @return
	 */
	public Namespace getNamespace() {
		return namespace;
	}

	/**
	 * �����µ������ļ������ü̳���ص������ռ�
	 * 
	 * @param namespace
	 *            �����ʹ�������ռ䣬������null
	 */
	public void setNamespace(Namespace namespace) {
		this.namespace = namespace;
	}

	/* �ڲ����õ������ļ���Ӧ���ļ������� */
	private static HashMap type_parser_map = new HashMap();

	/* �ڲ����е�ʵ�� */
	static ConfigurationManager _managerInstance = new ConfigurationManager();

	/**
	 * ConfigurationInheritanceģ���Ĭ��URL
	 */
	public static final String DEFAULT_NAMESPACE_URI = "http://www.pyramidframework.com/2008/ConfigurationInheritance";

	/**
	 * ConfigurationInheritanceģ�����XML�ĵ��е�Ĭ��ǰ׺
	 */
	public static final String DEFAULT_NAMESPACE_PREFIX = "ci";

	/**
	 * �����ü̳��ļ���ָ���ļ̳��Ե�����������parent-���ϼ�Ŀ¼�̳У�root-��ϵͳ�̳м���Ŀ¼��default-������ģ��ĸ��м̳�,none�����κεط��̳У�Ĭ��Ϊparent
	 */
	public static final String[] INHERITE_TYPE_CONSTANTS = { "parent", "root", "default", "none" };

	/**
	 * ����·����·��֮��ķָ�������ϵͳ���ļ�·���ķָ�Ҳ�ǲ������
	 */
	public static final String FUNCTION_PATH_SEPARATOR = "/";

	private ConfigServiceProvider _providerInstance;

	/**
	 * �ж�ָ���Ĺ���·���ǲ��Ƿ���Ҫ���·���ı�����ʽ��
	 * ��'/'��ʼ��������������'/'���ϵ�����һ�𣬲���ֻ�ܰ����ַ������ֺ�'/'��NULL��ʾĬ������
	 * 
	 * @param functionPath
	 * @return
	 */
	public static final boolean isCorrectFunctionPath(String functionPath) {
		if (functionPath == null || ConfigurationManager.INHERITE_TYPE_CONSTANTS[3].equals(functionPath)) {// ��ʾĬ������
			return true;
		}

		if (!functionPath.startsWith(FUNCTION_PATH_SEPARATOR)) {// ������/��ʼ
			return false;
		}

		String t = FUNCTION_PATH_SEPARATOR + FUNCTION_PATH_SEPARATOR;
		if (functionPath.indexOf(t) >= 0 || functionPath.indexOf('=') > 0) { // ��ʱ�����Ʋ��ܰ�����=��
			return false;
		}
		return true;

	}

	/**
	 * ����ָ����·���õ��ϼ��Ĺ���·�����������ϵͳ��Ŀ¼����'/'�����򷵻�NULL
	 * 
	 * @param functionPath
	 *            ���Ϻ���isCorrectFunctionPathУ��Ĺ���·��
	 * @return NULL or parent path
	 */
	public static final String getParentPath(String functionPath) {
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