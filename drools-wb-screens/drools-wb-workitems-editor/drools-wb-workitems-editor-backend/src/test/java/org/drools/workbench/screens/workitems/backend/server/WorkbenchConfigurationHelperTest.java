/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import org.drools.workbench.screens.workitems.service.WorkItemsEditorService;
import org.guvnor.structure.backend.config.ConfigurationFactoryImpl;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class WorkbenchConfigurationHelperTest {

    private WorkbenchConfigurationHelper workbenchConfigurationHelper;

    @Before
    public void setUp() throws Exception {
        workbenchConfigurationHelper = new WorkbenchConfigurationHelper();
        workbenchConfigurationHelper.setConfigurationFactory( new ConfigurationFactoryImpl() );
    }

    @Test
    public void testWorkitemDefinitions() throws Exception {
        ConfigGroup group = workbenchConfigurationHelper.getWorkItemElementDefinitions();

        assertEquals(ConfigType.EDITOR, group.getType());
        assertEquals(WorkItemsEditorService.WORK_ITEMS_EDITOR_SETTINGS, group.getName());
        assertEquals("", group.getDescription());

        assertEquals(7, group.getItems().size());

        assertEquals("\"customEditor\" : \"true\"", group.getConfigItemValue(WorkItemsEditorService.WORK_ITEMS_EDITOR_SETTINGS_CUSTOM_EDITOR));

        assertEquals("\"parameterValues\" : [\n" +
                "   \"MyFirstParam\" : \"A,B,C\",   \n" +
                "   \"MySecondParam\" : \"X,Y,Z\"\n" +
                "]", group.getConfigItemValue(WorkItemsEditorService.WORK_ITEMS_EDITOR_SETTINGS_PARAMETER_VALUES));

        assertEquals(group.getConfigItemValue(WorkItemsEditorService.WORK_ITEMS_EDITOR_SETTINGS_DEFAULT_HANDLER), "\"defaultHandler\" : \"mvel: new DefaultHandler()\"");
    }

}
