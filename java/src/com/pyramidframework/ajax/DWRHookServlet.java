package com.pyramidframework.ajax;

import java.io.InputStream;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import uk.ltd.getahead.dwr.AccessControl;
import uk.ltd.getahead.dwr.Configuration;
import uk.ltd.getahead.dwr.Container;
import uk.ltd.getahead.dwr.ConverterManager;
import uk.ltd.getahead.dwr.CreatorManager;
import uk.ltd.getahead.dwr.DWRServlet;
import uk.ltd.getahead.dwr.Processor;
import uk.ltd.getahead.dwr.WebContextBuilder;
import uk.ltd.getahead.dwr.impl.DefaultContainer;

public class DWRHookServlet extends DWRServlet {

	private static final long serialVersionUID = -7214475116529926581L;

	/**
	 * 返回实例
	 */
	public Container getContainer(ServletConfig config) throws ServletException {
		try {
			DefaultContainer factory = new DefaultContainer();
			factory.addParameter(AccessControl.class.getName(), "uk.ltd.getahead.dwr.impl.DefaultAccessControl"); //$NON-NLS-1$
			factory.addParameter(Configuration.class.getName(), "uk.ltd.getahead.dwr.impl.DefaultConfiguration"); //$NON-NLS-1$
			factory.addParameter(ConverterManager.class.getName(), "uk.ltd.getahead.dwr.impl.DefaultConverterManager"); //$NON-NLS-1$
			factory.addParameter(CreatorManager.class.getName(), LazyCreatorManager.class.getName()); //$NON-NLS-1$
			factory.addParameter(Processor.class.getName(), "uk.ltd.getahead.dwr.impl.DefaultProcessor"); //$NON-NLS-1$
			factory.addParameter(WebContextBuilder.class.getName(), "uk.ltd.getahead.dwr.impl.DefaultWebContextBuilder"); //$NON-NLS-1$

			factory.addParameter("index", "uk.ltd.getahead.dwr.impl.DefaultIndexProcessor"); //$NON-NLS-1$ //$NON-NLS-2$
			factory.addParameter("test", "uk.ltd.getahead.dwr.impl.DefaultTestProcessor"); //$NON-NLS-1$ //$NON-NLS-2$
			factory.addParameter("interface", "uk.ltd.getahead.dwr.impl.DefaultInterfaceProcessor"); //$NON-NLS-1$ //$NON-NLS-2$
			factory.addParameter("exec", ExecProcessor.class.getName()); //$NON-NLS-1$ //$NON-NLS-2$
			factory.addParameter("file", "uk.ltd.getahead.dwr.impl.FileProcessor"); //$NON-NLS-1$ //$NON-NLS-2$

			factory.addParameter("debug", "false"); //$NON-NLS-1$ //$NON-NLS-2$
			factory.addParameter("allowImpossibleTests", "false"); //$NON-NLS-1$ //$NON-NLS-2$

			factory.addParameter("scriptCompressed", "true"); //$NON-NLS-1$ //$NON-NLS-2$

			Enumeration en = config.getInitParameterNames();
			while (en.hasMoreElements()) {
				String name = (String) en.nextElement();
				String value = config.getInitParameter(name);
				factory.addParameter(name, value);
			}
			factory.configurationFinished();

			return factory;
		} catch (Exception ex) {
			throw new ServletException(ex);
		}
	}

	/**
	 * 增加自定义的创建器
	 */
	public void configure(ServletConfig config, Configuration configuration) throws ServletException {

		String fileName = "/" + getClass().getPackage().getName().replace(".", "/") + "/dwr.xml";
		InputStream stream = getClass().getResourceAsStream(fileName);

		try {
			configuration.addConfig(stream);
		} catch (Exception e) {
			throw new ServletException(e);
		}

		super.configure(config, configuration);
	}

}
