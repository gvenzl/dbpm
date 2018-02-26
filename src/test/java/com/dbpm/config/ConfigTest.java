// ***************************************************************************
//
// Author: gvenzl
// Created: 02/04/2016
//
// Name: ConfigTest.java
// Description: The Config class JUnit test driver.
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

package com.dbpm.config;

import org.junit.Assert;
import org.junit.Test;

public class ConfigTest {

	@Test
	public void test_ConfigConstructor() {
		new Config();
	}
	
	@Test
	public void test_ConfigFile() throws Exception {
		Config config = new Config();
		boolean result = config.createConfiguration();
		config.removeConfiguration();
		Assert.assertTrue(result);
	}
	
	@Test
	public void test_removeConfiguration() throws Exception {
		Config config = new Config();
		config.createConfiguration();
		Assert.assertTrue(config.removeConfiguration());
	}

}
