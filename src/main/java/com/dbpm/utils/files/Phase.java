// ***************************************************************************
//
// Author: gvenzl
// Created: 26/03/2016
//
// Name: Phase.java
// Description: The Phases of the installation and deinstallation.
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

public enum Phase {
	PREINSTALL, INSTALL, UPGRADE, ROLLBACK, DOWNGRADE, POSTINSTALL
}
