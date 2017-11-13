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
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dbpm.repository.Dependency;
import com.dbpm.repository.Package;

public class ManifestReader {

	private final Package 				dbpmPackage;
	private final ArrayList<Dependency>	dependencies;

    /**
     * Creates a new ManifestReader instance.
     * @param mf The manifest content.
     */
	public ManifestReader(String mf) {
		JSONObject manifest = new JSONObject(mf);
		dbpmPackage = new Package();
		dbpmPackage.setName(manifest.getString("name"));
		dbpmPackage.setDescrption(manifest.getString("description"));
		dbpmPackage.setMajor(manifest.getJSONObject("version").getInt("major"));
		dbpmPackage.setMinor(manifest.getJSONObject("version").getInt("minor"));
		dbpmPackage.setPatch(manifest.getJSONObject("version").getInt("patch"));
		dbpmPackage.setPlatform(manifest.getString("platform"));
		dependencies = new ArrayList<>();

		try {
			JSONArray packageDependencies = manifest.getJSONArray("dependencies");
		
			for (Object packageDept : packageDependencies) {
				Dependency dept = new Dependency();
				JSONObject jsonDept = (JSONObject) packageDept;
				dept.setName(jsonDept.getString("name"));
			
				JSONObject minDept = jsonDept.getJSONObject("min");
			
				int minor = 0;
				int patch = 0;
			
				try {
					minor = minDept.getInt("minor");
					patch = minDept.getInt("patch");
				} catch(JSONException e) {
					// Ignore as minor and patch are allowed to be null
				}
				// Major is mandatory!
				dept.setMin(minDept.getInt("major"), minor, patch);
			
				try {
					JSONObject maxDept = jsonDept.getJSONObject("max");
					minor = patch = Integer.MAX_VALUE;
				
					try {
						minor = maxDept.getInt("minor");
						patch = maxDept.getInt("patch");
					} catch (JSONException e) {
						// Ignore as minor and patch can be optional
					}
					// Major is mandatory!
					dept.setMax(maxDept.getInt("major"), minor, patch);
				} catch (JSONException e) {
					// Ignore as no max value is allowed
				}
			
				dependencies.add(dept);
			}
		} catch (JSONException e) {
			// Ignore as a package may not have dependencies
		}
		
	}
	
	public Package getPackage() {
		return dbpmPackage;
	}
	
	public ArrayList<Dependency> getDependencies() {
		return dependencies;
	}
}
