package org.guvnor.ala.services.rest.tests;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import com.spotify.docker.client.DockerException;
import org.apache.commons.io.FileUtils;
import org.guvnor.ala.build.Project;
import org.guvnor.ala.build.maven.config.MavenBuildConfig;
import org.guvnor.ala.build.maven.config.MavenProjectConfig;
import org.guvnor.ala.build.maven.config.impl.MavenProjectConfigImpl;
import org.guvnor.ala.build.maven.executor.MavenBuildConfigExecutor;
import org.guvnor.ala.build.maven.executor.MavenBuildExecConfigExecutor;
import org.guvnor.ala.build.maven.executor.MavenProjectConfigExecutor;
import org.guvnor.ala.build.maven.executor.MavenTestUtils;
import org.guvnor.ala.config.BuildConfig;
import org.guvnor.ala.config.ProjectConfig;
import org.guvnor.ala.config.ProviderConfig;
import org.guvnor.ala.docker.access.DockerAccessInterface;
import org.guvnor.ala.docker.access.impl.DockerAccessInterfaceImpl;
import org.guvnor.ala.docker.config.DockerProviderConfig;
import org.guvnor.ala.docker.config.DockerRuntimeConfig;
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
import org.guvnor.ala.pipeline.events.AfterPipelineExecutionEvent;
import org.guvnor.ala.pipeline.events.AfterStageExecutionEvent;
import org.guvnor.ala.pipeline.events.BeforePipelineExecutionEvent;
import org.guvnor.ala.pipeline.events.BeforeStageExecutionEvent;
import org.guvnor.ala.pipeline.execution.PipelineExecutor;
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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.commons.lifecycle.Disposable;

import static org.junit.Assert.*;

/**
 * Test that shows how to work with the Pipeline
 */
@RunWith(Arquillian.class)
public class RestPipelineEventsTest {

    @Inject
    private PipelineService pipelineService;

    private File tempPath;

    private String gitUrl;

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

        deployment.addAsManifestResource(EmptyAsset.INSTANCE,
                                         "beans.xml");
        return deployment;
    }

    @Before
    public void setUp() throws Exception {
        tempPath = Files.createTempDirectory("zzz").toFile();
        gitUrl = MavenTestUtils.createGitRepoWithPom(tempPath);
    }

    @After
    public void tearDown() {
        FileUtils.deleteQuietly(tempPath);
    }

    @Test
    public void testEventsPropagation() {

        List<PipelineConfigStage> configs = new ArrayList<>();
        configs.add(new PipelineConfigStage("GitConfig",
                                            new GitConfigImpl()));
        configs.add(new PipelineConfigStage("MavenProjectConfig",
                                            new MavenProjectConfigImpl()));

        pipelineService.newPipeline(new PipelineConfigImpl("mypipe",
                                                           configs));

        Input input = new Input();

        input.put("repo-name",
                  "drools-workshop-events");
        input.put("create-repo",
                  "true");
        input.put("branch",
                  "master");
        input.put("out-dir",
                  tempPath.getAbsolutePath());
        input.put("origin",
                  gitUrl);

        pipelineService.runPipeline("mypipe",
                                    input,
                                    false);

        assertEquals(6,
                     listener.getEvents().size());
        assertTrue(listener.getEvents().get(0) instanceof BeforePipelineExecutionEvent);
        assertTrue(listener.getEvents().get(1) instanceof BeforeStageExecutionEvent);
        assertTrue(listener.getEvents().get(2) instanceof AfterStageExecutionEvent);
        assertTrue(listener.getEvents().get(3) instanceof BeforeStageExecutionEvent);
        assertTrue(listener.getEvents().get(4) instanceof AfterStageExecutionEvent);
        assertTrue(listener.getEvents().get(5) instanceof AfterPipelineExecutionEvent);
    }
}
