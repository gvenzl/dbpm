/*
*
* author:  gvenzl
* created: 26 Mar 2016
*
* name: ALLOWEDFILETYPES.java
*
*/

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
