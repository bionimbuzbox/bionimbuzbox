package org.bionimbuzbox.exception;

public class DAOException extends Exception {

	private static final long serialVersionUID = -4940789988129528981L;

	public DAOException() {
		super();
	}
	
	public DAOException(String message) {
		super(message);
	}
	
	public DAOException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
	public DAOException(Throwable throwable) {
		super(throwable);
	}
}
