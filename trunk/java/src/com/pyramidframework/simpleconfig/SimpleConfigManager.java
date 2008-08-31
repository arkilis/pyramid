package com.pyramidframework.simpleconfig;

import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.dom4j.QName;

import com.pyramidframework.ci.ConfigDomain;
import com.pyramidframework.ci.IncrementDocumentParser;
import com.pyramidframework.ci.TypedManager;
import com.pyramidframework.sdi.NodeOperator;
import com.pyramidframework.sdi.xml.XmlDocument;
import com.pyramidframework.sdi.xml.XmlNode;

/**
 * 一个简单的配置信息管理的实现,简单的配置格式如下：<br>
 * &lt;configuration&gt;<br>
 * &nbsp;&nbsp;&lt;item name="msg"&gt;the first message&lt;/item&gt;<br>
 * &nbsp;&nbsp;&lt;item name="people"&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;people&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;name&gt;zhang doudou&lt;/name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;sex&gt;male&lt;/sex&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/people&gt;<br>
 * &nbsp;&nbsp;&lt;/item&gt;<br>
 * &lt;/configuration&gt;<br>
 * 对于第一项数据，可以使用getConfigDataItemAsString(path,"msg")获取他的数据，简单节点的值解析成String；
 * 如果是item包含了子元素的，则需要指定一个XmlBeanReader的实例来解析, 解析类的实现请参见{@link XStreamBeanReader}的实现，复杂节点解析后的返回的是一个数据bean。
 * 
 * @author Mikab Peng
 * 
 */
public class SimpleConfigManager extends TypedManager {

	String rootFileDirectory = null;
	XmlBeanReader beanReader = null;
	QName qname = null;

	/**
	 * 得到指定路径的指定名字下的具体的配置信息，返回原始的数据类型
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
	 * 得到指定路径的指定名字下的具体的配置信息，用字符串返回
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
	 * 默认的配置文件类型
	 */
	public final static String defaultType = "simpleconfig";

	/**
	 * 构造函数，使用默认的配置信息类型
	 * 
	 * @param rootFileDirectory
	 *            开始查找对应的配置文件的根目录
	 */
	public SimpleConfigManager(String rootFileDirectory) {
		this(defaultType, rootFileDirectory);
	}

	/**
	 * 构造函数
	 * 
	 * @param configType
	 *            配置信息类型
	 * @param rootFileDirectory
	 *            开始查找对应的配置文件的根目录
	 */
	public SimpleConfigManager(String configType, String rootFileDirectory) {
		super(configType);
		this.qname = new QName("name");

		// 去掉最后的那个/
		while (rootFileDirectory.endsWith("/")) {
			rootFileDirectory = rootFileDirectory.substring(0, rootFileDirectory.length() - 1);
		}
		this.rootFileDirectory = rootFileDirectory;
		createDocumentParser();
	}

	/**
	 * 创建文件解析器
	 */
	protected void createDocumentParser() {
		super.parser = new SimpleConfigDocumentParser();
	}

	/**
	 * 构造函数，使用默认的配置信息类型
	 * 
	 * @param rootFileDirectory
	 *            开始查找对应的配置文件的根目录
	 * @param beanReader
	 *            XmlBeanReader的实现的实例，可为NULL
	 * @param defaultNamespace
	 *            配置信息中使用的命名空间，可为NULL
	 */
	public SimpleConfigManager(String rootFileDirectory, XmlBeanReader beanReader, Namespace defaultNamespace) {
		this(defaultType, rootFileDirectory);
		setBeanReader(beanReader);
		this.qname = new QName("name", defaultNamespace);
	}

	/**
	 * 构造函数
	 * 
	 * @param configType
	 *            配置信息类型，不能为空
	 * @param rootFileDirectory
	 *            开始查找对应的配置文件的根目录
	 * @param beanReader
	 *            XmlBeanReader的实现的实例，可为NULL
	 * @param defaultNamespace
	 *            配置信息中使用的命名空间，可为NULL
	 */
	public SimpleConfigManager(String configType, String rootFileDirectory, XmlBeanReader beanReader, Namespace defaultNamespace) {
		this(configType, rootFileDirectory);
		this.beanReader = beanReader;
		this.qname = new QName("name", defaultNamespace);
	}

	/**
	 * 获取内部持有的XmlBeanReader的实例
	 * 
	 * @return
	 */
	public XmlBeanReader getBeanReader() {
		return beanReader;
	}

	/**
	 * 指定新的beanReader的实现
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
	 * simpleconfig的文件的解析器，使用内部类主要为了隔离函数之间的混淆
	 * 
	 * @author Mikab Peng
	 * 
	 */
	protected class SimpleConfigDocumentParser implements IncrementDocumentParser {

		/**
		 * 解析增量的配置信息
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
			String name = null;
			Node node = childOfRoot.getDom4JNode();

			if (node instanceof Element) { // 包含的元素是一个element
				Element e = (Element) node;
				name = e.attributeValue(qname);

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
			} else if (node instanceof Attribute) { // 属性，则需要看是不是指定的Name，只支持删除
				if (operator != null && "remove".equals(operator.getOperatorName()) && "name".equals(((Attribute) node).getName())) {
					name = ((Attribute) node).getValue();
					data.removeData(name);
				}
			} else {	//其他的完全按照文本对待，只支持删除
				if (operator != null && "remove".equals(operator.getOperatorName())) {
					name = node.getText().trim();
					data.removeData(name);
				}
			}
			return data;
		}

		/**
		 * 完整的解析真个配置文档的数据
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
		 * 配置信息文件的命名规则：rootFileDirectory + functionPath +configType+ ".xml"
		 * rootFileDirectory在类初始化时指定
		 * 
		 * @param functionPath
		 *            功能路径
		 * @param configType
		 *            配置类型
		 * @return 配置文件名
		 */
		public String getConfigFileName(String functionPath, String configType) {
			return rootFileDirectory + functionPath + configType + ".xml";
		}

		/**
		 * 创建新的配置信息域所有的配置信息的容器的实例
		 * 
		 * @param domain
		 *            配置信息域
		 * @return 如果domain=null,则返回为null，否则返回一个ConfigurationContainer的新的实例
		 */
		public Object getDefaultConfigData(ConfigDomain domain, Object parentDataNode) {

			// 软件没有默认设定配置信息
			if (domain == null) {
				return null;
			}

			if (parentDataNode != null) {
				ConfigContainer container = (ConfigContainer) parentDataNode;
				try {
					return container.clone();
				} catch (Exception e) {
					return new ConfigContainer();
				}
			} else {
				// 其他情况下默认生成一下新的实例
				return new ConfigContainer();
			}
		}
		
		/**
		 * 将manaer本身放入到上下文见中的"SimpleConfigManager"的名字下
		 * @param templateContext 模板执行的上下文
		 */
		public void InitTemplateContext(Map templateContext) {
			
			templateContext.put("SimpleConfigManager", SimpleConfigManager.this);
			
		}
	}

}
