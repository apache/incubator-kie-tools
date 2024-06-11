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
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.Import;
import org.kie.workbench.common.dmn.api.editors.included.DMNImportTypes;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedModel;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedNode;
import org.kie.workbench.common.dmn.client.api.included.legacy.DMNIncludeModelsClient;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.uberfire.client.mvp.UberElemental;

import static org.kie.workbench.common.dmn.api.editors.included.DMNImportTypes.determineImportType;

@Dependent
public class DecisionComponents {

    private final View view;

    private final DMNIncludeModelsClient client;

    private final ManagedInstance<DecisionComponentsItem> itemManagedInstance;

    private final DecisionComponentFilter filter;

    private final DMNDiagramsSession dmnDiagramsSession;

    private final DMNGraphUtils dmnGraphUtils;

    private final List<DecisionComponentsItem> decisionComponentsItems = new ArrayList<>();

    private final List<DMNIncludedModel> latestIncludedModelsLoaded = new ArrayList<>();

    private final List<DecisionComponent> includedDRGElements = new ArrayList<>();

    private final List<DecisionComponent> modelDRGElements = new ArrayList<>();

    public DecisionComponents() {
        this(null, null, null, null, null, null);
        // CDI proxy
    }

    @Inject
    public DecisionComponents(final View view,
                              final DMNIncludeModelsClient client,
                              final ManagedInstance<DecisionComponentsItem> itemManagedInstance,
                              final DecisionComponentFilter filter,
                              final DMNDiagramsSession dmnDiagramsSession,
                              final DMNGraphUtils dmnGraphUtils) {
        this.view = view;
        this.client = client;
        this.itemManagedInstance = itemManagedInstance;
        this.filter = filter;
        this.dmnDiagramsSession = dmnDiagramsSession;
        this.dmnGraphUtils = dmnGraphUtils;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public View getView() {
        return view;
    }

    public void refresh() {

        if (!dmnDiagramsSession.isSessionStatePresent()) {
            return;
        }

        if (!isIncludedNodeListsUpdated()) {
            refreshIncludedNodesList();
        }

        loadModelComponents();
    }

    private boolean isIncludedNodeListsUpdated() {
        final List<String> currentIncludedNamespaces = getNamespaces(getDMNIncludedModels());
        final List<String> latestLoadedNamespaces = getNamespaces(getLatestIncludedModelsLoaded());
        return Objects.equals(currentIncludedNamespaces, latestLoadedNamespaces);
    }

    void refreshIncludedNodesList() {

        final List<DMNIncludedModel> dmnIncludedModels = getDMNIncludedModels();

        startLoading();

        getLatestIncludedModelsLoaded().clear();
        getLatestIncludedModelsLoaded().addAll(dmnIncludedModels);
        client.loadNodesFromImports(dmnIncludedModels, getNodesConsumer());
    }

    void loadModelComponents() {

        final String dmnModelName = dmnGraphUtils.getModelDefinitions().getName().getValue();
        final List<DRGElement> dmnModelDRGElements = dmnDiagramsSession.getModelDRGElements();

        getModelDRGElements().clear();
        dmnModelDRGElements.forEach(drgElement -> {
            getModelDRGElements().add(makeDecisionComponent(dmnModelName, drgElement));
        });
        refreshView();
    }

    void refreshView() {

        clearDecisionComponents();

        createDecisionComponentItems(getIncludedDRGElements());
        createDecisionComponentItems(getModelDRGElements());
        setComponentsCounter(getDecisionComponentsItems().size());

        if (getDecisionComponentsItems().isEmpty()) {
            view.disableFilterInputs();
            view.showEmptyState();
        } else {
            view.enableFilterInputs();
        }
    }

    private void setComponentsCounter(final int numberOfDecisionComponents) {
        view.setComponentsCounter(numberOfDecisionComponents);
    }

    void createDecisionComponentItems(final List<DecisionComponent> decisionComponents) {
        for (final DecisionComponent component : decisionComponents) {
            if (!isDRGElementAdded(component)) {
                createDecisionComponentItem(component);
            }
        }
    }

    private boolean isDRGElementAdded(final DecisionComponent decisionComponent) {
        return getDecisionComponentsItems().stream().anyMatch(item -> {
            final String decisionComponentItemId = item.getDecisionComponent().getDrgElement().getId().getValue();
            final String decisionComponentId = decisionComponent.getDrgElement().getId().getValue();
            return Objects.equals(decisionComponentItemId, decisionComponentId);
        });
    }

    void createDecisionComponentItem(final DecisionComponent component) {
        final DecisionComponentsItem item = itemManagedInstance.get();

        item.setDecisionComponent(component);

        getDecisionComponentsItems().add(item);
        view.addListItem(item.getView().getElement());
    }

    Consumer<List<DMNIncludedNode>> getNodesConsumer() {
        return nodes -> {
            view.hideLoading();
            if (nodes != null) {
                getIncludedDRGElements().clear();
                nodes.forEach(node -> getIncludedDRGElements().add(makeDecisionComponent(node)));
                refreshView();
            }
        };
    }

    void startLoading() {
        view.showLoading();
        view.disableFilterInputs();
    }

    void clearDecisionComponents() {
        getDecisionComponentsItems().clear();
        view.clear();
    }

    void applyFilter() {
        hideAllItems();
        showFilteredItems();
    }

    void applyTermFilter(final String value) {
        getFilter().setTerm(value);
        applyFilter();
    }

    void applyDrgElementFilterFilter(final String value) {
        getFilter().setDrgElement(value);
        applyFilter();
    }

    private void showFilteredItems() {
        getFilter()
                .query(getDecisionComponentsItems().stream())
                .sorted(Comparator.comparing(item -> item.getDecisionComponent().getName()))
                .forEach(DecisionComponentsItem::show);
    }

    private void hideAllItems() {
        getDecisionComponentsItems().forEach(DecisionComponentsItem::hide);
    }

    DecisionComponent makeDecisionComponent(final DMNIncludedNode node) {
        return new DecisionComponent(node.getFileName(), node.getDrgElement(), true);
    }

    DecisionComponent makeDecisionComponent(final String id,
                                            final DRGElement drgElement) {
        return new DecisionComponent(id, drgElement, false);
    }

    List<DMNIncludedModel> getDMNIncludedModels() {
        return dmnDiagramsSession
                .getModelImports()
                .stream()
                .filter(anImport -> Objects.equals(DMNImportTypes.DMN, determineImportType(anImport.getImportType())))
                .map(this::asDMNIncludedModel)
                .collect(Collectors.toList());
    }

    DMNIncludedModel asDMNIncludedModel(final Import anImport) {
        final String modelName = anImport.getName().getValue();
        final String namespace = anImport.getNamespace();
        final String importType = anImport.getImportType();
        final String path = anImport.getLocationURI().getValue();
        return new DMNIncludedModel(modelName, "", path, namespace, importType, 0, 0);
    }

    DecisionComponentFilter getFilter() {
        return filter;
    }

    public void removeAllItems() {
        clearDecisionComponents();
    }

    List<DecisionComponentsItem> getDecisionComponentsItems() {
        return decisionComponentsItems;
    }

    List<DecisionComponent> getModelDRGElements() {
        return modelDRGElements;
    }

    List<DecisionComponent> getIncludedDRGElements() {
        return includedDRGElements;
    }

    List<DMNIncludedModel> getLatestIncludedModelsLoaded() {
        return latestIncludedModelsLoaded;
    }

    private List<String> getNamespaces(final List<DMNIncludedModel> dmnIncludedModels) {
        return dmnIncludedModels
                .stream()
                .map(DMNIncludedModel::getNamespace)
                .collect(Collectors.toList());
    }

    public interface View extends UberElemental<DecisionComponents>,
                                  IsElement {

        void clear();

        void addListItem(final HTMLElement htmlElement);

        void showEmptyState();

        void showLoading();

        void hideLoading();

        void disableFilterInputs();

        void enableFilterInputs();

        void setComponentsCounter(final Integer count);
    }
}
