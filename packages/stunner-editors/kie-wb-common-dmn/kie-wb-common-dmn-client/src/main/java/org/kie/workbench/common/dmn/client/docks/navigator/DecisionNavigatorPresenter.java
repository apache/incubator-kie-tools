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

package org.kie.workbench.common.dmn.client.docks.navigator;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.user.client.ui.IsWidget;
import elemental2.dom.DomGlobal;
import elemental2.dom.DomGlobal.SetTimeoutCallbackFn;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.client.common.KogitoChannelHelper;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.docks.navigator.events.RefreshDecisionComponents;
import org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponents;
import org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreePresenter;
import org.uberfire.client.mvp.AbstractActivity;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.mvp.Command;
import org.uberfire.security.ResourceType;
import org.uberfire.workbench.model.ActivityResourceType;

@ApplicationScoped
@Named(DecisionNavigatorPresenter.IDENTIFIER)
public class DecisionNavigatorPresenter extends AbstractActivity {

    public static final String IDENTIFIER = "org.kie.dmn.decision.navigator";

    static final int DEFER_DELAY = 250;

    private View view;

    private DecisionNavigatorTreePresenter treePresenter;

    private DecisionComponents decisionComponents;

    private DecisionNavigatorObserver decisionNavigatorObserver;

    private KogitoChannelHelper kogitoChannelHelper;

    private DecisionNavigatorItemsProvider navigatorItemsProvider;

    private DMNDiagramsSession dmnDiagramsSession;

    private boolean isRefreshComponentsViewSuspended;

    double latestDeferred = 0;

    @Inject
    public DecisionNavigatorPresenter(final View view,
                                      final DecisionNavigatorTreePresenter treePresenter,
                                      final DecisionComponents decisionComponents,
                                      final DecisionNavigatorObserver decisionNavigatorObserver,
                                      final TranslationService translationService,
                                      final KogitoChannelHelper kogitoChannelHelper,
                                      final DecisionNavigatorItemsProvider navigatorItemsProvider,
                                      final DMNDiagramsSession dmnDiagramsSession) {
        this.view = view;
        this.treePresenter = treePresenter;
        this.decisionComponents = decisionComponents;
        this.decisionNavigatorObserver = decisionNavigatorObserver;
        this.kogitoChannelHelper = kogitoChannelHelper;
        this.navigatorItemsProvider = navigatorItemsProvider;
        this.dmnDiagramsSession = dmnDiagramsSession;
    }

    @Override
    public ResourceType getResourceType() {
        return ActivityResourceType.DOCK;
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public IsWidget getWidget() {
        return ElementWrapperWidget.getWidget(view.getElement());
    }

    public View getView() {
        return view;
    }

    @PostConstruct
    void setup() {
        initialize();
        setupView();
        refreshComponentsView();
    }

    public void onRefreshDecisionComponents(final @Observes RefreshDecisionComponents events) {
        if (!isRefreshComponentsViewSuspended) {
            refreshComponentsView();
        }
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
        if (kogitoChannelHelper.isIncludedModelEnabled()) {
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

    public void refreshComponentsView() {
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

    public void setIsRefreshComponentsViewSuspended(final boolean isRefreshComponentsViewSuspended) {
        this.isRefreshComponentsViewSuspended = isRefreshComponentsViewSuspended;
    }

    public interface View extends UberElemental<DecisionNavigatorPresenter>,
                                  IsElement {

        void setupMainTree(final DecisionNavigatorTreePresenter.View mainTreeComponent);

        void setupDecisionComponents(final DecisionComponents.View decisionComponentsComponent);

        void showDecisionComponentsContainer();

        void hideDecisionComponentsContainer();
    }
}
