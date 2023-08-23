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

package org.kie.workbench.common.dmn.client.editors.included.imports;

import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.Definitions;
import org.kie.workbench.common.dmn.api.definition.model.Import;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.editors.included.BaseIncludedModelActiveRecord;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class IncludedModelsPageStateProviderImplTest {

    @Mock
    private DMNGraphUtils dmnGraphUtils;

    @Mock
    private IncludedModelsFactory factory;

    @Mock
    private DMNDiagramsSession dmnDiagramsSession;

    @Mock
    private Diagram diagram;

    private IncludedModelsPageStateProviderImpl stateProvider;

    @Before
    public void setup() {
        when(dmnDiagramsSession.getDRGDiagram()).thenReturn(diagram);
        stateProvider = spy(new IncludedModelsPageStateProviderImpl(dmnGraphUtils, factory, dmnDiagramsSession));
    }

    @Test
    public void testGenerateIncludedModels() {

        final List<Import> imports = asList(mock(Import.class), mock(Import.class));
        final List<BaseIncludedModelActiveRecord> expectedIncludedModels = asList(mock(BaseIncludedModelActiveRecord.class), mock(BaseIncludedModelActiveRecord.class));

        when(factory.makeIncludedModels(imports)).thenReturn(expectedIncludedModels);
        doReturn(imports).when(stateProvider).getImports();

        final List<BaseIncludedModelActiveRecord> actualIncludedModels = stateProvider.generateIncludedModels();

        assertEquals(expectedIncludedModels, actualIncludedModels);
    }

    @Test
    public void testGetImportsWhenDiagramIsPresent() {

        final Definitions definitions = mock(Definitions.class);
        final List<Import> expectedImports = asList(mock(Import.class), mock(Import.class));

        when(dmnGraphUtils.getDefinitions(diagram)).thenReturn(definitions);
        when(definitions.getImport()).thenReturn(expectedImports);

        final List<Import> actualImports = stateProvider.getImports();

        assertEquals(expectedImports, actualImports);
    }

    @Test
    public void testGetImportsWhenDiagramIsNotPresent() {

        when(dmnDiagramsSession.getDRGDiagram()).thenReturn(null);
        final List<Import> actualImports = stateProvider.getImports();
        final List<Import> expectedImports = emptyList();

        assertEquals(expectedImports, actualImports);
    }

    @Test
    public void testGetCurrentDiagramNamespace() {

        final Definitions definitions = mock(Definitions.class);
        final String expectedNamespace = "://namespace";

        when(dmnGraphUtils.getDefinitions(diagram)).thenReturn(definitions);
        when(definitions.getNamespace()).thenReturn(new Text(expectedNamespace));

        final String actualNamespace = stateProvider.getCurrentDiagramNamespace();

        assertEquals(expectedNamespace, actualNamespace);
    }
}
