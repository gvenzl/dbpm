/*
 *
 * author:  gvenzl
 * created: 25 Feb 2018
 *
 * name: ExitCode.java
 *
 */

package com.dbpm.utils;

/**
 * An enum to cover all possible exit codes of the program.
 */
public enum ExitCode {

    // General exit codes
    EXIT_SUCCESSFUL(0),
    EXIT_ERROR(1),

    // Build related codes: 101 - 199
    EXIT_BUILD_MANIFEST_NOT_FOUND(101),
    EXIT_BUILD_MANIFEST_NOT_READABLE(102),
    EXIT_BUILD_MANIFEST_NOT_VALID(103),
    EXIT_BUILD_PACKAGE_EXISTS_CANT_OVERRIDE(121),

    // Config related codes: 201 - 299
    EXIT_CONFIG_CANT_REMOVE_CONFIG(201),
    EXIT_CONFIG_CANT_CREATE_CONFIG(202),

    // Install related codes: 301 - 399
    EXIT_INSTALL_COULD_NOT_INSTALL_PACKAGE(301),
    EXIT_INSTALL_CANT_READ_PACKAGE(302);

    // Uninstall related codes: 401 = 499

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
