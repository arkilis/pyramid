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
import com.pyramidframework.script.CompilableScriptEngine;
import com.pyramidframework.sdi.xml.XmlDocument;
import com.pyramidframework.sdi.xml.XmlNode;

/**
 * ������Ϣ���νṹ
 * 
 * @author Mikab Peng
 * 
 */
public class ConfigDamainTree implements ConfigServiceProvider {

	ConfigurationManager managerInstance = null;
	protected Map rootContainerMap = createContainerMap();
	CompilableScriptEngine scriptEngine = null;

	/**
	 * 
	 */
	public ConfigDomain getDomain(String functionPath, String type, ConfigDocumentParser parser) {
		//System.err.println("getDomain" + functionPath);
		
		Map configTree = getTypedContainer(type);

		ConfigDomainImpl configDomain = (ConfigDomainImpl) configTree.get(functionPath);
		if (configDomain == null) {
			configDomain = lookupAndConstructDomain(functionPath, type, parser, functionPath);
			configTree.put(functionPath, configDomain);
			if (configDomain != null) { // �������¼���ϵ
				String parentPath = configDomain.getParentPath();
				if (parentPath == null || parentPath.length() == 0 || "none".equals(parentPath)) {
					configDomain.setParentNode(null);
				} else {
					ConfigDomainImpl parent = (ConfigDomainImpl) getDomain(parentPath, type, parser);
					configDomain.setParentNode(parent);
				}
			}

		}

		return configDomain;
	}

	/**
	 * @param type
	 * @return
	 */
	protected Map getTypedContainer(String type) {

		Map configTree = (Map) rootContainerMap.get(type);
		if (configTree == null) {
			configTree = createContainerMap();
			rootContainerMap.put(type, configTree);
		}
		return configTree;
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
		if (functionPath == null || "".equals(functionPath)) {
			return null;
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
		checkFileExist(configFiles, parser.getConfigFileName(separator, configType));
		
		// ���ε�����Ŀ¼���Ҷ�Ӧ���ļ�
		StringBuffer path = (new StringBuffer(oldPath.length())).append(separator);
		while (tokenizer.hasMoreElements()) {
			String t = tokenizer.nextToken();
			if (t != null && t.length() > 0) {
				path.append(t).append(separator);
				String fileName = parser.getConfigFileName(path.toString(), configType);
				checkFileExist(configFiles, fileName);
			}
		}
		
		//System.err.println(configFiles);
		
		// �������幦�ܽڵ�ĵط���ʼ���ϲ��ң�ֱ���ҵ�������ϢΪֹ
		for (int i = configFiles.size() - 1; i >= 0; i--) {
			String resourcePath = (String) configFiles.get(i);
			XmlNode node = null;
			try {
				XmlDocument d = new XmlDocument(resourcePath);
				node = searchNode(d, oldPath, targetPath, configType, parser);
				//System.err.println(d.getDom4JNode().asXML() + 2 + oldPath);
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

	/**
	 * @param configFiles
	 * @param fileName
	 */
	protected void checkFileExist(ArrayList configFiles, String fileName) {
		//System.err.println(fileName);
		
		File file = new File(fileName);
		if ((this.getClass().getResourceAsStream(fileName) != null || file.exists()  )&& !configFiles.contains(fileName)) {
			configFiles.add(fileName);
		}
	}

	public XmlNode searchNode(XmlDocument document, String funcPath, String targetPath, String configType, ConfigDocumentParser parser) {

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
				// System.err.println(ct);
				if (ct == null || "".equals(ct) || configType.equals(ct)) {
					XmlNode node = new XmlNode(element, document.getNamespaces());
					return ExpressionInterpreter.expandTemplateExpression(node, targetPath, getNamespace(), parser, scriptEngine);
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
		String rootPath = functionPath;
		if (functionPath.length() > 0) {
			functionPath = ConfigurationManager.getDefaultParentPath(functionPath);

			ConfigDomainImpl d = lookupAndConstructDomain(functionPath, configType, parser, targetPath);

			// ��Ҫ����һ���µ�������
			if (d == null){
				return null;
			}
			return constructDomain(rootPath, configType, d.getConfigData());
		}

		ConfigDomainImpl impl = null;
		if (parser instanceof IncrementDocumentParser) {
			Object o = ((IncrementDocumentParser) parser).getDefaultConfigData(null, null);
			if (o != null) {
				impl = constructDomain(rootPath, configType, o);
			}

		} else {
			if (parser instanceof DefaultDocumentParser) {
				XmlDocument d = ((DefaultDocumentParser) parser).getDefaultDocument();
				if (d != null) {
					Object o = parser.parseConfigDocument(null, d);
					impl = constructDomain(rootPath, configType, o);
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

		String parentPath = ConfigurationManager.getDefaultParentPath(functionPath);
		impl.setParentPath(parentPath);
		return impl;
	}

	public ConfigDomainImpl parseAndConstructDomain(ConfigDomainImpl domainImpl, String targetPath, XmlNode node, ConfigDocumentParser parser) {
		ConfigurationFileParser fileParser = new ConfigurationFileParser(domainImpl, node, parser, this);

		return fileParser.parseData(targetPath);
	}

	protected Map createContainerMap() {
		return new HashMap();
	}

	// final static ConfigDomainImpl NULL_CONFIG =
	// ConfigDomainImpl.NULL_CONFIG;// ��������û�����õ�����
	public static String separator = ConfigurationManager.FUNCTION_PATH_SEPARATOR;

	public ConfigDamainTree(ConfigurationManager manager) {
		this.managerInstance = manager;

		this.scriptEngine =  manager.getScriptEngine();

	}

	Namespace getNamespace() {
		return managerInstance.getNamespace();
	}

}
