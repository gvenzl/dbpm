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
import com.dbpm.utils.ExitCode;
import com.dbpm.utils.ManifestReader;
import com.dbpm.utils.PackageValidator;
import com.dbpm.utils.files.FileType;
import com.dbpm.utils.files.IllegalFileException;
import com.dbpm.utils.files.IllegalFolderException;
import org.json.JSONException;

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

	/**
	 * Returns the absolute path of the working directory.
	 * @return The absolute path of the working directory.
	 */
	public String getWorkDir() {
		return workDir.getAbsolutePath();
	}

    /**
     * Returns the package name.
     * @return The package name.
     */
	String getPackageName() {
	    return fullPackageName;
    }

    /**
     * Kicks off the main building process.
     */
	@Override
	public int run() {
		Logger.log("Building package...");
		Logger.verbose("Current directory: " + workDir);
		
		File manifestFile = new File (workDir + "/" + FileType.MANIFEST.getValue());
		if (!manifestFile.exists()) {
			Logger.error("Manifest not found!");
			return ExitCode.EXIT_BUILD_MANIFEST_NOT_FOUND.getValue();
		}

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
                    return ExitCode.EXIT_BUILD_PACKAGE_EXISTS_CANT_OVERRIDE.getValue();
                }
            }
            return createPackage(dbpgFileName);
        }
        catch (IOException e) {
            Logger.error("Can't read manifest file!");
            Logger.error(e.getMessage());
            return ExitCode.EXIT_BUILD_MANIFEST_NOT_READABLE.getValue();
        }
        catch(JSONException je) {
            Logger.error("Manifest file not valid!");
            Logger.error(je.getMessage());
            return ExitCode.EXIT_BUILD_MANIFEST_NOT_VALID.getValue();
        }
	}

    /**
     * Creates a DBPM package
     * @param packageFileName The name of the package file
     * @return An exit code, see {@link ExitCode}
     */
	private int createPackage(String packageFileName) {

		byte[] buffer = new byte[BYTES];
		int len;
		try {
			// Get all files in working directory
			ArrayList<File> files = getDirectoryStructure(workDir.getAbsoluteFile());
			
			// Create zip file
			ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(packageFileName));
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
			return ExitCode.EXIT_SUCCESSFUL.getValue();
		}
		catch (Exception e) {
			Logger.error("Cannot create package archive!");
			Logger.error(e.getMessage());
			if (e instanceof IllegalFolderException) {
                return ExitCode.EXIT_BUILD_ILLEGAL_FOLDER_FOUND.getValue();
            }
            else if (e instanceof IllegalFileException) {
			    return ExitCode.EXIT_BUILD_ILLEGAL_FILE_FOUND.getValue();
            }
            else {
			    return ExitCode.EXIT_ERROR.getValue();
            }
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
