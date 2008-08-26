package com.pyramidframework.ci.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;

import com.pyramidframework.ci.ConfigDocumentParser;
import com.pyramidframework.ci.ConfigurationManager;
import com.pyramidframework.ci.IncrementDocumentParser;
import com.pyramidframework.sdi.xml.XmlDocument;
import com.pyramidframework.sdi.xml.XmlNode;

/**
 * ������Ϣ���νṹ
 * 
 * @author Mikab Peng
 * 
 */
public class ConfigDamainTree {

	public static String separator = ConfigurationManager.FUNCTION_PATH_SEPARATOR;

	public ConfigDomainImpl getDomain(String functionPath, String type, ConfigDocumentParser parser) {
		if (configTypesMap == null) {
			configTypesMap = createContainerMap();
		}

		Map configTree = (Map) configTypesMap.get(type);
		if (configTree == null) {
			configTree = createContainerMap();
			configTypesMap.put(type, configTree);
		}

		ConfigDomainImpl configDomain = (ConfigDomainImpl) configTree.get(functionPath);
		if (configDomain == null) {
			if (parser == null) {
				return null;
			} else {
				configDomain = lookupAndConstructDomain(functionPath, type, parser);
				configTree.put(functionPath, configDomain);
			}
		}

		// ���û��������򷵻�NULL
		if (configDomain == NULL_CONFIG) {
			return null;
		}

		return configDomain;
	}

	protected ConfigDomainImpl lookupAndConstructDomain(String functionPath, String type, ConfigDocumentParser parser) {
		XmlNode node = lookupConfigedDomain(functionPath, type, parser);

		ConfigDomainImpl configDomain = null;

		if (node != null) {
			configDomain = new ConfigDomainImpl(functionPath, type);
			return parseAndConstructDomain(configDomain, node, parser);
		}

		if (configDomain == null) {
			configDomain = getDomainFromDefaultRule(functionPath, type, parser);
		}
		return configDomain;
	}

	protected XmlNode lookupConfigedDomain(String functionPath, String type, ConfigDocumentParser parser) {

		if (functionPath == null || functionPath.length() == 0) {
			return parser.getDefauDocument();
		}

		String oldPath = functionPath;
		// ȥ�����һ��FUNCTION_PATH_SEPARATOR�������
		if (functionPath.lastIndexOf(separator) >= 0) {
			functionPath = functionPath.substring(0, functionPath.lastIndexOf(separator));
		}

		StringTokenizer tokenizer = new StringTokenizer(functionPath, separator);
		ArrayList configFiles = new ArrayList();

		// ����Ӹ�Ŀ¼������
		configFiles.add(new File(parser.getConfigFileName(separator, type)));

		// ���ε�����Ŀ¼���Ҷ�Ӧ���ļ�
		StringBuffer path = new StringBuffer();
		while (tokenizer.hasMoreElements()) {
			String t = tokenizer.nextToken();
			if (t != null && t.length() > 0) {
				path.append(separator).append(t);
				File file = new File(parser.getConfigFileName(path.toString(), type));
				if (file.exists() && !configFiles.contains(file)) {
					configFiles.add(file);
				}
			}
		}

		// �������幦�ܽڵ�ĵط���ʼ���ϲ��ң�ֱ���ҵ�������ϢΪֹ
		for (int i = configFiles.size() - 1; i >= 0; i--) {
			File file = (File) configFiles.get(i);
			XmlNode node = null;
			try {
				XmlDocument d = new XmlDocument(file);
				node = searchNode(d, oldPath);
				if (node != null) {
					return node;
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
				// TODO:handle exception
			}
		}

		return null;
	}

	public XmlNode searchNode(XmlDocument document, String funcPath) {

		if (funcPath == null || funcPath.length() == 0) {
			return null;
		}

		Document doc = document.getDom4jDocument();
		List l = doc.getRootElement().elements();
		Namespace namespace = new Namespace(ConfigurationManager.DEFAULT_NAMESPACE_PREFIX, ConfigurationManager.DEFAULT_NAMESPACE_URI);
		QName attribute = new QName("functionPath", namespace);
		for (int i = l.size()-1; i >= 0; i--) {
			Element element = (Element) l.get(i);
			if (funcPath.equals(element.attributeValue(attribute))) {
				return new XmlNode(element, document.getNamespaces());
			}
		}
		return null;
	}

	protected ConfigDomainImpl getDomainFromDefaultRule(String functionPath, String type, ConfigDocumentParser parser) {
		// ʹ�õݹ�ķ�ʽ���ϼ�Ŀ¼��ʼ����
		if (functionPath.lastIndexOf(separator) >= 0) {
			functionPath = ConfigurationManager.getParentPath(functionPath);
			ConfigDomainImpl d = lookupAndConstructDomain(functionPath, type, parser);

			// ��Ҫ����һ���µ�������
			return constructDomain(functionPath, type, d.getConfigData(), d.isCached());
		}

		ConfigDomainImpl impl = NULL_CONFIG;
		if (parser instanceof IncrementDocumentParser) {
			Object o = ((IncrementDocumentParser) parser).getConfigData(null,null);
			if (o != null) {
				impl = constructDomain(functionPath, type, o, true);
			}

		} else {
			XmlDocument d = parser.getDefauDocument();
			if (d != null) {
				Object o = parser.parseConfigDocument(null, d);
				impl = constructDomain(functionPath, type, o, true);
			}
		}
		return impl;
	}

	/**
	 * ��װһ��configdomian��ʵ��
	 * 
	 * @param functionPath
	 * @param type
	 * @param o
	 * @param cached
	 * @return
	 */
	protected ConfigDomainImpl constructDomain(String functionPath, String type, Object o, boolean cached) {
		// TODO:�������νṹ
		ConfigDomainImpl impl = new ConfigDomainImpl(functionPath, type);
		impl.setCached(cached);
		impl.setConfigData(o);
		return impl;
	}

	public ConfigDomainImpl parseAndConstructDomain(ConfigDomainImpl domainImpl, XmlNode node, ConfigDocumentParser parser) {
		ConfigurationFileParser fileParser = new ConfigurationFileParser(domainImpl, node, parser, this);

		return fileParser.parseData();
	}

	protected Map createContainerMap() {
		return new HashMap();
	}

	static Map configTypesMap = null;
	static ConfigDamainTree _instance = null;

	static ConfigDomainImpl NULL_CONFIG = ConfigDomainImpl.NULL_CONFIG;// ��������û�����õ�����

	public static ConfigDomainImpl getConfigDomain(String functionPath, String type, ConfigDocumentParser parser) {
		if (_instance == null) {
			_instance = new ConfigDamainTree();
		}
		return _instance.getDomain(functionPath, type, parser);
	}

}
