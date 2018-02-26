// ***************************************************************************
//
// Author: gvenzl
// Created: 24/03/2016
//
// Name: Configurator.java
// Description: The Configurator module driver.
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

package com.dbpm.config;

import com.dbpm.Module;
import com.dbpm.db.DbType;
import com.dbpm.utils.ExitCode;
import com.dbpm.utils.Parameter;
import com.dbpm.utils.logger.Logger;

public class Configurator implements Module {

	private boolean remove = false;
	private boolean dbStorage = false;
	private String user;
	private String password;
	private String host;
	private String port;
	private String dbName;
	private String platform;
	
	/**
	 * Instantiates a new Configurator object.
	 * @param args The arguments for the configuration.<br/>
	 * Those can be:<br/><br/>
	 * db - Store configuration inside the database<br/>
	 * -user - The username to store the configuration in.<br/>
	 * -passwor - The password for the user<br/>
	 * -host - The host where the database is located<br/>
	 * -port - The port on which the database is reachable<br/>
	 * -dbname - The database name<br/>
	 * -platform - The database platform<br/>
	 * <br/><br/>
	 * file - stores the configuration in a file on the filesystem.
	 * 
	 * @throws IllegalArgumentException If an illegal argument has been passed on.
	 */
	public Configurator (String[] args) throws IllegalArgumentException {
		
		if (args[1].equals(RepoStorage.DB.name().toLowerCase())) {
			dbStorage = true;
		
			for (int i=2;i<args.length;i++) {
				if (args[i].equals(RepoStorage.DB.name().toLowerCase())) { dbStorage = true; }
				else if (args[i].equals(Parameter.USER)) { user = args[++i]; }
				else if (args[i].equals(Parameter.PASSWORD)) { password = args[++i]; }
				else if (args[i].equals(Parameter.HOST)) { host = args[++i]; }
				else if (args[i].equals(Parameter.PORT)) { port = args[++i]; }
				else if (args[i].equals(Parameter.DBNAME)) { dbName = args[++i]; }
				else if (args[i].equals(Parameter.PLATFORM)) {
					platform = args[++i];
					
					boolean supported = false;
					for (DbType type : DbType.values()) {
						if (platform.equals(type.name().toLowerCase())) {
							supported = true;
							break;
						}
					}
					
					if (!supported) {
						throw new IllegalArgumentException("Unsupported platform.");
					}
				}
				// Ignore verbose as already set in DBPM.
				else if (args[i].equals(Parameter.VERBOSE)) { }
				else {
					throw new IllegalArgumentException("Unknown argument: " + args[i]);
				}
			}
		}
		else if (args[1].equals(RepoStorage.FILE.name().toLowerCase()) && args.length > 2) {
			for (int i=2;i<args.length;i++) {
				if (!args[i].equals(Parameter.VERBOSE)) {
					throw new IllegalArgumentException("Too many arguments.");
				}
			}
		}
		else if (args[1].equals("remove")) {
			remove = true;
		}
	}
	
	@Override
	public int run() {
		Config config;
		
		try {
			if (remove) {
				if (!new Config().removeConfiguration()) {
				    return ExitCode.EXIT_CONFIG_CANT_REMOVE_CONFIG.getValue();
                }
			}
			else {
				if (!dbStorage) {
					config = new Config();
				}
				else {
					config = new Config(platform, user, password, host, port, dbName);
				}
				if (!config.createConfiguration()) {
				    return ExitCode.EXIT_CONFIG_CANT_CREATE_CONFIG.getValue();
                }
			}
		}
		catch (Exception e) {
			Logger.error(e.getMessage());
			return ExitCode.EXIT_ERROR.getValue();
		}
		return ExitCode.EXIT_SUCCESSFUL.getValue();
	}
}
