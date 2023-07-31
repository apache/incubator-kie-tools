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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import org.dashbuilder.client.external.ExternalDataSetClientProvider;
import org.dashbuilder.client.navigation.NavigationManager;
import org.dashbuilder.client.parser.RuntimeModelClientParserFactory;
import org.dashbuilder.client.perspective.generator.RuntimePerspectiveGenerator;
import org.dashbuilder.client.plugins.RuntimePerspectivePluginManager;
import org.dashbuilder.client.resources.i18n.AppConstants;
import org.dashbuilder.client.screens.RouterScreen;
import org.dashbuilder.client.services.SamplesService;
import org.dashbuilder.client.setup.RuntimeClientMode;
import org.dashbuilder.client.setup.RuntimeClientSetup;
import org.dashbuilder.dataset.events.DataSetDefRemovedEvent;
import org.dashbuilder.patternfly.busyindicator.BusyIndicator;
import org.dashbuilder.shared.event.UpdatedGlobalSettingsEvent;
import org.dashbuilder.shared.event.UpdatedRuntimeModelEvent;
import org.dashbuilder.shared.model.DashbuilderRuntimeMode;
import org.dashbuilder.shared.model.RuntimeModel;
import org.dashbuilder.shared.model.RuntimeServiceResponse;
import org.uberfire.mvp.Command;

import static elemental2.dom.DomGlobal.fetch;

@ApplicationScoped
public class RuntimeClientLoader {

    private static final int NOT_FOUND_CODE = 404;

    private static AppConstants i18n = AppConstants.INSTANCE;

    public static final String IMPORT_ID_PARAM = "import";

    RuntimePerspectiveGenerator perspectiveEditorGenerator;

    RuntimePerspectivePluginManager runtimePerspectivePluginManager;

    NavigationManager navigationManager;

    BusyIndicator busyIndicator;

    ExternalDataSetClientProvider externalDataSetProvider;

    RuntimeModelClientParserFactory parserFactory;

    RuntimeModelContentListener contentListener;

    Event<UpdatedRuntimeModelEvent> updatedRuntimeModelEvent;

    Event<UpdatedGlobalSettingsEvent> updatedGlobalSettingsEvent;

    RouterScreen router;

    RuntimeClientMode mode;

    RuntimeModel clientModel;

    RuntimeClientSetup setup;

    private SamplesService samplesService;

    Event<DataSetDefRemovedEvent> dataSetDefRemovedEvent;

    String clientModelBaseUrl;

    boolean samplesDefaultHome;

    public RuntimeClientLoader() {
        // do nothing
    }

    @Inject
    public RuntimeClientLoader(RuntimePerspectiveGenerator perspectiveEditorGenerator,
                               RuntimePerspectivePluginManager runtimePerspectivePluginManager,
                               NavigationManager navigationManager,
                               BusyIndicator busyIndicator,
                               ExternalDataSetClientProvider externalDataSetRegister,
                               SamplesService samplesService,
                               RuntimeModelClientParserFactory parserFactory,
                               RuntimeModelContentListener contentListener,
                               Event<UpdatedRuntimeModelEvent> updatedRuntimeModelEvent,
                               Event<DataSetDefRemovedEvent> dataSetDefRemovedEvent,
                               Event<UpdatedGlobalSettingsEvent> updatedGlobalSettingsEvent,
                               RouterScreen router) {
        this.perspectiveEditorGenerator = perspectiveEditorGenerator;
        this.runtimePerspectivePluginManager = runtimePerspectivePluginManager;
        this.navigationManager = navigationManager;
        this.externalDataSetProvider = externalDataSetRegister;
        this.samplesService = samplesService;
        this.parserFactory = parserFactory;
        this.contentListener = contentListener;
        this.busyIndicator = busyIndicator;
        this.updatedRuntimeModelEvent = updatedRuntimeModelEvent;
        this.dataSetDefRemovedEvent = dataSetDefRemovedEvent;
        this.updatedGlobalSettingsEvent = updatedGlobalSettingsEvent;
        this.router = router;
    }

    @PostConstruct
    void loadSetup() {
        samplesDefaultHome = false;
        mode = RuntimeClientMode.EDITOR;
        clientModelBaseUrl = GWT.getHostPageBaseURL();
        setup = RuntimeClientSetup.Builder.get();
        if (setup != null) {
            readSetup();
        }

        if (isEditor()) {
            setupEditorMode();
        }
    }

    private void readSetup() {
        var modeStr = setup.getMode();
        var path = setup.getPath();
        samplesDefaultHome = setup.getSamplesDefaultHome();
        if (modeStr != null) {
            mode = RuntimeClientMode.getOrDefault(modeStr);
        } else if ((setup.getDashboards() != null && setup.getDashboards().length > 0) ||
                   setup.getSamplesUrl() != null && !setup.getSamplesUrl().trim().isEmpty()) {
            mode = RuntimeClientMode.CLIENT;
        }

        if (path != null && !path.trim().isEmpty()) {
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            if (!path.endsWith("/")) {
                path += "/";
            }
            clientModelBaseUrl = clientModelBaseUrl + path;
        }

    }

    public void load(Consumer<RuntimeServiceResponse> responseConsumer,
                     BiConsumer<Object, Throwable> error) {
        final var importID = getImportId();
        busyIndicator.show(i18n.loadingDashboards());
        if (mode == RuntimeClientMode.CLIENT) {
            if ((importID != null && !importID.trim().isEmpty())) {
                loadClientModelInfo(resolveModel(importID), responseConsumer, error);
            } else if (setup.getDashboards() != null && setup.getDashboards().length == 1) {
                loadClientModelInfo(resolveModel(setup.getDashboards()[0]), responseConsumer, error);
            } else {
                busyIndicator.hide();
                responseConsumer.accept(buildClientResponse(clientModel));
            }
        } else if (mode == RuntimeClientMode.EDITOR) {
            busyIndicator.hide();
            responseConsumer.accept(buildEditorResponse());
        }

    }

    public void loadModel(String importId,
                          Consumer<RuntimeModel> modelLoaded,
                          Command emptyModel,
                          BiConsumer<Object, Throwable> error) {
        busyIndicator.show(i18n.loadingDashboards());
        if (mode == RuntimeClientMode.CLIENT) {
            loadClientModel(clientModelBaseUrl + importId, modelLoaded, error);
        } else if (mode == RuntimeClientMode.EDITOR) {
            busyIndicator.hide();
            if (clientModel != null) {
                modelLoaded.accept(clientModel);
            } else {
                emptyModel.execute();
            }
        }
    }

    public void loadModel(Consumer<RuntimeModel> modelLoaded,
                          Command emptyModel,
                          BiConsumer<Object, Throwable> error) {
        var importID = getImportId();
        loadModel(importID, modelLoaded, emptyModel, error);
    }

    public String getImportId() {
        return Window.Location.getParameter(IMPORT_ID_PARAM);
    }

    public boolean isEditor() {
        return mode == RuntimeClientMode.EDITOR;
    }

    public boolean isClient() {
        return mode == RuntimeClientMode.CLIENT;
    }

    public boolean isSamplesDefaultHome() {
        return samplesDefaultHome && hasSamples();
    }

    public boolean hasSamples() {
        return !samplesService.allSamples().isEmpty();
    }

    /**
     * Loads the given content and attempts to route the result. 
     * @param content
     *  The content to be loaded
     * @return
     *  true if client model was sucessfully loaded.
     */
    public boolean loadContentAndRoute(String content) {
        try {
            if (content == null || content.trim().isEmpty()) {
                clientModel = null;
                router.doRoute();
            } else {
                var parser = parserFactory.getEditorParser(content);
                var runtimeModel = parser.parse(content);
                registerModel(runtimeModel);
                this.clientModel = runtimeModel;
                updatedGlobalSettingsEvent.fire(new UpdatedGlobalSettingsEvent(runtimeModel.getGlobalSettings()));
                updatedRuntimeModelEvent.fire(new UpdatedRuntimeModelEvent(""));
                return true;
            }
        } catch (Exception e) {
            router.goToContentError(e);
        }

        return false;
    }

    private void loadClientModel(String url,
                                 Consumer<RuntimeModel> responseConsumer,
                                 BiConsumer<Object, Throwable> error) {

        fetch(url).then(response -> {
            if (response.status == NOT_FOUND_CODE) {
                throw new RuntimeException("Content not found on URL '" + url + "'");
            }
            return response.text();
        }).then(content -> {
            busyIndicator.hide();
            try {
                if (loadContentAndRoute(content)) {
                    responseConsumer.accept(this.clientModel);
                }
            } catch (Exception e) {
                error.accept("Error loading client content", e);
            }
            return null;
        }).catch_(errorResponse -> {
            busyIndicator.hide();
            error.accept(errorResponse, new RuntimeException("Not able to load client model"));
            return null;
        });
    }

    private void loadClientModelInfo(String url,
                                     Consumer<RuntimeServiceResponse> responseConsumer,
                                     BiConsumer<Object, Throwable> error) {
        loadClientModel(url, model -> responseConsumer.accept(buildClientResponse(model)), error);
    }

    private void setupEditorMode() {
        contentListener.start(this::loadContentAndRoute);
    }

    private void registerModel(RuntimeModel runtimeModel) {
        clearObsoletePerspectives(runtimeModel);
        clearObsoleteDataSets(runtimeModel);
        runtimeModel.getLayoutTemplates().forEach(perspectiveEditorGenerator::generatePerspective);
        runtimeModel.getClientDataSets().forEach(externalDataSetProvider::register);
        runtimePerspectivePluginManager.setTemplates(runtimeModel.getLayoutTemplates());
        navigationManager.setDefaultNavTree(runtimeModel.getNavTree());
    }

    private RuntimeServiceResponse buildEditorResponse() {
        return new RuntimeServiceResponse(DashbuilderRuntimeMode.STATIC,
                Optional.ofNullable(clientModel),
                Collections.emptyList(),
                false);
    }

    private RuntimeServiceResponse buildClientResponse(RuntimeModel clientModel) {
        var clientMode = DashbuilderRuntimeMode.SINGLE_IMPORT;
        var list = new ArrayList<String>();
        if (setup.getDashboards() != null && setup.getDashboards().length > 1) {
            clientMode = DashbuilderRuntimeMode.MULTIPLE_IMPORT;
            Collections.addAll(list, setup.getDashboards());
        }
        return new RuntimeServiceResponse(clientMode,
                Optional.ofNullable(clientModel),
                list,
                false);
    }

    private void clearObsoletePerspectives(RuntimeModel runtimeModel) {
        if (clientModel != null) {
            clientModel.getLayoutTemplates()
                    .stream()
                    .filter(lt -> runtimeModel.getLayoutTemplates()
                            .stream()
                            .noneMatch(lt2 -> lt2.getName().equals(lt.getName())))
                    .forEach(lt -> perspectiveEditorGenerator.unregisterPerspective(lt));
        }
    }

    private void clearObsoleteDataSets(RuntimeModel runtimeModel) {
        if (this.clientModel != null) {
            this.clientModel.getClientDataSets()
                    .stream()
                    .filter(ds -> runtimeModel.getClientDataSets().stream().noneMatch(dsOld -> dsOld.equals(ds)))
                    .forEach(ds -> dataSetDefRemovedEvent.fire(new DataSetDefRemovedEvent(ds)));
        }
    }

    private String resolveModel(String importID) {
        if (importID.startsWith("http://") || importID.startsWith("https://")) {
            if (setup.getAllowExternal()) {
                return importID;
            }
            throw new IllegalArgumentException("External models are not enabled");
        }
        if (samplesService.isSample(importID)) {
            return importID;
        }
        return clientModelBaseUrl + importID;
    }
}
