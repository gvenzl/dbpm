/*
*
* author:  gvenzl
* created: 27 Mar 2016
*
* name: Repository.java
*
*/

package com.dbpm.repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public interface Repository {
	
	boolean checkRepo();
	
	/**
	 * Creates a new repository
	 * @return True if the repository creation was successful
	 */
	boolean createRepo();
	
	/**
	 * Writes a new entry into the repository
	 * @param db The Database name
	 * @param schema The schema name
	 * @param pkg The package 
	 * @return Returns true if the repository creation has been successful
	 */
	boolean writeEntry(String db, String schema, Package pkg);
	
	/**
	 * Saves a package into the repository
	 * @param pkg The package to be saved
	 * @param content The content of the physical package
	 * @return True if the package could be saved, otherwise false
	 */
	boolean savePackage(Package pkg, byte[] content);

    /**
     * Gets a package form the repository.
     * @param pkg The package to load from the repository
     * @return The package file
     * @throws FileNotFoundException If the package file can't be found
     */
	File getPackage(Package pkg) throws FileNotFoundException;
	
	/**
	 * Checks whether a package is already installed in a given environment
	 * @param db The Database name of where the package should be installed
	 * @param schema The schema name of where the package should be installed
	 * @param pkg The package which should be installed
	 * @return True if the package is already installed, otherwise false
	 */
	boolean isPackageInstalled(String db, String schema, Package pkg);

	/**
	 * Verifies whether given dependencies of the package are installed
	 * @param db The Database name of which o install the package into
	 * @param schema The schema name of which to install the package into
	 * @param dependencies The list of dependencies to verify
	 * @return True if and only if all dependencies could be verified, otherwise false
	 */
	boolean verifyDependencies(String db, String schema, ArrayList<Dependency> dependencies);
	
	/**
	 * Removes the configuration
	 * @return True if the removal was successful, otherwise false
	 */
	boolean remove();

}
