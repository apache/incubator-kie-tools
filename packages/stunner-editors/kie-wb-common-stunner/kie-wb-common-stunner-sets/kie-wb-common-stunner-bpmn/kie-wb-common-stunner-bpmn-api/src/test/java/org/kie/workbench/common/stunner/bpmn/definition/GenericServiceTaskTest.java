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


package org.kie.workbench.common.stunner.bpmn.definition;

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.TaskGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.service.GenericServiceTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskType;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskTypes;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.AdvancedData;

import static junit.framework.TestCase.assertEquals;

public class GenericServiceTaskTest {

    @Test
    public void setAndGetExecutionSet() {
        GenericServiceTask genericServiceTask = new GenericServiceTask(new TaskGeneralSet(new Name("Service Task"),
                                                                                          new Documentation("")),
                                                                       new GenericServiceTaskExecutionSet(),
                                                                       new BackgroundSet(),
                                                                       new FontSet(),
                                                                       new RectangleDimensionsSet(),
                                                                       new SimulationSet(),
                                                                       new TaskType(TaskTypes.SERVICE_TASK),
                                                                       new AdvancedData());

        assertEquals(new GenericServiceTaskExecutionSet(), genericServiceTask.getExecutionSet());
        GenericServiceTaskExecutionSet set = new GenericServiceTaskExecutionSet();
        genericServiceTask.setExecutionSet(set);

        assertEquals(set, genericServiceTask.getExecutionSet());
    }

    @Test
    public void testHashCode() {
        GenericServiceTask a = new GenericServiceTask();
        GenericServiceTask b = new GenericServiceTask();

        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void equals() {
        GenericServiceTask a = new GenericServiceTask();
        GenericServiceTask b = new GenericServiceTask();
        assertEquals(a, b);
    }
}