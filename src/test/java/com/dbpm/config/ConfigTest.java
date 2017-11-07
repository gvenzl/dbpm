/*
*
* author:  gvenzl
* created: 2 Apr 2016
*
* name: ConfigTest.java
*
*/

package com.dbpm.config;

import org.junit.Assert;
import org.junit.Test;

public class ConfigTest {

	@Test
	public void test_ConfigConstructor() throws Exception {
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
