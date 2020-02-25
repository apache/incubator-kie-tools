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

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner;

import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.Import;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.PropertyWriterFactory;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.AdHoc;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.DiagramSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Executable;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.GlobalVariables;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Id;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Package;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.ProcessInstanceDescription;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.ProcessType;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Version;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.Imports;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.ImportsValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.WSDLImport;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.GraphImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.graph.store.GraphNodeStoreImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DefinitionsConverterTest {

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
                new GlobalVariables(""),
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

    @Test
    public void toDefinitions() {
        final String LOCATION = "Location";
        final String NAMESPACE = "Namespace";

        ImportsValue importsValue = new ImportsValue();
        importsValue.addImport(new WSDLImport(LOCATION, NAMESPACE));

        BPMNDiagramImpl diag = new BPMNDiagramImpl();
        diag.setDiagramSet(new DiagramSet(
                new Name(),
                new Documentation(),
                new Id(),
                new Package(),
                new ProcessType(),
                new Version(),
                new AdHoc(false),
                new ProcessInstanceDescription(),
                new GlobalVariables(),
                new Imports(importsValue),
                new Executable(true),
                new SLADueDate()
        ));

        GraphNodeStoreImpl nodeStore = new GraphNodeStoreImpl();
        NodeImpl x = new NodeImpl("x");
        x.setContent(new ViewImpl<>(diag, Bounds.create()));
        nodeStore.add(x);

        ConverterFactory f = new ConverterFactory(new DefinitionsBuildingContext(new GraphImpl("x", nodeStore)),
                                                  new PropertyWriterFactory());

        DefinitionsConverter definitionsConverter = new DefinitionsConverter(f, new PropertyWriterFactory());
        Definitions definitions = definitionsConverter.toDefinitions();

        assertImportsValue(LOCATION, NAMESPACE, definitions);
    }

    private void assertImportsValue(String LOCATION, String NAMESPACE, Definitions definitions) {
        Import imp = definitions.getImports().get(0);
        assertNotNull(imp);
        assertEquals(LOCATION, imp.getLocation());
        assertEquals(NAMESPACE, imp.getNamespace());
    }
}