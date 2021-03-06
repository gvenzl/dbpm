// ***************************************************************************
//
// Author: gvenzl
// Created: 27/03/2016
//
// Name: Config.java
// Description: The Config class is responsible for maintaining the configuration and repository for DBPM.
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

package com.dbpm.config;

import com.dbpm.repository.FileRepository;
import com.dbpm.repository.Repository;
import com.dbpm.utils.logger.Logger;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.Properties;

public class Config {

    private static final char[] PASSWORD = "kjldfjaowiablkdammaehdabs".toCharArray();
    private static final byte[] SALT = {
        (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
        (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
    };
	
    private static final String CONFIG_FILE_NAME = "dbpm.properties";
    private static final String REPOSITORY = "repo.xml";
    private static final String PACKAGESTORE = "store";
    
	private final Properties    properties;
	private static final String dbpmDir = System.getProperty("user.home") + "/.dbpm";
	private static final File   configFile = new File(dbpmDir + "/" + CONFIG_FILE_NAME);
	
	public Config() {
		properties = new Properties();
		properties.setProperty("platform", RepoStorage.FILE.name());
		properties.setProperty("repository", REPOSITORY);
		properties.setProperty("packagestore", PACKAGESTORE);
	}
	
	public Config (String platform, String user, String password, String host, String port, String dbName) throws Exception {
		properties = new Properties();
		properties.setProperty("platform", platform);
		properties.setProperty("user", user);
		properties.setProperty("password", encrypt(password));
		properties.setProperty("host", host);
		properties.setProperty("port", port);
		properties.setProperty("dbName", dbName);
	}
	
	public boolean createConfiguration() throws IOException {
		if (!configExists()) {
			Logger.log("Creating configuration");
			
			// Create directory structure
			createDirectory();
			createConfigFile();
		
			if (RepoStorage.FILE.name().equals(properties.getProperty("platform"))) {
				FileRepository repo = new FileRepository(dbpmDir + "/" + REPOSITORY, dbpmDir + "/" + PACKAGESTORE);
				if (!repo.createRepo()) {
					Logger.log("Could not initialize the configuration.");
					return false;
				}
			}
			else {
				// TODO: Implement repo in DB.
			}
			
			// Write property file
			FileOutputStream out = new FileOutputStream(configFile);
			properties.store(out, "DO NOT MODIFY THIS FILE!!!");
			out.close();

			return true;
		}
		else {
			Logger.error("Configuration already exists!");
			return false;
		}
	}
	
	public static Repository getRepository() throws IOException {
		
		// Load platform from config file
		Properties props = new Properties();
		props.load(new FileInputStream(configFile));
		if (props.getProperty("platform").equals(RepoStorage.FILE.name())) {
			String repo = dbpmDir + "/" + props.getProperty("repository");
			String store = dbpmDir + "/" + props.getProperty("packagestore");
			return new FileRepository(repo, store);
		}
		else {
			// TODO: Implement DB repositories
			return null;
		}
	}
	
	private boolean configExists() {
		File configDir = new File(dbpmDir);
		return (configDir.exists() && configFile.exists());
	}
	
	private String encrypt(String password) throws GeneralSecurityException, UnsupportedEncodingException {
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
		SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
        pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
        return Base64.getEncoder().encodeToString(pbeCipher.doFinal(password.getBytes("UTF-8")));
	}
	
	private String decrypt(String password) throws GeneralSecurityException, UnsupportedEncodingException {
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
		SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
		Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
		pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
		return new String(pbeCipher.doFinal(Base64.getDecoder().decode(password)), "UTF-8");
	}

    /**
     * Remove all configuration, including the repository.
     * @return True if and only if the remove has been successful.
     */
	public boolean removeConfiguration() {
		try {
			Repository repo = getRepository();
			if (!repo.remove()) {
				return false;
			}
			configFile.delete();
			File repoDir = new File(dbpmDir);
			for (File file : repoDir.listFiles()) {
				file.delete();
			}
			new File(dbpmDir).delete();
			return true;
			
		} catch (IOException e) {
			Logger.error(e.getMessage());
			return false;
		}
		
	}
	
	private void createDirectory() {
		File dir = new File(dbpmDir);
		if (!dir.exists()) {
			dir.mkdir();
		}
	}
	
	private void createConfigFile() throws IOException {
		if (!configFile.exists()) {
			configFile.createNewFile();
		}
	}
}
