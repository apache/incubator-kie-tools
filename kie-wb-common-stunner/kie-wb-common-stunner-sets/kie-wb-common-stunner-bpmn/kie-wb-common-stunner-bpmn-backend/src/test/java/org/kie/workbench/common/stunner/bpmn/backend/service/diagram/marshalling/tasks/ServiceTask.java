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

package org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.tasks;

import java.util.Arrays;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.Marshaller;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;
import org.kie.workbench.common.stunner.bpmn.workitem.BaseServiceTask;
import org.kie.workbench.common.stunner.bpmn.workitem.ServiceTaskExecutionSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.Marshaller.NEW;

@RunWith(Parameterized.class)
public abstract class ServiceTask<T extends BaseServiceTask> extends Task<T> {

    @Parameterized.Parameters
    public static List<Object[]> marshallers() {
        return Arrays.asList(new Object[][]{
                {NEW}
        });
    }

    ServiceTask(Marshaller marshallerType) {
        super(marshallerType);
    }

    @Ignore("Test is ignored, because does not make sense, since there is only new Marhsaller tested.")
    @Test
    @Override
    public void testMigration() {
    }

    protected void assertServiceTaskExecutionSet(ServiceTaskExecutionSet executionSet,
                                                 String onEntryActionScriptValue,
                                                 String onEntryActionScriptLanguage,
                                                 String onExitActionScriptValue,
                                                 String onExitActionScriptLanguage,
                                                 boolean isAsync,
                                                 boolean adHocAutostart) {
        assertNotNull(executionSet);
        assertNotNull(executionSet.getOnEntryAction());
        assertNotNull(executionSet.getOnExitAction());
        assertNotNull(executionSet.getIsAsync());
        assertNotNull(executionSet.getAdHocAutostart());

        assertNotNull(executionSet.getOnEntryAction().getValue());
        assertNotNull(executionSet.getOnExitAction().getValue());

        List<ScriptTypeValue> onEntryScriptTypeValues = executionSet.getOnEntryAction().getValue().getValues();
        List<ScriptTypeValue> onExitScriptTypeValues = executionSet.getOnExitAction().getValue().getValues();

        assertNotNull(onEntryScriptTypeValues);
        assertNotNull(onExitScriptTypeValues);
        assertNotNull(onEntryScriptTypeValues.get(0));
        assertNotNull(onExitScriptTypeValues.get(0));

        assertEquals(onEntryActionScriptValue, onEntryScriptTypeValues.get(0).getScript());
        assertEquals(onEntryActionScriptLanguage, onEntryScriptTypeValues.get(0).getLanguage());
        assertEquals(onExitActionScriptValue, onExitScriptTypeValues.get(0).getScript());
        assertEquals(onExitActionScriptLanguage, onExitScriptTypeValues.get(0).getLanguage());
        assertEquals(isAsync, executionSet.getIsAsync().getValue());
        assertEquals(adHocAutostart, executionSet.getAdHocAutostart().getValue());
    }

    protected void assertDataIOSet(DataIOSet dataIOSet, String value) {
        assertNotNull(dataIOSet);
        assertNotNull(dataIOSet.getAssignmentsinfo());
        assertEquals(value, dataIOSet.getAssignmentsinfo().getValue());
    }

    // The test is already defined in parent Task test class.
    @Test
    @Override
    public void testMarshallTopLevelTaskEmptyProperties() throws Exception {
        checkTaskMarshalling(getEmptyTopLevelTaskId(), ZERO_INCOME_EDGES, HAS_NO_OUTCOME_EDGE);
    }

    // The test is already defined in parent Task test class.
    @Test
    @Override
    public void testMarshallSubprocessLevelTaskOneIncomeEmptyProperties() throws Exception {
        checkTaskMarshalling(getEmptySubprocessLevelTaskOneIncomeId(), ONE_INCOME_EDGE, HAS_OUTCOME_EDGE);
    }

    // The test is already defined in parent Task test class.
    @Test
    @Override
    public void testMarshallSubprocessLevelTaskTwoIncomesEmptyProperties() throws Exception {
        checkTaskMarshalling(getEmptySubprocessLevelTaskTwoIncomesId(), TWO_INCOME_EDGES, HAS_OUTCOME_EDGE);
    }

    // The test is already defined in parent Task test class.
    @Test
    @Override
    public void testMarshallTopLevelTaskOneIncomeEmptyProperties() throws Exception {
        checkTaskMarshalling(getEmptyTopLevelTaskOneIncomeId(), ONE_INCOME_EDGE, HAS_OUTCOME_EDGE);
    }

    // The test is already defined in parent Task test class.
    @Test
    @Override
    public void testMarshallTopLevelTaskTwoIncomesEmptyProperties() throws Exception {
        checkTaskMarshalling(getEmptyTopLevelTaskTwoIncomesId(), TWO_INCOME_EDGES, HAS_OUTCOME_EDGE);
    }

    // The test is already defined in parent Task test class.
    @Test
    @Override
    public void testMarshallSubprocessLevelTaskEmptyProperties() throws Exception {
        checkTaskMarshalling(getEmptySubprocessLevelTaskId(), ZERO_INCOME_EDGES, HAS_NO_OUTCOME_EDGE);
    }
}
