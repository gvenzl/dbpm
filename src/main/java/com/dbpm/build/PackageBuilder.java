/*
*
* author:  gvenzl
* created: 24 Mar 2016
*
* name: PackageBuilder.java
*
*/

package com.dbpm.build;

import com.dbpm.Module;
import com.dbpm.logger.Logger;
import com.dbpm.repository.Package;
import com.dbpm.utils.ManifestReader;
import com.dbpm.utils.PackageValidator;
import com.dbpm.utils.files.IllegalFileException;
import com.dbpm.utils.files.IllegalFolderException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * This class handles the creation of dbpm packages.
 * @author gvenzl
 */
public class PackageBuilder implements Module {

	private static final int BYTES = 8192;
	private static final String MANIFEST = "manifest.dpm";
	private final File workDir;
	private String fullPackageName;

	/**
	 * Creates a new PackageBuilder instance.
	 */
	public PackageBuilder() {
		workDir = new File (System.getProperty("user.dir"));
	}

	/**
	 * Creates a new PackageBuilder instance.
	 * @param workDir The working directory to use.
	 */
	PackageBuilder (String workDir) {
		this.workDir =  new File(workDir);
	}

	public String getWorkDir() {
		return workDir.getAbsolutePath();
	}

	String getPackageName() {
	    return fullPackageName;
    }

	@Override
	public void run() {
		Logger.log("Building package...");
		Logger.verbose("Current directory: " + workDir);
		
		File manifestFile = new File (workDir + "/" + MANIFEST);
		if (!manifestFile.exists()) {
			Logger.error("Manifest not found!");
		}
		else {
			Logger.verbose("Found manifest, reading...");
			try {
				String manifest = new  String(
											Files.readAllBytes(
												Paths.get(manifestFile.getAbsolutePath())));
				Package dbpmPackage = new ManifestReader(manifest).getPackage();
				fullPackageName = dbpmPackage.getFullName();
				Logger.verbose("Package name: " + dbpmPackage.getName());
				Logger.verbose("Building for platform: " + dbpmPackage.getPlatform());
				
				String dbpgFileName = dbpmPackage.getFullName();
				
				File dbpgFile = new File(dbpgFileName);
				if (dbpgFile.exists()) {
					Logger.verbose("Package file already exists, overwriting...");
					if (!dbpgFile.delete()) {
					    Logger.error("Can't override package!");
					    return;
                    }
				}
				createPackage(dbpgFileName);
			}
			catch (IOException e) {
				Logger.error("Can't read manifest file!");
				Logger.error(e.getMessage());
			}
		}

	}
	
	private void createPackage(String packageName) {

		byte[] buffer = new byte[BYTES];
		int len;
		try {
			// Get all files in working directory
			ArrayList<File> files = getDirectoryStructure(workDir.getAbsoluteFile());
			
			// Create zip file
			ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(packageName));
			zip.setMethod(ZipOutputStream.DEFLATED);
			zip.setLevel(9);
			
			for (File entry : files) {
	    		Logger.verbose("Adding ", entry.getName());
	    		// Remove absolute path entries within zip file.
	    		String fileName = entry.getAbsolutePath().replace(workDir.getAbsolutePath() + "/", "");
				zip.putNextEntry(new ZipEntry(fileName));
				FileInputStream in = new FileInputStream(entry);

				// Read content into zip file
				while ((len = in.read(buffer)) > 0) {
					zip.write(buffer, 0, len);
				}
				// Close zip entry
				in.close();
			}
			// Close zip file
			zip.close();
			
		} catch (Exception e) {
			Logger.error("Cannot create package archive!");
			Logger.error(e.getMessage());
		}
	}
	
	private ArrayList<File> getDirectoryStructure(File dir) throws IllegalFolderException, IllegalFileException {

        PackageValidator.validateDirectoryStructure(dir);

        ArrayList<File> filesCollection = new ArrayList<>();
		
		File[] files = dir.listFiles();

		// If the directory doesn't contain any files, return an empty list.
		if (null == files) {
		    return new ArrayList<>();
        }

		for (File file : files) {
			// Recursive tree build
			if (file.isDirectory()) {
				filesCollection.addAll(getDirectoryStructure(file));
			}
			else {
				filesCollection.add(file);
			}
		}
		return filesCollection;
	}
}
