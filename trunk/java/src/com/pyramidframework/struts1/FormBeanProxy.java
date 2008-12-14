package com.pyramidframework.struts1;

import java.util.HashMap;
import java.util.Map;

import org.apache.struts.action.ActionForm;

import com.pyramidframework.proxy.ProxyHelper;

public class FormBeanProxy extends ActionForm {
	private static final long serialVersionUID = 933437366073076491L;
	private static Map actionFormClazz = new HashMap();
	public static FormBeanProxy getProxyForm(Object bean) {

		Class tc = (Class) actionFormClazz.get(bean.getClass());
		if (tc == null) {
			tc = ProxyHelper.mergeBeanClass(FormBeanProxy.class, bean.getClass());
			actionFormClazz.put(bean.getClass(), tc);
		}
		try {
			FormBeanProxy proxy = (FormBeanProxy) tc.newInstance();
			ProxyHelper.setBeanObject(proxy, bean);
			return proxy;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

}
