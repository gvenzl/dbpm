/*
*
* author:  gvenzl
* created: 10 Apr 2016
*
* name: PackageReader.java
*
*/

package com.dbpm.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import com.dbpm.repository.Package;

/**
 * Reads a package file and extracts content
 * @author gvenzl
 *
 */
public class PackageReader {

	private File packageFile;
	private ManifestReader manifest;
	
	/**
	 * Creates a new package reader
	 * @param pkg The package file
	 * @throws ZipException if the zip archive cannot be opened
	 * @throws IOException if the package cannot be opened or read
	 */
	public PackageReader(File pkg) throws ZipException, IOException {
		packageFile = pkg;
		try (ZipFile pkgFile = new ZipFile(packageFile);
				 Scanner s = new Scanner(pkgFile.getInputStream(pkgFile.getEntry("manifest.pm")))) {
				// Extract package information
				s.useDelimiter("\\A");
				manifest = new ManifestReader(s.next());
		}
	}

	/**
	 * Returns a manifest wrapped inside a ManifestReader instance
	 * @return A manifest wrapped inside a ManifestReader instance
	 */
	public ManifestReader getManifest() {
			return manifest;
	}
	
	/**
	 * Returns package information of the a package
	 * @return A Package instance containing the package information of a package
	 */
	public Package getPackage() {
		return getManifest().getPackage();
	}
	
	public ArrayList<File> getPreInstall() {
		//TODO: extract preinstall
		return new ArrayList<File>();
	}
	
	public ArrayList<File> getUpgrade() {
		//TODO: Extract upgrade
		return new ArrayList<File>();
	}
	
	public ArrayList<File> getInstall() {
		//TODO: Get install files
		return new ArrayList<File>();
	}
	
	public ArrayList<File> getRollback() {
		//TODO: Get rollback files
		return new ArrayList<File>();
	}
	
	public ArrayList<File> getDowngrade() {
		//TODO: Extract downgrade files
		return new ArrayList<File>();
	}
	
	public ArrayList<File> getPostInstall() {
		//TODO: Extract Post install files
		return new ArrayList<File>();
	}
	
}
