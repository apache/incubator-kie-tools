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

package org.kie.workbench.common.dmn.backend.definition.v1_1;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.dmn.model.api.DRGElement;
import org.kie.dmn.model.api.Import;
import org.kie.dmn.model.api.ItemDefinition;
import org.kie.dmn.model.v1_2.TImport;
import org.kie.soup.commons.util.Maps;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.model.Definitions;
import org.kie.workbench.common.dmn.api.definition.model.ImportDMN;
import org.kie.workbench.common.dmn.api.editors.included.DMNImportTypes;
import org.kie.workbench.common.dmn.api.editors.included.PMMLDocumentMetadata;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefinitionsConverterTest {

    private final String NAMESPACE = "SomePreviousSetNamespace";

    @Mock
    private org.kie.dmn.model.api.Definitions apiDefinitions;

    @Mock
    private Definitions wbDefinitions;

    @Test
    public void wbFromDMN() {

        final Import anImport = new TImport();
        anImport.setImportType(DMNImportTypes.DMN.getDefaultNamespace());

        final org.kie.dmn.model.api.Definitions definitions = mock(org.kie.dmn.model.api.Definitions.class);
        final Map<Import, org.kie.dmn.model.api.Definitions> importDefinitions = new Maps.Builder<Import, org.kie.dmn.model.api.Definitions>().put(anImport, definitions).build();
        final Map<Import, PMMLDocumentMetadata> pmmlDocuments = new Maps.Builder<Import, PMMLDocumentMetadata>().build();
        when(definitions.getDrgElement()).thenReturn(asList(mock(DRGElement.class), mock(DRGElement.class)));
        when(definitions.getItemDefinition()).thenReturn(asList(mock(ItemDefinition.class), mock(ItemDefinition.class), mock(ItemDefinition.class)));
        when(apiDefinitions.getImport()).thenReturn(singletonList(anImport));

        final Definitions wb = DefinitionsConverter.wbFromDMN(apiDefinitions, importDefinitions, pmmlDocuments);
        final String defaultNs = wb.getNsContext().get(DMNModelInstrumentedBase.Namespace.DEFAULT.getPrefix());
        final String namespace = wb.getNamespace().getValue();
        final List<org.kie.workbench.common.dmn.api.definition.model.Import> imports = wb.getImport();

        assertEquals(defaultNs, namespace);
        assertEquals(1, imports.size());
        assertTrue(imports.get(0) instanceof ImportDMN);

        final ImportDMN importDMN = (ImportDMN) imports.get(0);
        assertEquals(2, importDMN.getDrgElementsCount());
        assertEquals(3, importDMN.getItemDefinitionsCount());
    }

    @Test
    public void dmnFromWB() {
        when(wbDefinitions.getNamespace()).thenReturn(new Text());

        org.kie.dmn.model.api.Definitions dmn = DefinitionsConverter.dmnFromWB(wbDefinitions);
        String defaultNs = dmn.getNsContext().get(DMNModelInstrumentedBase.Namespace.DEFAULT.getPrefix());
        String namespace = dmn.getNamespace();

        assertNotNull(defaultNs);
        assertEquals(defaultNs, namespace);

        when(wbDefinitions.getNamespace()).thenReturn(new Text(NAMESPACE));

        dmn = DefinitionsConverter.dmnFromWB(wbDefinitions);
        defaultNs = dmn.getNsContext().get(DMNModelInstrumentedBase.Namespace.DEFAULT.getPrefix());
        namespace = dmn.getNamespace();

        assertNotNull(defaultNs);
        assertEquals(defaultNs, namespace);
        assertEquals(NAMESPACE, defaultNs);
    }

    @Test
    public void testDmnFromWBWithExistingDefaultNamespace() {

        final Map<String, String> existingNsContext = new HashMap<>();
        final String existing = "existing";
        existingNsContext.put(DMNModelInstrumentedBase.Namespace.DEFAULT.getPrefix(),
                              existing);

        when(wbDefinitions.getNamespace()).thenReturn(new Text());
        when(wbDefinitions.getNsContext()).thenReturn(existingNsContext);

        org.kie.dmn.model.api.Definitions dmn = DefinitionsConverter.dmnFromWB(wbDefinitions);
        String defaultNs = dmn.getNsContext().get(DMNModelInstrumentedBase.Namespace.DEFAULT.getPrefix());

        assertNotNull(defaultNs);
        assertEquals(existing, defaultNs);

        when(wbDefinitions.getNamespace()).thenReturn(new Text(NAMESPACE));

        dmn = DefinitionsConverter.dmnFromWB(wbDefinitions);
        defaultNs = dmn.getNsContext().get(DMNModelInstrumentedBase.Namespace.DEFAULT.getPrefix());

        assertNotNull(defaultNs);
        assertEquals(existing, defaultNs);
    }
}