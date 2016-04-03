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

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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
import org.xml.sax.SAXException;

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
		try {
			Document doc = openRepository();
			
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
			
		} catch (SAXException | IOException | ParserConfigurationException e) {
			Logger.error("Cannot open repository file");
			Logger.error(e.getMessage());
		}
		return false;
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
	
	private Document openRepository() throws SAXException, IOException, ParserConfigurationException {
		return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(repo);
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
		File pgkFile = new File(store.getAbsolutePath() + "/" + pgk.getFullName());
		FileOutputStream out;
		try {
			out = new FileOutputStream(pgkFile);
			out.write(content);
			out.close();
			return true;
		} catch (IOException e) {
			Logger.error("Cannot safe package!");
			Logger.error(e.getMessage());
			return false;
		}
	}

	@Override
	public boolean isPackageInstalled(String dbName, String schemaName, Package pgk) throws SAXException, IOException, ParserConfigurationException {
		Document doc = openRepository();
		
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

}
