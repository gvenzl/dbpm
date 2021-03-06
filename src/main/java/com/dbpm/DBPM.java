// ***************************************************************************
//
// Author: gvenzl
// Created: 24/03/2016
//
// Name: DBPM.java
// Description: The DBPM main class. Here is where all the fun starts but not where the magic happens. ;)
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

package com.dbpm;

import com.dbpm.build.PackageBuilder;
import com.dbpm.config.Configurator;
import com.dbpm.install.Installer;
import com.dbpm.uninstall.Uninstaller;
import com.dbpm.utils.ExitCode;
import com.dbpm.utils.Parameter;
import com.dbpm.utils.logger.Verbose;

public class DBPM {

	static class HelpPrinter implements Module {
		
		private static String msg;
		HelpPrinter(String message) {
			msg = message;
		}
		
		public int run() {
			// Print message is one is given
			if (null != msg) {
				System.out.println(msg);
				System.out.println();
			}
			System.out.println("dbpm - Database Package Manager");
			System.out.println("");
			System.out.println("Synopsis");
			System.out.println("");
			System.out.println("dbpm [command] [options] [package ...]");
			System.out.println("");
			System.out.println("Description");
			System.out.println("    dbpm builds, installs or uninstalls packages in your database.");
			System.out.println("");
			System.out.println("command is on of:");
			System.out.println("* build");
			System.out.println("* config");
			System.out.println("* install package1, [package2, ...]");
			System.out.println("* uninstall package1, [package2, ...]");
			System.out.println("");
			System.out.println("build");
			System.out.println("    The build process will build a dbpm package for distribution. dbpm has to be executed within the package root folder (see Package format).");
			System.out.println("");
			System.out.println("    -v               Verbose output");
			System.out.println("");
			System.out.println("config [file | db]");
			System.out.println("    The configuration will setup the initial repository needed for dbpm. By default, all packages and information will be stored on disk.");
			System.out.println("");
			System.out.println("    file             Will store the repository information within a file on disk (default)");
			System.out.println("");
			System.out.println("    db               Will store the repository information within a database");
			System.out.println("    -v               Verbose output");
			System.out.println("    -platform        The database platform (oracle, mysql, postgres)");
			System.out.println("    -user            Repository username (has to exist)");
			System.out.println("    -password        Repository user password");
			System.out.println("    -host            The host where the database is running on");
			System.out.println("    -port            The port of the database");
			System.out.println("    -dbName          The database name for the repository to be stored at");
			System.out.println("");
			System.out.println("install [options] package1, [package2, ...]");
			System.out.println("    The installation process will install a given package into the database.");
			System.out.println("");
			System.out.println("    -v               Verbose output    ");
			System.out.println("    -user            database username/schema where the package should be installed (has to exist)");
			System.out.println("    -password        database user password");
			System.out.println("    -adminUser       A privileged database user for executing the \".sys\" scripts within the package, if present. If there are no \".sys\" scripts within the package this user can be omitted.");
			System.out.println("    -adminPassword   The password for the privileged user");
			System.out.println("    -host            The host where the database is running on");
			System.out.println("    -port            The port of the database");
			System.out.println("    -dbName          The database name for the repository to be stored at");
			System.out.println("");
			System.out.println("uninstall [options] package1, [package2, ...]");
			System.out.println("    The uninstallation process will uninstall a given package from a schema.");
			System.out.println("");
			System.out.println("    -v               Verbose output");
			System.out.println("    -user            database username/schema where the package should be installed (has to exist)");
			System.out.println("    -password        database user password");
			System.out.println("    -adminUser       A privileged database user for executing the \".sys\" scripts within the package, if present. If there are no \".sys\" scripts within the package this user can be omitted.");
			System.out.println("    -adminPassword   The password for the privileged user");
			System.out.println("    -host            The host where the database is running on");
			System.out.println("    -port            The port of the database");
			System.out.println("    -dbName          The database name for the repository to be stored at");

			return ExitCode.EXIT_ERROR.getValue();
		}
	}

	public static int main(String[] args) {
		Module module = parseOptions(args);
		return module.run();
	}
	
	/**
	 * Parses the command line options and call the appropriate module.
	 * 
	 * @param args The list of arguments passed on
	 * @return A runtime module
	 */
	private static Module parseOptions(String[] args) {
		
		try {
			if (args.length == 0) {
				return new HelpPrinter("Nothing to do.");
			}
			
			// Check for verbose flag
			for (String arg : args) {
				if (arg.equals(Parameter.VERBOSE)) {
					Verbose.getInstance().setVerbose(true);
					break;
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
			return new HelpPrinter("ERROR: " + e.getMessage());
		}
	}
}
