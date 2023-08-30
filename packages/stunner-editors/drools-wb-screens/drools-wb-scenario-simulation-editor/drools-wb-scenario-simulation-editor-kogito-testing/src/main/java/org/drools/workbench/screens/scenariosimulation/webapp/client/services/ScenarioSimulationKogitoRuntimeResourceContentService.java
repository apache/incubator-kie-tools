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

package org.drools.workbench.screens.scenariosimulation.webapp.client.services;

import javax.inject.Inject;

import org.drools.workbench.screens.scenariosimulation.kogito.client.services.ScenarioSimulationKogitoResourceContentService;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.backend.vfs.Path;

public class ScenarioSimulationKogitoRuntimeResourceContentService implements ScenarioSimulationKogitoResourceContentService {

    @Inject
    private VFSServiceFake vfsServiceFake;

    @Override
    public void getFileContent(final Path path,
                               final RemoteCallback<String> remoteCallback,
                               final ErrorCallback<Object> errorCallback) {
        String result = vfsServiceFake.readAllString(path);
        if (result == null || result.isEmpty()) {
            String error = "Unable to open file: " + path;
            errorCallback.error(error, new Exception(error));
        } else {
            remoteCallback.callback(result);
        }
    }
}
