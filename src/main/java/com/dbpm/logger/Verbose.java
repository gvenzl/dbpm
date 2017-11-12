/*
*
* author:  gvenzl
* created: 24 Mar 2016
*
* name: Verbose.java
*
*/

package com.dbpm.logger;

public class Verbose {
	
	private static Verbose instance = null;
	private boolean verbose = false;
	
	private Verbose() {
	// Exists only to defeat instantiation.
	}
	public static Verbose getInstance() {
		if(instance == null) {
			instance = new Verbose();
		}
		return instance;
	}
	
	public void setVerbose(boolean v) {
		verbose = v;
	}
	
	public boolean isVerbose() {
		return verbose;
	}
}
