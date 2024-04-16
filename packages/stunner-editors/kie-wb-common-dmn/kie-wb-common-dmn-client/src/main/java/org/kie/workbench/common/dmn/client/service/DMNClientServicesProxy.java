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
package org.kie.workbench.common.dmn.client.service;

import java.util.List;

import elemental2.dom.DomGlobal;
import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedModel;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedNode;
import org.kie.workbench.common.dmn.api.editors.included.IncludedModel;
import org.kie.workbench.common.dmn.api.editors.included.PMMLDocumentMetadata;
import org.kie.workbench.common.dmn.api.editors.included.PMMLIncludedModel;
import org.kie.workbench.common.dmn.api.editors.types.DMNSimpleTimeZone;
import org.kie.workbench.common.dmn.api.editors.types.DataObject;
import org.kie.workbench.common.dmn.api.editors.types.RangeValue;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.uberfire.backend.vfs.Path;

/**
 * This is a proxy for the single provider of (external) services required by the Editor.
 * An external service is one that may require an RPC in some environments (e.g. Business Central) however may be
 * substituted for client-side implementations in other environments (e.g. Kogito).
 */
public interface DMNClientServicesProxy {

    // ------------------------------------
    // Default helper methods
    // ------------------------------------

    default void logWarning(final ClientRuntimeError error) {
        warn(error.getErrorMessage());
    }

    default void warn(final String message) {
        DomGlobal.console.warn(message);
    }

    // ------------------------------------
    // Proxy for DMNIncludedModelsService
    // ------------------------------------

    /**
     * This method loads all models (DMN and PMML) from a given project.
     * @param path Path of the DMN file being edited.
     * @param callback Invoked with all {@link IncludedModel}s from a given project.
     */
    void loadModels(final Path path,
                    final ServiceCallback<List<IncludedModel>> callback);

    /**
     * This method loads all nodes for the included DMN models.
     * @param includedModels represents all DMN imports that provide the list of nodes.
     * @param callback Invoked with a list of {@link DMNIncludedNode}s.
     */
    void loadNodesFromImports(final List<DMNIncludedModel> includedModels,
                              final ServiceCallback<List<DMNIncludedNode>> callback);

    /**
     * Returns metadata defining the PMMLDocuments for the included PMML models.
     * @param path Path of the DMN file being edited.
     * @param includedModels represents all PMML imports that provide the list of documents.
     * @param callback Invoked with a list of {@link PMMLDocumentMetadata}s.
     */
    void loadPMMLDocumentsFromImports(final Path path,
                                      final List<PMMLIncludedModel> includedModels,
                                      final ServiceCallback<List<PMMLDocumentMetadata>> callback);

    /**
     * This method finds the list of {@link ItemDefinition}s for a given <code>namespace</code>.
     * @param modelName is the value used as the prefix for imported {@link ItemDefinition}s.
     * @param namespace is the namespace of the model that provides the list of {@link ItemDefinition}s.
     * @param callback Invoked with a list of {@link ItemDefinition}s.
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

    /**
     * This method loads all Data Objects from .java classes from the current project.
     * @param callback Invoked with all {@link DataObject}s from the current project.
     */
    void loadDataObjects(final ServiceCallback<List<DataObject>> callback);
}
