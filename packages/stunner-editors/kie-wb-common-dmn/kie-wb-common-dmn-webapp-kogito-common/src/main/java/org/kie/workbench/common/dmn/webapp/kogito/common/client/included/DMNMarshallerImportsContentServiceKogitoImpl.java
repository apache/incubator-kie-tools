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

package org.kie.workbench.common.dmn.webapp.kogito.common.client.included;

import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import elemental2.promise.Promise;
import org.appformer.kogito.bridge.client.pmmleditor.marshaller.PMMLEditorMarshallerApi;
import org.appformer.kogito.bridge.client.pmmleditor.marshaller.model.PMMLDocumentData;
import org.appformer.kogito.bridge.client.resource.interop.ResourceListOptions;
import org.kie.workbench.common.dmn.api.editors.included.PMMLDocumentMetadata;
import org.kie.workbench.common.dmn.client.marshaller.included.DMNMarshallerImportsContentService;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.converters.PMMLMarshallerConverter;
import org.kie.workbench.common.kogito.webapp.base.client.workarounds.KogitoResourceContentService;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.uberfire.client.promise.Promises;

@Alternative
public class DMNMarshallerImportsContentServiceKogitoImpl implements DMNMarshallerImportsContentService {

    private final KogitoResourceContentService contentService;

    private final Promises promises;

    private final PMMLEditorMarshallerApi pmmlEditorMarshallerApi;

    static final String DMN_FILES_PATTERN = "**/*.dmn";

    static final String PMML_FILES_PATTERN = "**/*.pmml";

    static final String MODEL_FILES_PATTERN = "**/*.{dmn,pmml}";

    @Inject
    public DMNMarshallerImportsContentServiceKogitoImpl(final KogitoResourceContentService contentService,
                                                        final Promises promises,
                                                        final PMMLEditorMarshallerApi pmmlEditorMarshallerApi) {
        this.contentService = contentService;
        this.promises = promises;
        this.pmmlEditorMarshallerApi = pmmlEditorMarshallerApi;
    }

    @Override
    public Promise<String> loadFile(final String file) {
        return contentService.loadFile(file);
    }

    @Override
    public Promise<String[]> getModelsURIs() {
        return contentService.getFilteredItems(MODEL_FILES_PATTERN, ResourceListOptions.traversal());
    }

    @Override
    public Promise<String[]> getModelsDMNFilesURIs() {
        return contentService.getFilteredItems(DMN_FILES_PATTERN, ResourceListOptions.traversal());
    }

    @Override
    public Promise<String[]> getModelsPMMLFilesURIs() {
        return contentService.getFilteredItems(PMML_FILES_PATTERN, ResourceListOptions.traversal());
    }

    @Override
    public Promise<PMMLDocumentMetadata> getPMMLDocumentMetadata(final String pmmlFilePath) {

        if (StringUtils.isEmpty(pmmlFilePath)) {
            return promises.reject("PMML file path cannot be empty or null");
        }

        return loadFile(pmmlFilePath)
                .then(pmmlFileContent -> {

                    if (StringUtils.isEmpty(pmmlFileContent)) {
                        return promises.reject("PMML file " + pmmlFilePath + " content required to be marshalled is empty or null");
                    }

                    try {
                        final PMMLDocumentData pmmlDocumentData = pmmlEditorMarshallerApi.getPMMLDocumentData(pmmlFileContent);
                        final PMMLDocumentMetadata pmmlDocumentMetadata = PMMLMarshallerConverter.fromJSInteropToMetadata(pmmlFilePath, pmmlDocumentData);

                        return promises.resolve(pmmlDocumentMetadata);
                    } catch (final Exception e) {
                        return promises.reject("Error during marshalling of PMML file " + pmmlFilePath + ": " + e.getMessage());
                    }
                });
    }
}
