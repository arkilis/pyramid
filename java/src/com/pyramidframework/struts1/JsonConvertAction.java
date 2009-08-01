package com.pyramidframework.struts1;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pyramidframework.ajax.JsonBeanConverterUtil;
import com.pyramidframework.proxy.ProxyHelper;

/**
 * 利用DWR的机制转换Bean成Json表达式
 * 
 * @author Mikab Peng
 * 
 */
public class JsonConvertAction extends Action {

	public static final String JSON_FORWARD = "json";

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		PrintWriter writer = new PrintWriter(response.getOutputStream());

		Object bean = getFormBean(mapping, request);

		String result = JsonBeanConverterUtil.convertBean(bean);
		response.setContentLength(result.length());
		writer.write(result);
		writer.flush();
		return null;
	}

	/**
	 * @param mapping
	 * @param request
	 * @return
	 */
	protected Object getFormBean(ActionMapping mapping, HttpServletRequest request) {
		String attriName = mapping.getAttribute();
		Object bean = null;
		if (attriName == null) {
			bean = request.getAttribute(RequestProxyProcessor.FORM_BEAN_KEY);
		} else {
			if ("request".equals(mapping.getScope())) {
				bean = request.getAttribute(attriName);
			} else {
				bean = request.getSession().getAttribute(attriName);
			}
		}

		if (bean != null && bean instanceof FormBeanProxy) {
			bean = ProxyHelper.getBeanObject(bean);
		}
		return bean;
	}

}
