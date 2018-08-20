package org.kie.workbench.common.services.backend.compiler.offprocess.service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.openhft.chronicle.core.io.IOTools;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.workbench.common.services.backend.compiler.CompilationRequest;
import org.kie.workbench.common.services.backend.compiler.TestUtilMaven;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultKieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.WorkspaceCompilationInfo;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.offprocess.CompilerChronicleCoordinatorTest;
import org.kie.workbench.common.services.backend.compiler.offprocess.impl.QueueProvider;
import org.kie.workbench.common.services.backend.compiler.offprocess.service.impl.CompilerOffprocessServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class CompilerOffProcessServiceTest {

    private static Logger logger = LoggerFactory.getLogger(CompilerChronicleCoordinatorTest.class);
    private static Path prjPath;
    private static String mavenRepo;
    private static String alternateSettingsAbsPath;
    private static String queueName = "offprocess-queue-test";
    private static QueueProvider queueProvider;
    private static ExecutorService executor;

    @BeforeClass
    public static void setup() throws Exception{
        executor = Executors.newCachedThreadPool();
        mavenRepo = TestUtilMaven.getMavenRepo();
        System.setProperty("org.uberfire.nio.git.daemon.enabled", "false");
        System.setProperty("org.uberfire.nio.git.ssh.enabled", "false");
        queueProvider = new QueueProvider(queueName);
        logger.info("queue on test setup:{}", queueProvider.getAbsolutePath());
        prjPath = Paths.get("file://"+System.getProperty("user.dir")+"/target/test-classes/kjar-2-single-resources");
        alternateSettingsAbsPath = TestUtilMaven.getSettingsFile();
    }

    @AfterClass
    public static void tearDownClass() {
        System.clearProperty("org.uberfire.nio.git.daemon.enabled");
        System.clearProperty("org.uberfire.nio.git.ssh.enabled");
        IOTools.shallowDeleteDirWithFiles(queueProvider.getAbsolutePath());
    }

    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {

    }

    @Test
    public void offProcessServiceCompileAsyncTest() throws Exception {
        CompilerOffprocessService service = new CompilerOffprocessServiceImpl(executor, queueProvider);
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(prjPath);
        String uuid = UUID.randomUUID().toString();
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               new String[]{
                                                                       MavenCLIArgs.COMPILE,
                                                                       MavenCLIArgs.ALTERNATE_USER_SETTINGS + alternateSettingsAbsPath
                                                               },
                                                               Boolean.FALSE, uuid);

        CompletableFuture<KieCompilationResponse> futureRes =  service.compile(req);
        logger.info("offProcessOneBuildAsyncTest build completed");
        KieCompilationResponse res = futureRes.get();
        assertThat(res).isNotNull();
        assertThat(res.isSuccessful()).isTrue();
        assertThat(res.getMavenOutput()).isNotEmpty();
        DefaultKieCompilationResponse kres = (DefaultKieCompilationResponse) res;
        assertThat(uuid).isEqualToIgnoringCase( kres.getRequestUUID());
    }
}
