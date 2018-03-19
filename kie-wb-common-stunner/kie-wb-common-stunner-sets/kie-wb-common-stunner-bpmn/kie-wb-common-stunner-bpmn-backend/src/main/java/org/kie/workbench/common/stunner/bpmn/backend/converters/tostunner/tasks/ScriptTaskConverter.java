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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.tasks;

import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.ScriptTaskPropertyReader;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.ScriptTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.TaskGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.IsAsync;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Script;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTaskExecutionSet;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class ScriptTaskConverter {

    private final TypedFactoryManager factoryManager;
    private final PropertyReaderFactory propertyReaderFactory;

    public ScriptTaskConverter(TypedFactoryManager factoryManager, PropertyReaderFactory propertyReaderFactory) {
        this.factoryManager = factoryManager;
        this.propertyReaderFactory = propertyReaderFactory;
    }

    public Node<? extends View<? extends BPMNViewDefinition>, ?> convert(org.eclipse.bpmn2.ScriptTask task) {
        Node<View<ScriptTask>, Edge> node = factoryManager.newNode(task.getId(), ScriptTask.class);

        ScriptTask definition = node.getContent().getDefinition();
        ScriptTaskPropertyReader p = propertyReaderFactory.of(task);

        definition.setGeneral(new TaskGeneralSet(
                new Name(p.getName()),
                new Documentation(p.getDocumentation())
        ));

        definition.setExecutionSet(new ScriptTaskExecutionSet(
                new Script(p.getScript()),
                new IsAsync(p.isAsync())
        ));

        node.getContent().setBounds(p.getBounds());

        definition.setDimensionsSet(p.getRectangleDimensionsSet());
        definition.setBackgroundSet(p.getBackgroundSet());
        definition.setFontSet(p.getFontSet());

        definition.setSimulationSet(p.getSimulationSet());

        return node;
    }
}
