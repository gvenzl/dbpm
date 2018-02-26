// ***************************************************************************
//
// Author: gvenzl
// Created: 24/03/2016
//
// Name: Logger.java
// Description: The Logger singleton.
//
// Copyright 2016 - Gerald Venzl
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
// ***************************************************************************

package com.dbpm.utils.logger;

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
