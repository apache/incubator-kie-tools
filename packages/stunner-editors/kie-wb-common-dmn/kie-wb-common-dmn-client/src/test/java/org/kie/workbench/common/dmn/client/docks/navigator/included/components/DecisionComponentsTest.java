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

package org.kie.workbench.common.dmn.client.docks.navigator.included.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.Definitions;
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
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
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

    @Mock
    private DMNGraphUtils dmnGraphUtils;

    @Mock
    private DecisionComponentsItem.View decisionComponentsItemView;

    @Captor
    private ArgumentCaptor<DecisionComponent> decisionComponentArgumentCaptor;

    private DecisionComponents decisionComponents;

    @Before
    public void setup() {
        decisionComponents = spy(new DecisionComponents(view, client, itemManagedInstance, filter, dmnDiagramsSession, dmnGraphUtils));
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
    public void testRefreshWhenIncludedNodeListsIsUpdated() {

        final List<DMNIncludedModel> dmnModelIncludedModels = new ArrayList<>();
        final List<DMNIncludedModel> latestIncludedModelsLoaded = new ArrayList<>();

        when(dmnDiagramsSession.isSessionStatePresent()).thenReturn(true);
        doReturn(dmnModelIncludedModels).when(decisionComponents).getDMNIncludedModels();
        doReturn(latestIncludedModelsLoaded).when(decisionComponents).getLatestIncludedModelsLoaded();
        doNothing().when(decisionComponents).refreshIncludedNodesList();
        doNothing().when(decisionComponents).loadModelComponents();

        dmnModelIncludedModels.add(makeDMNIncludedModel("://namespace1"));
        dmnModelIncludedModels.add(makeDMNIncludedModel("://namespace2"));
        latestIncludedModelsLoaded.add(makeDMNIncludedModel("://namespace1"));
        latestIncludedModelsLoaded.add(makeDMNIncludedModel("://namespace2"));

        decisionComponents.refresh();

        verify(decisionComponents, never()).refreshIncludedNodesList();
        verify(decisionComponents).loadModelComponents();
    }

    @Test
    public void testRefreshWhenIncludedNodeListsIsNotUpdated() {

        final List<DMNIncludedModel> dmnModelIncludedModels = new ArrayList<>();
        final List<DMNIncludedModel> latestIncludedModelsLoaded = new ArrayList<>();

        when(dmnDiagramsSession.isSessionStatePresent()).thenReturn(true);
        doReturn(dmnModelIncludedModels).when(decisionComponents).getDMNIncludedModels();
        doReturn(latestIncludedModelsLoaded).when(decisionComponents).getLatestIncludedModelsLoaded();
        doNothing().when(decisionComponents).refreshIncludedNodesList();
        doNothing().when(decisionComponents).loadModelComponents();

        dmnModelIncludedModels.add(makeDMNIncludedModel("://namespace1"));
        dmnModelIncludedModels.add(makeDMNIncludedModel("://namespace2"));
        dmnModelIncludedModels.add(makeDMNIncludedModel("://namespaceNEW"));
        latestIncludedModelsLoaded.add(makeDMNIncludedModel("://namespace1"));
        latestIncludedModelsLoaded.add(makeDMNIncludedModel("://namespace2"));

        decisionComponents.refresh();

        verify(decisionComponents).refreshIncludedNodesList();
        verify(decisionComponents).loadModelComponents();
    }

    @Test
    public void testRefreshWhenSessionStateIsNotPresent() {

        when(dmnDiagramsSession.isSessionStatePresent()).thenReturn(false);

        decisionComponents.refresh();

        verify(decisionComponents, never()).refreshIncludedNodesList();
        verify(decisionComponents, never()).loadModelComponents();
    }

    @Test
    public void testRefresh() {

        final Consumer<List<DMNIncludedNode>> listConsumer = (list) -> {/* Nothing. */};
        final List<DMNIncludedModel> includedModels = new ArrayList<>();
        includedModels.add(makeDMNIncludedModel("://namespace1"));
        includedModels.add(makeDMNIncludedModel("://namespace2"));

        doReturn(includedModels).when(decisionComponents).getDMNIncludedModels();
        doReturn(listConsumer).when(decisionComponents).getNodesConsumer();

        decisionComponents.refreshIncludedNodesList();

        verify(decisionComponents).startLoading();
        verify(client).loadNodesFromImports(includedModels, listConsumer);
        assertEquals(includedModels, decisionComponents.getLatestIncludedModelsLoaded());
    }

    @Test
    public void testLoadModelComponents() {

        final String dmnModelName = "ModelName";
        final DRGElement drgElement1 = mock(DRGElement.class);
        final DRGElement drgElement2 = mock(DRGElement.class);
        final DecisionComponent decisionComponent1 = mock(DecisionComponent.class);
        final DecisionComponent decisionComponent2 = mock(DecisionComponent.class);
        final List<DecisionComponent> decisionComponentsList = new ArrayList<>();

        final Definitions definitions = mock(Definitions.class);
        when(definitions.getName()).thenReturn(new Name(dmnModelName));

        when(dmnGraphUtils.getModelDefinitions()).thenReturn(definitions);
        when(dmnDiagramsSession.getModelDRGElements()).thenReturn(Arrays.asList(drgElement1, drgElement2));
        when(drgElement1.getName()).thenReturn(new Name("Decision-1"));
        when(drgElement2.getName()).thenReturn(new Name("Decision-2"));
        when(decisionComponent1.getName()).thenReturn("Decision-1");
        when(decisionComponent2.getName()).thenReturn("Decision-2");

        doReturn(decisionComponent1).when(decisionComponents).makeDecisionComponent(dmnModelName, drgElement1);
        doReturn(decisionComponent2).when(decisionComponents).makeDecisionComponent(dmnModelName, drgElement2);
        doReturn(decisionComponentsList).when(decisionComponents).getModelDRGElements();
        doNothing().when(decisionComponents).refreshView();

        decisionComponents.loadModelComponents();

        assertTrue(decisionComponentsList.contains(decisionComponent1));
        assertTrue(decisionComponentsList.contains(decisionComponent2));
        assertEquals(2, decisionComponentsList.size());
        verify(decisionComponents).refreshView();
    }

    @Test
    public void testRefreshViewWhenDecisionComponentsListIsNotEmpty() {

        final List<DecisionComponent> modelDRGElements = new ArrayList<>();
        final List<DecisionComponent> includedDRGElements = new ArrayList<>();
        final List<DecisionComponent> decisionComponentsItems = spy(new ArrayList<>());

        doReturn(modelDRGElements).when(decisionComponents).getModelDRGElements();
        doReturn(includedDRGElements).when(decisionComponents).getIncludedDRGElements();
        doReturn(decisionComponentsItems).when(decisionComponents).getDecisionComponentsItems();
        doReturn(5).when(decisionComponentsItems).size();
        doReturn(false).when(decisionComponentsItems).isEmpty();
        doNothing().when(decisionComponents).createDecisionComponentItem(any());

        modelDRGElements.add(makeDecisionComponent("Decision-1", "ModeName", false));
        modelDRGElements.add(makeDecisionComponent("Decision-1", "ModeName", false));
        modelDRGElements.add(makeDecisionComponent("Decision-3", "ModeName", false));
        includedDRGElements.add(makeDecisionComponent("included.Decision-1", "includedModel.dmn", true));
        includedDRGElements.add(makeDecisionComponent("included.Decision-2", "includedModel.dmn", true));

        decisionComponents.refreshView();

        verify(decisionComponentsItems).clear();
        verify(view).clear();
        verify(view).setComponentsCounter(5);
        verify(decisionComponents, times(5)).createDecisionComponentItem(any(DecisionComponent.class));
        verify(view).enableFilterInputs();
        verify(view, never()).disableFilterInputs();
        verify(view, never()).showEmptyState();
    }

    @Test
    public void testRefreshViewWhenDecisionComponentsListIsEmpty() {

        final List<DecisionComponent> modelDRGElements = new ArrayList<>();
        final List<DecisionComponent> includedDRGElements = new ArrayList<>();
        final List<DecisionComponent> decisionComponentsItems = spy(new ArrayList<>());

        doReturn(modelDRGElements).when(decisionComponents).getModelDRGElements();
        doReturn(includedDRGElements).when(decisionComponents).getIncludedDRGElements();
        doReturn(decisionComponentsItems).when(decisionComponents).getDecisionComponentsItems();
        doNothing().when(decisionComponents).createDecisionComponentItem(any());

        decisionComponents.refreshView();

        verify(decisionComponentsItems).clear();
        verify(view).clear();
        verify(view).setComponentsCounter(0);
        verify(decisionComponents, never()).createDecisionComponentItem(any(DecisionComponent.class));
        verify(view, never()).enableFilterInputs();
        verify(view).disableFilterInputs();
        verify(view).showEmptyState();
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
        final List<DecisionComponent> includedDRGElements = spy(new ArrayList<>());

        doReturn(includedDRGElements).when(decisionComponents).getIncludedDRGElements();

        decisionComponents.getNodesConsumer().accept(list);

        verify(view).hideLoading();
        verify(includedDRGElements).clear();
        verify(includedDRGElements, never()).add(any());
        verify(decisionComponents).refreshView();
    }

    @Test
    public void testGetNodesConsumerWhenNodeListIsNotEmpty() {

        final DMNIncludedNode dmnIncludedNode1 = mock(DMNIncludedNode.class);
        final DMNIncludedNode dmnIncludedNode2 = mock(DMNIncludedNode.class);
        final DecisionComponent drgDecisionComponent1 = mock(DecisionComponent.class);
        final DecisionComponent drgDecisionComponent2 = mock(DecisionComponent.class);
        final List<DMNIncludedNode> includedNodes = asList(dmnIncludedNode1, dmnIncludedNode2);
        final List<DecisionComponent> includedDRGElements = spy(new ArrayList<>());

        doReturn(includedDRGElements).when(decisionComponents).getIncludedDRGElements();
        doReturn(drgDecisionComponent1).when(decisionComponents).makeDecisionComponent(dmnIncludedNode1);
        doReturn(drgDecisionComponent2).when(decisionComponents).makeDecisionComponent(dmnIncludedNode2);
        doNothing().when(decisionComponents).refreshView();

        decisionComponents.getNodesConsumer().accept(includedNodes);

        verify(view).hideLoading();
        verify(includedDRGElements).clear();
        verify(includedDRGElements).add(drgDecisionComponent1);
        verify(includedDRGElements).add(drgDecisionComponent2);
        verify(decisionComponents).refreshView();
    }

    @Test
    public void testGetNodesConsumerWhenNodesAreNull() {

        final List<DecisionComponent> includedDRGElements = spy(new ArrayList<>());

        doReturn(includedDRGElements).when(decisionComponents).getIncludedDRGElements();

        decisionComponents.getNodesConsumer().accept(null);

        verify(view).hideLoading();
        verify(includedDRGElements, never()).clear();
        verify(includedDRGElements, never()).add(any());
        verify(decisionComponents, never()).refreshView();
    }

    @Test
    public void testAsDMNIncludedModel() {

        final String modelName = "Model Name";
        final String namespace = "The Namespace";
        final String type = "The type";
        final String file = "my file.dmn";
        final String filePath = "users/some/" + file;
        final Import anImport = new Import();
        anImport.setName(new Name(modelName));
        anImport.setNamespace(namespace);
        anImport.setImportType(type);
        anImport.setLocationURI(new LocationURI(filePath));

        final DMNIncludedModel includedModel = decisionComponents.asDMNIncludedModel(anImport);

        assertEquals(modelName, includedModel.getModelName());
        assertEquals(namespace, includedModel.getNamespace());
        assertEquals(type, includedModel.getImportType());
        assertEquals(filePath, includedModel.getPath());
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

    @Test
    public void testCreateDecisionComponentItems() {

        final List<DecisionComponent> decisionComponentsItems = new ArrayList<>();
        decisionComponentsItems.add(makeDecisionComponent("Decision-1", "uuid1", "ModelName", false));
        decisionComponentsItems.add(makeDecisionComponent("Decision-1", "uuid1", "ModelName", false));
        decisionComponentsItems.add(makeDecisionComponent("Decision-1", "uuid2", "ModelName", false));
        decisionComponentsItems.add(makeDecisionComponent("included.Decision-2", "uuidA", "included.dmn", true));
        decisionComponentsItems.add(makeDecisionComponent("included.Decision-2", "uuidA", "included.dmn", true));
        decisionComponentsItems.add(makeDecisionComponent("included.Decision-2", "uuidB", "included.dmn", true));

        when(itemManagedInstance.get()).then((e) -> new DecisionComponentsItem(decisionComponentsItemView));

        decisionComponents.createDecisionComponentItems(decisionComponentsItems);

        verify(decisionComponents, times(4)).createDecisionComponentItem(decisionComponentArgumentCaptor.capture());

        final List<DecisionComponent> createdDecisionComponents = decisionComponentArgumentCaptor.getAllValues();
        assertEquals("uuid1", createdDecisionComponents.get(0).getDrgElement().getId().getValue());
        assertEquals("uuid2", createdDecisionComponents.get(1).getDrgElement().getId().getValue());
        assertEquals("uuidA", createdDecisionComponents.get(2).getDrgElement().getId().getValue());
        assertEquals("uuidB", createdDecisionComponents.get(3).getDrgElement().getId().getValue());
    }

    private DMNIncludedModel makeDMNIncludedModel(final String namespace) {
        return new DMNIncludedModel("", "", "", namespace, "", 0, 0);
    }

    private DecisionComponent makeDecisionComponent(final String name,
                                                    final String drgElementId,
                                                    final String fileName,
                                                    final boolean imported) {
        final Decision decision = new Decision();
        decision.setName(new Name(name));
        decision.setId(new Id(drgElementId));
        return new DecisionComponent(fileName, decision, imported);
    }

    private DecisionComponent makeDecisionComponent(final String name,
                                                    final String fileName,
                                                    final boolean imported) {
        final String drgElementId = new Id().getValue();
        return makeDecisionComponent(name, drgElementId, fileName, imported);
    }
}
