/*
*
* author:  gvenzl
* created: 2 Apr 2016
*
* name: FileRepositoryTest.java
*
*/

package com.dbpm.repository;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FileRepositoryTest{

	static String TESTPACKAGE = "Testpackage-1.0.0-oracle.dbpgk";
	String repoName = "testrepo.xml";
	String storeName = "store";
	
	FileRepository repo;
	
	@Before
	public void setup() {
		repo = new FileRepository(repoName, storeName);
	}
	
	@Test
	public void test_createRepo() {
		assertTrue(repo.createRepo());
	}
	
	@Test
	public void test_checkRepo() {
		assertTrue(repo.checkRepo());
	}
	
	@Test
	public void test_writeEntry() {
		repo.createRepo();
		assertTrue(repo.writeEntry("testDB", "TestSchema", new Package(TESTPACKAGE)));
	}
	
	@Test
	public void test_savePackage() throws IOException {
		repo.createRepo();
		byte[] content = Files.readAllBytes(Paths.get(repoName));
		
		repo.savePackage(new Package(TESTPACKAGE), content);
	}
	
	@After
	public void tearDown() {
		new File(repoName).delete();
		new File(storeName).delete();
	}
}
