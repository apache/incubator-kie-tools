/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.bpmn.workitem;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ScriptTask;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CustomTaskFactoryTest {

    @Mock
    private WorkItemDefinitionRegistry registry;

    private static final String WID_NAME = "widName";
    private static final String WID_CAT = "widCategory";
    private static final String WID_HANDLER = "widHandler";
    private static final String WID_DISP_NAME = "widDisplayName";
    private static final String WID_DESC = "widDescription";
    private static final String WID_DOC = "widDocumentation";
    private static final String WID_ICON_DATA = "widIconData";
    private static final String WID_PARAMS = "widParameters";
    private static final String WID_RESULTS = "widResults";

    private static final WorkItemDefinition WORK_ITEM_DEFINITION =
            new WorkItemDefinition()
                    .setName(WID_NAME)
                    .setCategory(WID_CAT)
                    .setDefaultHandler(WID_HANDLER)
                    .setDisplayName(WID_DISP_NAME)
                    .setDescription(WID_DESC)
                    .setDocumentation(WID_DOC)
                    .setIconDefinition(new IconDefinition().setIconData(WID_ICON_DATA))
                    .setParameters(WID_PARAMS)
                    .setResults(WID_RESULTS);

    private CustomTaskFactory tested;

    @Before
    public void init() {
        when(registry.get(eq(WID_NAME))).thenReturn(WORK_ITEM_DEFINITION);
        tested = new CustomTaskFactory(() -> registry);
    }

    @Test
    public void testAccepts() {
        assertFalse(tested.accepts(getId(UserTask.class)));
        assertFalse(tested.accepts(getId(NoneTask.class)));
        assertFalse(tested.accepts(getId(ScriptTask.class)));
        assertFalse(tested.accepts(getId(BusinessRuleTask.class)));
        assertTrue(tested.accepts(getId(CustomTask.class)));
        assertTrue(tested.accepts(getId(CustomTask.class) + ".Email"));
        assertTrue(tested.accepts(getId(CustomTask.class) + ".Log"));
        assertTrue(tested.accepts(getWorkItemDefinitionName()));
    }

    @Test
    public void testBuild() {
        final CustomTask customTask = tested.build(getWorkItemDefinitionName());
        assertEquals(WID_NAME, customTask.getName());
        assertEquals(WID_NAME, customTask.getTaskType().getRawType());
        assertEquals(WID_NAME, customTask.getExecutionSet().getTaskName().getValue());
        assertEquals(WID_CAT, customTask.getCategory());
        assertEquals(WID_HANDLER, customTask.getDefaultHandler());
        assertEquals(WID_DISP_NAME, customTask.getGeneral().getName().getValue());
        assertEquals(WID_DOC, customTask.getGeneral().getDocumentation().getValue());
        assertEquals(WID_DESC, customTask.getDescription());
        assertEquals(WID_PARAMS + WID_RESULTS, customTask.getDataIOSet().getAssignmentsinfo().getValue());
    }

    @Test
    public void testBuildItemNullWID() {
        final CustomTask customTask = tested.buildItem("Non Existent Wid Name");
        // Check for default custom task values
        assertTrue(customTask.getName().equals("Custom Task"));
        assertNull(customTask.getTaskType().getRawType());
        assertTrue(customTask.getExecutionSet().getTaskName().getValue().equals("Service Task"));
        assertTrue(customTask.getCategory().equals("CustomTasks"));
        assertTrue(customTask.getDefaultHandler().isEmpty());
        assertTrue(customTask.getGeneral().getName().getValue().equals("Custom Task"));
        assertTrue(customTask.getGeneral().getDocumentation().getValue().isEmpty());
        assertTrue(customTask.getDescription().equals("Custom Task"));
        assertTrue(customTask.getDataIOSet().getAssignmentsinfo().getValue().isEmpty());
    }

    private static String getWorkItemDefinitionName() {
        return getId(CustomTask.class) + "." + WID_NAME;
    }

    private static String getId(final Class<?> clazz) {
        return BindableAdapterUtils.getGenericClassName(clazz);
    }
}
