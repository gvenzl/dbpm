/*
*
* author:  gvenzl
* created: 21 Nov 2017
*
* name: CLIExecutor.java
*
*/
package com.dbpm.db;

import com.dbpm.logger.Logger;

public class CLIExecutor {

    /**
     * Executes commands in the command line.
     * @param commands The commands to be executed.
     * @return True if execution was successful, otherwise false.
     */
    public boolean executeCLI(String commands) {
        Runtime rt = Runtime.getRuntime();
        try {
            Process proc = rt.exec(commands);
            return (proc.waitFor() == 0);
        }
        catch (Exception e) {
            Logger.error(e.getMessage());
            return false;
        }
    }
}
