package com.pyramidframework.simpleconfig;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;

import com.pyramidframework.ci.ConfigDomain;
import com.pyramidframework.ci.IncrementDocumentParser;
import com.pyramidframework.ci.TypedManager;
import com.pyramidframework.sdi.NodeOperator;
import com.pyramidframework.sdi.xml.XmlDocument;
import com.pyramidframework.sdi.xml.XmlNode;

/**
 * һ���򵥵�������Ϣ�����ʵ��,�򵥵����ø�ʽ���£�<br>
 * &lt;configuration&gt;<br>
 * &nbsp;&nbsp;&lt;item name="msg"&gt;the first message&lt;/item&gt;<br>
 * &nbsp;&nbsp;&lt;item name="people"&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;people&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;name&gt;zhang doudou&lt;/name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;sex&gt;male&lt;/sex&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/people&gt;<br>
 * &nbsp;&nbsp;&lt;/item&gt;<br>
 * &lt;/configuration&gt;<br>
 * ���ڵ�һ�����ݣ�����ʹ��getConfigDataItemAsString(path,"msg")��ȡ�������ݣ��򵥽ڵ��ֵ������String��
 * �����item��������Ԫ�صģ�����Ҫָ��һ��XmlBeanReader��ʵ��������, �������ʵ����μ�{@link XstreamBeanReader}��ʵ�֣����ӽڵ������ķ��ص���һ������bean��
 * 
 * @author Mikab Peng
 * 
 */
public class SimpleConfigManager extends TypedManager implements IncrementDocumentParser {

	String rootFileDirectory = null;
	XmlBeanReader beanReader = null;
	QName qname = null;

	/**
	 * �õ�ָ��·����ָ�������µľ����������Ϣ������ԭʼ����������
	 * 
	 * @param functionPath
	 * @param itemName
	 * @return
	 */
	public Object getConfigDataItem(String functionPath, String itemName) {

		ConfigContainer container = (ConfigContainer) getConfigData(functionPath);
		return container.getData(itemName);
	}

	/**
	 * �õ�ָ��·����ָ�������µľ����������Ϣ�����ַ�������
	 * 
	 * @param functionPath
	 * @param itemName
	 * @return
	 */
	public String getConfigDataItemAsString(String functionPath, String itemName) {

		ConfigContainer container = (ConfigContainer) getConfigData(functionPath);
		return container.getString(itemName);
	}

	/**
	 * Ĭ�ϵ������ļ�����
	 */
	public final static String defaultType = "simpleconfig";

	/**
	 * ���캯����ʹ��Ĭ�ϵ�������Ϣ����
	 * 
	 * @param rootFileDirectory
	 *            ��ʼ���Ҷ�Ӧ�������ļ��ĸ�Ŀ¼
	 */
	public SimpleConfigManager(String rootFileDirectory) {
		this(defaultType, rootFileDirectory);
	}

	/**
	 * ���캯��
	 * 
	 * @param configType
	 *            ������Ϣ����
	 * @param rootFileDirectory
	 *            ��ʼ���Ҷ�Ӧ�������ļ��ĸ�Ŀ¼
	 */
	public SimpleConfigManager(String configType, String rootFileDirectory) {
		super(configType);
		this.qname = new QName("name");
		this.rootFileDirectory = rootFileDirectory;
		super.parser = this;
	}

	/**
	 * ���캯����ʹ��Ĭ�ϵ�������Ϣ����
	 * 
	 * @param rootFileDirectory
	 *            ��ʼ���Ҷ�Ӧ�������ļ��ĸ�Ŀ¼
	 * @param beanReader
	 *            XmlBeanReader��ʵ�ֵ�ʵ������ΪNULL
	 * @param defaultNamespace
	 *            ������Ϣ��ʹ�õ������ռ䣬��ΪNULL
	 */
	public SimpleConfigManager(String rootFileDirectory, XmlBeanReader beanReader, Namespace defaultNamespace) {
		this(defaultType, rootFileDirectory);
		setBeanReader(beanReader);
		this.qname = new QName("name", defaultNamespace);
	}

	/**
	 * ���캯��
	 * 
	 * @param configType
	 *            ������Ϣ���ͣ�����Ϊ��
	 * @param rootFileDirectory
	 *            ��ʼ���Ҷ�Ӧ�������ļ��ĸ�Ŀ¼
	 * @param beanReader
	 *            XmlBeanReader��ʵ�ֵ�ʵ������ΪNULL
	 * @param defaultNamespace
	 *            ������Ϣ��ʹ�õ������ռ䣬��ΪNULL
	 */
	public SimpleConfigManager(String configType, String rootFileDirectory, XmlBeanReader beanReader, Namespace defaultNamespace) {
		this(configType, rootFileDirectory);
		this.beanReader = beanReader;
		this.qname = new QName("name", defaultNamespace);
	}

	/**
	 * ����������������Ϣ
	 * 
	 * @param thisConfigData
	 * @param childOfRoot
	 * @param operator
	 */
	public Object parseIncrementElement(Object thisConfigData, XmlNode childOfRoot, NodeOperator operator) {
		ConfigContainer data = (ConfigContainer) thisConfigData;
		if (data == null) {
			data = new ConfigContainer();
		}

		Element e = (Element) childOfRoot.getDom4JNode();
		String name = e.attributeValue(qname);

		if (operator != null && "remove".equals(operator.getOperatorName())) {
			data.removeData(name);
		} else {
			List child = e.elements();
			if (child.size() > 0) {
				if (beanReader == null) {
					throw new NullPointerException("You must define a XmlBeanReader instance form an custom XML!");
				} else {
					XmlNode n = new XmlNode((Element) child.get(0), childOfRoot.getNamespaces());
					data.setData(name, beanReader.readFromXmlElement(n));
				}
			} else {
				data.setData(name, e.getText());
			}
		}

		return data;
	}

	/**
	 * �����Ľ�����������ĵ�������
	 * 
	 * @param thisDomain
	 * @param configDocument
	 * @return
	 */
	public Object parseConfigDocument(ConfigDomain thisDomain, XmlDocument configDocument) {
		ConfigContainer data = (ConfigContainer) thisDomain.getConfigData();
		if (data == null) {
			data = new ConfigContainer();
		}

		Document document = configDocument.getDom4jDocument();
		List list = document.getRootElement().elements();
		for (int i = 0; i < list.size(); i++) {
			Element e = (Element) list.get(i);
			String name = e.attributeValue(qname);
			List child = e.elements();
			if (child.size() > 0) {
				if (beanReader == null) {
					throw new NullPointerException("You must define a XmlBeanReader instance form an custom XML!");
				} else {
					XmlNode n = new XmlNode((Element) child.get(0), configDocument.getNamespaces());
					data.setData(name, beanReader.readFromXmlElement(n));
				}
			} else {
				data.setData(name, e.getText());
			}
		}

		return data;
	}

	/**
	 * ��ȡ�ڲ����е�XmlBeanReader��ʵ��
	 * 
	 * @return
	 */
	public XmlBeanReader getBeanReader() {
		return beanReader;
	}

	/**
	 * ָ���µ�beanReader��ʵ��
	 * 
	 * @param beanReader
	 */
	public void setBeanReader(XmlBeanReader beanReader) {
		/*
		 * if(beanReader == null){ throw new NullPointerException("The parameter
		 * beanReader can not be null!"); }
		 */
		this.beanReader = beanReader;
	}

	/**
	 * ������Ϣ�ļ�����������rootFileDirectory + functionPath +configType+ ".xml"
	 * rootFileDirectory�����ʼ��ʱָ��
	 * 
	 * @param functionPath
	 *            ����·��
	 * @param configType
	 *            ��������
	 * @return �����ļ���
	 */
	public String getConfigFileName(String functionPath, String configType) {
		return rootFileDirectory + functionPath + configType + ".xml";
	}

	/**
	 * �����µ�������Ϣ�����е�������Ϣ��������ʵ��
	 * 
	 * @param domain
	 *            ������Ϣ��
	 * @return ���domain=null,�򷵻�Ϊnull�����򷵻�һ��ConfigurationContainer���µ�ʵ��
	 */
	public Object getConfigData(ConfigDomain domain,Object parentDataNode) {

		// ���û��Ĭ���趨������Ϣ
		if (domain == null) {
			return null;
		}
		
		if (parentDataNode != null ){
			ConfigContainer container = (ConfigContainer)parentDataNode;
			try {
				return container.clone();
			} catch (Exception e) {
				return new ConfigContainer();
			}
		}else{
			// ���������Ĭ������һ���µ�ʵ��
			return new ConfigContainer();
		}
	}

	/**
	 * û��Ĭ������
	 * 
	 * @return ��ΪNULl
	 */
	public XmlDocument getDefauDocument() {
		return null;
	}
}
