/*
*
* author:  gvenzl
* created: 24 Mar 2016
*
* name: PackageBuilder.java
*
*/

package com.dbpm.build;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.dbpm.Module;
import com.dbpm.logger.Logger;
import com.dbpm.utils.ManifestReader;
import com.dbpm.utils.filesystem.AllowedFiles;
import com.dbpm.utils.filesystem.AllowedFolders;
import com.dbpm.utils.filesystem.IllegalFileException;
import com.dbpm.utils.filesystem.IllegalFolderException;

/**
 * This class handles the creation of dbpm packages.
 * @author gvenzl
 */
public class PackageBuilder implements Module {
	
	private File workDir;
	private ManifestReader manifest;
	
	public PackageBuilder() {
		workDir = new File (System.getProperty("user.dir"));
	}

	@Override
	public void run() {
		Logger.log("Building package...");
		Logger.verbose("Current directory: " + workDir);
		
		File manifestFile = new File ("manifest.pm");
		if (!manifestFile.exists()) {
			Logger.error("Manifest not found!");
		}
		else {
			Logger.verbose("Found manifest, reading...");
			try {
				manifest = new ManifestReader(manifestFile);
				Logger.verbose("Package name: " + manifest.getPackageName());
				Logger.verbose("Building for platform: " + manifest.getPlatform());
				
				String dbpgFileName = String.format("%s-%d.%d.%d-%s.dbpkg",
						manifest.getPackageName(),
							manifest.getMajor(), manifest.getMinor(), manifest.getPatch(),
								manifest.getPlatform());
				
				File dbpgFile = new File(dbpgFileName);
				if (dbpgFile.exists()) {
					Logger.verbose("Package file already exists, overwriting");
					dbpgFile.delete();
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
		
		byte[] buffer = new byte[1024];
		try {
			// Get all files in working directory
			ArrayList<File> files = getDirectoryStructure(workDir.getAbsoluteFile());
			
			// Create zip file
			ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(packageName));
			zip.setMethod(ZipOutputStream.DEFLATED);
			zip.setLevel(9);
			
			for (File entry : files) {
	    		Logger.verbose("Adding " + entry.getName());
	    		String fileName = entry.getAbsolutePath().replace(workDir.getAbsolutePath() + "/", "");
				zip.putNextEntry(new ZipEntry(fileName));
				FileInputStream in = new FileInputStream(entry);

				// Read content into zip file
				int len;
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
		
		ArrayList<File> filesCollection = new ArrayList<File>();
		
		File[] files = dir.listFiles();	
		for (int i=0; i<files.length; i++) {
			// Recursive tree build
			if (files[i].isDirectory()) {
				// Throw exception if folder is not allowed!
				if (!isAllowedDirectory(files[i])) {
					throw new IllegalFolderException("Folder " + files[i].getName() + " is invalid!");
				}
				filesCollection.addAll(getDirectoryStructure(files[i]));
			}
			else {
				if (!isAllowedFile(files[i])) {
					throw new IllegalFileException("File " + files[i].getName() + " has not a valid extension!");
				}
				filesCollection.add(files[i]);
			}
		}
		return filesCollection;
	}
	
	private boolean isAllowedFile(File file) {
		
		for (AllowedFiles fileExt : AllowedFiles.values()) {
			if (file.getName().toUpperCase().endsWith(fileExt.name().toUpperCase())) {
				return true;
			}
		}
		return false;
	}

	private boolean isAllowedDirectory(File file) {

		for (AllowedFolders folderName : AllowedFolders.values()) {
			if (folderName.name().equals(file.getName())) {
				return true;
			}
		}
		return false;
	}
}
