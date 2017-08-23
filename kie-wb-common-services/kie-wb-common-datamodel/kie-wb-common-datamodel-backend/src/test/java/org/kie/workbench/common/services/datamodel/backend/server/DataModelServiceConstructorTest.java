/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.kie.workbench.common.services.datamodel.backend.server;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Predicate;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;

import org.appformer.project.datamodel.oracle.ProjectDataModelOracle;
import org.appformer.project.datamodel.oracle.TypeSource;
import org.guvnor.ala.pipeline.ConfigExecutor;
import org.guvnor.ala.registry.PipelineRegistry;
import org.guvnor.ala.registry.inmemory.InMemoryPipelineRegistry;
import org.guvnor.common.services.backend.file.FileDiscoveryService;
import org.guvnor.common.services.backend.file.FileDiscoveryServiceImpl;
import org.guvnor.common.services.backend.metadata.MetadataServerSideService;
import org.guvnor.common.services.backend.metadata.MetadataServiceImpl;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.backend.util.CommentedOptionFactoryImpl;
import org.guvnor.common.services.project.backend.server.POMServiceImpl;
import org.guvnor.common.services.project.backend.server.ProjectConfigurationContentHandler;
import org.guvnor.common.services.project.backend.server.ProjectRepositoriesContentHandler;
import org.guvnor.common.services.project.backend.server.ProjectRepositoryResolverImpl;
import org.guvnor.common.services.project.backend.server.ProjectResourcePathResolver;
import org.guvnor.common.services.project.backend.server.utils.POMContentHandler;
import org.guvnor.common.services.project.builder.events.InvalidateDMOProjectCacheEvent;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.builder.service.BuildValidationHelper;
import org.guvnor.common.services.project.builder.service.PostBuildHandler;
import org.guvnor.common.services.project.events.NewPackageEvent;
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.events.RenameProjectEvent;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.project.service.ProjectRepositoriesService;
import org.guvnor.common.services.project.service.ProjectRepositoryResolver;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.m2repo.backend.server.M2RepoServiceImpl;
import org.guvnor.structure.backend.backcompat.BackwardCompatibleUtil;
import org.guvnor.structure.backend.config.ConfigGroupMarshaller;
import org.guvnor.structure.backend.config.ConfigurationFactoryImpl;
import org.guvnor.structure.backend.config.ConfigurationServiceImpl;
import org.guvnor.structure.backend.config.DefaultPasswordServiceImpl;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.guvnor.structure.security.RepositoryAction;
import org.guvnor.structure.server.config.ConfigurationFactory;
import org.guvnor.structure.server.config.ConfigurationService;
import org.guvnor.structure.server.config.PasswordService;
import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.backend.builder.ala.BuildPipelineInitializer;
import org.kie.workbench.common.services.backend.builder.ala.BuildPipelineInvoker;
import org.kie.workbench.common.services.backend.builder.ala.LocalBuildConfigExecutor;
import org.kie.workbench.common.services.backend.builder.ala.LocalBuildExecConfigExecutor;
import org.kie.workbench.common.services.backend.builder.ala.LocalProjectConfigExecutor;
import org.kie.workbench.common.services.backend.builder.ala.LocalSourceConfigExecutor;
import org.kie.workbench.common.services.backend.builder.core.BuildHelper;
import org.kie.workbench.common.services.backend.builder.core.DeploymentVerifier;
import org.kie.workbench.common.services.backend.builder.core.LRUBuilderCache;
import org.kie.workbench.common.services.backend.builder.core.LRUPomModelCache;
import org.kie.workbench.common.services.backend.builder.core.LRUProjectDependenciesClassLoaderCache;
import org.kie.workbench.common.services.backend.builder.service.BuildInfoService;
import org.kie.workbench.common.services.backend.builder.service.BuildServiceHelper;
import org.kie.workbench.common.services.backend.builder.service.BuildServiceImpl;
import org.kie.workbench.common.services.backend.dependencies.DependencyServiceImpl;
import org.kie.workbench.common.services.backend.kmodule.KModuleContentHandler;
import org.kie.workbench.common.services.backend.kmodule.KModuleServiceImpl;
import org.kie.workbench.common.services.backend.project.KieProjectRepositoriesServiceImpl;
import org.kie.workbench.common.services.backend.project.KieProjectServiceImpl;
import org.kie.workbench.common.services.backend.project.KieResourceResolver;
import org.kie.workbench.common.services.backend.project.ProjectImportsServiceImpl;
import org.kie.workbench.common.services.backend.project.ProjectSaver;
import org.kie.workbench.common.services.backend.whitelist.PackageNameSearchProvider;
import org.kie.workbench.common.services.backend.whitelist.PackageNameWhiteListLoader;
import org.kie.workbench.common.services.backend.whitelist.PackageNameWhiteListSaver;
import org.kie.workbench.common.services.backend.whitelist.PackageNameWhiteListServiceImpl;
import org.kie.workbench.common.services.datamodel.backend.server.cache.LRUDataModelOracleCache;
import org.kie.workbench.common.services.datamodel.backend.server.cache.LRUProjectDataModelOracleCache;
import org.kie.workbench.common.services.datamodel.backend.server.cache.ProjectDataModelOracleBuilderProvider;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.kie.workbench.common.services.shared.dependencies.DependencyService;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.kie.workbench.common.services.shared.project.ProjectImportsService;
import org.kie.workbench.common.services.shared.whitelist.PackageNameWhiteListService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.io.ConfigIOServiceProducer;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.io.impl.IOServiceDotFileImpl;
import org.uberfire.java.nio.file.FileSystemNotFoundException;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.rpc.impl.SessionInfoImpl;
import org.uberfire.security.ResourceType;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.security.authz.PermissionTypeRegistry;
import org.uberfire.security.impl.authz.DefaultAuthorizationManager;
import org.uberfire.security.impl.authz.DefaultPermissionManager;
import org.uberfire.security.impl.authz.DefaultPermissionTypeRegistry;
import org.uberfire.security.impl.authz.DotNamedPermissionType;

import static org.junit.Assert.*;
import static org.kie.workbench.common.services.datamodel.backend.server.ProjectDataModelOracleTestUtils.*;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class DataModelServiceConstructorTest {

    private SimpleFileSystemProvider fs = new SimpleFileSystemProvider();

    private ResourceType REPOSITORY_TYPE = Repository.RESOURCE_TYPE;

    @Mock
    private Instance<ProjectResourcePathResolver > resourcePathResolversInstance;

    private class HackedKModuleServiceImpl extends KModuleServiceImpl {

        public HackedKModuleServiceImpl(IOService ioService,
                                        KieProjectServiceImpl projectService,
                                        MetadataService metadataService,
                                        KModuleContentHandler moduleContentHandler) {
            super(ioService,
                  projectService,
                  metadataService,
                  moduleContentHandler);
        }

        @Override
        public void setProjectService(KieProjectService projectService) {
            super.setProjectService(projectService);
        }
    }

    private class HackedLRUProjectDependenciesClassLoaderCache extends LRUProjectDependenciesClassLoaderCache {

        @Override
        public void setBuildInfoService(BuildInfoService buildInfoService) {
            super.setBuildInfoService(buildInfoService);
        }
    }

    private class HackedKieProjectServiceImpl extends KieProjectServiceImpl {

        public HackedKieProjectServiceImpl(IOService ioService,
                                           ProjectSaver projectSaver,
                                           POMService pomService,
                                           ConfigurationService configurationService,
                                           ConfigurationFactory configurationFactory,
                                           Event<NewProjectEvent> newProjectEvent,
                                           Event<NewPackageEvent> newPackageEvent,
                                           Event<RenameProjectEvent> renameProjectEvent,
                                           Event<InvalidateDMOProjectCacheEvent> invalidateDMOCache,
                                           SessionInfo sessionInfo,
                                           AuthorizationManager authorizationManager,
                                           BackwardCompatibleUtil backward,
                                           CommentedOptionFactory commentedOptionFactory,
                                           KieResourceResolver resourceResolver,
                                           ProjectRepositoryResolver repositoryResolver) {
            super(ioService,
                  projectSaver,
                  pomService,
                  configurationService,
                  configurationFactory,
                  newProjectEvent,
                  newPackageEvent,
                  renameProjectEvent,
                  invalidateDMOCache,
                  sessionInfo,
                  authorizationManager,
                  backward,
                  commentedOptionFactory,
                  resourceResolver,
                  repositoryResolver
            );
        }

        @Override
        public void setProjectSaver(ProjectSaver projectSaver) {
            super.setProjectSaver(projectSaver);
        }
    }

    @Test
    public void testConstructor()
            throws IllegalArgumentException, FileSystemNotFoundException, SecurityException, URISyntaxException {

        final URL packageUrl = this.getClass().getResource("/DataModelServiceConstructorTest/src/main/java/t1p1");

        IOService ioService = new IOServiceDotFileImpl();
        Collection<Role> roles = new ArrayList<>();
        Collection<Group> groups = new ArrayList<>();
        User user = new UserImpl("admin",
                                 roles,
                                 groups);
        SessionInfo sessionInfo = new SessionInfoImpl("admin",
                                                      user);
        Instance<User> userInstance = mock( Instance.class );
        when( userInstance.get() ).thenReturn( user );

        ConfigIOServiceProducer cfiosProducer = new ConfigIOServiceProducer();
        cfiosProducer.setup();
        IOService configIOService = cfiosProducer.configIOService();
        MetadataService metadataService = new MetadataServiceImpl(ioService,
                                                                  configIOService,
                                                                  sessionInfo);

        POMContentHandler pomContentHandler = new POMContentHandler();
        M2RepoServiceImpl m2RepoService = new M2RepoServiceImpl();
        POMService pomService = new POMServiceImpl(ioService,
                                                   pomContentHandler,
                                                   m2RepoService,
                                                   metadataService);
        KModuleContentHandler moduleContentHandler = new KModuleContentHandler();
        PasswordService secureService = new DefaultPasswordServiceImpl();
        ConfigurationFactory configurationFactory = new ConfigurationFactoryImpl(secureService);

        org.guvnor.structure.repositories.Repository systemRepository = new GitRepository("system");
        ConfigGroupMarshaller marshaller = new ConfigGroupMarshaller();
        ConfigurationService configurationService = new ConfigurationServiceImpl(systemRepository,
                                                                                 marshaller,
                                                                                 user,
                                                                                 ioService,
                                                                                 new EventSourceMock<>(),
                                                                                 new EventSourceMock<>(),
                                                                                 new EventSourceMock<>(),
                                                                                 fs.getFileSystem(packageUrl.toURI()));

        CommentedOptionFactory commentedOptionFactory = new CommentedOptionFactoryImpl(sessionInfo);
        BackwardCompatibleUtil backward = new BackwardCompatibleUtil(configurationFactory);
        ProjectConfigurationContentHandler projectConfigurationContentHandler = new ProjectConfigurationContentHandler();
        ProjectImportsService projectImportsService = new ProjectImportsServiceImpl(ioService,
                                                                                    projectConfigurationContentHandler);

        Event<NewProjectEvent> newProjectEvent = new EventSourceMock<>();
        Event<NewPackageEvent> newPackageEvent = new EventSourceMock<>();
        Event<RenameProjectEvent> renameProjectEvent = new EventSourceMock<>();
        Event<InvalidateDMOProjectCacheEvent> invalidateDMOCache = new EventSourceMock<>();

        PermissionTypeRegistry permissionTypeRegistry = new DefaultPermissionTypeRegistry();
        DotNamedPermissionType permissionType = new DotNamedPermissionType(REPOSITORY_TYPE.getName());
        permissionType.createPermission(REPOSITORY_TYPE,
                                        RepositoryAction.READ,
                                        true);
        permissionType.createPermission(REPOSITORY_TYPE,
                                        RepositoryAction.CREATE,
                                        true);
        permissionType.createPermission(REPOSITORY_TYPE,
                                        RepositoryAction.UPDATE,
                                        true);
        permissionType.createPermission(REPOSITORY_TYPE,
                                        RepositoryAction.DELETE,
                                        true);
        permissionTypeRegistry.register(permissionType);
        PermissionManager permissionManager = new DefaultPermissionManager();

        AuthorizationManager authorizationManager = new DefaultAuthorizationManager(permissionManager);

        ProjectRepositoryResolver repositoryResolver = new ProjectRepositoryResolverImpl(ioService,
                                                                                         null,
                                                                                         null);

        FileDiscoveryService fileDiscoveryService = new FileDiscoveryServiceImpl();

        HackedKieProjectServiceImpl projectService = null;
        HackedKModuleServiceImpl kModuleService = new HackedKModuleServiceImpl(ioService,
                                                                               projectService,
                                                                               metadataService,
                                                                               moduleContentHandler);
        KieResourceResolver resourceResolver = new KieResourceResolver(ioService,
                                                                       pomService,
                                                                       configurationService,
                                                                       commentedOptionFactory,
                                                                       backward,
                                                                       kModuleService,
                                                                       resourcePathResolversInstance ) {
            @Override
            protected void addSecurityGroups(final KieProject project) {
                //Do nothing. This test demonstrating DMO usage without WELD does not use permissions.
            }
        };
        ProjectSaver projectSaver = null;
        projectService = new HackedKieProjectServiceImpl(ioService,
                                                         projectSaver,
                                                         pomService,
                                                         configurationService,
                                                         configurationFactory,
                                                         newProjectEvent,
                                                         newPackageEvent,
                                                         renameProjectEvent,
                                                         invalidateDMOCache,
                                                         sessionInfo,
                                                         authorizationManager,
                                                         backward,
                                                         commentedOptionFactory,
                                                         resourceResolver,
                                                         repositoryResolver);

        ProjectRepositoriesContentHandler contentHandler = new ProjectRepositoriesContentHandler();
        ProjectRepositoriesService projectRepositoriesService = new KieProjectRepositoriesServiceImpl(ioService,
                                                                                                      repositoryResolver,
                                                                                                      resourceResolver,
                                                                                                      contentHandler,
                                                                                                      commentedOptionFactory);

        DependencyService dependencyService = new DependencyServiceImpl();
        PackageNameSearchProvider packageNameSearchProvider = new PackageNameSearchProvider(dependencyService);
        PackageNameWhiteListLoader loader = new PackageNameWhiteListLoader(packageNameSearchProvider,
                                                                           ioService);
        MetadataServerSideService serverSideMetdataService = new MetadataServiceImpl(ioService,
                                                                                     configIOService,
                                                                                     sessionInfo);
        PackageNameWhiteListSaver saver = new PackageNameWhiteListSaver(ioService,
                                                                        serverSideMetdataService,
                                                                        commentedOptionFactory);
        PackageNameWhiteListService packageNameWhiteListService = new PackageNameWhiteListServiceImpl(ioService,
                                                                                                      projectService,
                                                                                                      loader,
                                                                                                      saver);

        projectSaver = new ProjectSaver(ioService,
                                        pomService,
                                        kModuleService,
                                        newProjectEvent,
                                        newPackageEvent,
                                        resourceResolver,
                                        projectImportsService,
                                        projectRepositoriesService,
                                        packageNameWhiteListService,
                                        commentedOptionFactory,
                                        sessionInfo);
        projectService.setProjectSaver(projectSaver);
        kModuleService.setProjectService(projectService);

        ProjectImportsService importsService = new ProjectImportsServiceImpl(ioService,
                                                                             projectConfigurationContentHandler);
        Instance<BuildValidationHelper> buildValidationHelperBeans = null;
        Instance<Predicate<String>> classFilterBeans = null;
        HackedLRUProjectDependenciesClassLoaderCache dependenciesClassLoaderCache = new HackedLRUProjectDependenciesClassLoaderCache();
        LRUPomModelCache pomModelCache = new LRUPomModelCache();
        LRUBuilderCache builderCache = new LRUBuilderCache(ioService,
                                                           projectService,
                                                           importsService,
                                                           buildValidationHelperBeans,
                                                           dependenciesClassLoaderCache,
                                                           pomModelCache,
                                                           packageNameWhiteListService,
                                                           classFilterBeans
        );

        Instance< PostBuildHandler > handlerInstance = mock( Instance.class );
        Iterator<PostBuildHandler> mockIterator = mock( Iterator.class );
        when( handlerInstance.iterator() ).thenReturn( mockIterator );
        when ( mockIterator.hasNext() ).thenReturn( false );

        DeploymentVerifier deploymentVerifier = new DeploymentVerifier( repositoryResolver, projectRepositoriesService );
        BuildHelper buildHelper = new BuildHelper( pomService,
                                                    m2RepoService,
                                                    projectService,
                                                    deploymentVerifier,
                                                    builderCache,
                                                    handlerInstance,
                                                    userInstance );
        PipelineRegistry pipelineRegistry = new InMemoryPipelineRegistry();
        BuildPipelineInitializer pipelineInitializer = new BuildPipelineInitializer( pipelineRegistry,
                getConfigExecutors( projectService, buildHelper ) );
        BuildPipelineInvoker pipelineInvoker = new BuildPipelineInvoker( pipelineInitializer.getExecutor(), pipelineRegistry  );

        BuildServiceHelper buildServiceHelper = new BuildServiceHelper( pipelineInvoker, deploymentVerifier );
        BuildService buildService = new BuildServiceImpl( projectService, buildServiceHelper, builderCache );
        BuildInfoService buildInfoService = new BuildInfoService( buildService, builderCache );

        ProjectDataModelOracleBuilderProvider builderProvider = new ProjectDataModelOracleBuilderProvider(packageNameWhiteListService,
                                                                                                          importsService);

        LRUProjectDataModelOracleCache cacheProjects = new LRUProjectDataModelOracleCache(builderProvider,
                                                                                          projectService,
                                                                                          buildInfoService );

        dependenciesClassLoaderCache.setBuildInfoService( buildInfoService );
        LRUDataModelOracleCache cachePackages = new LRUDataModelOracleCache(ioService,
                                                                            fileDiscoveryService,
                                                                            cacheProjects,
                                                                            projectService,
                                                                            buildInfoService );
        DataModelService dataModelService = new DataModelServiceImpl(cachePackages,
                                                                     cacheProjects,
                                                                     projectService);

        final org.uberfire.java.nio.file.Path nioPackagePath = fs.getPath(packageUrl.toURI());
        final Path packagePath = Paths.convert(nioPackagePath);

        final ProjectDataModelOracle oracle = dataModelService.getProjectDataModel(packagePath);

        assertNotNull(oracle);

        assertEquals(4,
                     oracle.getProjectModelFields().size());
        assertContains("t1p1.Bean1",
                       oracle.getProjectModelFields().keySet());
        assertContains("t1p1.DRLBean",
                       oracle.getProjectModelFields().keySet());
        assertContains("t1p2.Bean2",
                       oracle.getProjectModelFields().keySet());
        assertContains("java.lang.String",
                       oracle.getProjectModelFields().keySet());

        assertEquals(TypeSource.JAVA_PROJECT,
                     oracle.getProjectTypeSources().get("t1p1.Bean1"));
        assertEquals(TypeSource.DECLARED,
                     oracle.getProjectTypeSources().get("t1p1.DRLBean"));
        assertEquals(TypeSource.JAVA_PROJECT,
                     oracle.getProjectTypeSources().get("t1p2.Bean2"));
        assertEquals(TypeSource.JAVA_PROJECT,
                     oracle.getProjectTypeSources().get("java.lang.String"));
    }

    private Collection< ConfigExecutor > getConfigExecutors( KieProjectService projectService, BuildHelper buildHelper ) {
        Collection< ConfigExecutor > configs = new ArrayList<>( );
        configs.add( new LocalSourceConfigExecutor() );
        configs.add( new LocalProjectConfigExecutor( projectService ) );
        configs.add( new LocalBuildConfigExecutor() );
        configs.add( new LocalBuildExecConfigExecutor( buildHelper ) );
        return configs;
    }
}
