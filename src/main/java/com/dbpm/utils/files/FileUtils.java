// ***************************************************************************
//
// Author: gvenzl
// Created: 07/04/2016
//
// Name: FileUtils.java
// Description: Handy to have file utilities.
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
