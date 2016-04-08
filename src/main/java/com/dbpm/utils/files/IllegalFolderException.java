/*
*
* author:  gvenzl
* created: 25 Mar 2016
*
* name: IllegalFolderException.java
*
*/

package com.dbpm.utils.files;

public class IllegalFolderException extends Exception {

	private static final long serialVersionUID = 1264827144898641171L;

	public IllegalFolderException(String message) {
		super(message);
	}

	public IllegalFolderException(Throwable cause) {
		super(cause);
	}

	public IllegalFolderException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalFolderException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
