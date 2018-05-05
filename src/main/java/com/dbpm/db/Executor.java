// ***************************************************************************
//
// Author: gvenzl
// Created: 03/05/2018
//
// Name: Executor.java
// Description: 
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

package com.dbpm.db;

public interface Executor {

    /**
     * Executes given commands.
     * @param commands The commands to be executed
     * @return true on successful completion, otherwise false
     * @throws Exception Any error that happens during the execution
     */
    boolean execute(String commands) throws Exception;
}
