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
		Console console = System.console();
		if (null == console) {
			return defaultInput;
		}
		
		while (true) {
			System.out.println(message + "([y(es)]|[n(o)]|[r(etry)]):");
			// Only consider first character and make it lowercase.
			String response = console.readLine().substring(0, 1).toLowerCase();
			// No character passed (just hit enter) -> Default Input
			if (null == response) { return defaultInput; }
			// Response accepted, return response
			if ("ynr".indexOf(response) != -1) { return response; }
		}
	}
}
