/*
*
* author:  gvenzl
* created: 27 Mar 2016
*
* name: Package.java
*
*/

package com.dbpm.repository;

public class Package {

	private String  name;
	private int		major;
	private int		minor;
	private int		patch;
	private String  platform;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
