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
import java.util.zip.ZipFile;

import com.dbpm.logger.Logger;
import com.dbpm.repository.Package;

/**
 * Reads a package file and extracts content
 * @author gvenzl
 *
 */
public class PackageReader {

	private static final int BYTES = 8192;
	private final File packageFile;
	private ManifestReader manifest;
	
	/**
	 * Creates a new package reader.
	 * @param pkg The package file
	 * @throws IOException If the package cannot be opened or read
	 */
	public PackageReader(File pkg) throws IOException {
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
		HashMap<String, String> entries = new HashMap<>();
		
		try(ZipFile pkgZip = new ZipFile(packageFile)) {
			Enumeration <? extends ZipEntry> e = pkgZip.entries();
			while (e.hasMoreElements()) {
				ZipEntry entry = e.nextElement();
				// If the sub directory is null or a match has been found, add the match to map
				if (null == subDir || entry.getName().startsWith(subDir)) {
					//TODO: Check whether this can be made with less memory footprint by just storing file handles rarther than content.
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

		StringBuilder sb = new StringBuilder();
		while (stream.read(buffer, 0, BYTES) != -1) {
			sb.append(new String(buffer));
		}
		stream.close();

        String content = sb.toString();
		// Add a line feed if it is missing
		if (!content.isEmpty() && content.charAt(content.length()-1) != '\n') {
            content += '\n';
        }
		return content;
	}
	
	public HashMap<String, String> getPreInstallFiles() {
		return getFiles("PREINSTALL");
	}
	
	public HashMap<String, String> getUpgradeFiles() {
		return getFiles("UPGRADE");
	}
	
	public HashMap<String, String> getInstallFiles() {
		return getFiles("INSTALL");
	}
	
	public HashMap<String, String> getRollbackFiles() {
		return getFiles("ROLLBACK");
	}
	
	public HashMap<String, String> getDowngradeFiles() {
		return getFiles("DOWNGRADE");
	}
	
	public HashMap<String, String> getPostInstallFiles() {
		return getFiles("POSTINSTALL");
	}
	
}
