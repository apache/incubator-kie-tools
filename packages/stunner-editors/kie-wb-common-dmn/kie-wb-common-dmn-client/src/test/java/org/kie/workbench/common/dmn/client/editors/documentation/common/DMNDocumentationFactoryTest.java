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

package org.kie.workbench.common.dmn.client.editors.documentation.common;

import java.util.List;
import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.Definitions;
import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.api.definition.model.UnaryTests;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasFileExport;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.mockito.Mock;
import org.uberfire.client.views.pfly.widgets.Moment;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.editors.documentation.common.DMNDocumentationFactory.EMPTY;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DMNDocumentationFactory_Constraints;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DMNDocumentationFactory_ListYes;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DMNDocumentationFactory_Structure;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DMNDocumentationFactoryTest {

    @Mock
    private CanvasFileExport canvasFileExport;

    @Mock
    private TranslationService translationService;

    @Mock
    private DMNDocumentationDRDsFactory drdsFactory;

    @Mock
    private DMNGraphUtils graphUtils;

    @Mock
    private Diagram diagram;

    @Mock
    private Definitions definitions;

    @Mock
    private Moment moment;

    @Mock
    private DMNDocumentationI18n i18n;

    private DMNDocumentationFactory documentationFactory;

    @Before
    public void setup() {

        documentationFactory = spy(new DMNDocumentationFactory(canvasFileExport, translationService, drdsFactory, graphUtils));

        when(translationService.format(DMNDocumentationFactory_Constraints)).thenReturn("Constraints:");
        when(translationService.format(DMNDocumentationFactory_ListYes)).thenReturn("List: Yes");
        when(translationService.format(DMNDocumentationFactory_Structure)).thenReturn("Structure");
    }

    @Test
    public void testCreate() {
        final String diagramName = "Diagram name";
        final String diagramDescription = "Diagram description";
        final String image = "<image>";
        final String currentDate = "2 January 1992";
        final String currentYear = "1992";
        final String namespace = "://namespace";
        final String expectedDroolsLogo = "droolsLogo";
        final String expectedSupportedByRedHatLogo = "supportedByRedHatLogo";
        final List<DRGElement> drgElements = singletonList(mock(DRGElement.class));
        final ItemDefinition uuid = makeItemDefinition("tUUID", "String");
        final ItemDefinition id = makeItemDefinition("id", "tUUID");
        final ItemDefinition name = makeItemDefinition("name", "String");
        final ItemDefinition person = makeItemDefinition("tPerson", null, id, name);
        final List<ItemDefinition> itemDefinitions = asList(uuid, person);
        final UnaryTests unaryTests = new UnaryTests();

        unaryTests.setText(new Text("[1, 2, 3]"));
        id.setAllowedValues(unaryTests);
        id.setIsCollection(true);

        doReturn(image).when(documentationFactory).getDiagramImage();
        doReturn(i18n).when(documentationFactory).getDocumentationI18n();
        doReturn(moment).when(documentationFactory).moment();

        when(definitions.getNamespace()).thenReturn(new Text(namespace));
        when(graphUtils.getDefinitions(diagram)).thenReturn(definitions);
        when(definitions.getName()).thenReturn(new Name(diagramName));
        when(definitions.getDescription()).thenReturn(new Description(diagramDescription));
        when(graphUtils.getDRGElements(diagram)).thenReturn(drgElements);
        when(definitions.getItemDefinition()).thenReturn(itemDefinitions);
        when(moment.format("D MMMM YYYY")).thenReturn(currentDate);
        when(moment.format("YYYY")).thenReturn(currentYear);

        final DMNDocumentation documentation = documentationFactory.create(diagram);

        assertEquals(namespace, documentation.getNamespace());
        assertEquals(diagramName, documentation.getDiagramName());
        assertEquals(diagramDescription, documentation.getDiagramDescription());
        assertEquals(image, documentation.getDiagramImage());
        assertEquals(currentDate, documentation.getCurrentDate());
        assertEquals(currentYear, documentation.getCurrentYear());
        assertEquals(expectedDroolsLogo, documentation.getDroolsLogoURI());
        assertEquals(i18n, documentation.getI18n());
        assertNotNull(documentation.getModuleName());
        assertNotNull(documentation.getDataTypes());
        assertTrue(documentation.hasGraphNodes());

        final List<DMNDocumentationDataType> dataTypes = documentation.getDataTypesList();

        assertEquals(4, dataTypes.size());

        assertEquals("", dataTypes.get(0).getConstraint());
        assertEquals("tUUID", dataTypes.get(0).getName());
        assertEquals("String", dataTypes.get(0).getType());
        assertEquals("", dataTypes.get(0).getListLabel());
        assertEquals(0, dataTypes.get(0).getLevel());
        assertTrue(dataTypes.get(0).isTopLevel());

        assertEquals("", dataTypes.get(1).getConstraint());
        assertEquals("tPerson", dataTypes.get(1).getName());
        assertEquals("Structure", dataTypes.get(1).getType());
        assertEquals("", dataTypes.get(1).getListLabel());
        assertEquals(0, dataTypes.get(1).getLevel());
        assertTrue(dataTypes.get(1).isTopLevel());

        assertEquals("Constraints: [1, 2, 3]", dataTypes.get(2).getConstraint());
        assertEquals("id", dataTypes.get(2).getName());
        assertEquals("tUUID", dataTypes.get(2).getType());
        assertEquals("List: Yes", dataTypes.get(2).getListLabel());
        assertEquals(1, dataTypes.get(2).getLevel());
        assertFalse(dataTypes.get(2).isTopLevel());

        assertEquals("", dataTypes.get(3).getConstraint());
        assertEquals("name", dataTypes.get(3).getName());
        assertEquals("String", dataTypes.get(3).getType());
        assertEquals("", dataTypes.get(3).getListLabel());
        assertEquals(1, dataTypes.get(3).getLevel());
        assertFalse(dataTypes.get(3).isTopLevel());
    }

    @Test
    public void testGetDiagramImageWhenCanvasHandlerIsPresent() {
        final AbstractCanvasHandler canvasHandler = mock(AbstractCanvasHandler.class);
        final String image = "<image>";

        when(graphUtils.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasFileExport.exportToPng(canvasHandler)).thenReturn(image);

        assertEquals(image, documentationFactory.getDiagramImage());
    }

    @Test
    public void testGetDiagramImageWhenCanvasHandlerIsNotPresent() {
        when(graphUtils.getCurrentSession()).thenReturn(Optional.empty());
        assertEquals(EMPTY, documentationFactory.getDiagramImage());
    }

    @Test
    public void testGetHasGraphNodesWhenIsReturnsFalse() {
        when(graphUtils.getDRGElements(diagram)).thenReturn(emptyList());
        assertFalse(documentationFactory.hasGraphNodes(diagram));
    }

    @Test
    public void testGetHasGraphNodesWhenIsReturnsTrue() {
        when(graphUtils.getDRGElements(diagram)).thenReturn(singletonList(mock(DRGElement.class)));
        assertTrue(documentationFactory.hasGraphNodes(diagram));
    }

    private ItemDefinition makeItemDefinition(final String name,
                                              final String type,
                                              final ItemDefinition... itemDefinitions) {

        final ItemDefinition itemDefinition = spy(new ItemDefinition());
        itemDefinition.setName(new Name(name));

        if (type != null) {
            itemDefinition.setTypeRef(new QName("://namespace", type));
        }

        itemDefinition.getItemComponent().addAll(asList(itemDefinitions));

        return itemDefinition;
    }
}
