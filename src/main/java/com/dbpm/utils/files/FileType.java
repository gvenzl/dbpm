// ***************************************************************************
//
// Author: gvenzl
// Created: 26/03/2016
//
// Name: FileType.java
// Description: The allowed and supported file types.
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

package com.dbpm.utils.files;

public enum FileType {

	SQL("sql"),
    SYS("sys"),
    CMD("cmd"),
    MANIFEST("manifest.dpm"),
    DBPKG("dbpkg");

	private final String value;

	FileType(String value) {
	    this.value = value;
    }

    public String getValue() {
         return this.value;
    }

    @Override
    public String toString() {
        return this.getValue();
    }

    /**
     * Returns enum constant.
     * @param value The value for the enum to search
     * @return The enum constant
     */
    public static FileType getType(String value) {
	    value = value.toLowerCase();
        for(FileType v : values())
            if(v.getValue().equalsIgnoreCase(value)) {
                return v;
            }
        throw new IllegalArgumentException();
    }
}
