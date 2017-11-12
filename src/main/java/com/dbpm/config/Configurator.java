/*
*
* author:  gvenzl
* created: 24 Mar 2016
*
* name: Configurator.java
*
*/

package com.dbpm.config;

import com.dbpm.Module;
import com.dbpm.db.DBTYPE;
import com.dbpm.logger.Logger;
import com.dbpm.utils.Parameter;

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
					for (DBTYPE type : DBTYPE.values()) {
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
	public void run() {
		Config config;
		
		try {
			if (remove) {
				new Config().removeConfiguration();
			}
			else {
				if (!dbStorage) {
					config = new Config();
				}
				else {
					config = new Config(platform, user, password, host, port, dbName);
				}
				config.createConfiguration();
			}
		}
		catch (Exception e) {
			Logger.error(e.getMessage());
		}
	}

}
