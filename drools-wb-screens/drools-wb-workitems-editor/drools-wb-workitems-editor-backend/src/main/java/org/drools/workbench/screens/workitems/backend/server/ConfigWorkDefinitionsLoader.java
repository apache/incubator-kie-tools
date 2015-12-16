/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.workitems.backend.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

import org.drools.core.process.core.WorkDefinition;
import org.drools.workbench.screens.workitems.service.WorkItemsEditorService;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigItem;
import org.guvnor.structure.server.config.ConfigType;
import org.guvnor.structure.server.config.ConfigurationService;

/**
 * Class to load Work Definitions from configuration
 */
public class ConfigWorkDefinitionsLoader {

    @Inject
    private ConfigurationService configurationService;

    public Map<String, WorkDefinition> loadWorkDefinitions() {
        //Find all configured WIDs
        final Map<String, WorkDefinition> workDefinitions = new HashMap<String, WorkDefinition>();
        final List<ConfigGroup> configGroups = configurationService.getConfiguration( ConfigType.EDITOR );
        if ( configGroups == null || configGroups.isEmpty() ) {
            return workDefinitions;
        }

        //Load configured WIDs
        final List<String> definitions = new ArrayList<String>();
        for ( ConfigGroup configGroup : configGroups ) {
            if ( WorkItemsEditorService.WORK_ITEM_DEFINITION.equals( configGroup.getName() ) ) {
                for ( ConfigItem configItem : configGroup.getItems() ) {
                    definitions.add( configGroup.getConfigItemValue( configItem.getName() ) );
                }
            }
        }

        //Parse MVEL expressions into model
        workDefinitions.putAll( WorkDefinitionsParser.parse( definitions ) );

        return workDefinitions;
    }

}
