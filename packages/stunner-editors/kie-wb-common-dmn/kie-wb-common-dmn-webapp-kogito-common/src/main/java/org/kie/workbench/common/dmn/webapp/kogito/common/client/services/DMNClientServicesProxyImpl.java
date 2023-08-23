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

package org.kie.workbench.common.dmn.webapp.kogito.common.client.services;

import java.util.Collections;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedModel;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedNode;
import org.kie.workbench.common.dmn.api.editors.included.IncludedModel;
import org.kie.workbench.common.dmn.api.editors.included.PMMLDocumentMetadata;
import org.kie.workbench.common.dmn.api.editors.included.PMMLIncludedModel;
import org.kie.workbench.common.dmn.api.editors.types.DMNSimpleTimeZone;
import org.kie.workbench.common.dmn.api.editors.types.DataObject;
import org.kie.workbench.common.dmn.api.editors.types.RangeValue;
import org.kie.workbench.common.dmn.client.marshaller.included.DMNMarshallerImportsClientHelper;
import org.kie.workbench.common.dmn.client.service.DMNClientServicesProxy;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.uberfire.backend.vfs.Path;

@Dependent
public class DMNClientServicesProxyImpl implements DMNClientServicesProxy {

    private final DMNMarshallerImportsClientHelper kogitoImportsHelper;
    private final TimeZonesProvider timeZonesProvider;

    @Inject
    public DMNClientServicesProxyImpl(final TimeZonesProvider timeZonesProvider,
                                      final DMNMarshallerImportsClientHelper kogitoImportsHelper) {
        this.timeZonesProvider = timeZonesProvider;
        this.kogitoImportsHelper = kogitoImportsHelper;
    }

    @Override
    public void loadModels(final Path path,
                           final ServiceCallback<List<IncludedModel>> callback) {
        kogitoImportsHelper.loadModels(callback);
    }

    @Override
    public void loadNodesFromImports(final List<DMNIncludedModel> includedModels,
                                     final ServiceCallback<List<DMNIncludedNode>> callback) {
        kogitoImportsHelper.loadNodesFromModels(includedModels, callback);
    }

    @Override
    public void loadPMMLDocumentsFromImports(final Path path,
                                             final List<PMMLIncludedModel> includedModels,
                                             final ServiceCallback<List<PMMLDocumentMetadata>> callback) {
        kogitoImportsHelper.getPMMLDocumentsMetadataFromFiles(includedModels, callback);
    }

    @Override
    public void loadItemDefinitionsByNamespace(final String modelName,
                                               final String namespace,
                                               final ServiceCallback<List<ItemDefinition>> callback) {
        kogitoImportsHelper.getImportedItemDefinitionsByNamespaceAsync(
                modelName,
                namespace,
                callback);
    }

    @Override
    public void parseFEELList(final String source,
                              final ServiceCallback<List<String>> callback) {
        callback.onSuccess(FEELListParser.parse(source));
    }

    @Override
    public void parseRangeValue(final String source,
                                final ServiceCallback<RangeValue> callback) {
        callback.onSuccess(FEELRangeParser.parse(source));
    }

    @Override
    public void isValidVariableName(final String source,
                                    final ServiceCallback<Boolean> callback) {
        callback.onSuccess(FEELSyntaxLightValidator.isVariableNameValid(source));
    }

    @Override
    public void getTimeZones(final ServiceCallback<List<DMNSimpleTimeZone>> callback) {
        callback.onSuccess(timeZonesProvider.getTimeZones());
    }

    @Override
    public void loadDataObjects(final ServiceCallback<List<DataObject>> callback) {
        callback.onSuccess(Collections.emptyList());
    }
}
