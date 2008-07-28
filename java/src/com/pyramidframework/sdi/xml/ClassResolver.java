package com.pyramidframework.sdi.xml;

import com.pyramidframework.sdi.SDIException;
import com.pyramidframework.sdi.operator.AddChildOperator;
import com.pyramidframework.sdi.operator.ModifyOperator;
import com.pyramidframework.sdi.operator.RemoveOperator;
import com.pyramidframework.sdi.xml.reference.NodeReference;

/**
 * Ѱ�Ҹ������巽����ʵ�ֵĽӿ�
 * 
 * @author Mikab Peng
 */
public abstract class ClassResolver {

	// ������������Ĭ���������
	public static ClassResolver OPERATOR_RESOLVER = new DefaultOperatorResolver();

	// ����ֵ�����Ĭ���������
	public static ClassResolver REFERENCE_RESOLVER = new DefaultReferenceResolver();

	/**
	 * ���ݴ���Ĳ����õ�ָ����ʵ����
	 * 
	 * @param parameter
	 * @return
	 * @throws SDIException
	 */
	public abstract Class resolveClass(String parameter) throws SDIException;

	/**
	 * ���ݴ���Ĳ����õ��µ�ʵ��
	 * 
	 * @param parameter
	 * @return
	 * @throws SDIException
	 */
	public abstract Object newInstance(String parameter) throws SDIException;

	/**
	 * Ĭ�ϵĲ�����������
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

	/** ֵ��������Ľ����� */
	public static class DefaultReferenceResolver extends ClassResolver {
		static String packageName = NodeReference.class.getPackage().getName()+".";

		public Class resolveClass(String parameter) throws SDIException {

			// Ĭ�����¶���
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
