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

import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.workbench.screens.workitems.service.WorkItemsEditorService;
import org.kie.commons.services.cdi.Startup;
import org.kie.commons.services.cdi.StartupType;
import org.uberfire.backend.group.Group;
import org.uberfire.backend.group.GroupService;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.repositories.RepositoryService;
import org.uberfire.backend.server.config.ConfigGroup;
import org.uberfire.backend.server.config.ConfigType;
import org.uberfire.backend.server.config.ConfigurationFactory;
import org.uberfire.backend.server.config.ConfigurationService;

//This is a temporary solution when running in PROD-MODE as /webapp/.niogit/system.git folder
//is not deployed to the Application Servers /bin folder. This will be remedied when an
//installer is written to create the system.git repository in the correct location.
@Startup(StartupType.BOOTSTRAP)
@ApplicationScoped
public class AppSetup {

    // default groups
    private static final String DROOLS_WB_GROUP1 = "Group1";
    private static final String DROOLS_WB_GROUP2 = "Group2";

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
    private GroupService groupService;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private ConfigurationFactory configurationFactory;

    @PostConstruct
    public void assertPlayground() {
        // TODO in case groups are not defined
        Group group1 = groupService.getGroup( DROOLS_WB_GROUP1 );
        if ( group1 == null ) {
            group1 = groupService.createGroup( DROOLS_WB_GROUP1,
                                               "" );
            groupService.addRole( group1,
                                  "ADMIN" );
        }
        Group group2 = groupService.getGroup( DROOLS_WB_GROUP2 );
        if ( group2 == null ) {
            group2 = groupService.createGroup( DROOLS_WB_GROUP2,
                                               "" );
            groupService.addRole( group2,
                                  "NON_EXISTENT_ROLE" );
        }

        // TODO in case repo is not defined in system repository so we add default
        final Repository repository = repositoryService.getRepository( DROOLS_WB_PLAYGROUND_ALIAS );
        if ( repository == null ) {
            repositoryService.createRepository( DROOLS_WB_PLAYGROUND_SCHEME, DROOLS_WB_PLAYGROUND_ALIAS,
                                                new HashMap<String, Object>() {{
                                                    put( "origin", DROOLS_WB_PLAYGROUND_ORIGIN );
                                                    put( "username", DROOLS_WB_PLAYGROUND_UID );
                                                    put( "crypt:password", DROOLS_WB_PLAYGROUND_PWD );
                                                }} );
        }

        //Define mandatory properties
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

        //Define properties required by the Work Items Editor
        List<ConfigGroup> editorConfigGroups = configurationService.getConfiguration( ConfigType.EDITOR );
        boolean workItemsEditorSettingsDefined = false;
        for ( ConfigGroup editorConfigGroup : editorConfigGroups ) {
            if ( WorkItemsEditorService.WORK_ITEMS_EDITOR_SETTINGS.equals( editorConfigGroup.getName() ) ) {
                workItemsEditorSettingsDefined = true;
                break;
            }
        }
        if ( !workItemsEditorSettingsDefined ) {
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
                                                                 "[\n" +
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
