/*
*
* author:  gvenzl
* created: 27 Mar 2016
*
* name: Package.java
*
*/

package com.dbpm.repository;

import com.dbpm.DBPM;

public class Package {

	private String 	name;
	private String 	desciption;
	private int		major;
	private int		minor;
	private int		patch;
	private String 	platform;
	
	public Package(String packageName) {
		String pkgName = packageName.substring(0, packageName.lastIndexOf("."));
		name = pkgName.substring(0, pkgName.indexOf("-"));
		String version = packageName.substring(pkgName.indexOf("-")+1, pkgName.lastIndexOf("-"));
		major = Integer.valueOf(version.substring(0, version.indexOf("."))).intValue();
		minor = Integer.valueOf(version.substring(version.indexOf(".")+1, version.lastIndexOf("."))).intValue();
		patch = Integer.valueOf(version.substring(version.lastIndexOf(".")+1)).intValue();
		platform = pkgName.substring(pkgName.lastIndexOf("-")+1);
	}
	
	public Package() {

	}

	public String getFullName() {
		return name + "-" + major + "." + minor + "." + patch + "-" +  platform + DBPM.PKG_FILE_EXTENSION;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return desciption;
	}
	public void setDescrption(String desc) {
		desciption = desc;
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
