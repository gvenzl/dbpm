/*
*
* author:  gvenzl
* created: 27 Mar 2016
*
* name: FileRepository.java
*
*/

package com.dbpm.repository;

import java.io.File;

import com.dbpm.logger.Logger;

public class FileRepository implements Repository {
	
	private File repo;
	private File store;
	
	public FileRepository(String repo, String store) {
		this.repo = new File(repo);
		this.store = new File(store);
	}
	
	@Override
	public boolean checkRepo() {
		return false;
	}

	@Override
	public boolean createRepo() {
		if (repo.exists()) {
			if (!repo.delete()) {
				return false;
			}
		}
		try {
			repo.createNewFile();
		} catch (Exception e) {
			Logger.error(e.getMessage());
			return false;
		}
		
		if (store.exists()) {
			if (!store.delete()) {
				return false;
			}
		}
		if (!store.mkdir()) {
			return false;
		}
		
		return true;
	}

	@Override
	public boolean writeEntry(Package pgk) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean savePackage(Package pgk, byte[] content) {
		// TODO Auto-generated method stub
		return false;
	}

}
