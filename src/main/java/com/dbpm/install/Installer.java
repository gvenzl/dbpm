/*
*
* author:  gvenzl
* created: 24 Mar 2016
*
* name: Installer.java
*
*/

package com.dbpm.install;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import com.dbpm.DBPM;
import com.dbpm.Module;
import com.dbpm.config.Config;
import com.dbpm.logger.Logger;
import com.dbpm.repository.Repository;
import com.dbpm.utils.PackageReader;
import com.dbpm.utils.Parameter;

/**
 * The Installer takes care of the installation of packages
 * @author gvenzl
 *
 */
public class Installer implements Module {
	
	private File	packageFile;
	private String	userName;
	private String	password;
	private String	adminUser;
	private String	adminPassword;
	private String	host;
	private String	port;
	private String	dbName;
	
	/**
	 * Constructs an Installer object.
	 * @param args An array with the parameters for the installation
	 * @throws IllegalArgumentException if an illegal argument is passed along
	 */
	public Installer (String[] args) throws IllegalArgumentException {
		// Ignore first parameter as this one was already checked by DBPM in order to call installer
		for (int i=1;i<args.length;i++) {
			if (args[i].equals(Parameter.USER)) {
				userName = args[++i];
			}
			else if (args[i].equals(Parameter.PASSWORD)) {
				password = args[++i];
			}
			else if (args[i].equals(Parameter.ADMINUSER)) {
				adminUser = args[++i];
			}
			else if (args[i].equals(Parameter.ADMINPASSWORD)) {
				adminPassword = args[++i];
			}
			else if (args[i].equals(Parameter.HOST)) {
				host = args[++i];
			}
			else if (args[i].equals(Parameter.PORT)) {
				port = args[++i];
			}
			else if (args[i].equals(Parameter.DBNAME)) {
				dbName = args[++i];
			}
			// Ignore verbose as already set globally by DBPM
			else if (args[i].equals(Parameter.VERBOSE)) { }
			else {
				// Unknown parameter
				if (args[i].charAt(0) == '-') {
					throw new IllegalArgumentException(args[i] + " is not a valid argument for install");
				}
				else {
					// TODO: If file does not exist, check repository for file
					packageFile = new File(args[i]);
					if (!packageFile.exists()) {
						packageFile = new File(args[i] + DBPM.PKG_FILE_EXTENSION);
						if (!packageFile.exists()) {
							throw new IllegalArgumentException(args[i] + " is not a valid file name!");
						}
					}
				}
			}
		}
	}

	@Override
	public void run() {
		Path dir;
		// if no file has been passed on, install all dbpkg files
		if (null == packageFile) {
			dir = FileSystems.getDefault().getPath(System.getProperty("user.dir"));
			try {
				DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*" + DBPM.PKG_FILE_EXTENSION);
				// Install each dbpm package in the current working directory
				// TODO: Build dependency tree instead and call install with right order
				for (Path path : stream) {
					installFile(new File(path.getParent() + File.separator + path.getFileName()));
				}
				stream.close();
			} catch (IOException e) {
				Logger.error("Cannot read package file!");
				Logger.error(e.getMessage());
			}
		}
		else {
			installFile(packageFile);
		}
		
	}
	
	/**
	 * Installs a package in the database.
	 * @param packageFile The package to be installed
	 * @return True if and only if the installation was successful, otherwise false
	 */
	private boolean installFile(File packageFile) {
		if (!packageFile.exists()) {
			Logger.error("Package file ", packageFile.getAbsolutePath(), " does not exist!");
			return false;
		}
		
		try {
			PackageReader pkgReader = new PackageReader(packageFile);
			Repository repo = Config.getRepository();

			// Save package to repository
			if (!repo.savePackage(pkgReader.getPackage(), Files.readAllBytes(Paths.get(packageFile.getAbsolutePath())))) {
				boolean retry = true;
				while (retry) {
					switch (Logger.prompt("y","Package couldn't be saved in repository, continue without saving?")) {
						case "n": { 
							System.out.println("User response \"n\", abort.");
							return false;
						}
						case "y": {
							System.out.println("User response \"y\", continuing...");
							retry = false;
							break;
						}
						case "r": {
							// Retry save, if it works continue
							System.out.println("Retrying...");
							if (repo.savePackage(pkgReader.getPackage(), Files.readAllBytes(Paths.get(packageFile.getAbsolutePath())))) {
								retry = false;
							}
							break;
						}
					}
				}
			}

			Logger.verbose("Check whether package is already installed.");
			if (repo.isPackageInstalled(dbName, userName, pkgReader.getPackage())) {
				Logger.log("Package is already installed.");
				return true;
			}
			
			Logger.verbose("Check whether dependencies are already installed.");
			if (!repo.verifyDependencies(dbName, userName, pkgReader.getManifest().getDependencies())) {
				return false;
			}

			HashMap<String, String> installFiles = pkgReader.getInstallFiles();
				
			// TODO: Install file
				
			Logger.verbose("Save package information in repository.");
			if (!repo.writeEntry(dbName, userName, pkgReader.getPackage())) {
				Logger.error("Could not save install information in repository!");
			}
			return true;
		} catch(Exception e) {
			Logger.error("Cannot install package!");
			Logger.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
}
