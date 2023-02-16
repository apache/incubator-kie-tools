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

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.dashbuilder.client.RuntimeClientLoader;
import org.dashbuilder.client.perspective.EmptyPerspective;
import org.dashbuilder.client.resources.i18n.AppConstants;
import org.dashbuilder.shared.event.UpdatedRuntimeModelEvent;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.lifecycle.OnOpen;

/**
 * Screen displayed when there's no dashboards available.
 *
 */
@ApplicationScoped
@WorkbenchScreen(identifier = EmptyScreen.ID)
public class EmptyScreen {

    public static final String ID = "EmptyScreen";

    private static final AppConstants i18n = AppConstants.INSTANCE;

    @Inject
    View view;

    @Inject
    RouterScreen router;

    @Inject
    PerspectiveManager perspectiveManager;

    @Inject
    RuntimeClientLoader loader;

    public interface View extends UberElemental<EmptyScreen> {

        void editorMode();

        void noModel();

        void modelId(String modelId);

        void enableSamplesButton(Runnable action);

    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    @WorkbenchPartTitle
    public String title() {
        return i18n.uploadDashboardsTitle();
    }

    @WorkbenchPartView
    protected View getPart() {
        return view;
    }

    @OnOpen
    protected void onOpen() {
        var modelId = loader.getImportId();
        if (loader.isEditor()) {
            view.editorMode();
        } else if (modelId != null && !modelId.trim().equals("")) {
            view.modelId(modelId);
        } else {
            view.noModel();
        }

        if (loader.hasSamples()) {
            view.enableSamplesButton(router::goToSamplesScreen);
        }
    }

    public void onModelUpdated(@Observes UpdatedRuntimeModelEvent event) {
        reload();
    }

    private void reload() {
        var currentPlace = perspectiveManager.getCurrentPerspective().getIdentifier();
        if (EmptyPerspective.ID.equals(currentPlace)) {
            router.listDashboards();
        }
    }

}
