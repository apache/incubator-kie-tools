/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dashbuilder.client.screens;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import elemental2.dom.DomGlobal;
import org.dashbuilder.client.RuntimeClientLoader;
import org.dashbuilder.client.RuntimeCommunication;
import org.dashbuilder.client.navbar.AppNavBar;
import org.dashbuilder.client.perspective.DashboardsListPerspective;
import org.dashbuilder.client.perspective.EmptyPerspective;
import org.dashbuilder.client.perspective.RuntimePerspective;
import org.dashbuilder.client.resources.i18n.AppConstants;
import org.dashbuilder.shared.model.DashbuilderRuntimeMode;
import org.dashbuilder.shared.model.RuntimeModel;
import org.dashbuilder.shared.model.RuntimeServiceResponse;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.lifecycle.OnOpen;

/**
 * 
 * Responsible for handling screens transition and communication. No view for this screen is required.
 * 
 */
@ApplicationScoped
@WorkbenchScreen(identifier = RouterScreen.ID)
public class RouterScreen {

    public static final String ID = "RouterScreen";

    private static AppConstants i18n = AppConstants.INSTANCE;

    public interface View extends UberElemental<RouterScreen> {

    }

    @Inject
    RuntimeClientLoader clientLoader;

    @Inject
    RuntimeScreen runtimeScreen;

    @Inject
    DashboardsListScreen dashboardsListScreen;

    @Inject
    RuntimeCommunication runtimeCommunication;

    @Inject
    PlaceManager placeManager;

    @Inject
    AppNavBar appNavBar;

    @Inject
    View view;

    private DashbuilderRuntimeMode mode;

    @WorkbenchPartTitle
    public String title() {
        return i18n.routerScreenTitle();
    }

    @WorkbenchPartView
    public View getView() {
        return view;
    }

    @OnOpen
    public void onOpen() {
        doRoute();
    }

    public void doRoute() {
        clientLoader.load(this::route,
                          (a, t) -> runtimeCommunication.showError(i18n.errorLoadingDashboards(), t));
    }

    protected void route(RuntimeServiceResponse response) {
        this.mode  = response.getMode();
        Optional<RuntimeModel> runtimeModelOp = response.getRuntimeModelOp();

        if (mode == DashbuilderRuntimeMode.MULTIPLE_IMPORT) {
            appNavBar.setDashboardListEnabled(true);
            appNavBar.setup();
        }

        if (runtimeModelOp.isPresent()) {
            RuntimeModel runtimeModel = runtimeModelOp.get();
            placeManager.goTo(RuntimePerspective.ID);
            runtimeScreen.loadDashboards(runtimeModel);
            runtimeScreen.goToIndex(runtimeModel.getLayoutTemplates());
            return;
        }

        if (response.getAvailableModels().isEmpty()) {
            placeManager.goTo(EmptyPerspective.ID);
            return;
        }

        dashboardsListScreen.loadList(response.getAvailableModels());
        placeManager.goTo(DashboardsListPerspective.ID);
    }
    
    public void afterDashboardUpload(String id) {
        if (mode != null && mode == DashbuilderRuntimeMode.MULTIPLE_IMPORT) {
            listDashboards();
        } else {
            loadDashboard(id);
        }      
    }

    public void loadDashboard(String importId) {
        String newUrl = GWT.getHostPageBaseURL() + "?" +
                        RuntimeClientLoader.IMPORT_ID_PARAM + "=" +
                        importId;
        DomGlobal.window.history.replaceState(null,
                                              "Dashbuilder Runtime |" + importId,
                                              newUrl);
        doRoute();
    }

    public void listDashboards() {
        DomGlobal.window.history.replaceState(null,
                                              "Dashbuilder Runtime",
                                              GWT.getHostPageBaseURL());
        doRoute();
    }

}