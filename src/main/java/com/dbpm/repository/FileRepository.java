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

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
			// Initialize XML file
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element rootElement = doc.createElement("repository");
			doc.appendChild(rootElement);
			
			// Write XML to disk
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.transform(new DOMSource(doc), new StreamResult(repo));

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
	public boolean writeEntry(String db, String schema, Package pgk) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean savePackage(Package pgk, byte[] content) {
		// TODO Auto-generated method stub
		return false;
	}

}
