/*
*
* author:  gvenzl
* created: 24 Mar 2016
*
* name: ManifestReader.java
*
*/

package com.dbpm.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONObject;

public class ManifestReader {
	
	private JSONObject manifest;
	
	public ManifestReader(File manifestFile) throws IOException {
		manifest = new JSONObject(new String(
											Files.readAllBytes(
													Paths.get(manifestFile.getAbsolutePath()))));
	}
	
	public String getPackageName() {
		return manifest.getString("name");
	}
	
	public String getDescription() {
		return manifest.getString("description");
	}
	
	public String getPlatform() {
		return manifest.getString("platform");
	}
	
	public int getMajor() {
		return manifest.getJSONObject("version").getInt("major");
	}
	
	public int getMinor() {
		return manifest.getJSONObject("version").getInt("minor");
	}
	
	public int getPatch() {
		return manifest.getJSONObject("version").getInt("patch");
	}

}
