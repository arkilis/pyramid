package com.pyramidframework.sdi.xml;

import java.io.FileOutputStream;

import org.dom4j.Namespace;
import org.dom4j.io.XMLWriter;

import com.pyramidframework.sdi.OperatorConvter;
import com.pyramidframework.sdi.SDIDocument;
import com.pyramidframework.sdi.SDIException;
import com.pyramidframework.sdi.StructuredDocumentInheritance;

/**
 * 用于XML文件转换的标准实现.用法如下：<br>
 * XmlDocument templet_comp = new XmlDocument("resource/sample/show_company_templet.htm");<br>
 * XmlDocument rule_comp = new XmlDocument("resource/sample/rule_show_company.xml");<br>
 * XmlDocument target = XmlInhertance.doInheritance(templet_comp, rule_comp);<br>
 * 其中templet_comp是用于继承的父文档，rule_comp则是描述继承规则的规则文档，target则是转换后的结果。
 * 更多信息请参见{@link XmlDocument}
 * @author Mikab Peng
 * @version 2008-7-27
 */
public class XmlInhertance extends StructuredDocumentInheritance {

	/**
	 * StructuredDocumentInheritance模块的默认URL
	 */
	public static final String DEFAULT_NAMESPACE_URI = "http://www.pyramidframework.com/2008/StructuredDocumentInheritance";

	/**
	 * StructuredDocumentInheritance模块的在XML文档中的默认前缀
	 */
	public static final String DEFAULT_NAMESPACE_PREFIX = "sdi";

	/** 是否使用命名空间，默认为false(不使用) */
	protected boolean useNamespace = true;

	public boolean isUseNamespace() {
		return useNamespace;
	}

	public void setUseNamespace(boolean useNamespace) {
		this.useNamespace = useNamespace;
	}

	protected Namespace defaultNameSpace = null;

	/**
	 * 构建默认的转换器，并自动探测是否启用命名空间表示
	 */
	public SDIDocument getTargetDocument(SDIDocument parent, SDIDocument rule) throws SDIException {

		if (this.DEFAULT_OPERATOR_CONVTER == null) {
			XmlDocument document = (XmlDocument) rule;

			checkIfNeedUseNamespace(document);

			createDefaultNamespace();

			createOperatorConvter();
		}

		// 注意字符集的问题
		XmlDocument document = (XmlDocument) super.getTargetDocument(parent, rule);
		document.getDom4jDocument().setXMLEncoding(((XmlDocument) parent).getDom4jDocument().getXMLEncoding());
		return document;
	}

	/**
	 * @param document
	 */
	protected void checkIfNeedUseNamespace(XmlDocument document) {
		// 判断是否需要默认命名空间
		if (!useNamespace) {
			useNamespace = DEFAULT_NAMESPACE_URI.equals(document.namespaces.get(DEFAULT_NAMESPACE_PREFIX));
		}
	}

	/**
	 * 创建默认使用的命名空间
	 */
	protected Namespace createDefaultNamespace() {
		if (defaultNameSpace == null) {
			defaultNameSpace = new Namespace(XmlInhertance.DEFAULT_NAMESPACE_PREFIX, XmlInhertance.DEFAULT_NAMESPACE_URI);
		}

		return defaultNameSpace;
	}

	/**
	 * 根据原文件和继承定义文件得到完成转换后的文件
	 * 
	 * @param source
	 *            原文件
	 * @param rule
	 *            继承定义文件
	 * @return
	 * @throws SDIException
	 */
	public static XmlDocument doInheritance(XmlDocument source, XmlDocument rule) throws SDIException {
		return (XmlDocument) (new XmlInhertance()).getTargetDocument(source, rule);
	}

	/**
	 * 创建描述到操作的转换器
	 */
	protected OperatorConvter createOperatorConvter() {
		if (this.DEFAULT_OPERATOR_CONVTER == null) {
			this.DEFAULT_OPERATOR_CONVTER = new XmlConvter(this);
		}
		return this.DEFAULT_OPERATOR_CONVTER;
	}

	/**
	 * 执行，依次需要三个参数：输入文件、继承定义文件、输出文件
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 3) {
			System.out.println("Usage: java com.pyramidframework.sdi.xml.XmlInhertance inFile ruleFile outFile");
		} else {
			try {
				XmlDocument parent = new XmlDocument(args[0]);
				XmlDocument rule = new XmlDocument(args[1]);
				XmlDocument target = doInheritance(parent, rule);
				FileOutputStream ou = null;
				try {
					ou = new FileOutputStream(args[2]);
					XMLWriter writer = new XMLWriter();
					writer.write(target.getDom4jDocument());
				} finally {
					if (ou != null)
						ou.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
