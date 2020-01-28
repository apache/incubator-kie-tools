/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.workitems.backend.server;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.workbench.screens.workitems.service.WorkItemsEditorService;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigType;
import org.guvnor.structure.server.config.ConfigurationFactory;

@ApplicationScoped
public class WorkbenchConfigurationHelper {

    @Inject
    private ConfigurationFactory configurationFactory;

    public ConfigGroup getWorkItemElementDefinitions() {
        // Work Item Definition elements used when creating Work Item Definitions.
        // Each entry in this file represents a Button in the Editor's Palette:-
        //   - Underscores ('_') in the key will be converted in whitespaces (' ') and
        //     will be used as Button's labels.
        //   - The value will be the text pasted into the editor when an element in the
        //     palette is selected. You can use a pipe ('|') to specify the place where
        //     the cursor should be put after pasting the element into the editor.
        final ConfigGroup group = configurationFactory.newConfigGroup(ConfigType.EDITOR,
                                                                      WorkItemsEditorService.WORK_ITEMS_EDITOR_SETTINGS,
                                                                      "");
        group.addConfigItem(configurationFactory.newConfigItem(WorkItemsEditorService.WORK_ITEMS_EDITOR_SETTINGS_DEFINITION,
                                                               "[\n" +
                                                                       "  [\n" +
                                                                       "    \"name\" : \"MyTask\", \n" +
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
                                                                       "]"));
        group.addConfigItem(configurationFactory.newConfigItem(WorkItemsEditorService.WORK_ITEMS_EDITOR_SETTINGS_PARAMETER,
                                                               "\"MyParam\" : new StringDataType()"));
        group.addConfigItem(configurationFactory.newConfigItem(WorkItemsEditorService.WORK_ITEMS_EDITOR_SETTINGS_RESULT,
                                                               "\"Result\" : new ObjectDataType()"));
        group.addConfigItem(configurationFactory.newConfigItem(WorkItemsEditorService.WORK_ITEMS_EDITOR_SETTINGS_DISPLAY_NAME,
                                                               "\"displayName\" : \"My Task\""));

        group.addConfigItem(configurationFactory.newConfigItem(WorkItemsEditorService.WORK_ITEMS_EDITOR_SETTINGS_PARAMETER_VALUES,
                                                               "\"parameterValues\" : [\n" +
                                                                       "   \"MyFirstParam\" : \"A,B,C\",   \n" +
                                                                       "   \"MySecondParam\" : \"X,Y,Z\"\n" +
                                                                       "]"));

        group.addConfigItem(configurationFactory.newConfigItem(WorkItemsEditorService.WORK_ITEMS_EDITOR_SETTINGS_CUSTOM_EDITOR,
                                                               "\"customEditor\" : \"true\""));
        group.addConfigItem(configurationFactory.newConfigItem(WorkItemsEditorService.WORK_ITEMS_EDITOR_SETTINGS_DEFAULT_HANDLER,
                                                               "\"defaultHandler\" : \"mvel: new DefaultHandler()\""));
        return group;
    }

    /**
     * Sets ConfigurationFactory (for tests)
     * @param configurationFactory
     */
    public void setConfigurationFactory(ConfigurationFactory configurationFactory) {
        this.configurationFactory = configurationFactory;
    }
}
