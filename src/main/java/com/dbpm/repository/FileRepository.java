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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.dbpm.logger.Logger;
import com.dbpm.utils.files.FileUtils;

public class FileRepository implements Repository {
	
	private File repo;
	private File store;
	
	public FileRepository(String repo, String store) {
		this.repo = new File(repo);
		this.store = new File(store);
	}
	
	@Override
	public boolean checkRepo() {
		//TODO: Implement repository verification
		return true;
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
			doc.appendChild(doc.createElement("repository"));
			
			if (!saveRepository(doc)) {
				return false;
			}
			
		} catch (Exception e) {
			Logger.error(e.getMessage());
			return false;
		}
		
		if (store.exists()) {
			// Delete all files in the directory
			for (File file : store.listFiles()) {
				file.delete();
			}
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
		Document doc = openRepository();
		if (null == doc) {
			return false;
		}
		
		Element root = doc.getDocumentElement();
		Element dbElem = null;
		Element schElem = null;
			
		// Get database entry
		dbElem = findNode(root, "database", db);
		// If database entry doesn't exist yet, create it
		if (null == dbElem) {
			dbElem = createTag(doc, "database", db);
			root.appendChild(dbElem);
		}
			
		// Get schema entry
		schElem = findNode(dbElem, "schema", schema);
		if (null == schElem) {
			schElem = createTag(doc, "schema", schema);
			dbElem.appendChild(schElem);
		}
			
		Element pgkElem = createTag(doc, "package", null);
		pgkElem.appendChild(doc.createTextNode(pgk.getFullName()));
		schElem.appendChild(pgkElem);
							
		return saveRepository(doc);
	}
	
	private Element findNode(Element root, String nodeName, String name) {
		NodeList nodeList = root.getElementsByTagName(nodeName);
		for (int i=0;i<nodeList.getLength();i++) {
			if(nodeList.item(i).getAttributes().getNamedItem("name").getNodeValue().equals(name)) {
				return (Element) nodeList.item(i);
			}
		}
		return null;
	}
	
	private Document openRepository() {
		try {
			return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(repo);
		}
		catch (Exception e) {
			Logger.error("Cannot open repository");
			Logger.error(e.getMessage());
			return null;
		}
	}
	
	private boolean saveRepository(Document doc) {
		// Write XML to disk
		Transformer transformer;
		try {
			transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(new DOMSource(doc), new StreamResult(repo));
			return true;
			
		} catch (TransformerFactoryConfigurationError | TransformerException e) {
			Logger.error(e.getMessage());
			return false;
		}
	}
	
	private Element createTag(Document doc, String tagName, String name) {
		Element elem = doc.createElement(tagName);
		if (null != name) {
			elem.setAttribute("name", name);
		}
		return elem;
	}

	@Override
	public boolean savePackage(Package pgk, byte[] content) {
		File pkgFile = new File(store.getAbsolutePath() + "/" + pgk.getFullName());
		try {
			if (pkgFile.exists() && FileUtils.compareFileContentToBytess(pkgFile, content)) {
				// File is the same, no need to write it again
				return true;
			}
		} catch (IOException e1) {
			// Ignore exception and save file again
		}

		try {
			pkgFile.createNewFile();
			FileOutputStream out = new FileOutputStream(pkgFile);
			out.write(content);
			out.close();
			return true;
			
		} catch (IOException e) {
			Logger.error("Cannot safe package into repository!");
			Logger.error(e.getMessage());
			// Delete file in case writing did go wrong, otherwise the file won't be there anyway
			pkgFile.delete();
			return false;
		}
	}

	@Override
	public boolean isPackageInstalled(String dbName, String schemaName, Package pgk) {
		
		Document doc = openRepository();
		if (null == doc) {
			// Cannot open repository, mark package as uninstalled and reinstall
			return false;
		}
		
		Element database = findNode(doc.getDocumentElement(), "database", dbName);
		// Database is new
		if (null == database) {
			return false;
		}
		
		Element schema = findNode(database, "schema", schemaName);
		// Schema is new
		if (null == schema ) {
			return false;
		}
		
		NodeList pkgList = schema.getChildNodes();
		for(int i=0; i<pkgList.getLength();i++) {
			if (pkgList.item(i).getTextContent().equals(pgk.getFullName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean verifyDependencies(String dbName, String schema, ArrayList<Dependency> dependencies) {
		if (null == dependencies || dependencies.isEmpty()) {
			return true;
		}

		Document doc = openRepository();
		
		if (null == doc) {
			// Cannot read repository, abort
			return false;
		}
		// Check all dependencies
		for (Dependency dept : dependencies) {
			//TODO: Verify dependencies
		}
		return true;
	}

	@Override
	public boolean remove() {
		store.delete();
		repo.delete();
		return true;
	}
}
