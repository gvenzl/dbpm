package com.dbpm.build;

import com.dbpm.logger.Verbose;
import org.junit.Test;

import java.io.File;

public class PackageBuilderTest {

    @Test
    public void test_buildPackage() {
        Verbose.getInstance().setVerbose(true);
        PackageBuilder builder = new PackageBuilder("src/test/resources/TestPackageCorrectFormat/");
        builder.run();
        new File(builder.getPackageName()).delete();
    }
}
