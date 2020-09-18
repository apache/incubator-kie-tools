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

package org.kie.workbench.common.dmn.client.docks.navigator.included.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagramElement;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.Import;
import org.kie.workbench.common.dmn.api.definition.model.ImportDMN;
import org.kie.workbench.common.dmn.api.definition.model.ImportPMML;
import org.kie.workbench.common.dmn.api.editors.included.DMNImportTypes;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedModel;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedNode;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.LocationURI;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.api.included.legacy.DMNIncludeModelsClient;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionComponentsTest {

    @Mock
    private DecisionComponents.View view;

    @Mock
    private DMNIncludeModelsClient client;

    @Mock
    private ManagedInstance<DecisionComponentsItem> itemManagedInstance;

    @Mock
    private DecisionComponentFilter filter;

    @Mock
    private DMNDiagramsSession dmnDiagramsSession;

    @Captor
    private ArgumentCaptor<List<DecisionComponent>> decisionComponentListCaptor;

    private DecisionComponents decisionComponents;

    @Before
    public void setup() {

        decisionComponents = spy(new DecisionComponents(view, client, itemManagedInstance, filter, dmnDiagramsSession));
    }

    @Test
    public void testInit() {
        decisionComponents.init();
        verify(view).init(decisionComponents);
    }

    @Test
    public void testGetView() {
        assertEquals(view, decisionComponents.getView());
    }

    @Test
    public void testRefresh() {

        final List<DMNIncludedModel> includedModels = new ArrayList<>();
        final Consumer<List<DMNIncludedNode>> listConsumer = (list) -> {/* Nothing. */};

        doReturn(includedModels).when(decisionComponents).getDMNIncludedModels();
        doReturn(listConsumer).when(decisionComponents).getNodesConsumer();

        decisionComponents.refresh();

        verify(decisionComponents).clearDecisionComponents();
        verify(decisionComponents).startLoading();
        verify(client).loadNodesFromImports(includedModels, listConsumer);
        verify(decisionComponents).loadDRDComponents();
    }

    @Test
    public void testGetDMNIncludedModelsOnlyIncludesDMN() {
        final ImportDMN dmnImport = new ImportDMN();
        final ImportPMML pmmlImport = new ImportPMML();
        dmnImport.getName().setValue("dmn");
        dmnImport.setImportType(DMNImportTypes.DMN.getDefaultNamespace());
        pmmlImport.setImportType(DMNImportTypes.PMML.getDefaultNamespace());

        when(dmnDiagramsSession.getModelImports()).thenReturn(asList(dmnImport, pmmlImport));

        final List<DMNIncludedModel> includedModels = decisionComponents.getDMNIncludedModels();

        assertThat(includedModels).hasSize(1);
        assertThat(includedModels.get(0).getModelName()).isEqualTo("dmn");
        assertThat(includedModels.get(0).getImportType()).isEqualTo(DMNImportTypes.DMN.getDefaultNamespace());
    }

    @Test
    public void testApplyTermFilter() {

        final String value = "value";
        doNothing().when(decisionComponents).applyFilter();

        decisionComponents.applyTermFilter(value);

        verify(filter).setTerm(value);
        verify(decisionComponents).applyFilter();
    }

    @Test
    public void testApplyDrgElementFilterFilter() {

        final String value = "value";
        doNothing().when(decisionComponents).applyFilter();

        decisionComponents.applyDrgElementFilterFilter(value);

        verify(filter).setDrgElement(value);
        verify(decisionComponents).applyFilter();
    }

    @Test
    public void testApplyFilter() {

        final DecisionComponentsItem item1 = mock(DecisionComponentsItem.class);
        final DecisionComponentsItem item2 = mock(DecisionComponentsItem.class);
        final DecisionComponentsItem item3 = mock(DecisionComponentsItem.class);
        final DecisionComponent component1 = mock(DecisionComponent.class);
        final DecisionComponent component2 = mock(DecisionComponent.class);
        final DecisionComponent component3 = mock(DecisionComponent.class);
        final List<DecisionComponentsItem> decisionComponentsItems = asList(item1, item2, item3);

        doReturn(new DecisionComponentFilter()).when(decisionComponents).getFilter();
        doReturn(decisionComponentsItems).when(decisionComponents).getDecisionComponentsItems();
        when(item1.getDecisionComponent()).thenReturn(component1);
        when(item2.getDecisionComponent()).thenReturn(component2);
        when(item3.getDecisionComponent()).thenReturn(component3);
        when(component1.getName()).thenReturn("name3");
        when(component2.getName()).thenReturn("nome!!!");
        when(component3.getName()).thenReturn("name1");

        decisionComponents.getFilter().setTerm("name");
        decisionComponents.applyFilter();

        verify(item1).hide();
        verify(item2).hide();
        verify(item3).hide();
        verify(item3).show();
        verify(item1).show();
    }

    @Test
    public void testRemoveAllItems() {
        decisionComponents.removeAllItems();
        verify(decisionComponents).clearDecisionComponents();
    }

    @Test
    public void testGetNodesConsumerWhenNodeListIsEmpty() {

        final List<DMNIncludedNode> list = emptyList();

        decisionComponents.getNodesConsumer().accept(list);

        verify(view).setComponentsCounter(0);
        verify(view).hideLoading();
        verify(view).showEmptyState();
    }

    @Test
    public void testGetNodesConsumerWhenNodeListIsNotEmpty() {

        final DMNIncludedNode dmnIncludedNode1 = mock(DMNIncludedNode.class);
        final DMNIncludedNode dmnIncludedNode2 = mock(DMNIncludedNode.class);
        final List<DMNIncludedNode> list = asList(dmnIncludedNode1, dmnIncludedNode2);

        doNothing().when(decisionComponents).addComponent(any());

        decisionComponents.getNodesConsumer().accept(list);

        verify(view).setComponentsCounter(2);
        verify(view).hideLoading();
        verify(view).enableFilterInputs();
        verify(decisionComponents).addComponent(dmnIncludedNode1);
        verify(decisionComponents).addComponent(dmnIncludedNode2);
    }

    @Test
    public void testAddComponent() {

        final DMNIncludedNode node = mock(DMNIncludedNode.class);
        final DecisionComponent item = mock(DecisionComponent.class);
        doReturn(item).when(decisionComponents).makeDecisionComponent(node);
        doNothing().when(decisionComponents).createDecisionComponentItem(item);

        decisionComponents.addComponent(node);

        verify(decisionComponents).createDecisionComponentItem(item);
    }

    @Test
    public void testAsDMNIncludedModel() {

        final String modelName = "Model Name";
        final String namespace = "The Namespace";
        final String type = "The type";
        final String file = "my file.dmn";
        final String filePath = "//users//some//" + file;
        final Import anImport = new Import();
        anImport.setName(new Name(modelName));
        anImport.setNamespace(namespace);
        anImport.setImportType(type);
        anImport.setLocationURI(new LocationURI(filePath));

        final DMNIncludedModel includedModel = decisionComponents.asDMNIncludedModel(anImport);

        assertEquals(modelName, includedModel.getModelName());
        assertEquals(namespace, includedModel.getNamespace());
        assertEquals(type, includedModel.getImportType());
        assertEquals(file, includedModel.getPath());
    }

    @Test
    public void testGetNodesConsumerWhenNodesAreNull() {

        final Consumer<List<DMNIncludedNode>> consumer = decisionComponents.getNodesConsumer();

        consumer.accept(null);

        verify(view, never()).setComponentsCounter(anyInt());
        verify(view, never()).hideLoading();
        verify(view, never()).enableFilterInputs();
        verify(view, never()).showEmptyState();
    }

    @Test
    public void testGetNodesConsumerWhenNodesAreEmpty() {

        final Consumer<List<DMNIncludedNode>> consumer = decisionComponents.getNodesConsumer();

        consumer.accept(Collections.emptyList());

        verify(view).setComponentsCounter(0);
        verify(view).hideLoading();
        verify(view, never()).enableFilterInputs();
        verify(view).showEmptyState();
    }

    @Test
    public void testGetNodesConsumer() {

        final DMNIncludedNode includedNode1 = mock(DMNIncludedNode.class);
        final DMNIncludedNode includedNode2 = mock(DMNIncludedNode.class);
        final List<DMNIncludedNode> nodes = Arrays.asList(includedNode1, includedNode2);
        final Consumer<List<DMNIncludedNode>> consumer = decisionComponents.getNodesConsumer();

        doNothing().when(decisionComponents).addComponent(includedNode1);
        doNothing().when(decisionComponents).addComponent(includedNode2);

        consumer.accept(nodes);

        verify(view).setComponentsCounter(2);
        verify(view).hideLoading();
        verify(view).enableFilterInputs();
        verify(decisionComponents).addComponent(includedNode1);
        verify(decisionComponents).addComponent(includedNode2);
        verify(view, never()).showEmptyState();
    }

    @Test
    public void testLoadDRDComponents() {

        final int existingComponentsCounter = 3;
        final Id diagramId = mock(Id.class);
        final String id = "0000-1111-2222";
        final DMNDiagramElement diagramElement = mock(DMNDiagramElement.class);
        final DRGElement drg1Element = mock(DRGElement.class);
        final DRGElement drg2Element = mock(DRGElement.class);
        final DecisionComponent decisionComponent1 = mock(DecisionComponent.class);
        final DecisionComponent decisionComponent2 = mock(DecisionComponent.class);

        when(dmnDiagramsSession.getModelDRGElements()).thenReturn(Arrays.asList(drg1Element, drg2Element));
        when(diagramId.getValue()).thenReturn(id);
        when(diagramElement.getId()).thenReturn(diagramId);
        when(drg1Element.getDiagramId()).thenReturn(id);
        when(drg2Element.getDiagramId()).thenReturn(id);
        doReturn(decisionComponent1).when(decisionComponents).makeDecisionComponent(id, drg1Element);
        doReturn(decisionComponent2).when(decisionComponents).makeDecisionComponent(id, drg2Element);
        when(view.getComponentsCounter()).thenReturn(existingComponentsCounter);
        doNothing().when(decisionComponents).createDecisionComponentItems(any());

        decisionComponents.loadDRDComponents();

        verify(view).enableFilterInputs();
        verify(view).hideLoading();
        verify(view).setComponentsCounter(existingComponentsCounter + 2);
        verify(decisionComponents).createDecisionComponentItems(decisionComponentListCaptor.capture());
        verify(decisionComponents).makeDecisionComponent(id, drg1Element);
        verify(decisionComponents).makeDecisionComponent(id, drg2Element);

        final List<DecisionComponent> list = decisionComponentListCaptor.getValue();
        assertTrue(list.contains(decisionComponent1));
        assertTrue(list.contains(decisionComponent2));
        assertEquals(2, list.size());
    }

    @Test
    public void testDefinitionContainsDRGElementWhenContainsDRGElement() {

        final Node node = mock(Node.class);
        final Definition content = mock(Definition.class);
        final DRGElement drgElement = mock(DRGElement.class);
        when(node.getContent()).thenReturn(content);
        when(content.getDefinition()).thenReturn(drgElement);

        assertTrue(decisionComponents.definitionContainsDRGElement(node));
    }

    @Test
    public void testDefinitionContainsDRGElementWhenDoesNotContainsDRGElement() {

        final Node node = mock(Node.class);
        final Definition content = mock(Definition.class);
        final Object obj = mock(Object.class);
        when(node.getContent()).thenReturn(content);
        when(content.getDefinition()).thenReturn(obj);

        assertFalse(decisionComponents.definitionContainsDRGElement(node));
    }

    @Test
    public void testCreateDecisionComponentItem() {

        final DecisionComponentsItem item = mock(DecisionComponentsItem.class);
        final List<DecisionComponentsItem> decisionComponentsItems = spy(new ArrayList<>());
        final DecisionComponentsItem.View decisionComponentsView = mock(DecisionComponentsItem.View.class);
        final HTMLElement htmlElement = mock(HTMLElement.class);
        final DecisionComponent component = mock(DecisionComponent.class);

        when(decisionComponentsView.getElement()).thenReturn(htmlElement);
        when(itemManagedInstance.get()).thenReturn(item);
        when(item.getView()).thenReturn(decisionComponentsView);
        doReturn(decisionComponentsItems).when(decisionComponents).getDecisionComponentsItems();

        decisionComponents.createDecisionComponentItem(component);

        verify(item).setDecisionComponent(any(DecisionComponent.class));
        verify(decisionComponentsItems).add(item);
        verify(view).addListItem(htmlElement);
    }
}
