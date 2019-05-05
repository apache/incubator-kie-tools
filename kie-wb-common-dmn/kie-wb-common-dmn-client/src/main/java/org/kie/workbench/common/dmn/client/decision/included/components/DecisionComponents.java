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

package org.kie.workbench.common.dmn.client.decision.included.components;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.dmn.api.definition.v1_1.Import;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedModel;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedNode;
import org.kie.workbench.common.dmn.client.api.included.legacy.DMNIncludeModelsClient;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.uberfire.client.mvp.UberElemental;

@Dependent
public class DecisionComponents {

    private final View view;

    private final DMNGraphUtils graphUtils;

    private final DMNIncludeModelsClient client;

    private final ManagedInstance<DecisionComponentsItem> itemManagedInstance;

    private final DecisionComponentFilter filter;

    private final List<DecisionComponentsItem> decisionComponentsItems = new ArrayList<>();

    @Inject
    public DecisionComponents(final View view,
                              final DMNGraphUtils graphUtils,
                              final DMNIncludeModelsClient client,
                              final ManagedInstance<DecisionComponentsItem> itemManagedInstance,
                              final DecisionComponentFilter filter) {
        this.view = view;
        this.graphUtils = graphUtils;
        this.client = client;
        this.itemManagedInstance = itemManagedInstance;
        this.filter = filter;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public View getView() {
        return view;
    }

    public void refresh(final Diagram diagram) {

        clearDecisionComponents();
        startLoading();

        client.loadNodesFromImports(getDMNIncludedModels(diagram), getNodesConsumer());
    }

    Consumer<List<DMNIncludedNode>> getNodesConsumer() {
        return nodes -> {
            view.setComponentsCounter(nodes.size());
            view.hideLoading();
            if (!nodes.isEmpty()) {
                nodes.forEach(this::addComponent);
                view.enableFilterInputs();
            } else {
                view.showEmptyState();
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

        final DecisionComponentsItem item = itemManagedInstance.get();

        item.setDecisionComponent(makeDecisionComponent(node));

        getDecisionComponentsItems().add(item);
        view.addListItem(item.getView().getElement());
    }

    private DecisionComponent makeDecisionComponent(final DMNIncludedNode node) {
        return new DecisionComponent(node.getFileName(), node.getImportedElementId(), node.getDrgElementName(), node.getDrgElementClass());
    }

    List<DMNIncludedModel> getDMNIncludedModels(final Diagram diagram) {
        return graphUtils
                .getDefinitions(diagram)
                .getImport()
                .stream()
                .map(this::asDMNIncludedModel)
                .collect(Collectors.toList());
    }

    private DMNIncludedModel asDMNIncludedModel(final Import anImport) {
        final String modelName = anImport.getName().getValue();
        final String namespace = anImport.getNamespace();
        final String none = "";
        return new DMNIncludedModel(modelName, none, none, namespace);
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
    }
}
