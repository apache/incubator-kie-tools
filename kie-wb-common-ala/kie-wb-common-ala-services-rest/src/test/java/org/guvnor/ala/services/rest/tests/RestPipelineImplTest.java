
package org.guvnor.ala.services.rest.tests;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import com.spotify.docker.client.DockerException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;
import org.guvnor.ala.build.Project;
import org.guvnor.ala.build.maven.config.MavenBuildConfig;
import org.guvnor.ala.build.maven.config.MavenProjectConfig;
import org.guvnor.ala.build.maven.config.impl.MavenBuildConfigImpl;
import org.guvnor.ala.build.maven.config.impl.MavenBuildExecConfigImpl;
import org.guvnor.ala.build.maven.config.impl.MavenProjectConfigImpl;
import org.guvnor.ala.build.maven.executor.MavenBuildConfigExecutor;
import org.guvnor.ala.build.maven.executor.MavenBuildExecConfigExecutor;
import org.guvnor.ala.build.maven.executor.MavenProjectConfigExecutor;
import org.guvnor.ala.config.BuildConfig;
import org.guvnor.ala.config.ProjectConfig;
import org.guvnor.ala.config.ProviderConfig;
import org.guvnor.ala.docker.access.DockerAccessInterface;
import org.guvnor.ala.docker.access.impl.DockerAccessInterfaceImpl;
import org.guvnor.ala.docker.config.DockerProviderConfig;
import org.guvnor.ala.docker.config.DockerRuntimeConfig;
import org.guvnor.ala.docker.config.impl.ContextAwareDockerProvisioningConfig;
import org.guvnor.ala.docker.config.impl.ContextAwareDockerRuntimeExecConfig;
import org.guvnor.ala.docker.config.impl.DockerBuildConfigImpl;
import org.guvnor.ala.docker.config.impl.DockerProviderConfigImpl;
import org.guvnor.ala.docker.executor.DockerBuildConfigExecutor;
import org.guvnor.ala.docker.executor.DockerProviderConfigExecutor;
import org.guvnor.ala.docker.executor.DockerProvisioningConfigExecutor;
import org.guvnor.ala.docker.executor.DockerRuntimeExecExecutor;
import org.guvnor.ala.docker.model.DockerProvider;
import org.guvnor.ala.docker.model.DockerProviderType;
import org.guvnor.ala.docker.model.DockerRuntime;
import org.guvnor.ala.docker.service.DockerRuntimeManager;
import org.guvnor.ala.pipeline.BiFunctionConfigExecutor;
import org.guvnor.ala.pipeline.ConfigExecutor;
import org.guvnor.ala.pipeline.FunctionConfigExecutor;
import org.guvnor.ala.pipeline.Input;
import org.guvnor.ala.pipeline.PipelineConfigStage;
import org.guvnor.ala.pipeline.execution.PipelineExecutor;
import org.guvnor.ala.pipeline.execution.PipelineExecutorTask;
import org.guvnor.ala.pipeline.execution.impl.PipelineExecutorTaskManagerImpl;
import org.guvnor.ala.pipeline.impl.PipelineConfigImpl;
import org.guvnor.ala.registry.RuntimeRegistry;
import org.guvnor.ala.registry.inmemory.InMemoryBuildRegistry;
import org.guvnor.ala.registry.inmemory.InMemoryPipelineExecutorRegistry;
import org.guvnor.ala.registry.inmemory.InMemoryPipelineRegistry;
import org.guvnor.ala.registry.inmemory.InMemoryRuntimeRegistry;
import org.guvnor.ala.registry.inmemory.InMemorySourceRegistry;
import org.guvnor.ala.runtime.RuntimeBuilder;
import org.guvnor.ala.runtime.RuntimeDestroyer;
import org.guvnor.ala.runtime.RuntimeManager;
import org.guvnor.ala.runtime.providers.Provider;
import org.guvnor.ala.runtime.providers.ProviderBuilder;
import org.guvnor.ala.runtime.providers.ProviderType;
import org.guvnor.ala.services.api.PipelineService;
import org.guvnor.ala.services.api.RuntimeProvisioningService;
import org.guvnor.ala.services.api.RuntimeQueryBuilder;
import org.guvnor.ala.services.api.itemlist.PipelineConfigsList;
import org.guvnor.ala.services.api.itemlist.ProviderList;
import org.guvnor.ala.services.api.itemlist.ProviderTypeList;
import org.guvnor.ala.services.api.itemlist.RuntimeList;
import org.guvnor.ala.services.api.itemlist.RuntimeQueryResultItemList;
import org.guvnor.ala.services.rest.RestPipelineServiceImpl;
import org.guvnor.ala.services.rest.RestRuntimeProvisioningServiceImpl;
import org.guvnor.ala.services.rest.factories.ProviderFactory;
import org.guvnor.ala.services.rest.factories.RuntimeFactory;
import org.guvnor.ala.services.rest.factories.RuntimeManagerFactory;
import org.guvnor.ala.source.Source;
import org.guvnor.ala.source.git.config.impl.GitConfigImpl;
import org.guvnor.ala.source.git.executor.GitConfigExecutor;
import org.guvnor.ala.wildfly.executor.WildflyProviderConfigExecutor;
import org.guvnor.ala.wildfly.model.WildflyProviderType;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.commons.lifecycle.Disposable;

import static org.guvnor.ala.services.rest.tests.MockSystemPipelines.SYSTEM_PIPELINE1;
import static org.junit.Assert.*;

/**
 * Test that shows how to work with the Pipeline
 */
@RunWith(Arquillian.class)
public class RestPipelineImplTest {

    @Inject
    private PipelineService pipelineService;

    @Inject
    private RuntimeProvisioningService runtimeService;

    private File tempPath;

    @Inject
    private MockPipelineEventListener listener;

    @Deployment()
    public static Archive createDeployment() throws Exception {
        JavaArchive deployment = ShrinkWrap.create(JavaArchive.class);
        deployment.addClass(PipelineService.class);
        deployment.addClass(RestPipelineServiceImpl.class);
        deployment.addClass(PipelineExecutor.class);
        deployment.addClass(InMemoryPipelineRegistry.class);
        deployment.addClass(InMemoryBuildRegistry.class);
        deployment.addClass(InMemorySourceRegistry.class);
        deployment.addClass(DockerProviderConfigExecutor.class);
        deployment.addClass(WildflyProviderConfigExecutor.class);
        deployment.addClass(RestRuntimeProvisioningServiceImpl.class);
        deployment.addClass(RuntimeRegistry.class);
        deployment.addClass(InMemoryRuntimeRegistry.class);
        deployment.addClass(RuntimeRegistry.class);
        deployment.addClass(ProviderFactory.class);
        deployment.addClass(RuntimeFactory.class);
        deployment.addClass(RuntimeManagerFactory.class);
        deployment.addClass(DockerProviderType.class);
        deployment.addClass(DockerProviderConfig.class);
        deployment.addClass(DockerProvider.class);
        deployment.addClass(WildflyProviderType.class);
        deployment.addClass(ProviderBuilder.class);
        deployment.addClass(ProviderType.class);
        deployment.addClass(FunctionConfigExecutor.class);
        deployment.addClass(ConfigExecutor.class);
        deployment.addClass(ProviderConfig.class);
        deployment.addClass(Provider.class);
        deployment.addClass(DockerRuntimeConfig.class);
        deployment.addClass(DockerRuntime.class);
        deployment.addClass(RuntimeBuilder.class);
        deployment.addClass(DockerRuntimeExecExecutor.class);
        deployment.addClass(RuntimeDestroyer.class);
        deployment.addClass(DockerAccessInterface.class);
        deployment.addClass(DockerAccessInterfaceImpl.class);
        deployment.addClass(Disposable.class);
        deployment.addClass(DockerException.class);
        deployment.addClass(DockerRuntimeManager.class);
        deployment.addClass(RuntimeManager.class);
        deployment.addClass(org.guvnor.ala.config.Config.class);
        deployment.addClass(org.guvnor.ala.config.ProviderConfig.class);
        deployment.addClass(org.guvnor.ala.docker.config.DockerProviderConfig.class);
        deployment.addClass(org.guvnor.ala.docker.model.DockerProvider.class);
        deployment.addClass(org.guvnor.ala.pipeline.FunctionConfigExecutor.class);
        deployment.addClass(BiFunctionConfigExecutor.class);
        deployment.addClass(org.guvnor.ala.registry.RuntimeRegistry.class);
        deployment.addClass(org.guvnor.ala.runtime.providers.ProviderBuilder.class);
        deployment.addClass(org.guvnor.ala.runtime.providers.ProviderDestroyer.class);
        deployment.addClass(org.guvnor.ala.runtime.providers.ProviderId.class);

        deployment.addClass(GitConfigExecutor.class);
        deployment.addClass(MavenProjectConfig.class);
        deployment.addClass(MavenProjectConfigExecutor.class);
        deployment.addClass(Project.class);
        deployment.addClass(Source.class);
        deployment.addClass(ProjectConfig.class);
        deployment.addClass(BuildConfig.class);
        deployment.addClass(MavenBuildConfig.class);
        deployment.addClass(MavenBuildConfigExecutor.class);
        deployment.addClass(MavenBuildExecConfigExecutor.class);
        deployment.addClass(DockerBuildConfigExecutor.class);
        deployment.addClass(DockerProviderConfigExecutor.class);
        deployment.addClass(DockerProvisioningConfigExecutor.class);

        deployment.addClass(MockPipelineEventListener.class);

        deployment.addClass(PipelineExecutorTaskManagerImpl.class);
        deployment.addClass(InMemoryPipelineExecutorRegistry.class);

        deployment.addClass(MockSystemPipelines.class);

        deployment.addAsManifestResource(EmptyAsset.INSTANCE,
                                         "beans.xml");
        return deployment;
    }

    @BeforeClass
    public static void beforeClass() {
        Assume.assumeFalse(SystemUtils.IS_OS_WINDOWS);
    }

    @Before
    public void setUp() throws IOException {
        tempPath = Files.createTempDirectory("xxx").toFile();
    }

    @After
    public void tearDown() {
        FileUtils.deleteQuietly(tempPath);
    }

    @Test
    public void testAPI() {

        ProviderTypeList allProviderTypes = runtimeService.getProviderTypes(0,
                                                                            10,
                                                                            "",
                                                                            true);

        assertEquals(2,
                     allProviderTypes.getItems().size());
        DockerProviderConfig dockerProviderConfig = new DockerProviderConfig() {
        };
        runtimeService.registerProvider(dockerProviderConfig);

        ProviderList allProviders = runtimeService.getProviders(0,
                                                                10,
                                                                "",
                                                                true);

        assertEquals(1,
                     allProviders.getItems().size());

        Provider p = allProviders.getItems().get(0);
        assertTrue(p instanceof DockerProvider);

        int systemPipelines = 2; //by construction.
        PipelineConfigsList allPipelineConfigs = pipelineService.getPipelineConfigs(0,
                                                                                    10,
                                                                                    "",
                                                                                    true);

        assertNotNull(allPipelineConfigs);
        assertEquals(systemPipelines,
                     allPipelineConfigs.getItems().size());

        List<PipelineConfigStage> configs = new ArrayList<>();
        configs.add(new PipelineConfigStage("GitConfig",
                                            new GitConfigImpl()));
        configs.add(new PipelineConfigStage("MavenProjectConfig",
                                            new MavenProjectConfigImpl()));
        configs.add(new PipelineConfigStage("MavenBuildConfig",
                                            new MavenBuildConfigImpl()));
        configs.add(new PipelineConfigStage("DockerBuildConfig",
                                            new DockerBuildConfigImpl()));
        configs.add(new PipelineConfigStage("MavenBuildExecConfigImpl",
                                            new MavenBuildExecConfigImpl()));
        configs.add(new PipelineConfigStage("DockerProviderConfig",
                                            new DockerProviderConfigImpl()));
        configs.add(new PipelineConfigStage("ContextAwareDockerProvisioningConfig",
                                            new ContextAwareDockerProvisioningConfig()));
        configs.add(new PipelineConfigStage("ContextAwareDockerRuntimeExecConfig",
                                            new ContextAwareDockerRuntimeExecConfig()));

        pipelineService.newPipeline(new PipelineConfigImpl("mypipe",
                                                           configs));

        pipelineService.newPipeline(new PipelineConfigImpl("wildlfyPipe",
                                                           configs),
                                    WildflyProviderType.instance());

        pipelineService.newPipeline(new PipelineConfigImpl("dockerPipe",
                                                           configs),
                                    DockerProviderType.instance());

        allPipelineConfigs = pipelineService.getPipelineConfigs(0,
                                                                10,
                                                                "",
                                                                true);
        int createdPipelines = 3;
        int totalPipelines = systemPipelines + createdPipelines;
        assertEquals(totalPipelines,
                     allPipelineConfigs.getItems().size());

        PipelineConfigsList wildflyConfigs = pipelineService.getPipelineConfigs(WildflyProviderType.instance().getProviderTypeName(),
                                                                                WildflyProviderType.instance().getVersion(),
                                                                                0,
                                                                                10,
                                                                                "",
                                                                                true);
        assertEquals(2,
                     wildflyConfigs.getItems().size());

        List<String> wildflyPipelineNames = pipelineService.getPipelineNames(WildflyProviderType.instance().getProviderTypeName(),
                                                                             WildflyProviderType.instance().getVersion(),
                                                                             0,
                                                                             10,
                                                                             "",
                                                                             true);
        assertEquals(2,
                     wildflyPipelineNames.size());
        assertTrue(wildflyPipelineNames.contains("wildlfyPipe"));
        assertTrue(wildflyPipelineNames.contains(SYSTEM_PIPELINE1));

        PipelineConfigsList dockerConfigs = pipelineService.getPipelineConfigs(DockerProviderType.instance().getProviderTypeName(),
                                                                               DockerProviderType.instance().getVersion(),
                                                                               0,
                                                                               10,
                                                                               "",
                                                                               true);
        assertEquals(1,
                     dockerConfigs.getItems().size());

        List<String> dockerPipelineNames = pipelineService.getPipelineNames(DockerProviderType.instance().getProviderTypeName(),
                                                                            DockerProviderType.instance().getVersion(),
                                                                            0,
                                                                            10,
                                                                            "",
                                                                            true);

        assertEquals(1,
                     dockerPipelineNames.size());
        assertTrue(dockerPipelineNames.contains("dockerPipe"));

        Input input = new Input();

        input.put("provider-name",
                  "local");
        input.put("repo-name",
                  "drools-workshop");
        input.put("create-repo",
                  "true");
        input.put("branch",
                  "master");
        input.put("out-dir",
                  tempPath.getAbsolutePath());
        input.put("origin",
                  "https://github.com/kiegroup/drools-workshop");
        input.put("project-dir",
                  "drools-webapp-example");

        String pipelineExecutionId = pipelineService.runPipeline("mypipe",
                                                                 input,
                                                                 false);

        RuntimeQueryResultItemList itemList = runtimeService.executeQuery(RuntimeQueryBuilder.newInstance()
                                                                                  .withPipelineExecutionId(pipelineExecutionId)
                                                                                  .build());
        assertEquals(1,
                     itemList.getItems().size());
        assertEquals(pipelineExecutionId,
                     itemList.getItems().get(0).getPipelineExecutionId());

        pipelineService.deletePipelineExecution(pipelineExecutionId);
        itemList = runtimeService.executeQuery(RuntimeQueryBuilder.newInstance()
                                                       .withPipelineExecutionId(pipelineExecutionId)
                                                       .build());
        assertEquals(1,
                     itemList.getItems().size());
        assertNull(itemList.getItems().get(0).getPipelineExecutionId());

        RuntimeList allRuntimes = runtimeService.getRuntimes(0,
                                                             10,
                                                             "",
                                                             true);

        assertEquals(1,
                     allRuntimes.getItems().size());

        runtimeService.destroyRuntime(allRuntimes.getItems().get(0).getId(),
                                      true);

        allRuntimes = runtimeService.getRuntimes(0,
                                                 10,
                                                 "",
                                                 true);

        assertEquals(0,
                     allRuntimes.getItems().size());

        assertEquals(18,
                     listener.getEvents().size()); // 8 Stages x 2 + 2 pipeline events

        pipelineExecutionId = pipelineService.runPipeline("mypipe",
                                                          input,
                                                          true);
        pipelineService.stopPipelineExecution(pipelineExecutionId);

        itemList = runtimeService.executeQuery(RuntimeQueryBuilder.newInstance()
                                                       .withPipelineExecutionId(pipelineExecutionId)
                                                       .build());
        assertEquals(1,
                     itemList.getItems().size());
        assertEquals(pipelineExecutionId,
                     itemList.getItems().get(0).getPipelineExecutionId());
        assertEquals(PipelineExecutorTask.Status.STOPPED.name(),
                     itemList.getItems().get(0).getPipelineStatus());
    }
}
