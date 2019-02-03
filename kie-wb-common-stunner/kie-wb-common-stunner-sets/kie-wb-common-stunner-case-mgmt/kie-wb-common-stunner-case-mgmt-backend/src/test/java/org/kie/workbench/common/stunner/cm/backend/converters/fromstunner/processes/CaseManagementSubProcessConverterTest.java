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

package org.kie.workbench.common.stunner.cm.backend.converters.fromstunner.processes;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.DefinitionsBuildingContext;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.SubProcessPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.definition.BaseAdHocSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocAutostart;
import org.kie.workbench.common.stunner.cm.backend.converters.fromstunner.CaseManagementConverterFactory;
import org.kie.workbench.common.stunner.cm.backend.converters.fromstunner.properties.CaseManagementAdHocSubProcessPropertyWriter;
import org.kie.workbench.common.stunner.cm.backend.converters.fromstunner.properties.CaseManagementPropertyWriterFactory;
import org.kie.workbench.common.stunner.cm.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.GraphImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.graph.store.GraphNodeStoreImpl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CaseManagementSubProcessConverterTest {

    private CaseManagementSubProcessConverter tested;

    @Before
    public void setUp() throws Exception {
        CaseManagementPropertyWriterFactory factory = new CaseManagementPropertyWriterFactory();

        DefinitionsBuildingContext definitionsBuildingContext = new DefinitionsBuildingContext(new GraphImpl("x", new GraphNodeStoreImpl()),
                                                                                               CaseManagementDiagram.class);

        tested = new CaseManagementSubProcessConverter(definitionsBuildingContext,
                                                       factory,
                                                       new CaseManagementConverterFactory(definitionsBuildingContext, factory));
    }

    @Test
    public void testConvertAdHocSubprocessNode_autostart() throws Exception {
        final AdHocSubprocess definition = new AdHocSubprocess();
        definition.getExecutionSet().setAdHocAutostart(new AdHocAutostart(true));
        final View<BaseAdHocSubprocess> view = new ViewImpl<>(definition, Bounds.create());
        final Node<View<BaseAdHocSubprocess>, ?> node = new NodeImpl<>(UUID.randomUUID().toString());
        node.setContent(view);

        SubProcessPropertyWriter writer = tested.convertAdHocSubprocessNode(node);
        assertTrue(CaseManagementAdHocSubProcessPropertyWriter.class.isInstance(writer));
        assertTrue(CustomElement.autoStart.of(writer.getFlowElement()).get());
    }

    @Test
    public void testConvertAdHocSubprocessNode_notautostart() throws Exception {
        final AdHocSubprocess definition = new AdHocSubprocess();
        definition.getExecutionSet().setAdHocAutostart(new AdHocAutostart(false));
        final View<BaseAdHocSubprocess> view = new ViewImpl<>(definition, Bounds.create());
        final Node<View<BaseAdHocSubprocess>, ?> node = new NodeImpl<>(UUID.randomUUID().toString());
        node.setContent(view);

        SubProcessPropertyWriter writer = tested.convertAdHocSubprocessNode(node);
        assertTrue(CaseManagementAdHocSubProcessPropertyWriter.class.isInstance(writer));
        assertFalse(CustomElement.autoStart.of(writer.getFlowElement()).get());
    }
}