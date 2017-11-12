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
import java.util.Arrays;
import java.util.stream.Collectors;

public final class Logger {

	/**
	 * Issue a message to the command line.
	 * @param message The message to be printed to the command line..
	 */
	public static void log(String... message) {
		String output = Arrays.stream(message).collect(Collectors.joining());
		System.out.println(output);
	}

	/**
	 * Issue an error message to the command line.
	 * @param message The message to be printed to the command line.
	 */
	public static void error(String... message) {
		String output = Arrays.stream(message).collect(Collectors.joining());
		System.out.println("ERROR: " + output);
	}

	/**
	 * Issue a verbose message to the command line.
	 * @param message The messages to be printed to the command line.
	 */
	public static void verbose(String... message) {
		
		if (Verbose.getInstance().isVerbose()) {
			String output = Arrays.stream(message).collect(Collectors.joining());
			System.out.println(output);
		}
	}

	/**
	 * Issues a prompt to the user
	 * @param defaultResponse The default input that should be returned in case the user just hits enter.
	 *                     Pass in null to force the user to provide an input.
	 * @param message The message that should be prompted to the user
	 * @return The response of the user.
	 */
	public static String prompt(String defaultResponse, String... message) {
		Console console = System.console();
		if (null == console) {
			return defaultResponse;
		}

		String output = Arrays.stream(message).collect(Collectors.joining());
		while (true) {
			System.out.println(output + "([y(es)]|[n(o)]|[r(etry)]):");
			// Only consider first character and make it lowercase.
			String response = console.readLine().substring(0, 1).toLowerCase();
			// No character passed (just hit enter) and default response is not null -> Default Input
			if (null == response && null != defaultResponse) { return defaultResponse; }
			// Response accepted, return response
			if ("ynr".contains(response)) { return response; }
		}
	}
}
