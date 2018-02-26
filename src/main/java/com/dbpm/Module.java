/*
*
* author:  gvenzl
* created: 24 Mar 2016
*
* name: Module.java
*
*/

package com.dbpm;

/**
 * This interface describes a module of dbpm.
 * @author gvenzl
 *
 */
public interface Module {

	/**
	 * Implements the main run method.
	 * @return The return code from the module.<br/>
     * 0 = successful execution.<br/>
     * >0 = unsuccessful execution
	 */
	int run();
}
