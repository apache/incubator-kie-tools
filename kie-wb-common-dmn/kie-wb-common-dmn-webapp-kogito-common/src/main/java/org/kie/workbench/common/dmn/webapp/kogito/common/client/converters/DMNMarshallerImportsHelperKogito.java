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
package org.kie.workbench.common.dmn.webapp.kogito.common.client.converters;

import java.util.List;
import java.util.Map;

import elemental2.promise.Promise;
import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedModel;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedNode;
import org.kie.workbench.common.dmn.api.editors.included.IncludedModel;
import org.kie.workbench.common.dmn.api.marshalling.DMNMarshallerImportsHelper;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDRGElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDefinitions;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITImport;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITItemDefinition;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.diagram.Metadata;

public interface DMNMarshallerImportsHelperKogito extends DMNMarshallerImportsHelper<JSITImport, JSITDefinitions, JSITDRGElement, JSITItemDefinition> {

    Promise<Map<JSITImport, JSITDefinitions>> getImportDefinitionsAsync(final Metadata metadata,
                                                                        final List<JSITImport> imports);

    void getImportedItemDefinitionsByNamespaceAsync(final String modelName,
                                                    final String namespace,
                                                    final ServiceCallback<List<ItemDefinition>> callback);

    void loadNodesFromModels(final List<DMNIncludedModel> includedModels,
                             final ServiceCallback<List<DMNIncludedNode>> callback);

    void loadModels(final ServiceCallback<List<IncludedModel>> callback);
}
