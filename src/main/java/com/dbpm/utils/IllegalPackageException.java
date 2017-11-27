/*
*
* author:  gvenzl
* created: 26 Nov 2017
*
* name: IllegalPackageException.java
*
*/

package com.dbpm.utils;

class IllegalPackageException extends Exception {

    IllegalPackageException() {
        super();
    }

    IllegalPackageException(String message) {
        super(message);
    }

    IllegalPackageException(Throwable cause) {
        super(cause);
    }

    IllegalPackageException(String message, Throwable cause) {
        super(message, cause);
    }

    IllegalPackageException(String message, Throwable cause, boolean enableSuppression,
                                boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
