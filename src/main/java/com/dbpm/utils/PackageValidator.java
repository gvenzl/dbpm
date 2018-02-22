/*
*
* author:  gvenzl
* created: 26 Nov 2017
*
* name: PackageValidator.java
*
*/

package com.dbpm.utils;

import com.dbpm.utils.files.IllegalFileException;
import com.dbpm.utils.files.IllegalFolderException;
import com.dbpm.utils.files.PHASE;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * Provides various methods to validate package contents and structure.
 */
public class PackageValidator {

    /**
     * Validates a DBPM package of its valid structure.
     *
     * In certain cases it may be desired to first validate a package of its authenticity before trying to install it.
     * This routine will open the package and check whether the content conforms to the format and structure.
     * @param dbpmPackage The package to validate, of type {@link File}
     * @throws IOException Any exception occurring while trying to read the file
     * @return True if the package is of valid format, otherwise false
     */
    static boolean validatePackage(File dbpmPackage) throws IOException {

        try (ZipFile pkgFile = new ZipFile(dbpmPackage)) {

            Enumeration<? extends ZipEntry> entries = pkgFile.entries();

            // Empty zip file
            if (!entries.hasMoreElements()) {
                return false;
            }

            String parentDir = "";
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();

                // Entry is directory
                if (entry.isDirectory()) {
                    // Directory is allowed, remember parent directory
                    if (isAllowedDirectory(entry.getName())) {
                        parentDir = entry.getName();
                    }
                    // Directory is illegal, return false
                    else {
                        return false;
                    }

                }
                // File is not allowed, return false
                else if (!isAllowedFile(entry.getName(), parentDir)) {
                    return false;
                }
            }

            return true;
        }
        catch (ZipException ze) {
            // Not a zip file, return false.
            return false;
        }
    }

    /**
     * Checks whether a given file is of allowed type.
     * @param fileName The file name to check
     * @param parentDir The parent directory of the file
     * @return True if the file type is allowed, otherwise false
     */
    private static boolean isAllowedFile(String fileName, String parentDir) {

        String pattern = "";

        // Preinstall and Postinstall allow NNN.*.cmd, NNN.*.sql, and NNN.*.sys files.
        if (parentDir.equals(PHASE.PREINSTALL.name()) || parentDir.equals(PHASE.POSTINSTALL.name())) {
            pattern = "(\\d\\d\\d\\.)(.+)(\\.)(cmd|sql|sys)";
        }
        // Upgrade, Install, Rollback, and Downgrade only accept NNN.*.sql files
        else if (parentDir.equals(PHASE.UPGRADE.name()) || parentDir.equals(PHASE.INSTALL.name()) ||
                 parentDir.equals(PHASE.ROLLBACK.name()) || parentDir.equals(PHASE.DOWNGRADE.name())) {
            pattern = "(\\d\\d\\d\\.)(.+)(\\.)(sql)";
        }
        // Only manifest.dpm is allowed in the root directory
        else {
            pattern = "manifest.dpm";
        }


        return fileName.matches(pattern);
    }

    /**
     * Checks whether a given directory name is allowed.
     * @param directoryName The directory name to check, case sensitive
     * @return True if the directory name is allowed, otherwise false
     */
    private static boolean isAllowedDirectory(String directoryName) {

        for (PHASE folderName : PHASE.values()) {
            if (folderName.name().equals(directoryName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Validates a given directory structure whether or not it conforms to the DBPM format.
     * @param dir The directory to check
     * @throws IllegalFileException When an illegal file has been found
     * @throws IllegalFolderException When an illegal folder has been found
     * @return True if the Directory structure is valid
     */
    public static boolean validateDirectoryStructure(File dir) throws IllegalFileException, IllegalFolderException {

        File[] files = dir.listFiles();

        // If directory is empty or not a directory, return false
        if (null == files) {
            return false;
        }

        for (File file : files) {
            // Recursive tree build
            if (file.isDirectory()) {
                // Throw exception if folder is not allowed!
                if (!isAllowedDirectory(file.getName())) {
                    throw new IllegalFolderException("Folder " + file.getName() + " is not valid!");
                }
                validateDirectoryStructure(file);
            } else {
                //TODO: Filter out root directory
                if (!isAllowedFile(file.getName(), dir.getName())) {
                    throw new IllegalFileException("File " + file.getName() + " is not a valid file!");
                }
            }
        }
        return true;
    }
}