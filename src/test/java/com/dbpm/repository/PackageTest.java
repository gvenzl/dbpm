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
		Package testPgk = new Package(testName);
		
		assertEquals("Test", testPgk.getName());
		assertEquals(1, testPgk.getMajor());
		assertEquals(2, testPgk.getMinor());
		assertEquals(3, testPgk.getPatch());
		assertEquals("oracle", testPgk.getPlatform());
		
		assertEquals(testName, testPgk.getFullName());
	}
	
	@Test
	public void test_Name() {
		Package pgk = new Package();
		pgk.setName("name");
		assertEquals("name", pgk.getName());
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
