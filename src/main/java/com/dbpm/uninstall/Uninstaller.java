/*
*
* author:  gvenzl
* created: 24 Mar 2016
*
* name: Uninstaller.java
*
*/

package com.dbpm.uninstall;

import com.dbpm.Module;
import com.dbpm.utils.ExitCode;

public class Uninstaller implements Module {
	
	public Uninstaller (String[] args) throws IllegalArgumentException {
		
	}

	@Override
	public int run() {
		return ExitCode.EXIT_SUCCESSFUL.getValue();
	}

}
