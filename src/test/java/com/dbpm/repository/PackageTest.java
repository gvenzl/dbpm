// ***************************************************************************
//
// Author: gvenzl
// Created: 02/04/2016
//
// Name: PackageTest.java
// Description: The Package JUnit test driver.
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
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PackageTest {

	@Test
	public void test_PackageConstructor() {
		String testName = "Test-1.2.3-oracle." + FileType.DBPKG;
		Package testPkg = new Package(testName);
		
		assertEquals("Test", testPkg.getName());
		assertEquals(1, testPkg.getMajor());
		assertEquals(2, testPkg.getMinor());
		assertEquals(3, testPkg.getPatch());
		assertEquals("oracle", testPkg.getPlatform());
		
		assertEquals(testName, testPkg.getFullName());
	}
	
	@Test
	public void test_Name() {
		Package pkg = new Package();
		pkg.setName("name");
		assertEquals("name", pkg.getName());
	}
	
	@Test
	public void test_Major() {
		Package pkg = new Package();
		pkg.setMajor(1);
		assertEquals(1, pkg.getMajor());
	}

	@Test
	public void test_Minor() {
		Package pkg = new Package();
		pkg.setMinor(1);
		assertEquals(1, pkg.getMinor());
	}
	
	@Test
	public void test_Patch() {
		Package pkg = new Package();
		pkg.setPatch(1);
		assertEquals(1, pkg.getPatch());
	}
	
	@Test
	public void test_Platform() {
		Package pkg = new Package();
		pkg.setPlatform("oracle");
		assertEquals("oracle", pkg.getPlatform());
	}

}
