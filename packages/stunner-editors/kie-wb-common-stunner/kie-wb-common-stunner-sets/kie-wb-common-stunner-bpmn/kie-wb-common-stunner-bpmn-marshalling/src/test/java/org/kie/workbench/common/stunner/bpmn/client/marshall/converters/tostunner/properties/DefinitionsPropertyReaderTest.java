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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties;

import java.util.Collections;
import java.util.List;

import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.Import;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.util.PropertyWriterUtils;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.WSDLImport;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.di;

@RunWith(MockitoJUnitRunner.class)
public class DefinitionsPropertyReaderTest {

    private Definitions definitions;

    private DefinitionResolver definitionResolver;

    private Process process;

    private DefinitionsPropertyReader tested;

    @Before
    public void setUp() {
        definitions = bpmn2.createDefinitions();
        process = bpmn2.createProcess();
        definitions.getRootElements().add(process);
        BPMNDiagram bpmnDiagram = di.createBPMNDiagram();
        bpmnDiagram.setPlane(di.createBPMNPlane());
        definitions.getDiagrams().add(bpmnDiagram);

        definitionResolver = new DefinitionResolver(definitions, Collections.emptyList());

        tested = new DefinitionsPropertyReader(definitionResolver.getDefinitions(),
                                               definitionResolver.getDiagram(),
                                               definitionResolver.getShape(process.getId()),
                                               definitionResolver.getResolutionFactor());
    }

    @Test
    public void getWSDLImports() {
        final String LOCATION = "location";
        final String NAMESPACE = "namespace";
        final int QTY = 10;

        for (int i = 0; i < QTY; i++) {
            Import imp = PropertyWriterUtils.toImport(new WSDLImport(LOCATION + i, NAMESPACE + i));
            definitions.getImports().add(imp);
        }

        List<WSDLImport> wsdlImports = tested.getWSDLImports();

        assertEquals(QTY, wsdlImports.size());
        for (int i = 0; i < QTY; i++) {
            assertEquals(LOCATION + i, wsdlImports.get(i).getLocation());
            assertEquals(NAMESPACE + i, wsdlImports.get(i).getNamespace());
        }
    }
}