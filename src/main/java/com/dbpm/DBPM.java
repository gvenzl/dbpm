/*
*
* author:  gvenzl
* created: 24 Mar 2016
*
* name: Runtype.java
*
*/

package com.dbpm;

import com.dbpm.build.PackageBuilder;
import com.dbpm.config.Configurator;
import com.dbpm.install.Installer;
import com.dbpm.logger.Verbose;
import com.dbpm.uninstall.Uninstaller;

public class DBPM {

	static class HelpPrinter implements Module {
		
		private static String msg;
		public HelpPrinter(String message) {
			msg = message;
		}
		
		public void run() {
			// Print message is one is given
			if (null != msg) {
				System.out.println("ERROR: " + msg);
				System.out.println();
			}
			
			System.out.print("Help");
		}
	}

	public static void main(String[] args) {
		Module module = parseOptions(args);
		module.run();
	}
	
	/**
	 * Parses the command line options and call the appropriate module.
	 * 
	 * @param args The list of arguments passed on
	 * @return A runtime module
	 */
	static Module parseOptions(String[] args) {
		
		try {
			if (args.length == 0) {
				return new HelpPrinter("No arguments passed on!");
			}
			
			// Check for verbose flag
			for (int i=0; i<args.length;i++) {
				if ("-v".equals(args[i])) {
					Verbose.getInstance().setVerbose(true);
				}
			}
			
			switch (args[0]) {
				case "build": {
					return new PackageBuilder();
				}
				case "config": {
					return new Configurator(args);
				}
				case "install": {
					return new Installer(args);
				}
				case "uninstall": {
					return new Uninstaller(args);
				}
				default:
					return new HelpPrinter(args[0] + " is not a valid command!");
			}	
		}
		catch (IllegalArgumentException e) {
			return new HelpPrinter(e.getMessage());
		}
	}
}
