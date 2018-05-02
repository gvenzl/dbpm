// ***************************************************************************
//
// Author: gvenzl
// Created: 10/04/2016
//
// Name: PackageReader.java
// Description: The package reader class is responsible for reading the contents of a DBPM package.
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

package com.dbpm.utils;

import com.dbpm.repository.Package;
import com.dbpm.utils.files.FileType;
import com.dbpm.utils.files.Phase;
import com.dbpm.utils.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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
	 * @throws IllegalPackageException If the package is illegal
	 */
	public PackageReader(File pkg) throws IOException, IllegalPackageException {
        if (!PackageValidator.validatePackage(pkg)) {
            throw new IllegalPackageException("The package is not a valid DBPM package!");
        }

		packageFile = pkg;
		try (ZipFile pkgZip = new ZipFile(packageFile);
			 Scanner s = new Scanner(pkgZip.getInputStream(pkgZip.getEntry(FileType.MANIFEST.getValue())))) {
				// Extract package information
				s.useDelimiter("\\A");
				manifest = new ManifestReader(s.next());
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
	 * @param phase An optional directory name within the zip file
	 * @return A list of files form the zip file
	 */
	private HashMap<String, String> getFiles(Phase phase) {
		HashMap<String, String> entries = new HashMap<>();
		
		try(ZipFile pkgZip = new ZipFile(packageFile)) {
			Enumeration <? extends ZipEntry> e = pkgZip.entries();
			while (e.hasMoreElements()) {
				ZipEntry entry = e.nextElement();
				// If a match has been found, add the match to map
				if ( entry.getName().startsWith(phase.toString())) {
					//TODO: Check whether this can be made with less memory footprint by just storing file handles rather than content.
					String content = readContent(pkgZip.getInputStream(entry));
					if (!content.isEmpty()) {
						entries.put(entry.getName(), content);
					}
				}
			}
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

    /**
     * Retrieves all files for the PREINSTALL {@link Phase}.
     * @return A collection containing all pre-installation files
     */
	public HashMap<String, String> getPreInstallFiles() {
		return getFiles(Phase.PREINSTALL);
	}

    /**
     * Retrieves all files for the UPGRADE {@link Phase}.
     * @return A collection containing all upgrade files
     */
	public HashMap<String, String> getUpgradeFiles() {
		return getFiles(Phase.UPGRADE);
	}

    /**
     * Retrieves all files for the INSTALL {@link Phase}.
     * @return A collection containing all installation files
     */
	public HashMap<String, String> getInstallFiles() {
		return getFiles(Phase.INSTALL);
	}

    /**
     * Retrieves all files for the ROLLBACK {@link Phase}.
     * @return A collection containing all rollback files
     */
	public HashMap<String, String> getRollbackFiles() {
		return getFiles(Phase.ROLLBACK);
	}

    /**
     * Retrieves all files for the DOWNGRADE {@link Phase}.
     * @return A collection containing all downgrade files
     */
	public HashMap<String, String> getDowngradeFiles() {
		return getFiles(Phase.DOWNGRADE);
	}

    /**
     * Retrieves all files for the POSTINSTALL {@link Phase}.
     * @return A collection containing all post-install files
     */
	public HashMap<String, String> getPostInstallFiles() {
		return getFiles(Phase.POSTINSTALL);
	}
}
