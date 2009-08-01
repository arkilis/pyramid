package com.pyramidframework.spring;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.RequestScope;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 用于一个过程Bean实例的共享，类似于一个缩小范围的Session。 一般来说，在实现时可以考虑从主要菜单处开始即为一个过程或者向导。
 * 对一个用户来说，只存在一个当前过程，即通过这种方式来实现session效果和确保对象的清除。
 * 外部必须在处理bean之前调用ProcessScopeSupport静态方法实现支持。未达到进行的效果，
 * 必须在request参数中（getParameter）指定process名称（Name={@link ProcessScope.PARAM_NAME}）,
 * 如果未指定，则按照requestScope处理.
 * 
 * @author Mikab Peng
 * @see SpringFactory.getBeanFactory
 * 
 */
public class ProcessScope extends RequestScope {

	/** 在session中保存的当前进程的名字 */
	public static final String PROCESS_NAME_KEY = ProcessScope.class.getName() + ".NAME";

	/** 当前process所包含的bean的名字的集合 */
	public static final String BEANS_NAME_KEY = ProcessScope.class.getName() + ".BEANS";

	/** 指定当前process名字的参数名，用户可修改 */
	public static String PARAM_NAME = "processName";

	/** request参数中没有PARAM_NAME 是否清除掉以前的内容 */
	public static boolean clearBeansNoProcessNameInRequest = true;

	/**
	 * 获取或者创建bean
	 */
	public Object get(String name, ObjectFactory objectFactory) {
		RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
		String processName = (String) attributes.getAttribute(PROCESS_NAME_KEY, RequestAttributes.SCOPE_SESSION);
		if (processName != null) {

			// bean的容器,在辅助方法中已经确保创建了
			Map beanNames = (Map) attributes.getAttribute(BEANS_NAME_KEY, RequestAttributes.SCOPE_SESSION);

			Object scopedObject = beanNames.get(name);
			if (scopedObject == null) {// 没有的对象则创建之
				scopedObject = objectFactory.getObject();
				beanNames.put(name, scopedObject);
			}

			// 需要放置到request中，以便用户能访问到
			attributes.setAttribute(name, scopedObject, getScope());
			return scopedObject;
		} else {
			// 如果当前范围内未指定process名称，则按照request名字来处理
			return super.get(name, objectFactory);
		}
	}

	/**
	 * 用sessid加当前的process号码作为当前的ID
	 */
	public String getConversationId() {
		RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
		String processName = (String) attributes.getAttribute(PROCESS_NAME_KEY, RequestAttributes.SCOPE_SESSION);

		return attributes.getSessionId() + processName;
	}

	/**
	 * 有则删除
	 */
	public Object remove(String name) {
		RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
		String processName = (String) attributes.getAttribute(PROCESS_NAME_KEY, RequestAttributes.SCOPE_SESSION);
		if (processName != null) {

			// bean的容器,在辅助方法中已经确保创建了
			Map beanNames = (Map) attributes.getAttribute(BEANS_NAME_KEY, RequestAttributes.SCOPE_SESSION);
			return beanNames.remove(name);
		} else {
			return super.remove(name);
		}
	}

	/**
	 * 创建对ProcessScope的支持，在每个request开始处理之前必须调用此方法
	 * 
	 * @see SpringFactory.getBeanFactory
	 * @return 如果当前request包含{@link PARAM_NAME}的支持，则返回true,否则为false
	 */
	public static boolean ProcessScopeSupport() {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		String processName = (String) attributes.getAttribute(PROCESS_NAME_KEY, RequestAttributes.SCOPE_SESSION);
		String paramName = attributes.getRequest().getParameter(PARAM_NAME);
		if (paramName != null) {

			if (!paramName.equals(processName)) {// 切换进程时，清空以前创建的对象
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
