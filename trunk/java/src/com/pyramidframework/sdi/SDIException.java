/*
 * 创建于：2008-06-22
 */
package com.pyramidframework.sdi;

/**
 * StructuredDocumentInheritance模块的可能抛出的异常
 * 
 * @author Mikab Peng
 * @version 0.01 2008-06-22
 */
public class SDIException extends RuntimeException {
	private static final long serialVersionUID = 2434834132419314951L;

	public SDIException() {
		super();
	}

	public SDIException(String message) {
		super(message);
	}

	public SDIException(String message, Throwable cause) {
		super(message, cause);
	}

	public SDIException(Throwable cause) {
		super(cause);
	}

}
