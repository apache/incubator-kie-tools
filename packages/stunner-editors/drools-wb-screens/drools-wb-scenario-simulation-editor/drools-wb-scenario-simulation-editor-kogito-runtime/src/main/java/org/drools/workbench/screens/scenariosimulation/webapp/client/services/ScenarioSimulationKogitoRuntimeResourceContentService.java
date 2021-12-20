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
package org.drools.workbench.screens.scenariosimulation.webapp.client.services;

import javax.inject.Inject;

import org.drools.workbench.screens.scenariosimulation.kogito.client.services.ScenarioSimulationKogitoResourceContentService;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.kogito.webapp.base.client.workarounds.KogitoResourceContentService;
import org.uberfire.backend.vfs.Path;

public class ScenarioSimulationKogitoRuntimeResourceContentService implements ScenarioSimulationKogitoResourceContentService {

    @Inject
    private KogitoResourceContentService resourceContentService;

    @Override
    public void getFileContent(final Path path,
                               final RemoteCallback<String> remoteCallback,
                               final ErrorCallback<Object> errorCallback) {
        resourceContentService.loadFile(path.toURI(), remoteCallback, errorCallback);
    }

}
