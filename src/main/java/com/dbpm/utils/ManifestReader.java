// ***************************************************************************
//
// Author: gvenzl
// Created: 24/03/2016
//
// Name: ManifestReader.java
// Description: The manifest reader class for extracting information from the manifest.
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

package com.dbpm.utils;

import com.dbpm.repository.Dependency;
import com.dbpm.repository.Package;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
