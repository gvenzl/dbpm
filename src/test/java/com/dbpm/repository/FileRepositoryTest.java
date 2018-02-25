/*
*
* author:  gvenzl
* created: 2 Apr 2016
*
* name: FileRepositoryTest.java
*
*/

package com.dbpm.repository;

import com.dbpm.utils.files.FileType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FileRepositoryTest{

	private static final String TESTPACKAGE = "Testpackage-1.0.0-oracle." + FileType.DBPKG;
	private final String repoName = "testrepo.xml";
	private final String storeName = "store";
	
	private FileRepository repo;
	
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
	public void test_isPackageInstalledPositive() {
		repo.createRepo();
		assertTrue(repo.writeEntry("testDB", "TestSchema", new Package(TESTPACKAGE)));
		assertTrue(repo.isPackageInstalled("testDB", "TestSchema", new Package(TESTPACKAGE)));
	}
	
	@Test
	public void test_isPackageInstalledNegative() {
		repo.createRepo();
		// No database or schema exist
		assertFalse(repo.isPackageInstalled("testDB", "TestSchema", new Package(TESTPACKAGE)));
		// No schema exists but database does already exist
		assertTrue(repo.writeEntry("testDB", "TestSchema", new Package(TESTPACKAGE)));
		assertFalse(repo.isPackageInstalled("testDB", "OtherTestSchema", new Package(TESTPACKAGE)));
		// Schema and DB exist but package does not exist
		assertFalse(repo.isPackageInstalled("testDB", "TestSchema", new Package("dummy-9.9.9-mysql." + FileType.DBPKG)));
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
