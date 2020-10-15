/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.kogito.client.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import jsinterop.base.Js;
import org.drools.workbench.scenariosimulation.kogito.marshaller.mapper.JsUtils;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.MainJs;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.callbacks.DMN12UnmarshallCallback;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.DMN12;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDefinitions;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITItemDefinition;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.callbacks.Callback;

public class ScenarioSimulationKogitoDMNMarshallerService {

    @Inject
    private ScenarioSimulationKogitoResourceContentService resourceContentService;

    public void getDMNContent(final Path dmnFilePath,
                              final Callback<JSITDefinitions> callback,
                              final ErrorCallback<Object> errorCallback) {
        resourceContentService.getFileContent(dmnFilePath,
                                              getDMNFileContentCallback(dmnFilePath, callback, errorCallback),
                                              errorCallback);
    }

    private RemoteCallback<String> getDMNFileContentCallback(final Path dmnFilePath,
                                                             final Callback<JSITDefinitions> callback,
                                                             final ErrorCallback<Object> errorCallback) {
       return dmnContent -> unmarshallDMN(dmnContent, getDMN12UnmarshallCallback(dmnFilePath, callback, errorCallback));
    }

    protected DMN12UnmarshallCallback getDMN12UnmarshallCallback(final Path dmnFilePath,
                                                                 final Callback<JSITDefinitions> callback,
                                                                 final ErrorCallback<Object> errorCallback) {
        return dmn12 -> {
            final JSITDefinitions jsitDefinitions = uncheckedCast(dmn12);
            final Map<String, Path> includedDMNImportsPaths = new HashMap<>();
            if (jsitDefinitions.getImport() != null && !jsitDefinitions.getImport().isEmpty()) {
                includedDMNImportsPaths.putAll(jsitDefinitions.getImport().stream()
                    .filter(jsitImport -> jsitImport.getImportType().toUpperCase().contains("DMN"))
                    .collect(Collectors.toMap(jsitImport -> jsitImport.getName(),
                                              jsitImport -> PathFactory.newPath(jsitImport.getLocationURI(),
                                                                                dmnFilePath.toURI().replace(dmnFilePath.getFileName(),
                                                                                                            jsitImport.getLocationURI())))));
            }
            if (includedDMNImportsPaths.isEmpty()) {
                callback.callback(jsitDefinitions);
            } else {
                final List<JSITDefinitions> importedItemDefinitions = new ArrayList<>();
                for (Map.Entry<String, Path> importPath : includedDMNImportsPaths.entrySet()) {
                    resourceContentService.getFileContent(importPath.getValue(),
                                                          getDMNImportContentRemoteCallback(callback,
                                                                                            jsitDefinitions,
                                                                                            importedItemDefinitions,
                                                                                            includedDMNImportsPaths.size()),
                                                          errorCallback);
                }
            }
        };
    }

    protected RemoteCallback<String> getDMNImportContentRemoteCallback(final Callback<JSITDefinitions> callback,
                                                                       final JSITDefinitions definitions,
                                                                       final List<JSITDefinitions> importedDefinitions,
                                                                       final int importsNumber) {
        return dmnContent -> {
            DMN12UnmarshallCallback dmn12UnmarshallCallback = getDMN12ImportsUnmarshallCallback(callback,
                                                                                                definitions,
                                                                                                importedDefinitions,
                                                                                                importsNumber);
            unmarshallDMN(dmnContent, dmn12UnmarshallCallback);
        };
    }
    protected DMN12UnmarshallCallback getDMN12ImportsUnmarshallCallback(final Callback<JSITDefinitions> callback,
                                                                        final JSITDefinitions definitions,
                                                                        final List<JSITDefinitions> importedDefinitions,
                                                                        final int importsNumber) {
        return dmn12 -> {
            final JSITDefinitions jsitDefinitions = uncheckedCast(dmn12);
            importedDefinitions.add(jsitDefinitions);

            if (importsNumber == importedDefinitions.size()) {

                for (int i = 0; i < importedDefinitions.size(); i++) {

                    final JSITDefinitions jsitDefinitions1 = Js.uncheckedCast(importedDefinitions.get(i));
                    List<JSITItemDefinition> itemDefinitionsRaw = jsitDefinitions1.getItemDefinition();

                    for (int j = 0; j< itemDefinitionsRaw.size(); j++) {
                        JSITItemDefinition value = Js.uncheckedCast(itemDefinitionsRaw.get(j));
                        definitions.addItemDefinition(value);
                    }
                }

                callback.callback(definitions);
            }
        };
    }

    // Indirection for tests
    protected void unmarshallDMN(String dmnContent, DMN12UnmarshallCallback dmn12UnmarshallCallback) {
        MainJs.unmarshall(dmnContent, "", dmn12UnmarshallCallback);
    }

    // Indirection for tests
    protected JSITDefinitions uncheckedCast(DMN12 dmn12) {
        return Js.uncheckedCast(JsUtils.getUnwrappedElement(dmn12));
    }
}
