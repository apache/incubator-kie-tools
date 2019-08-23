/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.DefaultImport;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.di;

public class ProcessPropertyReaderTest {

    private Definitions definitions;

    private DefinitionResolver definitionResolver;

    private Process process;

    private ProcessPropertyReader tested;

    @Before
    public void setUp() {
        definitions = bpmn2.createDefinitions();
        process = bpmn2.createProcess();
        definitions.getRootElements().add(process);
        BPMNDiagram bpmnDiagram = di.createBPMNDiagram();
        bpmnDiagram.setPlane(di.createBPMNPlane());
        definitions.getDiagrams().add(bpmnDiagram);

        definitionResolver = new DefinitionResolver(definitions, Collections.emptyList());

        tested = new ProcessPropertyReader(process,
                                           definitionResolver.getDiagram(),
                                           definitionResolver.getShape(process.getId()),
                                           definitionResolver.getResolutionFactor());
    }

    @Test
    public void getDefaultImports() {
        final String CLASS_NAME = "className";
        final int QTY = 10;

        List<DefaultImport> defaultImports = new ArrayList<>();
        for (int i = 0; i < QTY; i++) {
            defaultImports.add(new DefaultImport(CLASS_NAME + i));
        }

        CustomElement.defaultImports.of(process).set(defaultImports);
        List<DefaultImport> result = tested.getDefaultImports();

        assertEquals(QTY, result.size());
        assertEquals(defaultImports, result);
    }
}