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
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.util.FileUtils;
import org.uberfire.client.mvp.UberElemental;

import static org.kie.workbench.common.dmn.api.editors.included.DMNImportTypes.determineImportType;

@Dependent
public class DecisionComponents {

    private final View view;

    private final DMNIncludeModelsClient client;

    private final ManagedInstance<DecisionComponentsItem> itemManagedInstance;

    private final DecisionComponentFilter filter;

    private final List<DecisionComponentsItem> decisionComponentsItems = new ArrayList<>();

    private final DMNDiagramsSession dmnDiagramsSession;

    public DecisionComponents() {
        this(null, null, null, null, null);
        // CDI proxy
    }

    @Inject
    public DecisionComponents(final View view,
                              final DMNIncludeModelsClient client,
                              final ManagedInstance<DecisionComponentsItem> itemManagedInstance,
                              final DecisionComponentFilter filter,
                              final DMNDiagramsSession dmnDiagramsSession) {
        this.view = view;
        this.client = client;
        this.itemManagedInstance = itemManagedInstance;
        this.filter = filter;
        this.dmnDiagramsSession = dmnDiagramsSession;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public View getView() {
        return view;
    }

    public void refresh() {

        clearDecisionComponents();
        startLoading();

        client.loadNodesFromImports(getDMNIncludedModels(), getNodesConsumer());

        loadDRDComponents();
    }

    void loadDRDComponents() {

        final List<DecisionComponent> decisionComponents = new ArrayList<>();

        dmnDiagramsSession.getModelDRGElements().forEach(drgElement -> {
            decisionComponents.add(makeDecisionComponent(drgElement.getDiagramId(),
                                                         drgElement));
        });

        createDecisionComponentItems(decisionComponents);

        view.enableFilterInputs();
        view.hideLoading();
        view.setComponentsCounter(view.getComponentsCounter() + decisionComponents.size());
    }

    boolean definitionContainsDRGElement(final Node node) {
        return (node.getContent() instanceof Definition)
                && ((Definition) node.getContent()).getDefinition() instanceof DRGElement;
    }

    void createDecisionComponentItems(final List<DecisionComponent> decisionComponents) {
        for (final DecisionComponent component : decisionComponents) {
            createDecisionComponentItem(component);
        }
    }

    void createDecisionComponentItem(final DecisionComponent component) {
        final DecisionComponentsItem item = itemManagedInstance.get();

        item.setDecisionComponent(component);

        getDecisionComponentsItems().add(item);
        view.addListItem(item.getView().getElement());
    }

    Consumer<List<DMNIncludedNode>> getNodesConsumer() {
        return nodes -> {
            if (!Objects.isNull(nodes)) {
                view.setComponentsCounter(nodes.size());
                view.hideLoading();
                if (!nodes.isEmpty()) {
                    nodes.forEach(this::addComponent);
                    view.enableFilterInputs();
                } else {
                    view.showEmptyState();
                }
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

    void addComponent(final DMNIncludedNode node) {
        createDecisionComponentItem(makeDecisionComponent(node));
    }

    DecisionComponent makeDecisionComponent(final DMNIncludedNode node) {
        return new DecisionComponent(node.getFileName(), node.getDrgElement(), true);
    }

    DecisionComponent makeDecisionComponent(final String id, final DRGElement drgElement) {
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
        final String path = FileUtils.getFileName(anImport.getLocationURI().getValue());
        return new DMNIncludedModel(modelName, "", path, namespace, importType, 0, 0);
    }

    List<DecisionComponentsItem> getDecisionComponentsItems() {
        return decisionComponentsItems;
    }

    DecisionComponentFilter getFilter() {
        return filter;
    }

    public void removeAllItems() {
        clearDecisionComponents();
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

        Integer getComponentsCounter();
    }
}
