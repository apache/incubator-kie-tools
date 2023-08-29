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


package org.drools.workbench.screens.scenariosimulation.webapp.client.editor;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;
import elemental2.dom.DomGlobal;
import elemental2.promise.Promise;
import jsinterop.annotations.JsFunction;
import jsinterop.base.Js;
import org.drools.workbench.screens.scenariosimulation.client.popup.FileUploadPopupPresenter;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.kogito.client.editor.ScenarioSimulationEditorKogitoWrapper;
import org.drools.workbench.screens.scenariosimulation.webapp.client.popup.ScenarioSimulationKogitoLoadingScesimPopupPresenter;
import org.drools.workbench.screens.scenariosimulation.webapp.client.services.TestingVFSService;
import org.gwtbootstrap3.client.ui.Popover;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.mvp.AbstractActivity;
import org.uberfire.client.mvp.EditorActivity;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.security.ResourceType;
import org.uberfire.workbench.model.ActivityResourceType;
import org.uberfire.workbench.model.bridge.Notification;

@ApplicationScoped
@Named(ScenarioSimulationEditorActivity.ID)
public class ScenarioSimulationEditorActivity extends AbstractActivity implements EditorActivity {
    public static final String ID = "ScenarioSimulationEditor";

    private static final String BASE_URI = "/asserts/";
    public static final String BASE_DMN_URI = BASE_URI + "dmn/";
    public static final String BASE_SCESIM_URI = BASE_URI + "scesim/";
    public static final Path DMN_PATH = PathFactory.newPath("DMN", BASE_DMN_URI);
    public static final Path SCESIM_PATH = PathFactory.newPath("SCESIM", BASE_SCESIM_URI);

    @Inject
    protected TestingVFSService testingVFSService;

    @Inject
    protected FileUploadPopupPresenter fileUploadPopupPresenter;

    @Inject
    protected ScenarioSimulationEditorKogitoWrapper scenarioSimulationEditorKogitoWrapper;

    @Inject
    protected ScenarioSimulationKogitoLoadingScesimPopupPresenter loadScesimPopupPresenter;

    @Override
    public void onStartup(final PlaceRequest place) {
        super.onStartup(place);
        scenarioSimulationEditorKogitoWrapper.onStartup(place);
    }

    @Override
    public void onOpen() {
        super.onOpen();
    }

    @Override
    public void onClose() {
        super.onClose();
    }

    @Override
    public IsWidget getWidget() {
        return scenarioSimulationEditorKogitoWrapper.getWidget();
    }

    @Override
    public Promise<Void> setContent(String path, String value) {
        return scenarioSimulationEditorKogitoWrapper.setContent(path, value);
    }

    @Override
    public Promise<String> getContent() {
        return scenarioSimulationEditorKogitoWrapper.getContent();
    }

    @Override
    public Promise<String> getPreview() {
        return null;
    }

    @Override
    public Promise<List<Notification>> validate() {
        return Promise.resolve(Collections.emptyList());
    }

    @Override
    public String getIdentifier() {
        return ID;
    }

    @Override
    public ResourceType getResourceType() {
        return ActivityResourceType.EDITOR;
    }

    @PostConstruct
    public void init() {
        Js.asPropertyMap(DomGlobal.window).set("importDMN", (DoAction) () -> {
            importDMN();
        });

        Js.asPropertyMap(DomGlobal.window).set("importSCESIM", (DoAction) () -> {
            importSCESIM();
        });

        Js.asPropertyMap(DomGlobal.window).set("loadFile", (DoAction) () -> {
            loadFile();
        });
    }

    protected void importDMN() {
        createImportCommand("dmn", BASE_DMN_URI, "Choose a DMN file");
    }

    protected void importSCESIM() {
        createImportCommand("scesim", BASE_SCESIM_URI, "Choose a SCESIM file");
    }

    protected void loadFile() {
        Command loadCommand = () -> {
            String fullUri = loadScesimPopupPresenter.getSelectedPath();
            String fileName = fullUri.substring(fullUri.lastIndexOf('/') + 1);
            final Path path = PathFactory.newPath(fileName, fullUri);
            testingVFSService.loadFile(path, content -> {
                scenarioSimulationEditorKogitoWrapper.gotoPath(path);
                scenarioSimulationEditorKogitoWrapper.setContent(null, content);
            }, getErrorCallback("Failed to load"));
        };
        loadScesimPopupPresenter.show("Choose SCESIM", loadCommand);
    }

    protected ErrorCallback<String> getErrorCallback(String prependMessage) {
        return (message, throwable) -> {
            GWT.log(prependMessage + ": " + message, throwable);
            return false;
        };
    }

    protected void showPopover(String title, String content) {
        new Popover(title, content).show();
    }

    protected void saveFile(final Path path, final String content) {
        testingVFSService.saveFile(path, content,
                                   item -> GWT.log("Saved " + item),
                                   getErrorCallback("Failed to save"));
    }

    protected void createImportCommand(String extension, String uri, String mainTitleText) {
        Command okImportCommand = () -> {
            String fileName = fileUploadPopupPresenter.getFileName();
            if (fileName == null || fileName.isEmpty()) {
                showPopover("ERROR", "Missing file name");
                return;
            }
            fileName = fileName.replaceAll("\\s+", "_");
            String content = fileUploadPopupPresenter.getFileContents();
            final Path path = PathFactory.newPath(fileName, uri + fileName);
            saveFile(path, content);
        };
        fileUploadPopupPresenter.show(Collections.singletonList(extension),
                                      mainTitleText,
                                      ScenarioSimulationEditorConstants.INSTANCE.importLabel(),
                                      okImportCommand);
    }

    @FunctionalInterface
    @JsFunction
    public interface DoAction {

        void onInvoke();
    }
}
