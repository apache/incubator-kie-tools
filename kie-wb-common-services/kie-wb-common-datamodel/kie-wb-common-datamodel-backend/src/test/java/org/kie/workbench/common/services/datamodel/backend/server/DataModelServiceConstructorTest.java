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
import java.util.Collections;
import java.util.Iterator;
import java.util.function.Predicate;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;

import org.guvnor.ala.pipeline.ConfigExecutor;
import org.guvnor.ala.registry.PipelineRegistry;
import org.guvnor.ala.registry.inmemory.InMemoryPipelineRegistry;
import org.guvnor.common.services.backend.file.FileDiscoveryService;
import org.guvnor.common.services.backend.file.FileDiscoveryServiceImpl;
import org.guvnor.common.services.backend.metadata.MetadataServerSideService;
import org.guvnor.common.services.backend.metadata.MetadataServiceImpl;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.backend.util.CommentedOptionFactoryImpl;
import org.guvnor.common.services.project.backend.server.DefaultPomEnhancer;
import org.guvnor.common.services.project.backend.server.ModuleFinder;
import org.guvnor.common.services.project.backend.server.ModuleRepositoriesContentHandler;
import org.guvnor.common.services.project.backend.server.ModuleRepositoryResolverImpl;
import org.guvnor.common.services.project.backend.server.ModuleResourcePathResolver;
import org.guvnor.common.services.project.backend.server.POMServiceImpl;
import org.guvnor.common.services.project.backend.server.PomEnhancer;
import org.guvnor.common.services.project.backend.server.ProjectConfigurationContentHandler;
import org.guvnor.common.services.project.backend.server.utils.POMContentHandler;
import org.guvnor.common.services.project.builder.events.InvalidateDMOModuleCacheEvent;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.builder.service.BuildValidationHelper;
import org.guvnor.common.services.project.builder.service.PostBuildHandler;
import org.guvnor.common.services.project.events.NewModuleEvent;
import org.guvnor.common.services.project.events.NewPackageEvent;
import org.guvnor.common.services.project.service.ModuleRepositoriesService;
import org.guvnor.common.services.project.service.ModuleRepositoryResolver;
import org.guvnor.common.services.project.service.ModuleService;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.m2repo.backend.server.M2RepoServiceImpl;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.security.RepositoryAction;
import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.commons.util.RawMVELEvaluator;
import org.kie.soup.project.datamodel.oracle.ModuleDataModelOracle;
import org.kie.soup.project.datamodel.oracle.TypeSource;
import org.kie.workbench.common.services.backend.builder.ala.BuildPipelineInitializer;
import org.kie.workbench.common.services.backend.builder.ala.BuildPipelineInvoker;
import org.kie.workbench.common.services.backend.builder.ala.LocalBuildConfigExecutor;
import org.kie.workbench.common.services.backend.builder.ala.LocalBuildExecConfigExecutor;
import org.kie.workbench.common.services.backend.builder.ala.LocalModuleConfigExecutor;
import org.kie.workbench.common.services.backend.builder.ala.LocalSourceConfigExecutor;
import org.kie.workbench.common.services.backend.builder.core.BuildHelper;
import org.kie.workbench.common.services.backend.builder.core.DeploymentVerifier;
import org.kie.workbench.common.services.backend.builder.core.LRUBuilderCache;
import org.kie.workbench.common.services.backend.builder.core.LRUModuleDependenciesClassLoaderCache;
import org.kie.workbench.common.services.backend.builder.core.LRUPomModelCache;
import org.kie.workbench.common.services.backend.builder.service.BuildInfoService;
import org.kie.workbench.common.services.backend.builder.service.BuildServiceHelper;
import org.kie.workbench.common.services.backend.builder.service.BuildServiceImpl;
import org.kie.workbench.common.services.backend.dependencies.DependencyServiceImpl;
import org.kie.workbench.common.services.backend.kmodule.KModuleContentHandler;
import org.kie.workbench.common.services.backend.kmodule.KModuleServiceImpl;
import org.kie.workbench.common.services.backend.project.KieModuleRepositoriesServiceImpl;
import org.kie.workbench.common.services.backend.project.KieModuleServiceImpl;
import org.kie.workbench.common.services.backend.project.KieResourceResolver;
import org.kie.workbench.common.services.backend.project.ModuleSaver;
import org.kie.workbench.common.services.backend.project.ProjectImportsServiceImpl;
import org.kie.workbench.common.services.backend.whitelist.PackageNameSearchProvider;
import org.kie.workbench.common.services.backend.whitelist.PackageNameWhiteListLoader;
import org.kie.workbench.common.services.backend.whitelist.PackageNameWhiteListSaver;
import org.kie.workbench.common.services.backend.whitelist.PackageNameWhiteListServiceImpl;
import org.kie.workbench.common.services.datamodel.backend.server.cache.LRUDataModelOracleCache;
import org.kie.workbench.common.services.datamodel.backend.server.cache.LRUModuleDataModelOracleCache;
import org.kie.workbench.common.services.datamodel.backend.server.cache.ModuleDataModelOracleBuilderProvider;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.kie.workbench.common.services.datamodel.spi.DataModelExtension;
import org.kie.workbench.common.services.shared.dependencies.DependencyService;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.common.services.shared.project.ProjectImportsService;
import org.kie.workbench.common.services.shared.whitelist.PackageNameWhiteListService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.io.ConfigIOServiceProducer;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.backend.service.SaveAndRenameServiceImpl;
import org.uberfire.ext.editor.commons.service.RenameService;
import org.uberfire.io.IOService;
import org.uberfire.io.impl.IOServiceDotFileImpl;
import org.uberfire.java.nio.file.FileSystemNotFoundException;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.rpc.impl.SessionInfoImpl;
import org.uberfire.security.ResourceType;
import org.uberfire.security.authz.PermissionTypeRegistry;
import org.uberfire.security.impl.authz.DefaultPermissionTypeRegistry;
import org.uberfire.security.impl.authz.DotNamedPermissionType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.kie.workbench.common.services.datamodel.backend.server.ModuleDataModelOracleTestUtils.assertContains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DataModelServiceConstructorTest {

    private SimpleFileSystemProvider fs = new SimpleFileSystemProvider();

    private ResourceType REPOSITORY_TYPE = Repository.RESOURCE_TYPE;

    @Mock
    private Instance<ModuleResourcePathResolver> resourcePathResolversInstance;

    @Mock
    private Instance<DataModelExtension> dataModelExtensionProvider;

    @Before
    public void setup() {
        when(dataModelExtensionProvider.iterator()).thenReturn(Collections.emptyListIterator());
    }

    @Test
    public void testConstructor()
            throws IllegalArgumentException, FileSystemNotFoundException, SecurityException, URISyntaxException {

        final URL packageUrl = this.getClass().getResource("/DataModelServiceConstructorTest/src/main/java/t1p1");
        final RenameService renameService = mock(RenameService.class);
        final SaveAndRenameServiceImpl saveAndRenameService = mock(SaveAndRenameServiceImpl.class);

        RepositoryService repoService = mock(RepositoryService.class);
        IOService ioService = new IOServiceDotFileImpl();
        Collection<Role> roles = new ArrayList<>();
        Collection<Group> groups = new ArrayList<>();
        User user = new UserImpl("admin",
                                 roles,
                                 groups);
        SessionInfo sessionInfo = new SessionInfoImpl("admin",
                                                      user);
        Instance<User> userInstance = mock(Instance.class);
        when(userInstance.get()).thenReturn(user);

        ConfigIOServiceProducer cfiosProducer = new ConfigIOServiceProducer();
        cfiosProducer.setup();
        IOService configIOService = cfiosProducer.configIOService();
        MetadataService metadataService = new MetadataServiceImpl(ioService,
                                                                  configIOService,
                                                                  sessionInfo);

        POMContentHandler pomContentHandler = new POMContentHandler();
        M2RepoServiceImpl m2RepoService = new M2RepoServiceImpl();
        PomEnhancer pomEnhancer = new DefaultPomEnhancer();
        POMService pomService = new POMServiceImpl(ioService,
                                                   pomContentHandler,
                                                   m2RepoService,
                                                   metadataService,
                                                   new EventSourceMock<>(),
                                                   mock(ModuleService.class),
                                                   mock(CommentedOptionFactory.class),
                                                   pomEnhancer);
        KModuleContentHandler moduleContentHandler = new KModuleContentHandler();

        CommentedOptionFactory commentedOptionFactory = new CommentedOptionFactoryImpl(sessionInfo);
        ProjectConfigurationContentHandler moduleConfigurationContentHandler = new ProjectConfigurationContentHandler();
        ProjectImportsService moduleImportsService = new ProjectImportsServiceImpl(ioService,
                                                                                   moduleConfigurationContentHandler,
                                                                                   renameService,
                                                                                   saveAndRenameService);

        Event<NewModuleEvent> newModuleEvent = new EventSourceMock<>();
        Event<NewPackageEvent> newPackageEvent = new EventSourceMock<>();
        Event<InvalidateDMOModuleCacheEvent> invalidateDMOCache = new EventSourceMock<>();

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

        ModuleRepositoryResolver repositoryResolver = new ModuleRepositoryResolverImpl(ioService,
                                                                                       null,
                                                                                       null);

        FileDiscoveryService fileDiscoveryService = new FileDiscoveryServiceImpl();

        HackedKieModuleServiceImpl moduleService = null;
        HackedKModuleServiceImpl kModuleService = new HackedKModuleServiceImpl(ioService,
                                                                               moduleService,
                                                                               metadataService,
                                                                               moduleContentHandler);
        KieResourceResolver resourceResolver = new KieResourceResolver(ioService,
                                                                       pomService,
                                                                       commentedOptionFactory,
                                                                       kModuleService,
                                                                       resourcePathResolversInstance);
        ModuleSaver moduleSaver = null;
        moduleService = new HackedKieModuleServiceImpl(ioService,
                                                       moduleSaver,
                                                       pomService,
                                                       repoService,
                                                       newModuleEvent,
                                                       newPackageEvent,
                                                       invalidateDMOCache,
                                                       sessionInfo,
                                                       commentedOptionFactory,
                                                       mock(ModuleFinder.class),
                                                       resourceResolver,
                                                       repositoryResolver);

        ModuleRepositoriesContentHandler contentHandler = new ModuleRepositoriesContentHandler();
        ModuleRepositoriesService moduleRepositoriesService = new KieModuleRepositoriesServiceImpl(ioService,
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
                                                                                                      moduleService,
                                                                                                      loader,
                                                                                                      saver);

        moduleSaver = new ModuleSaver(ioService,
                                      pomService,
                                      kModuleService,
                                      newModuleEvent,
                                      newPackageEvent,
                                      resourceResolver,
                                      moduleImportsService,
                                      moduleRepositoriesService,
                                      packageNameWhiteListService,
                                      commentedOptionFactory,
                                      sessionInfo);
        moduleService.setModuleSaver(moduleSaver);
        kModuleService.setModuleService(moduleService);

        ProjectImportsService importsService = new ProjectImportsServiceImpl(ioService,
                                                                             moduleConfigurationContentHandler,
                                                                             renameService,
                                                                             saveAndRenameService);
        Instance<BuildValidationHelper> buildValidationHelperBeans = null;
        Instance<Predicate<String>> classFilterBeans = null;
        HackedLRUModuleDependenciesClassLoaderCache dependenciesClassLoaderCache = new HackedLRUModuleDependenciesClassLoaderCache();
        LRUPomModelCache pomModelCache = new LRUPomModelCache();
        LRUBuilderCache builderCache = new LRUBuilderCache(ioService,
                                                           moduleService,
                                                           importsService,
                                                           buildValidationHelperBeans,
                                                           dependenciesClassLoaderCache,
                                                           pomModelCache,
                                                           packageNameWhiteListService,
                                                           classFilterBeans
        );

        Instance<PostBuildHandler> handlerInstance = mock(Instance.class);
        Iterator<PostBuildHandler> mockIterator = mock(Iterator.class);
        when(handlerInstance.iterator()).thenReturn(mockIterator);
        when(mockIterator.hasNext()).thenReturn(false);

        DeploymentVerifier deploymentVerifier = new DeploymentVerifier(repositoryResolver,
                                                                       moduleRepositoriesService);
        BuildHelper buildHelper = new BuildHelper(pomService,
                                                  m2RepoService,
                                                  moduleService,
                                                  deploymentVerifier,
                                                  builderCache,
                                                  handlerInstance,
                                                  userInstance);
        PipelineRegistry pipelineRegistry = new InMemoryPipelineRegistry();
        BuildPipelineInitializer pipelineInitializer = new BuildPipelineInitializer(pipelineRegistry,
                                                                                    getConfigExecutors(moduleService,
                                                                                                       buildHelper));
        BuildPipelineInvoker pipelineInvoker = new BuildPipelineInvoker(pipelineInitializer.getExecutor(),
                                                                        pipelineRegistry);

        BuildServiceHelper buildServiceHelper = new BuildServiceHelper(pipelineInvoker,
                                                                       deploymentVerifier);
        BuildService buildService = new BuildServiceImpl(moduleService,
                                                         buildServiceHelper,
                                                         builderCache);
        BuildInfoService buildInfoService = new BuildInfoService(buildService,
                                                                 builderCache);

        ModuleDataModelOracleBuilderProvider builderProvider = new ModuleDataModelOracleBuilderProvider(packageNameWhiteListService,
                                                                                                        importsService);

        LRUModuleDataModelOracleCache cacheModules = new LRUModuleDataModelOracleCache(builderProvider,
                                                                                       moduleService,
                                                                                       buildInfoService);

        dependenciesClassLoaderCache.setBuildInfoService(buildInfoService);
        LRUDataModelOracleCache cachePackages = new LRUDataModelOracleCache(ioService,
                                                                            fileDiscoveryService,
                                                                            cacheModules,
                                                                            moduleService,
                                                                            buildInfoService,
                                                                            dataModelExtensionProvider,
                                                                            new RawMVELEvaluator());
        DataModelService dataModelService = new DataModelServiceImpl(cachePackages,
                                                                     cacheModules,
                                                                     moduleService);

        final org.uberfire.java.nio.file.Path nioPackagePath = fs.getPath(packageUrl.toURI());
        final Path packagePath = Paths.convert(nioPackagePath);

        final ModuleDataModelOracle oracle = dataModelService.getModuleDataModel(packagePath);

        assertNotNull(oracle);

        assertEquals(4,
                     oracle.getModuleModelFields().size());
        assertContains("t1p1.Bean1",
                       oracle.getModuleModelFields().keySet());
        assertContains("t1p1.DRLBean",
                       oracle.getModuleModelFields().keySet());
        assertContains("t1p2.Bean2",
                       oracle.getModuleModelFields().keySet());
        assertContains("java.lang.String",
                       oracle.getModuleModelFields().keySet());

        assertEquals(TypeSource.JAVA_PROJECT,
                     oracle.getModuleTypeSources().get("t1p1.Bean1"));
        assertEquals(TypeSource.DECLARED,
                     oracle.getModuleTypeSources().get("t1p1.DRLBean"));
        assertEquals(TypeSource.JAVA_PROJECT,
                     oracle.getModuleTypeSources().get("t1p2.Bean2"));
        assertEquals(TypeSource.JAVA_PROJECT,
                     oracle.getModuleTypeSources().get("java.lang.String"));
    }

    private Collection<ConfigExecutor> getConfigExecutors(KieModuleService moduleService,
                                                          BuildHelper buildHelper) {
        Collection<ConfigExecutor> configs = new ArrayList<>();
        configs.add(new LocalSourceConfigExecutor());
        configs.add(new LocalModuleConfigExecutor(moduleService));
        configs.add(new LocalBuildConfigExecutor());
        configs.add(new LocalBuildExecConfigExecutor(buildHelper));
        return configs;
    }

    private class HackedKModuleServiceImpl extends KModuleServiceImpl {

        public HackedKModuleServiceImpl(IOService ioService,
                                        KieModuleServiceImpl moduleService,
                                        MetadataService metadataService,
                                        KModuleContentHandler moduleContentHandler) {
            super(ioService,
                  moduleService,
                  metadataService,
                  moduleContentHandler);
        }

        @Override
        public void setModuleService(KieModuleService moduleService) {
            super.setModuleService(moduleService);
        }
    }

    private class HackedLRUModuleDependenciesClassLoaderCache extends LRUModuleDependenciesClassLoaderCache {

        @Override
        public void setBuildInfoService(BuildInfoService buildInfoService) {
            super.setBuildInfoService(buildInfoService);
        }
    }

    private class HackedKieModuleServiceImpl extends KieModuleServiceImpl {

        public HackedKieModuleServiceImpl(IOService ioService,
                                          ModuleSaver moduleSaver,
                                          POMService pomService,
                                          RepositoryService repoService,
                                          Event<NewModuleEvent> newModuleEvent,
                                          Event<NewPackageEvent> newPackageEvent,
                                          Event<InvalidateDMOModuleCacheEvent> invalidateDMOCache,
                                          SessionInfo sessionInfo,
                                          CommentedOptionFactory commentedOptionFactory,
                                          ModuleFinder moduleFinder,
                                          KieResourceResolver resourceResolver,
                                          ModuleRepositoryResolver repositoryResolver) {
            super(ioService,
                  moduleSaver,
                  pomService,
                  repoService,
                  newModuleEvent,
                  newPackageEvent,
                  invalidateDMOCache,
                  sessionInfo,
                  commentedOptionFactory,
                  moduleFinder,
                  resourceResolver,
                  repositoryResolver
            );
        }

        @Override
        public void setModuleSaver(ModuleSaver moduleSaver) {
            super.setModuleSaver(moduleSaver);
        }
    }
}
