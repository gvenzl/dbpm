// ***************************************************************************
//
// Author: gvenzl
// Created: 25/02/2018
//
// Name: ExitCode.java
// Description: The exit codes of the program.
//
// Copyright 2018 - Gerald Venzl
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

package com.dbpm.utils;

/**
 * An enum to cover all possible exit codes of the program.
 */
public enum ExitCode {

    /*
     ********************************** /
     ******* General exit codes ******* /
     ********************************** /
     */

    /**
     * Successful program termination.
     */
    EXIT_SUCCESSFUL(0),
    /**
     * An error has occurred, usually caused by some exception within libraries.
     */
    EXIT_ERROR(1),

    /*
     ********************************** /
     * Build related codes: 101 - 199 * /
     ********************************** /
     */

    /**
     * The manifest was not found.
     */
    EXIT_BUILD_MANIFEST_NOT_FOUND(101),
    /**
     * The manifest was not readable.
     */
    EXIT_BUILD_MANIFEST_NOT_READABLE(102),
    /**
     * The manifest was not a valid JSON document.
     */
    EXIT_BUILD_MANIFEST_NOT_VALID(103),
    /**
     * The package already exists and couldn't be overwritten.
     */
    EXIT_BUILD_PACKAGE_EXISTS_CANT_OVERRIDE(121),
    /**
     * The package contains an illegal folder.
     */
    EXIT_BUILD_ILLEGAL_FOLDER_FOUND(131),
    /**
     * The package contains an illegal file.
     */
    EXIT_BUILD_ILLEGAL_FILE_FOUND(132),

    /*
     *********************************** /
     * Config related codes: 201 - 299 * /
     *********************************** /
     */

    /**
     * The configuration could not be removed.
     */
    EXIT_CONFIG_CANT_REMOVE_CONFIG(201),
    /**
     * The configuration could not be created.
     */
    EXIT_CONFIG_CANT_CREATE_CONFIG(202),

    /*
     ************************************ /
     * Install related codes: 301 - 399 * /
     ************************************ /
     */

    /**
     * The package could not be installed.
     */
    EXIT_INSTALL_COULD_NOT_INSTALL_PACKAGE(301),
    /**
     * The package to be installed could not be read.
     */
    EXIT_INSTALL_CANT_READ_PACKAGE(302);

    /*
     ************************************** /
     * Uninstall related codes: 401 - 499 * /
     ************************************** /
     */

    private int value;

    /**
     * Private constructor for the enum.
     * @param value The value of the enum
     */
    ExitCode(int value) {
        this.value = value;
    }

    /**
     * Get the value of the enum.
     * @return The value.
     */
    public int getValue() {
        return this.value;
    }

    /**
     * Provide a toString representation of the enum.
     * @return A string representing the enum.
     */
    @Override
    public String toString() {
        return Integer.toString(this.getValue());
    }

    /**
     * Returns enum constant.
     * @param value The value for the enum to search
     * @return The enum constant
     */
    public static ExitCode getType(int value) {
        for(ExitCode v : values())
            if(v.getValue() == value) {
                return v;
            }
        throw new IllegalArgumentException();
    }

}
