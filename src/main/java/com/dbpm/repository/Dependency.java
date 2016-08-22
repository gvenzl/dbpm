/*
*
* author:  gvenzl
* created: 7 Apr 2016
*
* name: Dependency.java
*
*/

package com.dbpm.repository;

public class Dependency {

	public class Version {

		private int major;
		private int minor;
		private int patch;
		
		public Version (int maj, int min, int ptch) {
			major = maj;
			minor = min;
			patch = ptch;
		}
		
		public int getMajor() {
			return major;
		}
		public int getMinor() {
			return minor;
		}
		public int getPatch() {
			return patch;
		}
		
		@Override
		public String toString() {
			return major + "." + minor + "." + patch;
		}

	}
	
	private String name;
	private Version min;
	private Version max;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Version getMax() {
		return max;
	}
	
	public void setMax(int major, int minor, int patch) {
		max = new Version(major, minor, patch);
	}
	
	public Version getMin() {
		return min;
	}
	
	public void setMin(int major, int minor, int patch) {
		min = new Version(major, minor, patch);
	}
}
