/*
 * Copyright 2012 JBoss Inc
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.workbench.screens.testscenario.service.ScenarioTestEditorService;
import org.drools.workbench.screens.workitems.service.WorkItemsEditorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.organizationalunit.OrganizationalUnit;
import org.uberfire.backend.organizationalunit.OrganizationalUnitService;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.repositories.RepositoryService;
import org.uberfire.backend.server.config.ConfigGroup;
import org.uberfire.backend.server.config.ConfigItem;
import org.uberfire.backend.server.config.ConfigType;
import org.uberfire.backend.server.config.ConfigurationFactory;
import org.uberfire.backend.server.config.ConfigurationService;
import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.commons.services.cdi.StartupType;

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
    private RepositoryService repositoryService;

    @Inject
    private OrganizationalUnitService organizationalUnitService;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private ConfigurationFactory configurationFactory;

    @PostConstruct
    public void assertPlayground() {
        // TODO in case repo is not defined in system repository so we add default
        try {
            Repository repository1 = repositoryService.getRepository( DROOLS_WB_PLAYGROUND_ALIAS );
            if ( repository1 == null ) {
                repository1 = repositoryService.createRepository( DROOLS_WB_PLAYGROUND_SCHEME, DROOLS_WB_PLAYGROUND_ALIAS,
                                                                  new HashMap<String, Object>() {{
                                                                      put( "origin", DROOLS_WB_PLAYGROUND_ORIGIN );
                                                                      put( "username", DROOLS_WB_PLAYGROUND_UID );
                                                                      put( "crypt:password", DROOLS_WB_PLAYGROUND_PWD );
                                                                  }} );
            }

            // TODO in case Organizational Units are not defined
            OrganizationalUnit organizationalUnit1 = organizationalUnitService.getOrganizationalUnit( DROOLS_WB_ORGANIZATIONAL_UNIT1 );
            if ( organizationalUnit1 == null ) {
                final List<Repository> repositories = new ArrayList<Repository>();
                repositories.add( repository1 );
                organizationalUnitService.createOrganizationalUnit( DROOLS_WB_ORGANIZATIONAL_UNIT1,
                                                                    DROOLS_WB_ORGANIZATIONAL_UNIT1_OWNER,
                                                                    repositories );
            }
        } catch ( Exception e ) {
            logger.error( "Failed to setup Repository '" + DROOLS_WB_PLAYGROUND_ALIAS + "'",
                          e );
        }

        //Define mandatory properties
        defineGlobalProperties();

        defineEditorProperties();
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

}
