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
import com.pyramidframework.ci.ConfigDomain;
import com.pyramidframework.ci.ConfigurationManager;
import com.pyramidframework.ci.DefaultDocumentParser;
import com.pyramidframework.ci.IncrementDocumentParser;
import com.pyramidframework.sdi.xml.XmlDocument;
import com.pyramidframework.sdi.xml.XmlNode;

/**
 * ������Ϣ���νṹ
 * 
 * @author Mikab Peng
 * 
 */
public class ConfigDamainTree implements ConfigServiceProvider {

	public static String separator = ConfigurationManager.FUNCTION_PATH_SEPARATOR;
	ConfigurationManager managerInstance = null;

	/**
	 * 
	 */
	public ConfigDomain getDomain(String functionPath, String type, ConfigDocumentParser parser) {
		if (configTypesMap == null) {
			configTypesMap = createContainerMap();
		}

		Map configTree = (Map) configTypesMap.get(type);
		if (configTree == null) {
			configTree = createContainerMap();
			configTypesMap.put(type, configTree);
		}

		ConfigDomainImpl configDomain = (ConfigDomainImpl) configTree.get(functionPath);
		if (configDomain == null ) { // cached=falseʱ���ݿ��ܲ����ڲ�����
			configDomain = lookupAndConstructDomain(functionPath, type, parser, functionPath);
			configTree.put(functionPath, configDomain);
		}

		// ���û��������򷵻�NULL
		if (configDomain == NULL_CONFIG) {
			return null;
		}

		return configDomain;
	}

	/**
	 * @param functionPath
	 * @param targetPath
	 *            �������𱾴���ֵ��Ŀ��·��
	 * @param configType
	 * @param parser
	 * @return
	 */
	protected ConfigDomainImpl lookupAndConstructDomain(String functionPath, String configType, ConfigDocumentParser parser, String targetPath) {
		if(functionPath == null || "".equals(functionPath)){
			return  null;
		}
		
		XmlNode node = lookupConfigedDomain(functionPath, configType, parser, targetPath);

		ConfigDomainImpl configDomain = null;

		if (node != null) {
			configDomain = new ConfigDomainImpl(functionPath, configType);
			return parseAndConstructDomain(configDomain, targetPath, node, parser);
		}

		if (configDomain == null) {
			configDomain = getDomainFromDefaultRule(functionPath, configType, parser, targetPath);
		}
		return configDomain;
	}

	protected XmlNode lookupConfigedDomain(String functionPath, String configType, ConfigDocumentParser parser, String targetPath) {

		if (functionPath == null || functionPath.length() == 0) {
			if (parser instanceof DefaultDocumentParser) {
				return ((DefaultDocumentParser) parser).getDefaultDocument();
			}
			return null;
		}

		String oldPath = functionPath;
		// ȥ�����һ��FUNCTION_PATH_SEPARATOR�������
		if (functionPath.lastIndexOf(separator) >= 0) {
			functionPath = functionPath.substring(0, functionPath.lastIndexOf(separator));
		}

		StringTokenizer tokenizer = new StringTokenizer(functionPath, separator);
		ArrayList configFiles = new ArrayList();

		// ����Ӹ�Ŀ¼������
		configFiles.add(new File(parser.getConfigFileName(separator, configType)));

		// ���ε�����Ŀ¼���Ҷ�Ӧ���ļ�
		StringBuffer path = (new StringBuffer(oldPath.length())).append(separator);
		while (tokenizer.hasMoreElements()) {
			String t = tokenizer.nextToken();
			if (t != null && t.length() > 0) {
				path.append(t).append(separator);
				File file = new File(parser.getConfigFileName(path.toString(), configType));
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
				node = searchNode(d, oldPath, targetPath, configType);
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

	public XmlNode searchNode(XmlDocument document, String funcPath, String targetPath, String configType) {

		if (funcPath == null || funcPath.length() == 0) {
			return null;
		}

		Document doc = document.getDom4jDocument();
		List l = doc.getRootElement().elements();

		QName fpAttribute = new QName("functionPath", getNamespace());
		QName ctAttribute = new QName("configType", getNamespace());
		// ���������������Ϣ���ҺͲ��ҵĲ�һ����Ҳ�����Կ���

		for (int i = l.size() - 1; i >= 0; i--) {
			Element element = (Element) l.get(i);
			if (funcPath.equals(element.attributeValue(fpAttribute))) {

				String ct = element.attributeValue(ctAttribute);
				if (ct == null || "".equals(ct) || configType.equals(ct)) {
					XmlNode node = new XmlNode(element, document.getNamespaces());
					return OGNLExpressionInterpreter.expandTemplateExpression(node, targetPath, getNamespace());
				}
			}
		}
		return null;
	}

	/**
	 * ����Ĭ�ϼ̳еĹ�����ִ��
	 * 
	 * @param functionPath
	 * @param configType
	 * @param parser
	 * @return
	 */
	protected ConfigDomainImpl getDomainFromDefaultRule(String functionPath, String configType, ConfigDocumentParser parser, String targetPath) {
		// ʹ�õݹ�ķ�ʽ���ϼ�Ŀ¼��ʼ����
		if (functionPath.lastIndexOf(separator) > 0) {
			functionPath = ConfigurationManager.getParentPath(functionPath);

			ConfigDomainImpl d = lookupAndConstructDomain(functionPath, configType, parser, targetPath);

			// ��Ҫ����һ���µ�������
			return constructDomain(functionPath, configType, d.getConfigData());
		}

		ConfigDomainImpl impl = null;
		if (parser instanceof IncrementDocumentParser) {
			Object o = ((IncrementDocumentParser) parser).getConfigData(null, null);
			if (o != null) {
				impl = constructDomain(functionPath, configType, o);
			}

		} else {
			if (parser instanceof DefaultDocumentParser) {
				XmlDocument d = ((DefaultDocumentParser) parser).getDefaultDocument();
				if (d != null) {
					Object o = parser.parseConfigDocument(null, d);
					impl = constructDomain(functionPath, configType, o);
				}
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
	protected ConfigDomainImpl constructDomain(String functionPath, String type, Object o) {
		// TODO:�������νṹ
		ConfigDomainImpl impl = new ConfigDomainImpl(functionPath, type);
		impl.setConfigData(o);
		return impl;
	}

	public ConfigDomainImpl parseAndConstructDomain(ConfigDomainImpl domainImpl, String targetPath, XmlNode node, ConfigDocumentParser parser) {
		ConfigurationFileParser fileParser = new ConfigurationFileParser(domainImpl, node, parser, this);

		return fileParser.parseData(targetPath);
	}

	protected Map createContainerMap() {
		return new HashMap();
	}

	static Map configTypesMap = null;
	static ConfigServiceProvider _instance = null;

	static ConfigDomainImpl NULL_CONFIG = ConfigDomainImpl.NULL_CONFIG;// ��������û�����õ�����

	public ConfigDamainTree(ConfigurationManager manager) {
		this.managerInstance = manager;
	}

	Namespace getNamespace() {
		return managerInstance.getNamespace();
	}

}
