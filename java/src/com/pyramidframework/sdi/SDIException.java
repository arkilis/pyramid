/*
 * �����ڣ�2008-06-22
 */
package com.pyramidframework.sdi;

import java.io.PrintStream;

/**
 * StructuredDocumentInheritanceģ��Ŀ����׳����쳣
 * 
 * @author Mikab Peng
 * @version 0.01 2008-06-22
 */
public class SDIException extends Exception {
	private static final long serialVersionUID = 2434834132419314951L;

	/** Ĭ�ϵĴ�����Ϣ */
	public static final String DEFAULT_ERROR_MESSAGE = SDIException.class.getName();

	private Throwable nestedException = null;	//Ƕ�׵��쳣

	/**
	 * Ĭ�Ϲ��캯����ʹ��Ĭ�ϵ���ϢΪ<code>DEFAULT_ERROR_MESSAGE</code>
	 */
	public SDIException() {
		super(DEFAULT_ERROR_MESSAGE);
	}

	public SDIException(String errorMsg) {
		super(errorMsg == null ? DEFAULT_ERROR_MESSAGE : errorMsg);
	}

	public SDIException(Throwable e) {
		super("Nested Exception:" + e.getMessage());
		nestedException = e;
	}

	public SDIException(String Message, Throwable e) {
		super(Message);
		nestedException = e;
	}

	/**
	 * �����Ƕ�׵��쳣��һ�����
	 */
	public void printStackTrace(PrintStream s) {
		if (nestedException != null) {
			nestedException.printStackTrace(s);
		}
		super.printStackTrace(s);
	}

	public String toString() {
		if (nestedException != null) {
			return super.toString() + nestedException.toString();
		} else {
			return super.toString();
		}
	}

}
