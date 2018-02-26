// ***************************************************************************
//
// Author: gvenzl
// Created: 21/11/2017
//
// Name: CLIExecutor.java
// Description: The CLIExecutor is responsible for running commands on the command line interface.
//
// Copyright 2017 - Gerald Venzl
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

package com.dbpm.db;

import com.dbpm.utils.logger.Logger;

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
