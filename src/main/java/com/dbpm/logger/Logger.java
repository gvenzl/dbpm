/*
*
* author:  gvenzl
* created: 24 Mar 2016
*
* name: Logger.java
*
*/

package com.dbpm.logger;

import java.io.Console;

public final class Logger {
	
	public static void log(String message) {
		System.out.println(message);
	}
	
	public static void error(String message) {
		System.out.println("ERROR: " + message);
	}

	public static void verbose(String message) {
		
		if (Verbose.getInstance().isVerbose()) {
			System.out.println(message);
		}
	}
	
	public static String prompt(String message, String defaultInput) {
		System.out.println(message + "[" + defaultInput + "]:");
		Console console = System.console();
		if (null == console) {
			return defaultInput;
		}
		return console.readLine();
	}
}
