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

package org.kie.workbench.common.dmn.client.docks.navigator;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.docks.navigator.events.RefreshDecisionComponents;
import org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponents;
import org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreePresenter;
import org.kie.workbench.common.dmn.client.editors.included.common.IncludedModelsContext;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementAddedEvent;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.Position;

import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DecisionNavigatorPresenter_DecisionNavigator;

@ApplicationScoped
@WorkbenchScreen(identifier = DecisionNavigatorPresenter.IDENTIFIER)
public class DecisionNavigatorPresenter {

    public static final String IDENTIFIER = "org.kie.dmn.decision.navigator";

    private View view;

    private DecisionNavigatorTreePresenter treePresenter;

    private DecisionComponents decisionComponents;

    private DecisionNavigatorObserver decisionNavigatorObserver;

    private TranslationService translationService;

    private IncludedModelsContext includedModelContext;

    private DecisionNavigatorItemsProvider navigatorItemsProvider;

    private DMNDiagramsSession dmnDiagramsSession;

    private boolean isRefreshHandlersEnabled = false;

    @Inject
    public DecisionNavigatorPresenter(final View view,
                                      final DecisionNavigatorTreePresenter treePresenter,
                                      final DecisionComponents decisionComponents,
                                      final DecisionNavigatorObserver decisionNavigatorObserver,
                                      final TranslationService translationService,
                                      final IncludedModelsContext includedModelContext,
                                      final DecisionNavigatorItemsProvider navigatorItemsProvider,
                                      final DMNDiagramsSession dmnDiagramsSession) {
        this.view = view;
        this.treePresenter = treePresenter;
        this.decisionComponents = decisionComponents;
        this.decisionNavigatorObserver = decisionNavigatorObserver;
        this.translationService = translationService;
        this.includedModelContext = includedModelContext;
        this.navigatorItemsProvider = navigatorItemsProvider;
        this.dmnDiagramsSession = dmnDiagramsSession;
    }

    @WorkbenchPartView
    public View getView() {
        return view;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return translationService.format(DecisionNavigatorPresenter_DecisionNavigator);
    }

    @DefaultPosition
    public Position getDefaultPosition() {
        return CompassPosition.WEST;
    }

    @PostConstruct
    void setup() {
        initialize();
        setupView();
        enableRefreshHandlers();
        refreshComponentsView();
    }

    public void onRefreshDecisionComponents(final @Observes RefreshDecisionComponents events) {
        refreshComponentsView();
    }

    public void onElementAdded(final @Observes CanvasElementAddedEvent event) {
        refreshComponentsView();
    }

    public DecisionNavigatorTreePresenter getTreePresenter() {
        return treePresenter;
    }

    public void removeAllElements() {
        treePresenter.removeAllItems();
        decisionComponents.removeAllItems();
    }

    void initialize() {
        view.init(this);
        decisionNavigatorObserver.init(this);
    }

    void setupView() {
        view.setupMainTree(treePresenter.getView());
        if (includedModelContext.isIncludedModelChannel()) {
            view.showDecisionComponentsContainer();
            view.setupDecisionComponents(decisionComponents.getView());
        } else {
            view.hideDecisionComponentsContainer();
        }
    }

    public void refresh() {
        if (dmnDiagramsSession.isSessionStatePresent()) {
            refreshTreeView();
            refreshComponentsView();
        }
    }

    public void refreshTreeView() {
        if (isRefreshHandlersEnabled()) {
            treePresenter.setupItems(getItems());
        }
    }

    void refreshComponentsView() {
        if (isRefreshHandlersEnabled()) {
            decisionComponents.refresh();
        }
    }

    public void enableRefreshHandlers() {
        isRefreshHandlersEnabled = true;
    }

    public void disableRefreshHandlers() {
        isRefreshHandlersEnabled = false;
    }

    private boolean isRefreshHandlersEnabled() {
        return isRefreshHandlersEnabled;
    }

    List<DecisionNavigatorItem> getItems() {
        return navigatorItemsProvider.getItems();
    }

    public void clearSelections() {
        getTreePresenter().deselectItem();
    }

    public interface View extends UberElemental<DecisionNavigatorPresenter>,
                                  IsElement {

        void setupMainTree(final DecisionNavigatorTreePresenter.View mainTreeComponent);

        void setupDecisionComponents(final DecisionComponents.View decisionComponentsComponent);

        void showDecisionComponentsContainer();

        void hideDecisionComponentsContainer();
    }
}
