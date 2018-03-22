/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ServiceTaskFactoryTest {

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
                    .setIconData(WID_ICON_DATA)
                    .setParameters(WID_PARAMS)
                    .setResults(WID_RESULTS);

    private ServiceTaskFactory tested;

    @Before
    public void init() {
        when(registry.get(eq(WID_NAME))).thenReturn(WORK_ITEM_DEFINITION);
        tested = new ServiceTaskFactory(() -> registry);
    }

    @Test
    public void testAccepts() {
        assertFalse(tested.accepts(getId(UserTask.class)));
        assertFalse(tested.accepts(getId(NoneTask.class)));
        assertFalse(tested.accepts(getId(ScriptTask.class)));
        assertFalse(tested.accepts(getId(BusinessRuleTask.class)));
        assertTrue(tested.accepts(getId(ServiceTask.class)));
        assertTrue(tested.accepts(getId(ServiceTask.class) + ".Email"));
        assertTrue(tested.accepts(getId(ServiceTask.class) + ".Log"));
        assertTrue(tested.accepts(getWorkItemDefinitionName()));
    }

    @Test
    public void testBuild() {
        final ServiceTask serviceTask = tested.build(getWorkItemDefinitionName());
        assertEquals(WID_NAME, serviceTask.getName());
        assertEquals(WID_NAME, serviceTask.getTaskType().getRawType());
        assertEquals(WID_NAME, serviceTask.getExecutionSet().getTaskName().getValue());
        assertEquals(WID_CAT, serviceTask.getCategory());
        assertEquals(WID_HANDLER, serviceTask.getDefaultHandler());
        assertEquals(WID_DISP_NAME, serviceTask.getGeneral().getName().getValue());
        assertEquals(WID_DOC, serviceTask.getGeneral().getDocumentation().getValue());
        assertEquals(WID_DESC, serviceTask.getDescription());
        assertEquals(WID_PARAMS + WID_RESULTS, serviceTask.getDataIOSet().getAssignmentsinfo().getValue());
    }

    private static String getWorkItemDefinitionName() {
        return getId(ServiceTask.class) + "." + WID_NAME;
    }

    private static String getId(final Class<?> clazz) {
        return BindableAdapterUtils.getGenericClassName(clazz);
    }
}
