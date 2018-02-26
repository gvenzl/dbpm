// ***************************************************************************
//
// Author: gvenzl
// Created: 27/03/2016
//
// Name: Package.java
// Description: The Package class contains information about the package.
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

package com.dbpm.repository;

import com.dbpm.utils.files.FileType;

public class Package {

	private String 	name;
	private String  description;
	private int		major;
	private int		minor;
	private int		patch;
	private String 	platform;
	
	public Package(String packageName) {
		String pkgName = packageName.substring(0, packageName.lastIndexOf("."));
		name = pkgName.substring(0, pkgName.indexOf("-"));
		String version = packageName.substring(pkgName.indexOf("-")+1, pkgName.lastIndexOf("-"));
		major = Integer.valueOf(version.substring(0, version.indexOf(".")));
		minor = Integer.valueOf(version.substring(version.indexOf(".")+1, version.lastIndexOf(".")));
		patch = Integer.valueOf(version.substring(version.lastIndexOf(".")+1));
		platform = pkgName.substring(pkgName.lastIndexOf("-")+1);
	}
	
	public Package() {

	}

	public String getFullName() {
		return new StringBuilder(name).append("-")
                    .append(major).append(".")
                    .append(minor).append(".")
                    .append(patch).append("-")
                    .append(platform).append(".")
                    .append(FileType.DBPKG.getValue())
                    .toString();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescrption(String desc) {
		description = desc;
	}
	public int getMajor() {
		return major;
	}
	public void setMajor(int major) {
		this.major = major;
	}
	public int getMinor() {
		return minor;
	}
	public void setMinor(int minor) {
		this.minor = minor;
	}
	public int getPatch() {
		return patch;
	}
	public void setPatch(int patch) {
		this.patch = patch;
	}
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}
}
