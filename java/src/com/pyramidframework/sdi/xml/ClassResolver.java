package com.pyramidframework.sdi.xml;

import com.pyramidframework.sdi.SDIException;
import com.pyramidframework.sdi.operator.AddChildOperator;
import com.pyramidframework.sdi.operator.ModifyOperator;
import com.pyramidframework.sdi.operator.RemoveOperator;
import com.pyramidframework.sdi.xml.reference.NodeReference;

/**
 * 寻找各个具体方法的实现的接口
 * 
 * @author Mikab Peng
 */
public abstract class ClassResolver {

	// 解析操作符的默认类解析器
	public static ClassResolver OPERATOR_RESOLVER = new DefaultOperatorResolver();

	// 解析值对象的默认类解析器
	public static ClassResolver REFERENCE_RESOLVER = new DefaultReferenceResolver();

	/**
	 * 根据传入的参数得到指定的实现类
	 * 
	 * @param parameter
	 * @return
	 * @throws SDIException
	 */
	public abstract Class resolveClass(String parameter) throws SDIException;

	/**
	 * 根据传入的参数得到新的实例
	 * 
	 * @param parameter
	 * @return
	 * @throws SDIException
	 */
	public abstract Object newInstance(String parameter) throws SDIException;

	/**
	 * 默认的操作符解析类
	 */
	public static class DefaultOperatorResolver extends ClassResolver {
		public Class resolveClass(String parameter) throws SDIException {
			if ("add".equals(parameter)) {
				return AddChildOperator.class;
			} else if ("modify".equals(parameter)) {
				return ModifyOperator.class;
			} else if ("remove".equals(parameter)) {
				return RemoveOperator.class;
			} else {
				throw new SDIException("UNKNOWN OPERATOR!");
			}
		}

		public Object newInstance(String parameter) throws SDIException {
			try {
				return resolveClass(parameter).newInstance();
			} catch (Exception e) {
				throw new SDIException(e);
			}
		}
	}

	/** 值检索对象的解析类 */
	public static class DefaultReferenceResolver extends ClassResolver {
		static String packageName = NodeReference.class.getPackage().getName()+".";

		public Class resolveClass(String parameter) throws SDIException {

			// 默认是新对象
			if (parameter == null || "".equals(parameter)) {
				parameter = "New";
			}

			try {

				String className = packageName + parameter + "Reference";
				return Class.forName(className);
			} catch (Exception e) {
				throw new SDIException(e);
			}
		}

		public Object newInstance(String parameter) throws SDIException {
			try {
				return resolveClass(parameter).newInstance();
			} catch (Exception e) {
				throw new SDIException(e);
			}
		}
	}
}
