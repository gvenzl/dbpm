/**
 *
 * @author gvenzl
 * created: 25/02/2018
 * <p>
 * Name: PackageBuilderTest.java
 * Description: JUnit test file for PackageBuilder
 * <p>
 * Copyright 2018 Gerald Venzl
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.dbpm.build;

import com.dbpm.utils.ExitCode;
import com.dbpm.utils.logger.Verbose;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class PackageBuilderTest {


    @Before
    public void setup() {
        Verbose.getInstance().setVerbose(true);
    }

    /**
     * Test building a valid pacakge
     */
    @Test
    public void test_buildPackage() {
        PackageBuilder builder = new PackageBuilder("src/test/resources/TestPackageCorrectFormat/");
        Assert.assertEquals(ExitCode.EXIT_SUCCESSFUL.getValue(), builder.run());
        // Clean up package
        Assert.assertTrue(new File(builder.getPackageName()).delete());
    }

    /**
     * Tests the successful build of a package that just has a manifest file and empty folders in it (i.e. nothing to do)
     */
    @Test
    public void test_buildPackageWithOnlyManifestAndFolders() {
        PackageBuilder builder = new PackageBuilder("src/test/resources/TestPackageWithOnlyManifestAndFolders/");
        Assert.assertEquals(ExitCode.EXIT_SUCCESSFUL.getValue(), builder.run());
        // Clean up package
        Assert.assertTrue(new File(builder.getPackageName()).delete());
    }

    /**
     * Test the unsuccessful build when no manifest is present
     */
    @Test
    public void test_negative_buildPackageNoManifest() {
        PackageBuilder builder = new PackageBuilder("src/test/resources/TestPackageNoManifest/");
        Assert.assertEquals(ExitCode.EXIT_BUILD_MANIFEST_NOT_FOUND.getValue(), builder.run());
    }

    /**
     * Test the unsuccessful build when an invalid manifest is present
     */
    @Test
    public void test_negative_buildPackageInvalidManifest() {
        PackageBuilder builder = new PackageBuilder("src/test/resources/TestPackageInvalidManifest/");
        Assert.assertEquals(ExitCode.EXIT_BUILD_MANIFEST_NOT_VALID.getValue(), builder.run());
    }

    /**
     * Tests the unsuccessful package build with an invalid folder
     */
    @Test
    public void test_negative_buildPackageWithInvalidFolder() {
        PackageBuilder builder = new PackageBuilder("src/test/resources/TestPackageWithInvalidFolder/");
        Assert.assertEquals(ExitCode.EXIT_BUILD_ILLEGAL_FOLDER_FOUND.getValue(), builder.run());
    }

    /**
     * Test the unsuccessful package build with an invalid file in the PREINSTALL folder
     */
    @Test
    public void test_negative_buildPackageWithInvalidFileInPreinstall() {
        PackageBuilder builder = new PackageBuilder("src/test/resources/TestPackageWithInvalidFileInPreinstall/");
        Assert.assertEquals(ExitCode.EXIT_BUILD_ILLEGAL_FILE_FOUND.getValue(), builder.run());
    }

    /**
     * Test the unsuccessful package build with an invalid file in the UPGRADE folder (cmd file)
     */
    @Test
    public void test_negative_buildPackageWithInvalidFileInUpgrade() {
        PackageBuilder builder = new PackageBuilder("src/test/resources/TestPackageWithInvalidFileInUpgrade/");
        Assert.assertEquals(ExitCode.EXIT_BUILD_ILLEGAL_FILE_FOUND.getValue(), builder.run());
    }

    /**
     * Test the unsuccessful package build with an invalid file in the INSTALL folder (sys file)
     */
    @Test
    public void test_negative_buildPackageWithInvalidFileInInstall() {
        PackageBuilder builder = new PackageBuilder("src/test/resources/TestPackageWithInvalidFileInInstall/");
        Assert.assertEquals(ExitCode.EXIT_BUILD_ILLEGAL_FILE_FOUND.getValue(), builder.run());
    }

    /**
     * Test the unsuccessful package build with an invalid file in the ROLLBACK folder (cmd file)
     */
    @Test
    public void test_negative_buildPackageWithInvalidFileInRollback() {
        PackageBuilder builder = new PackageBuilder("src/test/resources/TestPackageWithInvalidFileInRollback/");
        Assert.assertEquals(ExitCode.EXIT_BUILD_ILLEGAL_FILE_FOUND.getValue(), builder.run());
    }

    /**
     * Test the unsuccessful package build with an invalid file in the DOWNGRADE folder (very long extension)
     */
    @Test
    public void test_negative_buildPackageWithInvalidFileInDowngrade() {
        PackageBuilder builder = new PackageBuilder("src/test/resources/TestPackageWithInvalidFileInDowngrade/");
        Assert.assertEquals(ExitCode.EXIT_BUILD_ILLEGAL_FILE_FOUND.getValue(), builder.run());
    }

    /**
     * Test the unsuccessful package build with an invalid file in the POSTINSTALL folder
     */
    @Test
    public void test_negative_buildPackageWithInvalidFileInPostinstall() {
        PackageBuilder builder = new PackageBuilder("src/test/resources/TestPackageWithInvalidFileInPostinstall/");
        Assert.assertEquals(ExitCode.EXIT_BUILD_ILLEGAL_FILE_FOUND.getValue(), builder.run());
    }
}
