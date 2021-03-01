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

import elemental2.dom.DomGlobal;
import elemental2.dom.DomGlobal.SetTimeoutCallbackFn;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.docks.navigator.events.RefreshDecisionComponents;
import org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponents;
import org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreePresenter;
import org.kie.workbench.common.dmn.client.editors.included.common.IncludedModelsContext;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.Position;

import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DecisionNavigatorPresenter_DecisionNavigator;

@ApplicationScoped
@WorkbenchScreen(identifier = DecisionNavigatorPresenter.IDENTIFIER)
public class DecisionNavigatorPresenter {

    public static final String IDENTIFIER = "org.kie.dmn.decision.navigator";

    static final int DEFER_DELAY = 250;

    private View view;

    private DecisionNavigatorTreePresenter treePresenter;

    private DecisionComponents decisionComponents;

    private DecisionNavigatorObserver decisionNavigatorObserver;

    private TranslationService translationService;

    private IncludedModelsContext includedModelContext;

    private DecisionNavigatorItemsProvider navigatorItemsProvider;

    private DMNDiagramsSession dmnDiagramsSession;

    double latestDeferred = 0;

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
        refreshComponentsView();
    }

    public void onRefreshDecisionComponents(final @Observes RefreshDecisionComponents events) {
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
        deferredRefresh();
    }

    public void refreshTreeView() {
        treePresenter.setupItems(getItems());
    }

    void refreshComponentsView() {
        decisionComponents.refresh();
    }

    List<DecisionNavigatorItem> getItems() {
        return navigatorItemsProvider.getItems();
    }

    public void clearSelections() {
        getTreePresenter().deselectItem();
    }

    void deferredRefresh() {
        if (dmnDiagramsSession.isSessionStatePresent()) {
            defer(() -> {
                refreshTreeView();
                refreshComponentsView();
            });
        }
    }

    void defer(final Command cmd) {
        clearTimeout(latestDeferred);
        latestDeferred = setTimeout((e) -> cmd.execute(), DEFER_DELAY);
    }

    double setTimeout(final SetTimeoutCallbackFn callbackFn,
                      final int delay) {
        return DomGlobal.setTimeout(callbackFn, delay);
    }

    void clearTimeout(final double latestDeferred) {
        DomGlobal.clearTimeout(latestDeferred);
    }

    public interface View extends UberElemental<DecisionNavigatorPresenter>,
                                  IsElement {

        void setupMainTree(final DecisionNavigatorTreePresenter.View mainTreeComponent);

        void setupDecisionComponents(final DecisionComponents.View decisionComponentsComponent);

        void showDecisionComponentsContainer();

        void hideDecisionComponentsContainer();
    }
}
