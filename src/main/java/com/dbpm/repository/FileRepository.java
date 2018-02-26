// ***************************************************************************
//
// Author: gvenzl
// Created: 27/03/2016
//
// Name: FileRepository.java
// Description: The FileRepository calls is responsible for managing the file based repository.
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

package com.dbpm.repository;

import com.dbpm.utils.files.FileUtils;
import com.dbpm.utils.logger.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class FileRepository implements Repository {
	
	private final File repo;
	private final File store;
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

        return store.mkdir();
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
		for (int i=0; i<nodeList.getLength(); i++) {
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
			if (pkgFile.exists() && FileUtils.compareFileContentToBytes(pkgFile, content)) {
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

	/**
	 * Gets a package form the repository.
	 *
	 * @param pkg The package to load from the repository
	 * @return The package file
	 * @throws FileNotFoundException If the package file can't be found
	 */
	@Override
	public File getPackage(Package pkg) throws FileNotFoundException {
		File packageFile = new File(store.getAbsolutePath() + "/" + pkg.getFullName());
		if (packageFile.exists()) {
		    return packageFile;
        }
        else {
		    throw new FileNotFoundException("File " + pkg.getFullName() + " not in repository");
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
		
		Logger.verbose("Check all dependencies...");
		NodeList pkgTree = getPackageTree(dbName, schemaName);
		for (Dependency dep : dependencies) {
			if (!verifyDependency(pkgTree, dep)) {
				Logger.error("Cannot resolve dependency \"", dep.getName(), "-", dep.getMin().toString(), "\"");
				return false;
			}
		}
		Logger.verbose("All dependencies present.");
		return true;
	}
	
	private boolean verifyDependency(NodeList pkgTree, Dependency dep) {
		// TODO: Verify dependencies for package installation
		return false;
	}

	@Override
	public boolean remove() {
		return (store.delete() && repo.delete());
	}
}
