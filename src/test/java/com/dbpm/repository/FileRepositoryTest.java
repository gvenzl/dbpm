// ***************************************************************************
//
// Author: gvenzl
// Created: 02/04/2016
//
// Name: FileRepositoryTest.java
// Description: The FileRepository JUnit test driver.
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
