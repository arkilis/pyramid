package com.pyramidframework.struts1;

import java.util.Map;
import java.util.WeakHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pyramidframework.proxy.ProxyHelper;
import com.pyramidframework.proxy.SingleMethodProxyHandler;

public class ActionProxy extends Action {
	ActionSupport support = null;

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			return mapping.findForward(support.execute());
		} catch (Throwable t) {
			Throwable e = t;
			while(true){
				if (e instanceof Exception){
					if (((Exception)e).getCause() != null){
						e = ((Exception)e).getCause();
					}else{
						break;
					}
				}else{
					break;
				}
			}
			e.printStackTrace();
			request.setAttribute("error","¥ÌŒÛ£∫" +  e.getMessage());
			return mapping.findForward("error");
		}
	}

	public ActionProxy(ActionSupport support) {
		this.support = support;
	}

	private static Map actionMaps = new WeakHashMap();

	public static ActionProxy getActionProxy(Object bean) {
		ActionProxy proxy = (ActionProxy) actionMaps.get(bean);

		// TODO:øº¬«bean.Equal÷ÿ‘ÿµƒ”∞œÏ
		if (proxy == null) {
			if (bean instanceof ActionSupport) {
				proxy = new ActionProxy((ActionSupport) bean);
			} else {
				ActionSupport support = (ActionSupport) ProxyHelper.translateObject(ActionSupport.class, new SingleMethodProxyHandler(bean));
				proxy = new ActionProxy(support);
			}
			actionMaps.put(bean, proxy);
		}
		return proxy;
	}
}
