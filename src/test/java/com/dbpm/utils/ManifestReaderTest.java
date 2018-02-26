// ***************************************************************************
//
// Author: gvenzl
// Created: 07/04/2016
//
// Name: ManifestReaderTest.java
// Description: The ManifestReader JUnit test driver.
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
import com.dbpm.repository.Dependency.Version;
import com.dbpm.repository.Package;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class ManifestReaderTest {

	private ManifestReader manifest;
	
	@Before
	public void setup() throws IOException {
		manifest = new ManifestReader(new String(Files.readAllBytes(Paths.get("src/test/resources/manifest.dpm"))));
	}

	@Test
	public void test_getPackage() {
		Package pkg = manifest.getPackage();
		assertEquals("Test package", pkg.getName());
		assertEquals("Test description", pkg.getDescription());
		assertEquals("oracle", pkg.getPlatform());
	}
	
	@Test
	public void test_getDependencies() {
		ArrayList<Dependency> depts = manifest.getDependencies();
		
		for (Dependency dep : depts) {
			switch (dep.getName()) {
				case "package1": {
					Version min = dep.getMin();
					assertEquals(2, min.getMajor());
					assertEquals(1, min.getMinor());
					assertEquals(0, min.getPatch());
					
					Version max = dep.getMax();
					assertEquals(2, max.getMajor());
					assertEquals(Integer.MAX_VALUE, max.getMinor());
					assertEquals(Integer.MAX_VALUE, max.getPatch());
					break;
				}
				case "package2": {
					Version min = dep.getMin();
					assertEquals(1, min.getMajor());
					assertEquals(15, min.getMinor());
					assertEquals(22, min.getPatch());
					
					Version max = dep.getMax();
					assertEquals(2, max.getMajor());
					assertEquals(99, max.getMinor());
					assertEquals(Integer.MAX_VALUE, max.getPatch());
					break;
				}
				case "package3": {
					Version min = dep.getMin();
					assertEquals(5, min.getMajor());
					assertEquals(3, min.getMinor());
					assertEquals(0, min.getPatch());
					
					assertEquals(null, dep.getMax());
					break;
				}
			}
		}
	}
}
