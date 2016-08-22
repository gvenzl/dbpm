/*
*
* author:  gvenzl
* created: 27 Mar 2016
*
* name: Repository.java
*
*/

package com.dbpm.repository;

import java.util.ArrayList;
import com.dbpm.repository.Package;

public interface Repository {
	
	public boolean checkRepo();
	
	/**
	 * Creates a new repository
	 * @return True if the repository creation was successful
	 */
	public boolean createRepo();
	
	/**
	 * Writes a new entry into the repository
	 * @param db The Database name
	 * @param schema The schema name
	 * @param pkg The package 
	 * @return
	 */
	public boolean writeEntry (String db, String schema, Package pkg);
	
	/**
	 * Saves a package into the repository
	 * @param pkg The package to be saved
	 * @param content The content of the physical package
	 * @return True if the package could be saved, otherwise false
	 */
	public boolean savePackage(Package pkg, byte[] content);
	
	/**
	 * Checks whether a package is already installed in a given environment
	 * @param db The Database name of where the package should be installed
	 * @param schema The schema name of where the package should be installed
	 * @param pkg The package which should be installed
	 * @return True if the package is already installed, otherwise false
	 */
	public boolean isPackageInstalled(String db, String schema, Package pkg);

	/**
	 * Verifies whether given dependencies of the package are installed
	 * @param db The Database name of which o install the package into
	 * @param schema The schema name of which to install the package into
	 * @param dependencies The list of dependencies to verify
	 * @return True if and only if all dependencies could be verified, otherwise false
	 */
	public boolean verifyDependencies(String db, String schema, ArrayList<Dependency> dependencies);
	
	/**
	 * Removes the configuration
	 * @return True if the removal was successful, otherwise false
	 */
	public boolean remove();
}
