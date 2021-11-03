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

package org.dashbuilder.client;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.Window;
import org.dashbuilder.client.navigation.NavigationManager;
import org.dashbuilder.client.perspective.generator.RuntimePerspectiveGenerator;
import org.dashbuilder.client.plugins.RuntimePerspectivePluginManager;
import org.dashbuilder.client.resources.i18n.AppConstants;
import org.dashbuilder.shared.model.RuntimeModel;
import org.dashbuilder.shared.model.RuntimeServiceResponse;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mvp.Command;

@ApplicationScoped
public class RuntimeClientLoader {

    private static AppConstants i18n = AppConstants.INSTANCE;

    public static final String IMPORT_ID_PARAM = "import";

    RuntimeModelResourceClient runtimeModelResourceClient;

    RuntimePerspectiveGenerator perspectiveEditorGenerator;

    RuntimePerspectivePluginManager runtimePerspectivePluginManager;

    NavigationManager navigationManager;

    BusyIndicatorView loading;

    public RuntimeClientLoader() {
        // do nothing
    }

    @Inject
    public RuntimeClientLoader(RuntimeModelResourceClient runtimeModelResourceClient,
                               RuntimePerspectiveGenerator perspectiveEditorGenerator,
                               RuntimePerspectivePluginManager runtimePerspectivePluginManager,
                               NavigationManager navigationManager,
                               BusyIndicatorView loading) {
        this.runtimeModelResourceClient = runtimeModelResourceClient;
        this.perspectiveEditorGenerator = perspectiveEditorGenerator;
        this.runtimePerspectivePluginManager = runtimePerspectivePluginManager;
        this.navigationManager = navigationManager;
        this.loading = loading;
    }

    public void load(Consumer<RuntimeServiceResponse> responseConsumer,
                     BiConsumer<Object, Throwable> error) {
        String importID = getImportId();
        loading.showBusyIndicator(i18n.loadingDashboards());
        
        runtimeModelResourceClient.getRuntimeModelInfo(importID, response -> {
            loading.hideBusyIndicator();
            response.getRuntimeModelOp().ifPresent(this::registerModel);
            responseConsumer.accept(response);
        }, (msg, t) -> {
            handleError(error, msg, t);
        });
    }

    public void loadModel(Consumer<RuntimeModel> modelLoaded,
                          Command emptyModel,
                          BiConsumer<Object, Throwable> error) {
        String importID = getImportId();
        loadModel(importID, modelLoaded, emptyModel, error);

    }

    public void loadModel(String importId,
                          Consumer<RuntimeModel> modelLoaded,
                          Command emptyModel,
                          BiConsumer<Object, Throwable> error) {
        loading.showBusyIndicator(i18n.loadingDashboards());
        runtimeModelResourceClient.getRuntimeModel(importId,
                                                   modelOp -> handleResponse(modelLoaded, emptyModel, modelOp),
                                                   errorMessage -> handleError(error,
                                                                               errorMessage,
                                                                               new RuntimeException("Not able to retrieve Runtime Model"))

        );

    }

    private boolean handleError(BiConsumer<Object, Throwable> error, Object message, Throwable throwable) {
        loading.hideBusyIndicator();
        error.accept(message, throwable);
        return false;
    }

    private void handleResponse(Consumer<RuntimeModel> modelLoaded, Command emptyModel, Optional<RuntimeModel> runtimeModelOp) {
        loading.hideBusyIndicator();
        if (runtimeModelOp.isPresent()) {
            RuntimeModel runtimeModel = runtimeModelOp.get();
            registerModel(runtimeModel);
            modelLoaded.accept(runtimeModel);
        } else {
            emptyModel.execute();
        }
    }

    private void registerModel(RuntimeModel runtimeModel) {
        runtimeModel.getLayoutTemplates().forEach(perspectiveEditorGenerator::generatePerspective);
        runtimePerspectivePluginManager.setTemplates(runtimeModel.getLayoutTemplates());
        navigationManager.setDefaultNavTree(runtimeModel.getNavTree());
    }

    public String getImportId() {
        return Window.Location.getParameter(IMPORT_ID_PARAM);
    }

}