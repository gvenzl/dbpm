# dbpm
Database Package Manager (by gvenzl - 2016)

## Overview
Database Package Manager lets you create, distribute and install packages for databases. The idea is to provide similar functionality to package database code just like binaries in Unix environments. dbpm is written in Java, making it easy to run it on any platform and against any database that complies with the JDBC standard.

The dbpm package format is **NAME-MAJOR.MINOR.PATCH-PLATFORM.dbpkg**  

**NAME:** Package name  
**MAJOR:** Major version  
**MINOR:** Minor version  
**PATCH:** Patch version  
**PLATFORM:** The Database platform (i.e. Oracle, MySQL, PostgreSQL)  
**.dbpkg:** Extension marking the file as dbpm file  

***The package numbers (major, minor, and patch) must comply with [Semantic Versioning 2.0.0](http://semver.org/)***

## Package format
In order to build a package archive (.dbpkg) the package source folder must have following structure:

    (root folder)
    |- manifest.dpm
    |- PREINSTALL
    |--- 001.name.sql
    |--- 002.name.cmd
    |--- 003.name.sys
    |--- NNN.name.xyz
    |- UPGRADE
    |--- 001.name.sql
    |--- 002.name.sql
    |--- NNN.name.sql
    |- INSTALL
    |--- 001.name.sql
    |--- 002.name.sql
    |--- NNN.name.sql
    |- ROLLBACK
    |--- 001.name.sql
    |--- 002.name.sql
    |--- NNN.name.sql
    |- DOWNGRADE
    |--- 001.name.sql
    |--- 002.name.sql
    |--- NNN.name.sql
    |- POSTINSTALL
    |--- 001.name.sql
    |--- 002.name.cmd
    |--- 003.name.sys
    |--- NNN.name.xyz

### manifest.dpm
The manifest describes the package that is being built. It is a JSON document containing all information necessary for building and installing the package:

    {
        name: "Package name",
        description: "Package description",
        platform: "oracle",
        version: {
          major: 1,
          minor: 0,
          patch: 0
        },
        dependencies: [
          {
            name: "abc",
            min: {
              major: 2,
              minor: 1
            },
            max: {
              major: 2
            }
          },
          {
            name: "efg",
            min: {
              major: 1,
              minor: 15,
              patch: 22
            },
            max: {
              major: 2,
              minor: 99
            }
          },
          {
            name: "xyz",
            min: {
              major: 5,
              minor: 3
            }
          }
        ]
    }

Any dependencies will be verified first and if not present the installation aborted!

**name:** Name of the package. (100 char max)  
**description:** Package description (4000 char max)  
**platform:** The database platform this patch is intended for. Valid options are: `oracle`, `mysql`, `postgres`.  

**version:**  
&nbsp;&nbsp;&nbsp;**major:** Major version of the package  
&nbsp;&nbsp;&nbsp;**minor:** Minor version of the package  
&nbsp;&nbsp;&nbsp;**patch:** Patch version of the package  

**dependencies:** dbpm allows you to specify depending packages that have to be present before your package is installed. This is the fundamental cornerstone of modularisation. If the package you are providing depends on one or more other packages specify those dependency packages and versions here to guarantee they are present.  
&nbsp;&nbsp;&nbsp;**name:** Name of the required package to be present.  
&nbsp;&nbsp;&nbsp;**min (mandatory):** The minimal version of the package  
&nbsp;&nbsp;&nbsp;Only the `major` version number is required, `minor` and `patch` are optional. Whatever version is specified, dbpm will automatically check whether the version requirement is fulfilled.  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**major (mandatory):** Minimum major version of the dependency  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**minor (optional):** Minimum minor version of the dependency  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**patch (optional):** Minimum patch version of the dependency  
&nbsp;&nbsp;&nbsp;**max (optional):** The maximum version of the package. The entire set is optional but it is recommended to at least specify the `major` version component.  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**major (optional):** Maximum major version of the dependency  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**minor (optional):** Maximum minor version of the dependency  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**patch (optional):** Maximum patch version of the dependency

### PREINSTALL
Any pre-installation tasks have to be specified here. There are three different variants of pre-installation tasks:  
**.sql:** Pre-installation task that consists of simple SQL statements.  
**.cmd:** Pre-installation tasks that have to be executed on the command line. Examples could include things like additional installation of other binaries.  
**.sys:** Pre-installation tasks that will be executed as administrator. Examples could include things like database parameter change, forceful closing of connections, etc.

**Note:** Pre-installation scripts are not considered part of the installation process and are therefore not rolled back on error during installation.

### UPGRADE
The upgrade directory is intended for **data model and data upgrade only!** Any upgrade of existing code should be placed in the `INSTALL` directory. The `UPGRADE` directory is executed first so that potential data model and data changes are present before installation of the new code. The upgrade will proceed in the order specified but the numbering of the files, i.e. files starting with 001 will be executed before 002 before 003 and so forth. If there are multiple files with the same number the files will be **executed in parallel** in order to speed up the installation process. This can only be done for files that have no inter-dependencies. Anything dependent on another file has to be specified with a higher number, i.e. 002.

### INSTALL
All installation files required for the package have to be put here as .sql files. No other file type is allowed and will throw an error when building the package!  
The installation will proceed in the order specified but the numbering of the sql files, i.e. files starting with 001 will be executed before 002 before 003 and so forth. If there are multiple files with the same number the files will be **executed in parallel** in order to speed up the installation process. This can only be done for files that have no inter-dependencies. Anything dependent on another file has to be specified with a higher number, i.e. 002.

### ROLLBACK
In case that any error occurs during installation of the new code the author of the package has to provide the equivalent rollback scripts. The rollback scripts correlate with the install scrips meaning that the rollback script 001.mycode.sql contains the rollback statements for install script 001.mycode.sql, 002.mypackage.sql contains the rollback statements for install script 002.mypackage.sql and so forth. Rollback statement will therefore be executed in **decending** order, rather than ascending as with the install scripts. dbpm will call the corresponding rollback files if an error occurs during the installation, i.e if there is an error in install script 029.mypackage.sql dbpm will execute the rollback script 029.mypackage.sql followed by 028.abc.sql, 027.xzy.sql and so forth all the way down to 001.abc.sql. Just as with the `INSTALL` scripts, any scripts with the same number will be executed in parallel.

### DOWNGRADE
Just as with the `ROLLBACK` scripts dbpm will call the corresponding downgrade scripts if any error occurs during upgrade or installation. Downgrade scripts will be **executed last** as part of the rollback process.

### POSTINSTALL
Any post-installation tasks have to be specified here. There are three different variants of post-installation tasks:  
**.sql:** Post-installation task that consists of simple SQL statements.  
**.cmd:** Post-installation tasks that have to be executed on the command line. Examples could include things like sending an email that the installation has been completed.  
**.sys:** Post-installation tasks that will be executed as administrator. Examples could include things like database parameter change after successful install, reopening the database for business again, etc.  

**Note:** Post-installation scripts are not considered part of the installation process and therefore cause no rollback of the installation on error!

## Usage

    Name
    
    dbpm - Database Package Manager
    
    Synopsis
    
    dbpm [command] [options] [package ...]
    
    Description
        dbpm builds, installs or uninstalls packages in your database.
    
    command is on of:
    * build
    * config
    * install package1, [package2, ...]
    * uninstall package1, [package2, ...]
        
    build
        The build process will build a dbpm package for distribution. dbpm has to be executed within the package root folder (see Package format).
        
        -v               Verbose output
    
    config [file | db | remove]
        The configuration will setup the initial repository needed for dbpm. By default, all packages and information will be stored on disk.
    
        file             Will store the repository information within a file on disk (default)
        
        db               Will store the repository information within a database
        -v               Verbose output
        -platform        The database platform (oracle, mysql, postgres)
        -user            Repository username (has to exist)
        -password        Repository user password
        -host            The host where the database is running on
        -port            The port of the database
        -dbName          The database name for the repository to be stored at
        
        remove           Will remove the entire configuration. Packages that have been installed remain installed.
    
    install [options] package1, [package2, ...]
        The installation process will install a given package into the database.

        -v               Verbose output    
        -user            database username/schema where the package should be installed (has to exist)
        -password        database user password
        -adminUser       A privileged database user for executing the ".sys" scripts within the package, if present. If there are no ".sys" scripts within the package this user can be omitted.
        -adminPassword   The password for the privileged user
        -host            The host where the database is running on
        -port            The port of the database
        -dbName          The database name for the repository to be stored at
          
    uninstall [options] ' package1, [package2, ...]
        The uninstallation process will uninstall a given package from a schema.
    
        -v               Verbose output
        -user            database username/schema where the package should be installed (has to exist)
        -password        database user password
        -adminUser       A privileged database user for executing the ".sys" scripts within the package, if present. If there are no ".sys" scripts within the package this user can be omitted.
        -adminPassword   The password for the privileged user
        -host            The host where the database is running on
        -port            The port of the database
        -dbName          The database name for the repository to be stored at
    
### Exit Codes
DBPM uses several exit codes to signal the outcome of the program. This makes it easier for scripts calling DBPM to react to certain conditions.

#### List of exit codes

Exit Code                               | Value | Description
--------------------------------------- | ----- | ---------------------------
EXIT_SUCCESSFUL                         | 0     | The program has executed successfully
EXIT_ERROR                              | 1     | The program has encountered an error. Please check the output for further information.
EXIT_BUILD_MANIFEST_NOT_FOUND           | 101   | The program was unable to find the manifest file during a package build.
EXIT_BUILD_MANIFEST_NOT_READABLE        | 102   | The program could not read the manifest file during a package build. Please check the output for further information.
EXIT_BUILD_MANIFEST_NOT_VALID           | 103   | The manifest file is not a valid JSON document. Please check the output for further information.
EXIT_BUILD_PACKAGE_EXISTS_CANT_OVERRIDE | 121   | The package to be built does already exist in the directory. An attempt was made to overwrite the existing package but the attempt has failed. Please check the output for further information.
EXIT_BUILD_ILLEGAL_FOLDER_FOUND         | 131   | The program has found an illegal folder in the package source directory. Please check the output for further information.
EXIT_BUILD_ILLEGAL_FILE_FOUND           | 132   | The program has found an illegal file in the package source directory. Please check the output for further information.
EXIT_CONFIG_CANT_REMOVE_CONFIG          | 201   | The configuration could not be removed. Please check the output for further information.
EXIT_CONFIG_CANT_CREATE_CONFIG          | 202   | The program was unable to create the configuration. Please check the output for further information.
EXIT_INSTALL_COULD_NOT_INSTALL_PACKAGE  | 301   | The program was unable to install the package. Please check the output for further information.
EXIT_INSTALL_CANT_READ_PACKAGE          | 302   | The program was unable to read the package to be installed. Please check the output for further information.

## Repository
dbpm has a repository in which it stores a copy of packages as all as and the information which packages have been installed where. The repository can either be held on disk in a file or in a database itself. The repository information holds a simple table of packages and packages installed:

### Repository structure

### XML based

**repo.xml**

    <repository>
      <database name="Dev">
        <schema name="QA12">
          <package>Starter-1.0.0-oracle.dbpkg</package>
        </schema>
      </database>
    </repository>

### Database based

**DBPM_PACKAGES**

Column        | Datatype
------------- | ------------
Package_Name  | STRING (125)
Package       | BLOB

**DBPM_ENVIRONMENTS**

Column        | Datatype	
------------- | ------------
Database      | STRING (255)
Schema        | STRING (255)
Package_Name  | STRING (125)

In any case dbpm **does not store** any username and password information of database users and privileged users. Those have to be known at the time of execution of dbpm. The only credentials storeD will be those of the repository if the user decided to store the repository within a database.

## Copyright (c)

Copyright 2018 Gerald Venzl
 
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

