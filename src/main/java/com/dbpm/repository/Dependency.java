// ***************************************************************************
//
// Author: gvenzl
// Created: 07/04/2016
//
// Name: Dependency.java
// Description: The Dependency class holds information about package dependencies.
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

public class Dependency {

	public class Version {

		private final int major;
		private final int minor;
		private final int patch;
		
		Version(int maj, int min, int ptch) {
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
