/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.screens.workbench.backend;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guvnor.common.services.project.model.POM;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryEnvironmentConfigurations;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigItem;
import org.guvnor.structure.server.config.ConfigType;
import org.guvnor.structure.server.config.ConfigurationFactory;
import org.guvnor.structure.server.config.ConfigurationService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.mocks.FileSystemTestingUtils;

import static org.mockito.Mockito.*;

public class BaseAppSetupTest {

    private FileSystemTestingUtils fileSystemTestingUtils = new FileSystemTestingUtils();

    protected IOService ioService;

    protected RepositoryService repositoryService;

    protected OrganizationalUnitService organizationalUnitService;

    protected KieProjectService projectService;

    protected ConfigurationService configurationService;

    protected ConfigurationFactory configurationFactory;

    private Repository repository;

    private OrganizationalUnit organizationalUnit;

    private FileSystem fileSystem;

    private BaseAppSetup baseAppSetup;

    private Map<String, ConfigItem<String>> configItemsByName;

    @Before
    public void setup() throws IOException {
        fileSystemTestingUtils.setup();

        ioService = spy( fileSystemTestingUtils.getIoService() );
        fileSystem = fileSystemTestingUtils.getFileSystem();

        repositoryService = mock( RepositoryService.class );
        organizationalUnitService = mock( OrganizationalUnitService.class );
        projectService = mock( KieProjectService.class );
        configurationService = mock( ConfigurationService.class );
        configurationFactory = mock( ConfigurationFactory.class );
        repository = mock( Repository.class );
        organizationalUnit = mock( OrganizationalUnit.class );

        baseAppSetup = spy( new BaseAppSetup( ioService,
                                              repositoryService,
                                              organizationalUnitService,
                                              projectService,
                                              configurationService,
                                              configurationFactory ) {} );

        mockConfigurations();
        mockRepository();
        mockOrganizationalUnitService();
        mockIoService();
    }

    @After
    public void cleanup() {
        fileSystemTestingUtils.cleanup();
    }

    @Test
    public void createNonexistentRepositoryTest() {
        baseAppSetup.createRepository( "nonexistentRepository", "scheme", "origin", "user", "password", organizationalUnit );

        verify( repositoryService ).createRepository( anyString(), anyString(), any( RepositoryEnvironmentConfigurations.class ) );
        verify( organizationalUnitService ).addRepository( eq( organizationalUnit ), eq( repository ) );
    }

    @Test
    public void createExistentRepositoryTest() {
        baseAppSetup.createRepository( "existentRepository", "scheme", "origin", "user", "password", organizationalUnit );

        verify( repositoryService, never() ).createRepository( anyString(), anyString(), any( RepositoryEnvironmentConfigurations.class ) );
        verify( organizationalUnitService, never() ).addRepository( eq( organizationalUnit ), eq( repository ) );
    }

    @Test
    public void createNonexistentOrganizationalUnitTest() {
        baseAppSetup.createOU( repository, "nonexistentOrganizationalUnit", "ouOwner" );

        verify( organizationalUnitService ).createOrganizationalUnit( anyString(), anyString(), anyString(), anyCollectionOf( Repository.class ) );
    }

    @Test
    public void createExistentOrganizationalUnitTest() {
        baseAppSetup.createOU( repository, "existentOrganizationalUnit", "ouOwner" );

        verify( organizationalUnitService, never() ).createOrganizationalUnit( anyString(), anyString(), anyString(), anyCollectionOf( Repository.class ) );
    }

    @Test
    public void createNonexistentProjectTest() {
        baseAppSetup.createProject( repository, "org.kie", "nonexistentProject", "1.0.0" );

        verify( projectService ).newProject( any( Path.class ), any( POM.class ), anyString() );
    }

    @Test
    public void createNonexistentProjectInNullRepositoryTest() {
        baseAppSetup.createProject( null, "org.kie", "nonexistentProject", "1.0.0" );

        verify( projectService, never() ).newProject( any( Path.class ), any( POM.class ), anyString() );
    }

    @Test
    public void createExistentProjectTest() {
        baseAppSetup.createProject( repository, "org.kie", "existentProject", "1.0.0" );

        verify( projectService, never() ).newProject( any( Path.class ), any( POM.class ), anyString() );
    }

    @Test
    public void loadExampleRepositoriesTest() throws IOException {
        final java.nio.file.Path repositoriesRoot = Files.createTempDirectory( new File( "repositoriesRoot" ).getPath() );
        final java.nio.file.Path repo1Path = Files.createTempDirectory( repositoriesRoot, "repo1" );
        final java.nio.file.Path repo2Path = Files.createTempDirectory( repositoriesRoot, "repo2" );

        baseAppSetup.loadExampleRepositories( repositoriesRoot.toFile().getAbsolutePath(), "existentOrganizationalUnit", "ouOwner", BaseAppSetup.GIT_SCHEME );

        verify( baseAppSetup ).createOU( null, "existentOrganizationalUnit", "ouOwner" );
        verify( baseAppSetup ).createRepository( eq( repo1Path.toFile().getName() ), eq( BaseAppSetup.GIT_SCHEME ), eq( repo1Path.toFile().getAbsolutePath() ), eq( organizationalUnit ) );
        verify( baseAppSetup ).createRepository( eq( repo2Path.toFile().getName() ), eq( BaseAppSetup.GIT_SCHEME ), eq( repo2Path.toFile().getAbsolutePath() ), eq( organizationalUnit ) );
    }

    @Test
    public void setupDefinedConfigurationGroupTest() {
        final ConfigType configType = ConfigType.GLOBAL;
        final String configGroupName = BaseAppSetup.GLOBAL_SETTINGS;

        // Existent configs to be checked
        final ConfigGroup existentConfigGroup = setupPredefinedGlobalConfiguration();
        List<ConfigGroup> definedConfigGroups = new ArrayList<>( 1 );
        definedConfigGroups.add( existentConfigGroup );
        doReturn( definedConfigGroups ).when( configurationService ).getConfiguration( eq( ConfigType.GLOBAL ) );
        final ConfigItem<String> existentDroolsDefaultLanguageConfigItem = configItemsByName.get( "drools.defaultlanguage" );
        final ConfigItem<String> existentSupportRuntimeDeployConfigItem = configItemsByName.get( "support.runtime.deploy" );

        // To update
        final ConfigItem<String> droolsDefaultLanguageConfigItem = new ConfigItem<>();
        droolsDefaultLanguageConfigItem.setName( "drools.defaultlanguage" );
        droolsDefaultLanguageConfigItem.setValue( "en" );

        // To create
        final ConfigItem<String> droolsDefaultCountryConfigItem = new ConfigItem<>();
        droolsDefaultCountryConfigItem.setName( "drools.defaultcountry" );
        droolsDefaultCountryConfigItem.setValue( "US" );

        // To do nothing
        final ConfigItem<String> supportRuntimeDeployConfigItem = new ConfigItem<>();
        supportRuntimeDeployConfigItem.setName( "support.runtime.deploy" );
        supportRuntimeDeployConfigItem.setValue( "true" );

        baseAppSetup.setupConfigurationGroup( configType,
                                              configGroupName,
                                              null,
                                              droolsDefaultLanguageConfigItem,
                                              droolsDefaultCountryConfigItem,
                                              supportRuntimeDeployConfigItem );

        verify( configurationService, never() ).addConfiguration( any( ConfigGroup.class ) );
        verify( configurationService, times( 2 ) ).updateConfiguration( existentConfigGroup );

        verify( existentConfigGroup, never() ).addConfigItem( droolsDefaultLanguageConfigItem );
        verify( existentConfigGroup ).addConfigItem( droolsDefaultCountryConfigItem ); // new config item
        verify( existentConfigGroup, never() ).addConfigItem( supportRuntimeDeployConfigItem );

        verify( existentDroolsDefaultLanguageConfigItem ).setValue( droolsDefaultLanguageConfigItem.getValue() ); // updated config item
        verify( existentSupportRuntimeDeployConfigItem, never() ).setValue( anyString() );
    }

    @Test
    public void setupUndefinedConfigurationGroupTest() {
        doReturn( new ArrayList<>() ).when( configurationService ).getConfiguration( eq( ConfigType.GLOBAL ) );

        final ConfigType configType = ConfigType.GLOBAL;
        final String configGroupName = BaseAppSetup.GLOBAL_SETTINGS;
        final ConfigGroup configGroup = setupPredefinedGlobalConfiguration();

        baseAppSetup.setupConfigurationGroup( configType,
                                              configGroupName,
                                              configGroup );

        verify( configurationService ).addConfiguration( any( ConfigGroup.class ) );
        verify( configurationService, never() ).updateConfiguration( any( ConfigGroup.class ) );
    }

    private ConfigGroup setupPredefinedGlobalConfiguration() {
        ConfigItem<String> droolsDefaultLanguageConfigItem = new ConfigItem<>();
        droolsDefaultLanguageConfigItem.setName( "drools.defaultlanguage" );
        droolsDefaultLanguageConfigItem.setValue( "pt" );
        droolsDefaultLanguageConfigItem = spy( droolsDefaultLanguageConfigItem );

        ConfigItem<String> supportRuntimeDeployConfigItem = new ConfigItem<>();
        supportRuntimeDeployConfigItem.setName( "support.runtime.deploy" );
        supportRuntimeDeployConfigItem.setValue( "true" );
        supportRuntimeDeployConfigItem = spy( supportRuntimeDeployConfigItem );

        configItemsByName = new HashMap<>( 2 );
        configItemsByName.put( "drools.defaultlanguage", droolsDefaultLanguageConfigItem );
        configItemsByName.put( "support.runtime.deploy", supportRuntimeDeployConfigItem );

        final ConfigGroup group = spy( configurationFactory.newConfigGroup( ConfigType.GLOBAL,
                                                                            BaseAppSetup.GLOBAL_SETTINGS,
                                                                            "" ) );
        group.setName( BaseAppSetup.GLOBAL_SETTINGS );
        group.addConfigItem( droolsDefaultLanguageConfigItem );
        group.addConfigItem( supportRuntimeDeployConfigItem );

        return group;
    }

    private void mockConfigurations() {
        //doReturn( new ArrayList<ConfigGroup>() ).when( configurationService ).getConfiguration( any( ConfigType.class ) );
        doReturn( true ).when( configurationService ).updateConfiguration( any( ConfigGroup.class ) );
        doReturn( true ).when( configurationService ).addConfiguration( any( ConfigGroup.class ) );

        doReturn( new ConfigGroup() ).when( configurationFactory ).newConfigGroup( any( ConfigType.class ), anyString(), anyString() );
    }

    private void mockRepository() {
        doReturn( repository ).when( repositoryService ).createRepository( anyString(), anyString(), any( RepositoryEnvironmentConfigurations.class ) );
        doReturn( repository ).when( repositoryService ).getRepository( eq( "existentRepository" ) );
        doReturn( "git://amend-repo-test" ).when( repository ).getUri();
    }

    private void mockOrganizationalUnitService() {
        doReturn( organizationalUnit ).when( organizationalUnitService ).getOrganizationalUnit( eq( "existentOrganizationalUnit" ) );
    }

    private void mockIoService() {
        final String artifact = "existentProject";
        final String uri = repository.getUri() + fileSystem.getSeparator() + artifact;
        final Path path = PathFactory.newPath( artifact, uri );

        doReturn( fileSystem ).when( ioService ).getFileSystem( any( URI.class ) );
        doReturn( Paths.convert( path ) ).when( ioService ).get( eq( uri ) );
        doReturn( true ).when( ioService ).exists( eq( Paths.convert( path ) ) );
    }
}
