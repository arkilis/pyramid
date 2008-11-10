package com.pyramidframework.dao;

public class DAOException extends RuntimeException {
	private static final long serialVersionUID = 1043396336409591416L;

	public DAOException() {

	}

	public DAOException(Throwable inner) {
		super(inner);
	}

	public DAOException(String message) {
		super(message);
	}

	public DAOException(String message, Throwable cause) {
		super(message, cause);
	}

}
