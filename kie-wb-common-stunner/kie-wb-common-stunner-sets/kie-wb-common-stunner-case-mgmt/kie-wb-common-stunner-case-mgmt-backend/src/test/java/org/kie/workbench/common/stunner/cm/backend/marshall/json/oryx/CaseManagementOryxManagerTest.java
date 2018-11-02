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

package org.kie.workbench.common.stunner.cm.backend.marshall.json.oryx;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.Bpmn2OryxIdMappings;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.OryxIdMappings;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.AssigneeTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.AssignmentsTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.BooleanTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.Bpmn2OryxPropertyManager;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.Bpmn2OryxPropertySerializer;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.ColorTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.DoubleTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.EnumTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.IntegerTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.StringTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.VariablesTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndTerminateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.ExclusiveGateway;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.Lane;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ParallelGateway;
import org.kie.workbench.common.stunner.bpmn.definition.ScriptTask;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.cm.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram;
import org.kie.workbench.common.stunner.cm.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CaseManagementOryxManagerTest {

    @Mock
    private DefinitionManager definitionManager;

    @Mock
    private FactoryManager factoryManager;

    private CaseManagementOryxManager oryxManager;

    @Before
    public void setup() {
        OryxIdMappings oryxIdMappings = new Bpmn2OryxIdMappings(definitionManager, WorkItemDefinitionEmptyRegistry::new);
        DefinitionUtils definitionUtils = new DefinitionUtils(definitionManager,
                                                              factoryManager,
                                                              null);// TODO!
        List<Bpmn2OryxPropertySerializer<?>> oryxPropertySerializers = new ArrayList<>();
        oryxPropertySerializers.add(new AssigneeTypeSerializer());
        oryxPropertySerializers.add(new AssignmentsTypeSerializer());
        oryxPropertySerializers.add(new BooleanTypeSerializer());
        oryxPropertySerializers.add(new ColorTypeSerializer());
        oryxPropertySerializers.add(new DoubleTypeSerializer());
        oryxPropertySerializers.add(new EnumTypeSerializer(definitionUtils));
        oryxPropertySerializers.add(new IntegerTypeSerializer());
        oryxPropertySerializers.add(new StringTypeSerializer());
        oryxPropertySerializers.add(new VariablesTypeSerializer());
        Bpmn2OryxPropertyManager oryxPropertyManager = new Bpmn2OryxPropertyManager(oryxPropertySerializers);
        this.oryxManager = new CaseManagementOryxManager(oryxIdMappings,
                                                         oryxPropertyManager);
    }

    @Test
    public void checkGetDefinitionClasses() {
        final Set<Class<?>> classes = oryxManager.getDefinitionClasses();
        assertEquals(18,
                     classes.size());
        assertTrue(classes.contains(CaseManagementDiagram.class));
        assertTrue(classes.contains(Lane.class));
        assertTrue(classes.contains(NoneTask.class));
        assertTrue(classes.contains(UserTask.class));
        assertTrue(classes.contains(ScriptTask.class));
        assertTrue(classes.contains(BusinessRuleTask.class));
        assertTrue(classes.contains(StartNoneEvent.class));
        assertTrue(classes.contains(StartSignalEvent.class));
        assertTrue(classes.contains(StartTimerEvent.class));
        assertTrue(classes.contains(EndNoneEvent.class));
        assertTrue(classes.contains(EndTerminateEvent.class));
        assertTrue(classes.contains(IntermediateTimerEvent.class));
        assertTrue(classes.contains(ParallelGateway.class));
        assertTrue(classes.contains(ExclusiveGateway.class));
        assertTrue(classes.contains(AdHocSubprocess.class));
        assertTrue(classes.contains(ReusableSubprocess.class));
        assertTrue(classes.contains(SequenceFlow.class));
    }
}
