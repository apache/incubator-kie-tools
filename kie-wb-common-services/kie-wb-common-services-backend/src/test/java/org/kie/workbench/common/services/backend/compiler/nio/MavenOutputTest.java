package org.kie.workbench.common.services.backend.compiler.nio;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
import org.kie.workbench.common.services.backend.compiler.TestUtil;
import org.kie.workbench.common.services.backend.compiler.configuration.Decorator;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.nio.impl.DefaultCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.nio.impl.MavenCompilerFactory;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;

public class MavenOutputTest {

    private Path mavenRepo;

    @Before
    public void setUp() throws Exception {
        mavenRepo = Paths.get(System.getProperty("user.home"),
                              "/.m2/repository");

        if (!Files.exists(mavenRepo)) {
            if (!Files.exists(Files.createDirectories(mavenRepo))) {
                throw new Exception("Folder not writable in the project");
            }
        }
    }

    @Test
    public void testOutputWithTakari() throws Exception {
        Path tmpRoot = Files.createTempDirectory("repo");
        Path tmpNio = Files.createDirectories(Paths.get(tmpRoot.toString(),
                                                        "dummy"));
        TestUtil.copyTree(Paths.get("src/test/projects/dummy"),
                          tmpNio);

        Path tmp = Paths.get(tmpNio.toAbsolutePath().toString());

        AFCompiler compiler = MavenCompilerFactory.getCompiler(Decorator.LOG_OUTPUT_AFTER);

        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(tmp);
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo.toAbsolutePath().toString(),
                                                               info,
                                                               new String[]{MavenCLIArgs.CLEAN, MavenCLIArgs.COMPILE},
                                                               new HashMap<>(),
                                                               Boolean.TRUE);
        CompilationResponse res = compiler.compileSync(req);
        if (res.getMavenOutput().isPresent() && !res.isSuccessful()) {
            TestUtil.writeMavenOutputIntoTargetFolder(res.getMavenOutput().get(),
                                                      "MavenOutputTest.testOutputWithTakari");
        }
        Assert.assertTrue(res.isSuccessful());
        Assert.assertTrue(res.getMavenOutput().isPresent());
        Assert.assertTrue(res.getMavenOutput().get().size() > 0);

        TestUtil.rm(tmpRoot.toFile());
    }
}
