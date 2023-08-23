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

package org.kie.workbench.common.dmn.client.api.included.legacy;

import java.util.List;
import java.util.function.Consumer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedModel;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedNode;
import org.kie.workbench.common.dmn.api.editors.included.IncludedModel;
import org.kie.workbench.common.dmn.client.api.DMNServiceClient;
import org.kie.workbench.common.dmn.client.service.DMNClientServicesProxy;
import org.uberfire.backend.vfs.Path;

@Dependent
public class DMNIncludeModelsClient extends DMNServiceClient {

    @Inject
    public DMNIncludeModelsClient(final DMNClientServicesProxy clientServicesProxy) {
        super(clientServicesProxy);
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
}
