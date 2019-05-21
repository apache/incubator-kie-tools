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
package org.kie.workbench.common.dmn.client.service;

import java.util.List;

import elemental2.dom.DomGlobal;
import org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedModel;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedNode;
import org.kie.workbench.common.dmn.api.editors.types.DMNSimpleTimeZone;
import org.kie.workbench.common.dmn.api.editors.types.RangeValue;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;

/**
 * This is a proxy for the single provider of (external) services required by the Editor.
 * An external service is one that may require an RPC in some environments (e.g. Business Central) however may be
 * substituted for client-side implementations in other environments (e.g. Submarine).
 */
public interface DMNClientServicesProxy {

    // ------------------------------------
    // Default helper methods
    // ------------------------------------

    default void logWarning(final ClientRuntimeError error) {
        warn(error.getMessage());
    }

    default void warn(final String message) {
        DomGlobal.console.warn(message);
    }

    // ------------------------------------
    // Proxy for DMNIncludedModelsService
    // ------------------------------------

    /**
     * This method loads all DMN models from a given project.
     * @return all {@link DMNIncludedModel}s from a given project.
     */
    void loadModels(final ServiceCallback<List<DMNIncludedModel>> callback);

    /**
     * This method loads all nodes from an included model.
     * @param includedModels represents all imports that provide the list of nodes.
     * @return a list of {@link DMNIncludedNode}s.
     */
    void loadNodesFromImports(final List<DMNIncludedModel> includedModels,
                              final ServiceCallback<List<DMNIncludedNode>> callback);

    /**
     * This method finds the list of {@link ItemDefinition}s for a given <code>namespace</code>.
     * @param modelName is the value used as the prefix for imported {@link ItemDefinition}s.
     * @param namespace is the namespace of the model that provides the list of {@link ItemDefinition}s.
     * @return a list of {@link ItemDefinition}s.
     */
    void loadItemDefinitionsByNamespace(final String modelName,
                                        final String namespace,
                                        final ServiceCallback<List<ItemDefinition>> callback);

    // ------------------------------------
    // Proxy for DMNParseService
    // ------------------------------------

    void parseFEELList(final String source,
                       final ServiceCallback<List<String>> callback);

    void parseRangeValue(final String source,
                         final ServiceCallback<RangeValue> callback);

    // ------------------------------------
    // Proxy for DMNValidationService
    // ------------------------------------

    void isValidVariableName(final String source,
                             final ServiceCallback<Boolean> callback);

    // ------------------------------------
    // Proxy for TimeZoneService
    // ------------------------------------

    void getTimeZones(final ServiceCallback<List<DMNSimpleTimeZone>> callback);
}
