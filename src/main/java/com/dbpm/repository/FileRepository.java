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
	private Document repoDoc;
	
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
			repoDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			repoDoc.appendChild(repoDoc.createElement("repository"));
			
			if (!saveRepository()) {
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
	public boolean writeEntry(String db, String schema, Package pkg) {
		openRepository();
		if (null == repoDoc) {
			return false;
		}
		
		Element root = repoDoc.getDocumentElement();
		Element dbElem = null;
		Element schElem = null;
			
		// Get database entry
		dbElem = findNode(root, "database", db);
		// If database entry doesn't exist yet, create it
		if (null == dbElem) {
			dbElem = createTag("database", db);
			root.appendChild(dbElem);
		}
			
		// Get schema entry
		schElem = findNode(dbElem, "schema", schema);
		if (null == schElem) {
			schElem = createTag("schema", schema);
			dbElem.appendChild(schElem);
		}
			
		Element pkgElem = createTag("package", null);
		pkgElem.appendChild(repoDoc.createTextNode(pkg.getFullName()));
		schElem.appendChild(pkgElem);
							
		return saveRepository();
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
	
	private boolean openRepository() {
		try {
			repoDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(repo);
			return (null != repoDoc);
		}
		catch (Exception e) {
			Logger.error("Cannot open repository.");
			Logger.error(e.getMessage());
			return false;
		}
	}
	
	private boolean saveRepository() {
		// Write XML to disk
		Transformer transformer;
		try {
			transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(new DOMSource(repoDoc), new StreamResult(repo));
			return true;
			
		} catch (TransformerFactoryConfigurationError | TransformerException e) {
			Logger.error(e.getMessage());
			return false;
		}
	}
	
	private Element createTag(String tagName, String name) {
		Element elem = repoDoc.createElement(tagName);
		if (null != name) {
			elem.setAttribute("name", name);
		}
		return elem;
	}

	@Override
	public boolean savePackage(Package pkg, byte[] content) {
		File pkgFile = new File(store.getAbsolutePath() + "/" + pkg.getFullName());
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
			Logger.error("Cannot save package in repository!");
			Logger.error(e.getMessage());
			// Delete file in case writing did go wrong, otherwise the file won't be there anyway
			pkgFile.delete();
			return false;
		}
	}

	private NodeList getPackageTree(String dbName, String schemaName) {

		if (!openRepository()) {
			return null;
		}
		
		Element database = findNode(repoDoc.getDocumentElement(), "database", dbName);
		// Cannot find database
		if (null == database) {
			return null;
		}
		
		Element schema = findNode(database, "schema", schemaName);
		// Cannot find schema
		if (null == schema ) {
			return null;
		}
		return schema.getChildNodes();
	}
	
	@Override
	public boolean isPackageInstalled(String dbName, String schemaName, Package pkg) {
		
		NodeList pkgList = getPackageTree(dbName, schemaName);
		if (null != pkgList) {
			for(int i=0; i<pkgList.getLength();i++) {
				// getFullName includes version number as well
				if (pkgList.item(i).getTextContent().equals(pkg.getFullName())) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean verifyDependencies(String dbName, String schemaName, ArrayList<Dependency> dependencies) {
		if (null == dependencies || dependencies.isEmpty()) {
			return true;
		}
		
		if (!openRepository()) {
			Logger.verbose("Cannot read repository.");
			return false;
		}
		
		Logger.verbose("Check all dependencies.");
		NodeList pkgTree = getPackageTree(dbName, schemaName);
		for (Dependency dep : dependencies) {
			if (!verifyDependency(pkgTree, dep)) {
				Logger.error("Cannot resolve dependency \"", dep.getName(), "-", dep.getMin().toString(), "\"");
				return false;
			}
		}
		return true;
	}
	
	private boolean verifyDependency(NodeList pkgTree, Dependency dep) {
		// TODO: Verify dependencies for package installation
		return false;
	}

	@Override
	public boolean remove() {
		store.delete();
		repo.delete();
		return true;
	}
}
