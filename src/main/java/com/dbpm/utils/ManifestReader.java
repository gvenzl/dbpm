/*
*
* author:  gvenzl
* created: 24 Mar 2016
*
* name: ManifestReader.java
*
*/

package com.dbpm.utils;

import java.io.IOException;

import org.json.JSONObject;

import com.dbpm.repository.Package;

public class ManifestReader {
	
	private JSONObject manifest;
	private Package dbpmPackage;
	
	public ManifestReader(String mf) throws IOException {
		manifest = new JSONObject(mf);
		dbpmPackage = new Package();
		dbpmPackage.setName(manifest.getString("name"));
		dbpmPackage.setDescrption(manifest.getString("description"));
		dbpmPackage.setMajor(manifest.getJSONObject("version").getInt("major"));
		dbpmPackage.setMinor(manifest.getJSONObject("version").getInt("minor"));
		dbpmPackage.setPatch(manifest.getJSONObject("version").getInt("patch"));
		dbpmPackage.setPlatform(manifest.getString("platform"));

		// TODO: Build dependencies array
	}
	
	public Package getPackage() {
		return dbpmPackage;
	}
}
