/*
*
* author:  gvenzl
* created: 6 Nov 2017
*
* name: LoggerTest.java
*
*/

package com.dbpm.utils.logger;

import org.junit.Test;

public class LoggerTest {

	@Test
	public void test_log() {
		Logger.log("Test message");
	}

	@Test
	public void test_log_multiple_params() { Logger.log("This ", "is ", "a ", "Test ", "Message"); }
	
	@Test
	public void test_error() {
		Logger.error("Test error message");
	}

	@Test
	public void test_verbose() {
		Verbose.getInstance().setVerbose(true);
		Logger.verbose("Verbose message, printed!");
	}
	
	@Test
	public void test_verboseDontPrint() {
		Verbose.getInstance().setVerbose(false);
		Logger.verbose("Verbose message, not printed!");
	}
}
