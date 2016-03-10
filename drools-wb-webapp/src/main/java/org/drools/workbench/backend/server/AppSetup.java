/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.backend.server;

import java.io.File;
import java.io.FileFilter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.workbench.screens.testscenario.service.ScenarioTestEditorService;
import org.drools.workbench.screens.workitems.service.WorkItemsEditorService;
import org.guvnor.common.services.project.model.GAV;
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
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.commons.services.cdi.StartupType;
import org.uberfire.io.IOService;

//This is a temporary solution when running in PROD-MODE as /webapp/.niogit/system.git folder
//is not deployed to the Application Servers /bin folder. This will be remedied when an
//installer is written to create the system.git repository in the correct location.
@Startup(StartupType.BOOTSTRAP)
@ApplicationScoped
public class AppSetup {

    private static final Logger logger = LoggerFactory.getLogger( AppSetup.class );

    // default groups
    private static final String DROOLS_WB_ORGANIZATIONAL_UNIT1 = "demo";
    private static final String DROOLS_WB_ORGANIZATIONAL_UNIT1_OWNER = "demo@drools.org";

    // default repository section - start
    private static final String DROOLS_WB_PLAYGROUND_SCHEME = "git";
    private static final String DROOLS_WB_PLAYGROUND_ALIAS = "uf-playground";
    private static final String DROOLS_WB_PLAYGROUND_ORIGIN = "https://github.com/guvnorngtestuser1/guvnorng-playground.git";
    private static final String DROOLS_WB_PLAYGROUND_UID = "guvnorngtestuser1";
    private static final String DROOLS_WB_PLAYGROUND_PWD = "test1234";

    private static final String GLOBAL_SETTINGS = "settings";
    // default repository section - end

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private OrganizationalUnitService organizationalUnitService;

    @Inject
    private RepositoryService repositoryService;

    @Inject
    private KieProjectService projectService;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private ConfigurationFactory configurationFactory;

    @PostConstruct
    public void assertPlayground() {
        try {
            configurationService.startBatch();
            final String exampleRepositoriesRoot = System.getProperty( "org.kie.example.repositories" );
            if ( !( exampleRepositoriesRoot == null || "".equalsIgnoreCase( exampleRepositoriesRoot ) ) ) {
                loadExampleRepositories( exampleRepositoriesRoot );

            } else if ( !"false".equalsIgnoreCase( System.getProperty( "org.kie.demo" ) ) ) {
                Repository repository = createRepository( DROOLS_WB_PLAYGROUND_ALIAS,
                                                          DROOLS_WB_PLAYGROUND_SCHEME,
                                                          DROOLS_WB_PLAYGROUND_ORIGIN,
                                                          DROOLS_WB_PLAYGROUND_UID,
                                                          DROOLS_WB_PLAYGROUND_PWD );
                createOU( repository,
                          DROOLS_WB_ORGANIZATIONAL_UNIT1,
                          DROOLS_WB_ORGANIZATIONAL_UNIT1_OWNER );

            } else if ( "true".equalsIgnoreCase( System.getProperty( "org.kie.example" ) ) ) {

                Repository exampleRepo = createRepository( "repository1",
                                                           "git",
                                                           null,
                                                           "",
                                                           "" );
                createOU( exampleRepo,
                          "example",
                          "" );

                createProject( exampleRepo,
                               "org.kie.example",
                               "project1",
                               "1.0.0-SNAPSHOT" );
            }

            //Define mandatory properties
            defineGlobalProperties();

            defineEditorProperties();

        } catch ( final Exception e ) {
            logger.error( "Error during update config", e );
            throw new RuntimeException( e );
        } finally {
            configurationService.endBatch();
        }
    }

    private void loadExampleRepositories( final String exampleRepositoriesRoot ) {
        final File root = new File( exampleRepositoriesRoot );
        if ( !root.isDirectory() ) {
            logger.error( "System Property 'org.kie.example.repositories' does not point to a folder." );

        } else {
            //Create a new Organizational Unit
            logger.info( "Creating Organizational Unit '" + DROOLS_WB_ORGANIZATIONAL_UNIT1 + "'." );
            OrganizationalUnit organizationalUnit = organizationalUnitService.getOrganizationalUnit( DROOLS_WB_ORGANIZATIONAL_UNIT1 );
            if ( organizationalUnit == null ) {
                final List<Repository> repositories = new ArrayList<Repository>();
                organizationalUnit = organizationalUnitService.createOrganizationalUnit( DROOLS_WB_ORGANIZATIONAL_UNIT1,
                                                                                         DROOLS_WB_ORGANIZATIONAL_UNIT1_OWNER,
                                                                                         null,
                                                                                         repositories );
                logger.info( "Created Organizational Unit '" + DROOLS_WB_ORGANIZATIONAL_UNIT1 + "'." );

            } else {
                logger.info( "Organizational Unit '" + DROOLS_WB_ORGANIZATIONAL_UNIT1 + "' already exists." );
            }

            final FileFilter filter = new FileFilter() {
                @Override
                public boolean accept( final File pathName ) {
                    return pathName.isDirectory();
                }
            };

            logger.info( "Cloning Example Repositories." );
            for ( File child : root.listFiles( filter ) ) {
                final String repositoryAlias = child.getName();
                final String repositoryOrigin = child.getAbsolutePath();
                logger.info( "Cloning Repository '" + repositoryAlias + "' from '" + repositoryOrigin + "'." );
                Repository repository = repositoryService.getRepository( repositoryAlias );
                if ( repository == null ) {
                    try {
                        RepositoryEnvironmentConfigurations configurations = new RepositoryEnvironmentConfigurations();

                        configurations.setOrigin( repositoryOrigin );

                        repository = repositoryService.createRepository( "git",
                                                                         repositoryAlias,
                                                                         configurations );
                        organizationalUnitService.addRepository( organizationalUnit,
                                                                 repository );
                    } catch ( Exception e ) {
                        logger.error( "Failed to clone Repository '" + repositoryAlias + "'",
                                      e );
                    }
                } else {
                    logger.info( "Repository '" + repositoryAlias + "' already exists." );
                }
            }
            logger.info( "Example Repositories cloned." );
        }
    }

    private void defineGlobalProperties() {
        List<ConfigGroup> globalConfigGroups = configurationService.getConfiguration( ConfigType.GLOBAL );
        boolean globalSettingsDefined = false;
        for ( ConfigGroup globalConfigGroup : globalConfigGroups ) {
            if ( GLOBAL_SETTINGS.equals( globalConfigGroup.getName() ) ) {
                globalSettingsDefined = true;
                break;
            }
        }
        if ( !globalSettingsDefined ) {
            configurationService.addConfiguration( getGlobalConfiguration() );
        }
    }

    private void defineEditorProperties() {
        List<ConfigGroup> editorConfigGroups = configurationService.getConfiguration( ConfigType.EDITOR );
        defineWorkItemsProperties( editorConfigGroups );
        defineTestScenarioProperties( editorConfigGroups );
    }

    private void defineTestScenarioProperties( List<ConfigGroup> editorConfigGroups ) {
        boolean settingsDefined = false;
        for ( ConfigGroup editorConfigGroup : editorConfigGroups ) {
            if ( ScenarioTestEditorService.TEST_SCENARIO_EDITOR_SETTINGS.equals( editorConfigGroup.getName() ) ) {
                settingsDefined = true;
                break;
            }
        }
        if ( !settingsDefined ) {
            /**
             *
             * TODO : get TEST SCENARIO ELEMENT DEFINITIONS
             *
             */
            configurationService.addConfiguration( getTestScenarioElementDefinitions() );
        }
    }

    private void defineWorkItemsProperties( List<ConfigGroup> editorConfigGroups ) {
        boolean settingsDefined = false;
        for ( ConfigGroup editorConfigGroup : editorConfigGroups ) {
            if ( WorkItemsEditorService.WORK_ITEMS_EDITOR_SETTINGS.equals( editorConfigGroup.getName() ) ) {
                settingsDefined = true;
                break;
            }
        }
        if ( !settingsDefined ) {
            configurationService.addConfiguration( getWorkItemElementDefinitions() );
        }
    }

    private ConfigGroup getGlobalConfiguration() {
        //Global Configurations used by many of Drools Workbench editors
        final ConfigGroup group = configurationFactory.newConfigGroup( ConfigType.GLOBAL,
                                                                       GLOBAL_SETTINGS,
                                                                       "" );
        group.addConfigItem( configurationFactory.newConfigItem( "drools.dateformat",
                                                                 "dd-MMM-yyyy" ) );
        group.addConfigItem( configurationFactory.newConfigItem( "drools.datetimeformat",
                                                                 "dd-MMM-yyyy hh:mm:ss" ) );
        group.addConfigItem( configurationFactory.newConfigItem( "drools.defaultlanguage",
                                                                 "en" ) );
        group.addConfigItem( configurationFactory.newConfigItem( "drools.defaultcountry",
                                                                 "US" ) );
        group.addConfigItem( configurationFactory.newConfigItem( "build.enable-incremental",
                                                                 "true" ) );
        group.addConfigItem( configurationFactory.newConfigItem( "rule-modeller-onlyShowDSLStatements",
                                                                 "false" ) );
        return group;
    }

    private ConfigGroup getTestScenarioElementDefinitions() {
        final ConfigGroup group = configurationFactory.newConfigGroup( ConfigType.EDITOR,
                                                                       ScenarioTestEditorService.TEST_SCENARIO_EDITOR_SETTINGS,
                                                                       "" );

        ConfigItem<Integer> configItem = new ConfigItem<Integer>();
        configItem.setName( ScenarioTestEditorService.TEST_SCENARIO_EDITOR_MAX_RULE_FIRINGS );
        configItem.setValue( 10000 );
        group.addConfigItem( configItem );

        return group;
    }

    private ConfigGroup getWorkItemElementDefinitions() {
        // Work Item Definition elements used when creating Work Item Definitions.
        // Each entry in this file represents a Button in the Editor's Palette:-
        //   - Underscores ('_') in the key will be converted in whitespaces (' ') and
        //     will be used as Button's labels.
        //   - The value will be the text pasted into the editor when an element in the
        //     palette is selected. You can use a pipe ('|') to specify the place where
        //     the cursor should be put after pasting the element into the editor.
        final ConfigGroup group = configurationFactory.newConfigGroup( ConfigType.EDITOR,
                                                                       WorkItemsEditorService.WORK_ITEMS_EDITOR_SETTINGS,
                                                                       "" );
        group.addConfigItem( configurationFactory.newConfigItem( WorkItemsEditorService.WORK_ITEMS_EDITOR_SETTINGS_DEFINITION,
                                                                 "import org.drools.core.process.core.datatype.impl.type.StringDataType;\n" +
                                                                         "import org.drools.core.process.core.datatype.impl.type.ObjectDataType;\n" +
                                                                         "\n" +
                                                                         "[\n" +
                                                                         "  [\n" +
                                                                         "    \"name\" : \"MyTask|\", \n" +
                                                                         "    \"parameters\" : [ \n" +
                                                                         "        \"MyFirstParam\" : new StringDataType(), \n" +
                                                                         "        \"MySecondParam\" : new StringDataType(), \n" +
                                                                         "        \"MyThirdParam\" : new ObjectDataType() \n" +
                                                                         "    ], \n" +
                                                                         "    \"results\" : [ \n" +
                                                                         "        \"Result\" : new ObjectDataType(\"java.util.Map\") \n" +
                                                                         "    ], \n" +
                                                                         "    \"displayName\" : \"My Task\", \n" +
                                                                         "    \"icon\" : \"\" \n" +
                                                                         "  ]\n" +
                                                                         "]" ) );
        group.addConfigItem( configurationFactory.newConfigItem( WorkItemsEditorService.WORK_ITEMS_EDITOR_SETTINGS_PARAMETER,
                                                                 "\"MyParam|\" : new StringDataType()" ) );
        group.addConfigItem( configurationFactory.newConfigItem( WorkItemsEditorService.WORK_ITEMS_EDITOR_SETTINGS_RESULT,
                                                                 "\"Result|\" : new ObjectDataType()" ) );
        group.addConfigItem( configurationFactory.newConfigItem( WorkItemsEditorService.WORK_ITEMS_EDITOR_SETTINGS_DISPLAY_NAME,
                                                                 "\"displayName\" : \"My Task|\"" ) );
        return group;
    }

    private Repository createRepository( final String alias,
                                         final String scheme,
                                         final String origin,
                                         final String user,
                                         final String password ) {
        Repository repository = repositoryService.getRepository( alias );
        if ( repository == null ) {
            RepositoryEnvironmentConfigurations configurations=new RepositoryEnvironmentConfigurations();
            configurations.setOrigin( origin );
            configurations.setUserName( user );
            configurations.setPassword( password );
            repository = repositoryService.createRepository( scheme,
                                                             alias,
                                                             configurations );
        }
        return repository;
    }

    private OrganizationalUnit createOU( final Repository repository,
                                         final String ouName,
                                         final String ouOwner ) {
        OrganizationalUnit ou = organizationalUnitService.getOrganizationalUnit( ouName );
        if ( ou == null ) {
            List<Repository> repositories = new ArrayList<Repository>();
            repositories.add( repository );
            organizationalUnitService.createOrganizationalUnit( ouName,
                                                                ouOwner,
                                                                null,
                                                                repositories );
        }
        return ou;
    }

    private void createProject( final Repository repository,
                                final String group,
                                final String artifact,
                                final String version ) {
        final GAV gav = new GAV( group,
                                 artifact,
                                 version );
        try {
            if ( repository != null ) {
                final String projectLocation = repository.getUri() + ioService.getFileSystem( URI.create( repository.getUri() ) ).getSeparator() + artifact;
                if ( !ioService.exists( ioService.get( URI.create( projectLocation ) ) ) ) {
                    POM pom = new POM( gav );
                    pom.setName( artifact );
                    projectService.newProject( repository.getBranchRoot( repository.getDefaultBranch() ),
                                               pom,
                                               "/" );
                }
            } else {
                logger.error( "Repository was not found (is null), cannot add project" );
            }
        } catch ( Exception e ) {
            logger.error( "Unable to bootstrap project {} in repository {}",
                          gav,
                          repository.getAlias(),
                          e );
        }
    }

}
