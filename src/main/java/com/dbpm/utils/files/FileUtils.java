/*
*
* author:  gvenzl
* created: 7 Apr 2016
*
* name: FileUtils.java
*
*/

package com.dbpm.utils.files;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class FileUtils {

	/**
	 * Compares the bytes of two files.
	 * @param file File to compare
	 * @param content The content to compare
	 * @return True if the files are the same, otherwise false
	 * @throws IOException Any exception while reading the file
	 */
	public static boolean compareFileContentToBytes(File file, byte[] content) throws IOException {
		
		if (!file.exists()) {
			return false;
		}
		if (file.length() != content.length) {
			// Length is different, so file has to be
			return false;
		}
		
		byte[] fileContent = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
		
		return Arrays.equals(fileContent, content);
	}

    /**
     * Returns the extension of a file name.
     * @param fileName The file name to retrieve the extension from
     * @return The extension
     */
	public static String getExtension (String fileName) {
	    return fileName.substring(fileName.lastIndexOf(".")+1);
    }

    /**
     * Returns the extension of a file.
     * @param file The file to retrieve the extension from
     * @return The extension
     */
    public static String getExtension (File file) {
	    return getExtension(file.getAbsolutePath());
    }
}
