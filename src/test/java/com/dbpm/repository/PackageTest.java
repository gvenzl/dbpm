/*
*
* author:  gvenzl
* created: 2 Apr 2016
*
* name: PackageTest.java
*
*/

package com.dbpm.repository;

import static org.junit.Assert.*;

import org.junit.Test;

import com.dbpm.DBPM;

public class PackageTest {

	@Test
	public void test_PackageConstructor() {
		String testName = "Test-1.2.3-oracle" + DBPM.PKG_FILE_EXTENSION;
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
