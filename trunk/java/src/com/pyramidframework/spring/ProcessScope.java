package com.pyramidframework.spring;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.RequestScope;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * ����һ������Beanʵ���Ĺ���������һ����С��Χ��Session�� һ����˵����ʵ��ʱ���Կ��Ǵ���Ҫ�˵�����ʼ��Ϊһ�����̻����򵼡�
 * ��һ���û���˵��ֻ����һ����ǰ���̣���ͨ�����ַ�ʽ��ʵ��sessionЧ����ȷ������������
 * �ⲿ�����ڴ���bean֮ǰ����ProcessScopeSupport��̬����ʵ��֧�֡�δ�ﵽ���е�Ч����
 * ������request�����У�getParameter��ָ��process���ƣ�Name={@link ProcessScope.PARAM_NAME}��,
 * ���δָ��������requestScope����.
 * 
 * @author Mikab Peng
 * @see SpringFactory.getBeanFactory
 * 
 */
public class ProcessScope extends RequestScope {

	/** ��session�б���ĵ�ǰ���̵����� */
	public static final String PROCESS_NAME_KEY = ProcessScope.class.getName() + ".NAME";

	/** ��ǰprocess��������bean�����ֵļ��� */
	public static final String BEANS_NAME_KEY = ProcessScope.class.getName() + ".BEANS";

	/** ָ����ǰprocess���ֵĲ��������û����޸� */
	public static String PARAM_NAME = "processName";

	/** request������û��PARAM_NAME �Ƿ��������ǰ������ */
	public static boolean clearBeansNoProcessNameInRequest = true;

	/**
	 * ��ȡ���ߴ���bean
	 */
	public Object get(String name, ObjectFactory objectFactory) {
		RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
		String processName = (String) attributes.getAttribute(PROCESS_NAME_KEY, RequestAttributes.SCOPE_SESSION);
		if (processName != null) {

			// bean������,�ڸ����������Ѿ�ȷ��������
			Map beanNames = (Map) attributes.getAttribute(BEANS_NAME_KEY, RequestAttributes.SCOPE_SESSION);

			Object scopedObject = beanNames.get(name);
			if (scopedObject == null) {// û�еĶ����򴴽�֮
				scopedObject = objectFactory.getObject();
				beanNames.put(name, scopedObject);
			}

			// ��Ҫ���õ�request�У��Ա��û��ܷ��ʵ�
			attributes.setAttribute(name, scopedObject, getScope());
			return scopedObject;
		} else {
			// �����ǰ��Χ��δָ��process���ƣ�����request����������
			return super.get(name, objectFactory);
		}
	}

	/**
	 * ��sessid�ӵ�ǰ��process������Ϊ��ǰ��ID
	 */
	public String getConversationId() {
		RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
		String processName = (String) attributes.getAttribute(PROCESS_NAME_KEY, RequestAttributes.SCOPE_SESSION);

		return attributes.getSessionId() + processName;
	}

	/**
	 * ����ɾ��
	 */
	public Object remove(String name) {
		RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
		String processName = (String) attributes.getAttribute(PROCESS_NAME_KEY, RequestAttributes.SCOPE_SESSION);
		if (processName != null) {

			// bean������,�ڸ����������Ѿ�ȷ��������
			Map beanNames = (Map) attributes.getAttribute(BEANS_NAME_KEY, RequestAttributes.SCOPE_SESSION);
			return beanNames.remove(name);
		} else {
			return super.remove(name);
		}
	}

	/**
	 * ������ProcessScope��֧�֣���ÿ��request��ʼ����֮ǰ������ô˷���
	 * 
	 * @see SpringFactory.getBeanFactory
	 * @return �����ǰrequest����{@link PARAM_NAME}��֧�֣��򷵻�true,����Ϊfalse
	 */
	public static boolean ProcessScopeSupport() {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		String processName = (String) attributes.getAttribute(PROCESS_NAME_KEY, RequestAttributes.SCOPE_SESSION);
		String paramName = attributes.getRequest().getParameter(PARAM_NAME);
		if (paramName != null) {

			if (!paramName.equals(processName)) {// �л�����ʱ�������ǰ�����Ķ���
				attributes.setAttribute(PROCESS_NAME_KEY, paramName, RequestAttributes.SCOPE_SESSION);
				attributes.setAttribute(BEANS_NAME_KEY, new HashMap(), RequestAttributes.SCOPE_SESSION);
			}
			return true;
		} else {
			if (clearBeansNoProcessNameInRequest) {
				attributes.removeAttribute(PROCESS_NAME_KEY, RequestAttributes.SCOPE_SESSION);
				attributes.removeAttribute(BEANS_NAME_KEY, RequestAttributes.SCOPE_SESSION);
			}
			return true;
		}
	}

}
