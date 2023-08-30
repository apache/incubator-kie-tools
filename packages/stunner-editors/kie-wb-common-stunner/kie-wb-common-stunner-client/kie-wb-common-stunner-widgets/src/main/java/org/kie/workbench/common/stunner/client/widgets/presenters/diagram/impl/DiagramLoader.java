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


package org.kie.workbench.common.stunner.client.widgets.presenters.diagram.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.preferences.StunnerPreferencesRegistryLoader;
import org.kie.workbench.common.stunner.core.client.service.ClientDiagramService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.preferences.StunnerPreferences;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mvp.ParameterizedCommand;

@ApplicationScoped
public class DiagramLoader {

    private final ClientDiagramService clientDiagramServices;
    private final StunnerPreferencesRegistryLoader preferencesRegistryLoader;

    @Inject
    public DiagramLoader(final ClientDiagramService clientDiagramServices,
                         final StunnerPreferencesRegistryLoader preferencesRegistryLoader) {
        this.clientDiagramServices = clientDiagramServices;
        this.preferencesRegistryLoader = preferencesRegistryLoader;
    }

    @SuppressWarnings("unchecked")
    public void loadByPath(final Path path,
                           final ServiceCallback<Diagram> callback) {
        clientDiagramServices.getByPath(path,
                                        new ServiceCallback<Diagram<Graph, Metadata>>() {
                                            @Override
                                            public void onSuccess(final Diagram<Graph, Metadata> diagram) {
                                                loadPreferences(diagram,
                                                                prefs -> callback.onSuccess(diagram),
                                                                error -> callback.onError(new ClientRuntimeError(error)));
                                            }

                                            @Override
                                            public void onError(final ClientRuntimeError error) {
                                                callback.onError(error);
                                            }
                                        });
    }

    private void loadPreferences(final Diagram<Graph, Metadata> diagram,
                                 final ParameterizedCommand<StunnerPreferences> callback,
                                 final ParameterizedCommand<Throwable> errorCallback) {
        final Metadata metadata = diagram.getMetadata();
        preferencesRegistryLoader.load(metadata,
                                       callback,
                                       errorCallback);
    }
}
