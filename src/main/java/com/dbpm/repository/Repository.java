/*
*
* author:  gvenzl
* created: 27 Mar 2016
*
* name: Repository.java
*
*/

package com.dbpm.repository;

public interface Repository {
	
	public boolean checkRepo();
	
	public boolean createRepo();
	
	public boolean writeEntry (Package pgk);
	
	public boolean savePackage(Package pgk, byte[] content);

}
