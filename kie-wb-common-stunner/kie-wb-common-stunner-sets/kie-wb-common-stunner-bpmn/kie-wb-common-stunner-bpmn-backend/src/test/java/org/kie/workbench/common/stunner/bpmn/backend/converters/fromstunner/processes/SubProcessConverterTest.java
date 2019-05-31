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

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.processes;

import java.util.UUID;

import org.eclipse.bpmn2.Activity;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.ConverterFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.DefinitionsBuildingContext;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.AdHocSubProcessPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.PropertyWriterFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.SubProcessPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.BaseAdHocSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EventSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocAutostart;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.IsAsync;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.GraphImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.graph.store.GraphNodeStoreImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SubProcessConverterTest {

    @Test
    public void JBPM_7525_eventSubProcessShouldStoreIsAsync() {
        ConverterFactory f = new ConverterFactory(new DefinitionsBuildingContext(
                new GraphImpl("x", new GraphNodeStoreImpl())),
                                                  new PropertyWriterFactory());
        SubProcessConverter c = f.subProcessConverter();

        NodeImpl<View<? extends BPMNViewDefinition>> n = new NodeImpl<>("n");
        EventSubprocess subProcessNode = new EventSubprocess();
        subProcessNode.getExecutionSet().setIsAsync(new IsAsync(true));
        n.setContent(new ViewImpl<>(subProcessNode, Bounds.create()));

        Activity activity = c.convertSubProcess(n).value().getFlowElement();
        Boolean value = CustomElement.async.of(activity).get();

        assertThat(value).isEqualTo(true);
    }

    private SubProcessConverter tested;

    @Before
    public void setUp() throws Exception {
        PropertyWriterFactory factory = new PropertyWriterFactory();

        DefinitionsBuildingContext definitionsBuildingContext = new DefinitionsBuildingContext(new GraphImpl("x", new GraphNodeStoreImpl()));

        tested = new SubProcessConverter(definitionsBuildingContext,
                                         factory,
                                         new ConverterFactory(definitionsBuildingContext, factory));
    }

    @Test
    public void testConvertAdHocSubprocessNode_autostart() {
        final AdHocSubprocess definition = new AdHocSubprocess();
        definition.getExecutionSet().setAdHocAutostart(new AdHocAutostart(true));
        final View<BaseAdHocSubprocess> view = new ViewImpl<>(definition, Bounds.create());
        final Node<View<BaseAdHocSubprocess>, ?> node = new NodeImpl<>(UUID.randomUUID().toString());
        node.setContent(view);

        SubProcessPropertyWriter writer = tested.convertAdHocSubprocessNode(node);
        assertTrue(AdHocSubProcessPropertyWriter.class.isInstance(writer));
        assertTrue(CustomElement.autoStart.of(writer.getFlowElement()).get());
    }

    @Test
    public void testConvertAdHocSubprocessNode_notautostart() {
        final AdHocSubprocess definition = new AdHocSubprocess();
        definition.getExecutionSet().setAdHocAutostart(new AdHocAutostart(false));
        final View<BaseAdHocSubprocess> view = new ViewImpl<>(definition, Bounds.create());
        final Node<View<BaseAdHocSubprocess>, ?> node = new NodeImpl<>(UUID.randomUUID().toString());
        node.setContent(view);

        SubProcessPropertyWriter writer = tested.convertAdHocSubprocessNode(node);
        assertTrue(AdHocSubProcessPropertyWriter.class.isInstance(writer));
        assertFalse(CustomElement.autoStart.of(writer.getFlowElement()).get());
    }
}