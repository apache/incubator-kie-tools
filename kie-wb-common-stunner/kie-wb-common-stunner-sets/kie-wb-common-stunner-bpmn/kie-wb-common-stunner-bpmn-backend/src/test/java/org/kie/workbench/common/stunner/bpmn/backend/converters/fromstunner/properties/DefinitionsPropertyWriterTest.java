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

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.Import;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.WSDLImport;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;

public class DefinitionsPropertyWriterTest {

    private Definitions definitions;

    private DefinitionsPropertyWriter tested;

    @Before
    public void setUp() {
        definitions = bpmn2.createDefinitions();
        tested = new DefinitionsPropertyWriter(definitions);
    }

    @Test
    public void setWSDLImports() {
        final String LOCATION = "location";
        final String NAMESPACE = "namespace";
        final int QTY = 10;

        List<WSDLImport> wsdlImports = new ArrayList<>();
        for (int i = 0; i < QTY; i++) {
            wsdlImports.add(new WSDLImport(LOCATION + i, NAMESPACE + i));
        }

        tested.setWSDLImports(wsdlImports);
        List<Import> imports = definitions.getImports();

        assertEquals(QTY, imports.size());

        for (int i = 0; i < QTY; i++) {
            assertEquals(LOCATION + i, imports.get(i).getLocation());
            assertEquals(NAMESPACE + i, imports.get(i).getNamespace());
        }
    }
}