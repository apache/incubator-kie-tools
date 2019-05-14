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

package org.kie.workbench.common.stunner.bpmn.backend.service.diagram;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.Bpmn2OryxIdMappings;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.Bpmn2OryxManager;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.AssignmentsTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.BooleanTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.Bpmn2OryxPropertyManager;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.Bpmn2OryxPropertySerializer;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.ColorTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.DoubleTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.EnumTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.IntegerTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.NotificationsTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.ReassignmentsTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.StringTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.TimerSettingsTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.VariablesTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionRegistry;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

public class Oryx {

    public static Bpmn2OryxManager createOryxManager(final DefinitionManager definitionManager,
                                                     final DefinitionUtils definitionUtils,
                                                     final Supplier<WorkItemDefinitionRegistry> workItemDefinitionRegistry) {
        // Bpmn 2 oryx stuff.
        Bpmn2OryxIdMappings oryxIdMappings = new Bpmn2OryxIdMappings(definitionManager,
                                                                     workItemDefinitionRegistry);
        StringTypeSerializer stringTypeSerializer = new StringTypeSerializer();
        BooleanTypeSerializer booleanTypeSerializer = new BooleanTypeSerializer();
        ColorTypeSerializer colorTypeSerializer = new ColorTypeSerializer();
        DoubleTypeSerializer doubleTypeSerializer = new DoubleTypeSerializer();
        IntegerTypeSerializer integerTypeSerializer = new IntegerTypeSerializer();
        EnumTypeSerializer enumTypeSerializer = new EnumTypeSerializer(definitionUtils);
        AssignmentsTypeSerializer assignmentsTypeSerializer = new AssignmentsTypeSerializer();
        NotificationsTypeSerializer notificationsTypeSerializer = new NotificationsTypeSerializer();
        ReassignmentsTypeSerializer reassignmentsTypeSerializer = new ReassignmentsTypeSerializer();
        VariablesTypeSerializer variablesTypeSerializer = new VariablesTypeSerializer();
        TimerSettingsTypeSerializer timerSettingsTypeSerializer = new TimerSettingsTypeSerializer();
        List<Bpmn2OryxPropertySerializer<?>> propertySerializers = new LinkedList<>();
        propertySerializers.add(stringTypeSerializer);
        propertySerializers.add(booleanTypeSerializer);
        propertySerializers.add(colorTypeSerializer);
        propertySerializers.add(doubleTypeSerializer);
        propertySerializers.add(integerTypeSerializer);
        propertySerializers.add(enumTypeSerializer);
        propertySerializers.add(assignmentsTypeSerializer);
        propertySerializers.add(notificationsTypeSerializer);
        propertySerializers.add(reassignmentsTypeSerializer);
        propertySerializers.add(variablesTypeSerializer);
        propertySerializers.add(timerSettingsTypeSerializer);

        Bpmn2OryxPropertyManager oryxPropertyManager = new Bpmn2OryxPropertyManager(propertySerializers);
        Bpmn2OryxManager oryxManager = new Bpmn2OryxManager(oryxIdMappings,
                                                            oryxPropertyManager);
        oryxManager.init();
        return oryxManager;
    }
}
