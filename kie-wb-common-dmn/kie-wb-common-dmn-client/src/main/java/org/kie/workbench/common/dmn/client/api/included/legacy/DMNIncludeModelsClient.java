/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.api.included.legacy;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedModel;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedNode;
import org.kie.workbench.common.dmn.api.editors.included.IncludedModel;
import org.kie.workbench.common.dmn.client.service.DMNClientServicesProxy;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.uberfire.backend.vfs.Path;

@Dependent
public class DMNIncludeModelsClient {

    private final DMNClientServicesProxy clientServicesProxy;

    @Inject
    public DMNIncludeModelsClient(final DMNClientServicesProxy clientServicesProxy) {
        this.clientServicesProxy = clientServicesProxy;
    }

    public void loadModels(final Path path,
                           final Consumer<List<IncludedModel>> listConsumer) {
        clientServicesProxy.loadModels(path,
                                       callback(listConsumer));
    }

    public void loadNodesFromImports(final List<DMNIncludedModel> includeModels,
                                     final Consumer<List<DMNIncludedNode>> consumer) {
        clientServicesProxy.loadNodesFromImports(includeModels,
                                                 callback(consumer));
    }

    public void loadItemDefinitionsByNamespace(final String modelName,
                                               final String namespace,
                                               final Consumer<List<ItemDefinition>> consumer) {
        clientServicesProxy.loadItemDefinitionsByNamespace(modelName,
                                                           namespace,
                                                           callback(consumer));
    }

    <T> ServiceCallback<List<T>> callback(final Consumer<List<T>> consumer) {
        return new ServiceCallback<List<T>>() {
            @Override
            public void onSuccess(final List<T> item) {
                consumer.accept(item);
            }

            @Override
            public void onError(final ClientRuntimeError error) {
                clientServicesProxy.logWarning(error);
                consumer.accept(new ArrayList<>());
            }
        };
    }
}
