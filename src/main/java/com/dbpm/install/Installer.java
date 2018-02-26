// ***************************************************************************
//
// Author: gvenzl
// Created: 24/03/2016
//
// Name: Installer.java
// Description: The Installer module driver.
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

package com.dbpm.install;

import com.dbpm.Module;
import com.dbpm.config.Config;
import com.dbpm.db.DbType;
import com.dbpm.repository.Package;
import com.dbpm.repository.Repository;
import com.dbpm.utils.ExitCode;
import com.dbpm.utils.PackageReader;
import com.dbpm.utils.Parameter;
import com.dbpm.utils.files.FileType;
import com.dbpm.utils.files.FileUtils;
import com.dbpm.utils.files.Phase;
import com.dbpm.utils.logger.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

/**
 * The Installer takes care of the installation of packages
 * @author gvenzl
 *
 */
public class Installer implements Module {

    private File	packageFile;
    private String	userName;
    private String	password;
    private String	adminUser;
    private String	adminPassword;
    private String	host;
    private String	port;
    private String	dbName;

    /**
     * Constructs an Installer object.
     * @param args An array with the parameters for the installation
     * @throws IllegalArgumentException if an illegal argument is passed along
     */
    public Installer (String[] args) throws IllegalArgumentException {
        // Ignore first parameter as this one was already checked by DBPM in order to call installer
        for (int i=1; i<args.length; i++) {
            switch (args[i]) {
                case Parameter.USER:
                    userName = args[++i];
                    break;
                case Parameter.PASSWORD:
                    password = args[++i];
                    break;
                case Parameter.ADMINUSER:
                    adminUser = args[++i];
                    break;
                case Parameter.ADMINPASSWORD:
                    adminPassword = args[++i];
                    break;
                case Parameter.HOST:
                    host = args[++i];
                    break;
                case Parameter.PORT:
                    port = args[++i];
                    break;
                case Parameter.DBNAME:
                    dbName = args[++i];
                    break;
                // Ignore verbose as already set globally by DBPM
                case Parameter.VERBOSE:
                    break;
                default:
                    // Unknown parameter
                    if (args[i].charAt(0) == '-') {
                        throw new IllegalArgumentException(args[i] + " is not a valid argument for install.");
                    }
                    else {
                        String packageName = args[i];

                        // If extensions hasn't been passed on, automatically add it
                        if (!packageName.endsWith("." + FileType.DBPKG.getValue())) {
                            packageName = packageName + "." + FileType.DBPKG.getValue();
                        }

                        packageFile = new File(packageName);
                        if (!packageFile.exists()) {
                            Logger.verbose("File " + packageFile.getName() + " not found in location.");
                            Logger.verbose("Checking repository...");

                            // If file can't be found, try to get it from the repository
                            try {
                                packageFile = Config.getRepository().getPackage(new Package(packageName));
                                Logger.verbose("Package loaded from repository.");
                            }
                            catch (FileNotFoundException e) {
                                throw new IllegalArgumentException(args[i] + " does not exist!");
                            }
                            catch (IOException e) {
                                Logger.verbose("Repository cannot be opened:");
                                Logger.verbose(e.getMessage());
                                throw new IllegalArgumentException(args[i] + " does not exist!");
                            }
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public int run() {
        Path dir;
        // if no file has been passed on, install all dbpkg files
        if (null == packageFile) {
            dir = FileSystems.getDefault().getPath(System.getProperty("user.dir"));
            try {
                DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*" + FileType.DBPKG);
                // Install each dbpm package in the current working directory
                // TODO: Build dependency tree instead and call install with right order
                // TODO: Think about invalid packages and how to raise the exit code for it.
                for (Path path : stream) {
                    installPackage(new File(path.getParent() + File.separator + path.getFileName()));
                }
                stream.close();
                return ExitCode.EXIT_SUCCESSFUL.getValue();
            } catch (IOException e) {
                Logger.error("Cannot read package file!");
                Logger.error(e.getMessage());
                return ExitCode.EXIT_INSTALL_CANT_READ_PACKAGE.getValue();
            }
        }
        else {
            if (installPackage(packageFile)) {
                return ExitCode.EXIT_SUCCESSFUL.getValue();
            }
            else {
                return ExitCode.EXIT_INSTALL_COULD_NOT_INSTALL_PACKAGE.getValue();
            }
        }

    }

    /**
     * Runs the installation routines for a package installation into the database.
     * Those include saving the package itself into the repository, checking whether the package is already installed,
     * verifying the dependencies, installing the actual package into the database, and saving the installation success into the repository.
     * @param packageFile The package to be installed
     * @return True if and only if the installation was successful, otherwise false
     */
    private boolean installPackage(File packageFile) {
        if (!packageFile.exists()) {
            Logger.error("Package file ", packageFile.getAbsolutePath(), " does not exist!");
            return false;
        }

        try {
            PackageReader pkgReader = new PackageReader(packageFile);
            Repository repo = Config.getRepository();

            // Save package to repository
            if (!repo.savePackage(pkgReader.getPackage(), Files.readAllBytes(Paths.get(packageFile.getAbsolutePath())))) {
                boolean retry = true;
                while (retry) {
                    switch (Logger.prompt("y","Package couldn't be saved in repository, continue without saving?")) {
                        case "n": {
                            System.out.println("User response \"n\", abort.");
                            return false;
                        }
                        case "y": {
                            System.out.println("User response \"y\", continuing...");
                            retry = false;
                            break;
                        }
                        case "r": {
                            // Retry save, if it works continue
                            System.out.println("Retrying...");
                            if (repo.savePackage(pkgReader.getPackage(), Files.readAllBytes(Paths.get(packageFile.getAbsolutePath())))) {
                                retry = false;
                            }
                            break;
                        }
                    }
                }
            }

            Logger.verbose("Check whether package is already installed...");
            if (repo.isPackageInstalled(dbName, userName, pkgReader.getPackage())) {
                Logger.log("Package is already installed, doing nothing.");
                return true;
            }
            Logger.verbose("Package not yet installed, installing...");

            Logger.verbose("Check whether dependencies are already installed...");
            if (!repo.verifyDependencies(dbName, userName, pkgReader.getManifest().getDependencies())) {
                Logger.error("Dependencies not installed!");
                return false;
            }
            Logger.verbose("Check whether dependencies are already installed... DONE");

            Logger.log("Installing file...");
            if (!executeInstallation(pkgReader)) {
                Logger.error("Installation of ", pkgReader.getManifest().getPackage().getFullName(), " has been unsuccessful!");
                return false;
            }
            Logger.log("Installing file... DONE");

            Logger.verbose("Save package information in repository...");
            if (!repo.writeEntry(dbName, userName, pkgReader.getPackage())) {
                Logger.error("Could not save install information in repository!");
                return false;
            }
            Logger.verbose("Save package information in repository... DONE");

            return true;

        } catch(Exception e) {
            Logger.error("Cannot install package!");
            Logger.error(e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Executes the installation of the package into the database.
     * @param pkgReader The {@link PackageReader} package to be installed
     * @return True if and only if the installation was successful
     */
    private boolean executeInstallation(PackageReader pkgReader) {

        if (!executePreInstallScripts(pkgReader)) {
            Logger.error("Error executing pre-install scripts, aborting!");
            return false;
        }

        if (!executeUpgradeScripts(pkgReader)) {
            Logger.error("Error executing upgrades scripts!");
            //Logger.log("Running downgrading files...");
            //TODO: Run downgrade files
            //Logger.log("Done, aborting!");
            return false;
        }

        if (!executeInstallScripts(pkgReader)) {
            Logger.error("Error executing install scripts!");
            //Logger.log("Running uninstall scripts...");
            //TODO: Run uninstall files and downgrade files
            //Logger.log("Done, aborting!");
            return false;
        }

        if (!executePostInstallScripts(pkgReader)) {
            Logger.error("Error running post-install scripts!");
            return false;
        }

        return true;
    }

    /**
     * Executes the installation of the package into the database from a particular {@link Phase} and sequence onwards.
     * @param pkgReader The {@link PackageReader} package to be installed
     * @param phase The {@link Phase} from which to start installing
     * @param sequence The sequence from which to start, e.g. (001, 002, 003, ...)
     * @return True if the installation has been successful, otherwise false
     */
    public boolean executeInstallation(PackageReader pkgReader, Phase phase, String sequence) {
        //TODO: Implement
        return false;
    }

    /**
     * Executes the pre-install scripts of the package.
     * @param pkgReader The {@link PackageReader} package containing the scripts
     * @return True if the execution was successful
     */
    private boolean executePreInstallScripts(PackageReader pkgReader) {
        DbType dbType = DbType.valueOf(pkgReader.getManifest().getPackage().getPlatform());
        HashMap<String, String> preInstallScript = pkgReader.getPreInstallFiles();
        if (!preInstallScript.isEmpty()) {
            Logger.verbose("Executing pre-installation scripts...");
            if (executeScripts(dbType, preInstallScript)) {
                Logger.verbose("Executing pre-installation scripts... DONE");
                return true;
            }
            else {
                return false;
            }
        }
        return true;
    }

    /**
     * Executes the upgrade scripts of the package.
     * @param pkgReader The {@link PackageReader} package containing the scripts
     * @return True if the execution was successful
     */
    private boolean executeUpgradeScripts(PackageReader pkgReader) {
        DbType dbType = DbType.valueOf(pkgReader.getManifest().getPackage().getPlatform());
        HashMap<String, String> upgradeScripts = pkgReader.getUpgradeFiles();
        if (!upgradeScripts.isEmpty()) {
            Logger.verbose("Executing upgrade scripts...");
            if (executeSQLScripts(dbType, upgradeScripts)) {
                Logger.verbose("Executing upgrade scripts... DONE");
            }
            else {
                Logger.error("Error executing upgrade scripts!");
                return false;
            }
        }
        return true;
    }

    /**
     * Executes the install scripts of the package.
     * @param pkgReader The {@link PackageReader} package containing the scripts
     * @return True if the execution was successful
     */
    private boolean executeInstallScripts(PackageReader pkgReader) {
        DbType dbType = DbType.valueOf(pkgReader.getManifest().getPackage().getPlatform());
        HashMap<String, String> installScripts = pkgReader.getInstallFiles();
        if (!installScripts.isEmpty()) {
            Logger.verbose("Executing installation scripts...");
            if (executeSQLScripts(dbType, installScripts)) {
                Logger.verbose("Executing installation scripts... DONE");
                return true;
            }
            else {
                return false;
            }
        }
        return true;
    }

    /**
     * Executes the post-install scripts of the package.
     * @param pkgReader The {@link PackageReader} package containing the scripts
     * @return True if the execution was successful
     */
    private boolean executePostInstallScripts(PackageReader pkgReader) {
        DbType dbType = DbType.valueOf(pkgReader.getManifest().getPackage().getPlatform());
        HashMap<String, String> postInstallScripts = pkgReader.getPostInstallFiles();
        if (!postInstallScripts.isEmpty()) {
            Logger.verbose("Executing post-installation scripts...");
            if (executeScripts(dbType, postInstallScripts)) {
                Logger.verbose("Executing post-installation scripts... DONE");
                return true;
            }
            else {
                return false;
            }
        }
        return true;
    }

    /**
     * Executes all .sql scripts.
     * @param dbType The database type to execute against
     * @param scripts The scripts to be executed
     * @return True if the execution has been successful
     */
    private boolean executeSQLScripts(DbType dbType, HashMap<String, String> scripts) {

        HashMap<String, String> sqlEntries = new HashMap<>();

        // Iterate over all entries and only extract SQL files
        for (Map.Entry<String, String> entry : scripts.entrySet()) {
            if (FileUtils.getExtension(entry.getKey()).toLowerCase().equals(FileType.SQL.toString())) {
                sqlEntries.put(entry.getKey(), entry.getValue());
            }
        }

        return executeScripts(dbType, sqlEntries);
    }

    /**
     * Executes installation scripts.
     * @param dbType The database type to execute against
     * @param scripts The scripts files to be executed
     * @return True if the execution has been successful
     */
    private boolean executeScripts(DbType dbType, HashMap<String, String> scripts) {

        Logger.log("Executing scripts...");
        // Loop over scripts and execute them
        //TODO: Parallelize scripts with same number
        //TODO: Verify that files are iterated in correct order
        for (Map.Entry<String, String> entry : scripts.entrySet()) {
            String fileName = entry.getKey();
            Logger.verbose("Executing file ", fileName);

            //TODO: Implement scripts execution
            switch(FileType.getType(FileUtils.getExtension(fileName))) {
                case SQL: {
                    break;
                }
                case SYS: {
                    break;
                }
                case CMD: {
                    break;
                }
            }
        }
        return false;
    }
}
