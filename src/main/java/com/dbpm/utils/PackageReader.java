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
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import com.dbpm.logger.Logger;
import com.dbpm.repository.Package;

/**
 * Reads a package file and extracts content
 * @author gvenzl
 *
 */
public class PackageReader {

	private static int BYTES = 8192;
	private File packageFile;
	private ManifestReader manifest;
	
	/**
	 * Creates a new package reader.
	 * @param pkg The package file
	 * @throws ZipException If the zip archive cannot be opened
	 * @throws IOException If the package cannot be opened or read
	 */
	public PackageReader(File pkg) throws ZipException, IOException {
		packageFile = pkg;
		try (ZipFile pkgZip = new ZipFile(packageFile);
			 Scanner s = new Scanner(pkgZip.getInputStream(pkgZip.getEntry("manifest.pm")))) {
				// Extract package information
				s.useDelimiter("\\A");
				manifest = new ManifestReader(s.next());
				s.close();
				pkgZip.close();
		}
	}

	/**
	 * Returns a manifest wrapped inside a ManifestReader instance.
	 * @return A manifest wrapped inside a ManifestReader instance
	 */
	public ManifestReader getManifest() {
			return manifest;
	}
	
	/**
	 * Returns package information of the a package.
	 * @return A Package instance containing the package information of a package
	 */
	public Package getPackage() {
		return getManifest().getPackage();
	}
	
	/**
	 * Returns a list of files within a zip file.
	 * @param subDir An optional directory name within the zip file
	 * @return A list of files form the zip file
	 */
	private HashMap<String, String> getFiles(String subDir) {
		HashMap<String, String> entries = new HashMap<String, String>();
		
		try(ZipFile pkgZip = new ZipFile(packageFile)) {
			Enumeration <? extends ZipEntry> e = pkgZip.entries();
			while (e.hasMoreElements()) {
				ZipEntry entry = e.nextElement();
				// If the sub directory is null or a match has been found, add the match to map
				if (null == subDir || entry.getName().startsWith(subDir)) {
					String content = readContent(pkgZip.getInputStream(entry));
					if (!content.isEmpty()) {
						entries.put(entry.getName(), content);
					}
				}
			}
			pkgZip.close();
		} catch (IOException e) {
			Logger.error("Cannot read package file");
			Logger.error(e.getMessage());
		}
		
		return entries;
	}
	
	private String readContent(InputStream stream) throws IOException {
		byte buffer[] = new byte[BYTES];
		String content = "";
		
		while (stream.read(buffer, 0, BYTES) != -1) {
			content += new String(buffer);
		}
		
		stream.close();
		// Add a line feed if it is missing
		if (!content.isEmpty() && content.charAt(content.length()-1) != '\n')
		content += '\n';
		return content;
	}
	
	public HashMap<String, String> getPreInstallFiles() {
		//TODO: extract preinstall
		return new HashMap<String, String>();
	}
	
	public HashMap<String, String> getUpgradeFiles() {
		//TODO: Extract upgrade
		return new HashMap<String, String>();
	}
	
	public HashMap<String, String> getInstallFiles() {
		return getFiles("INSTALL");
	}
	
	public HashMap<String, String> getRollbackFiles() {
		//TODO: Get rollback files
		return new HashMap<String, String>();
	}
	
	public HashMap<String, String> getDowngradeFiles() {
		//TODO: Extract downgrade files
		return new HashMap<String, String>();
	}
	
	public HashMap<String, String> getPostInstallFiles() {
		//TODO: Extract Post install files
		return new HashMap<String, String>();
	}
	
}
