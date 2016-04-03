/*
*
* author:  gvenzl
* created: 2 Apr 2016
*
* name: ConfiguratorTest.java
*
*/

package com.dbpm.config;

import org.junit.Test;

public class ConfiguratorTest {

	@Test
	public void test_ConfiguratorFile() {
		String[] args = {"file", "-v"};
		Configurator config = new Configurator(args);
		config.run();
	}

}
