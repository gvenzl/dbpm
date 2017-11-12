package com.dbpm.build;

import com.dbpm.logger.Logger;
import com.dbpm.logger.Verbose;
import org.junit.Test;

import java.io.File;

public class PackageBuilderTest {

    @Test
    public void test_buildPackage() {
        String workDir = new File(System.getProperty("user.dir")).getAbsolutePath();
        System.setProperty("user.dir", System.getProperty("user.dir") + "/src/test/resources/TestPackage/");
        Verbose.getInstance().setVerbose(true);
        PackageBuilder builder = new PackageBuilder();
        builder.run();
        System.setProperty("user.dir", workDir);

    }
}
