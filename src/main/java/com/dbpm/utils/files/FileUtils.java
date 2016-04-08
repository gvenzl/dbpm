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
	 * Compares the bytes of two files
	 * @param file File to compare
	 * @param content The content to compare
	 * @return True if the files are the same, otherwise false
	 * @throws IOException
	 */
	public static boolean compareFileContentToBytess(File file, byte[] content) throws IOException {
		
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

}
