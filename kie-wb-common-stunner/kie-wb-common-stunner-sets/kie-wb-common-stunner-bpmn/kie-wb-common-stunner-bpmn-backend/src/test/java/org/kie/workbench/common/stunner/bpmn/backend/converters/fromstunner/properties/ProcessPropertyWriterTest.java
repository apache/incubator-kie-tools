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

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.bpmn2.di.BPMNShape;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.ProcessPropertyReader;
import org.kie.workbench.common.stunner.bpmn.definition.property.cm.CaseFileVariables;
import org.kie.workbench.common.stunner.bpmn.definition.property.cm.CaseIdPrefix;
import org.kie.workbench.common.stunner.bpmn.definition.property.cm.CaseRoles;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.GlobalVariables;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.MetaDataAttributes;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.DefaultImport;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessVariables;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.di;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProcessPropertyWriterTest {

    ProcessPropertyWriter p;
    FlatVariableScope variableScope;

    @Before
    public void before() {
        this.variableScope = new FlatVariableScope();
        this.p = new ProcessPropertyWriter(
                bpmn2.createProcess(), variableScope);
    }

    @Test
    public void setIdWithWhitespace() {
        p.setId("some weird   id \t");
        Process process = p.getProcess();
        assertThat(process.getId()).isEqualTo("someweirdid");
    }

    @Test
    public void addChildElement() {
        Process process = p.getProcess();

        BoundaryEventPropertyWriter boundaryEventPropertyWriter =
                new BoundaryEventPropertyWriter(bpmn2.createBoundaryEvent(), variableScope);

        UserTaskPropertyWriter userTaskPropertyWriter =
                new UserTaskPropertyWriter(bpmn2.createUserTask(), variableScope);

        p.addChildElement(boundaryEventPropertyWriter);
        p.addChildElement(userTaskPropertyWriter);

        // boundary event should always occur after other nodes (compat with old marshallers)
        assertThat(process.getFlowElements().get(0)).isEqualTo(userTaskPropertyWriter.getFlowElement());
        assertThat(process.getFlowElements().get(1)).isEqualTo(boundaryEventPropertyWriter.getFlowElement());
    }

    @Test
    public void addChildShape() {
        BPMNShape bpmnShape = di.createBPMNShape();
        bpmnShape.setId("a");
        p.addChildShape(bpmnShape);
        assertThatThrownBy(() -> p.addChildShape(bpmnShape))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("Cannot add the same shape twice");
    }

    @Test
    public void addChildEdge() {
        BPMNEdge bpmnEdge = di.createBPMNEdge();
        bpmnEdge.setId("a");
        p.addChildEdge(bpmnEdge);
        assertThatThrownBy(() -> p.addChildEdge(bpmnEdge))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("Cannot add the same edge twice");
    }

    @Test
    public void caseRoles() {
        CaseRoles caseRole = new CaseRoles("role");
        p.setCaseRoles(caseRole);
        String cdata = CustomElement.caseRole.of(p.getProcess()).get();
        assertThat("role").isEqualTo(CustomElement.caseRole.stripCData(cdata));
    }

    @Test
    public void caseIdPrefix() {
        CaseIdPrefix caseIdPrefix = new CaseIdPrefix("caseIdPrefix");
        p.setCaseIdPrefix(caseIdPrefix);
        String cdata = CustomElement.caseIdPrefix.of(p.getProcess()).get();
        assertThat("caseIdPrefix").isEqualTo(CustomElement.caseIdPrefix.stripCData(cdata));
    }

    @Test
    public void caseFileVariables() {
        CaseFileVariables caseFileVariables = new CaseFileVariables("CFV1:Boolean,CFV2:Boolean,CFV3:Boolean");
        p.setCaseFileVariables(caseFileVariables);
        assertThat(p.itemDefinitions.size() == 3);
    }

    @Test
    public void executable() {
        p.setExecutable(true);
        assertTrue(p.getProcess().isIsExecutable());
        p.setExecutable(false);
        assertFalse(p.getProcess().isIsExecutable());
    }

    @Test
    public void processType() {
        p.setType("Private");
        assertEquals("Private", p.getProcess().getProcessType().getName());
    }

    @Test
    public void globalVariables() {
        GlobalVariables globalVariables = new GlobalVariables("GV1:Boolean,GV2:Boolean,GV3:Integer");
        p.setGlobalVariables(globalVariables);
        String globalVariablesString = CustomElement.globalVariables.of(p.getProcess()).get();
        assertThat(globalVariablesString).isEqualTo("GV1:Boolean,GV2:Boolean,GV3:Integer");
    }

    @Test
    public void processVariables() {
        ProcessVariables processVariables = new ProcessVariables("GV1:Boolean:true,GV2:Boolean:true,GV3:Integer:false");
        p.setProcessVariables(processVariables);

        final ProcessPropertyReader pp = new ProcessPropertyReader(
                p.getProcess(), p.getBpmnDiagram(), p.getShape(), 1.0);
        String processVariablesString = pp.getProcessVariables();
        assertThat(processVariablesString).isEqualTo("GV1:Boolean:true,GV2:Boolean:true,GV3:Integer:false");
    }

    @Test
    public void slaDueDate() {
        SLADueDate slaDueDate = new SLADueDate("12/25/1983");
        p.setSlaDueDate(slaDueDate);
        String slaDueDateString = CustomElement.slaDueDate.of(p.getProcess()).get();
        assertThat(slaDueDateString).isEqualTo("<![CDATA[12/25/1983]]>");
    }

    @Test
    public void defaultImports() {
        List<DefaultImport> defaultImports = new ArrayList<>();
        defaultImports.add(new DefaultImport("className1"));
        defaultImports.add(new DefaultImport("className2"));
        defaultImports.add(new DefaultImport("className3"));

        p.setDefaultImports(defaultImports);

        assertEquals(defaultImports, CustomElement.defaultImports.of(p.getProcess()).get());
    }

    @Test
    public void testSetDocumentationNotEmpty() {
        p.setDocumentation("DocumentationValue");
        assertNotNull(p.getProcess().getDocumentation());
        assertEquals(1, p.getProcess().getDocumentation().size());
        assertEquals("<![CDATA[DocumentationValue]]>", p.getProcess().getDocumentation().get(0).getText());
    }

    @Test
    public void testSetMetaData() {
        MetaDataAttributes metaDataAttributes = new MetaDataAttributes("att1ßval1Øatt2ßval2");
        p.setMetadata(metaDataAttributes);
        String metaDataString = CustomElement.metaDataAttributes.of(p.getProcess()).get();
        assertThat(metaDataString).isEqualTo("att1ß<![CDATA[val1]]>Øatt2ß<![CDATA[val2]]>");
    }

    @Test
    public void testSetMetaDataNull() {
        MetaDataAttributes metaDataAttributes = mock(MetaDataAttributes.class);
        when(metaDataAttributes.getValue()).thenReturn(null);
        p.setMetadata(metaDataAttributes);
        String metaDataString = CustomElement.metaDataAttributes.of(p.getProcess()).get();
        assertThat(metaDataString).isEqualTo("");
    }

    @Test
    public void testSetMetaDataEmpty() {
        MetaDataAttributes metaDataAttributes = mock(MetaDataAttributes.class);
        when(metaDataAttributes.getValue()).thenReturn("");
        p.setMetadata(metaDataAttributes);
        String metaDataString = CustomElement.metaDataAttributes.of(p.getProcess()).get();
        assertThat(metaDataString).isEqualTo("");
    }
}