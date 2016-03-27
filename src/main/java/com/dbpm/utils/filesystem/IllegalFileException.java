/*
*
* author:  gvenzl
* created: 25 Mar 2016
*
* name: IllegalFileException.java
*
*/

package com.dbpm.utils.filesystem;

public class IllegalFileException extends Exception {

	private static final long serialVersionUID = 8760980124885261672L;

	public IllegalFileException(String message) {
		super(message);
	}

	public IllegalFileException(Throwable cause) {
		super(cause);
	}

	public IllegalFileException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalFileException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
