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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner;

import org.eclipse.bpmn2.Definitions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.URL;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.PropertyWriterFactory;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.AdHoc;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.DiagramSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Executable;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Id;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Package;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.ProcessInstanceDescription;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.ProcessType;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Version;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.Imports;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.GraphImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.graph.store.GraphNodeStoreImpl;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class DefinitionsConverterTest {

    @Mock
    private URL url;

    private Answer<String> returnArgument = invocation -> (String) invocation.getArguments()[0];

    @Before
    public void setUp() {
        StringUtils.setURL(url);
    }

    @Test
    public void JBPM_7526_shouldSetExporter() {
        GraphNodeStoreImpl nodeStore = new GraphNodeStoreImpl();
        NodeImpl x = new NodeImpl("x");
        BPMNDiagramImpl diag = new BPMNDiagramImpl();
        diag.setDiagramSet(new DiagramSet(
                new Name("x"),
                new Documentation("doc"),
                new Id("x"),
                new Package("org.jbpm"),
                new ProcessType(),
                new Version("1.0"),
                new AdHoc(false),
                new ProcessInstanceDescription("descr"),
                new Imports(),
                new Executable(true),
                new SLADueDate("")
        ));
        x.setContent(new ViewImpl<>(diag, Bounds.create()));
        nodeStore.add(x);
        ConverterFactory f = new ConverterFactory(new DefinitionsBuildingContext(
                new GraphImpl("x", nodeStore)),
                                                  new PropertyWriterFactory());

        DefinitionsConverter definitionsConverter =
                new DefinitionsConverter(f, new PropertyWriterFactory());
        Definitions definitions =
                definitionsConverter.toDefinitions();

        assertThat(definitions.getExporter()).isNotBlank();
        assertThat(definitions.getExporterVersion()).isNotBlank();
    }
}