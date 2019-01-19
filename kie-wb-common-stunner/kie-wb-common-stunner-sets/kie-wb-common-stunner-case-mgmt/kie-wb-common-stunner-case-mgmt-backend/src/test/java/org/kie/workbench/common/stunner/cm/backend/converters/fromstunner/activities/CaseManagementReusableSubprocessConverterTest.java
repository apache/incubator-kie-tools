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

package org.kie.workbench.common.stunner.cm.backend.converters.fromstunner.activities;

import java.util.UUID;

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.PropertyWriter;
import org.kie.workbench.common.stunner.cm.backend.converters.customproperties.CaseManagementCustomElement;
import org.kie.workbench.common.stunner.cm.backend.converters.fromstunner.properties.CaseManagementCallActivityPropertyWriter;
import org.kie.workbench.common.stunner.cm.backend.converters.fromstunner.properties.CaseManagementPropertyWriterFactory;
import org.kie.workbench.common.stunner.cm.definition.BaseCaseManagementReusableSubprocess;
import org.kie.workbench.common.stunner.cm.definition.CaseReusableSubprocess;
import org.kie.workbench.common.stunner.cm.definition.ProcessReusableSubprocess;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CaseManagementReusableSubprocessConverterTest {

    private CaseManagementReusableSubprocessConverter tested =
            new CaseManagementReusableSubprocessConverter(new CaseManagementPropertyWriterFactory());

    @Test
    public void testToFlowElement_case() throws Exception {
        final BaseCaseManagementReusableSubprocess definition = new CaseReusableSubprocess();
        final View<BaseCaseManagementReusableSubprocess> view = new ViewImpl<>(definition, Bounds.create());
        final Node<View<BaseCaseManagementReusableSubprocess>, ?> node = new NodeImpl<>(UUID.randomUUID().toString());
        node.setContent(view);

        final PropertyWriter propertyWriter = tested.toFlowElement(node);
        assertTrue(propertyWriter instanceof CaseManagementCallActivityPropertyWriter);
        assertTrue(CaseManagementCustomElement.isCase.of(propertyWriter.getFlowElement()).get());
    }

    @Test
    public void testToFlowElement_process() throws Exception {
        final BaseCaseManagementReusableSubprocess definition = new ProcessReusableSubprocess();
        final View<BaseCaseManagementReusableSubprocess> view = new ViewImpl<>(definition, Bounds.create());
        final Node<View<BaseCaseManagementReusableSubprocess>, ?> node = new NodeImpl<>(UUID.randomUUID().toString());
        node.setContent(view);

        final PropertyWriter propertyWriter = tested.toFlowElement(node);
        assertTrue(propertyWriter instanceof CaseManagementCallActivityPropertyWriter);
        assertFalse(CaseManagementCustomElement.isCase.of(propertyWriter.getFlowElement()).get());
    }
}