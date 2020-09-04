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
package org.kie.workbench.common.dmn.webapp.kogito.common.client.services;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.promise.Promise;
import org.appformer.kogito.bridge.client.pmmleditor.marshaller.PMMLEditorMarshallerApi;
import org.appformer.kogito.bridge.client.pmmleditor.marshaller.model.PMMLDocumentData;
import org.kie.workbench.common.dmn.api.editors.included.PMMLDocumentMetadata;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.converters.PMMLMarshallerConverter;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.uberfire.client.promise.Promises;

/**
 * Scope of this bean is to provide an entry point to the PMML client marshaller available thought enveloper
 */
@Dependent
public class PMMLMarshallerService {

    private Promises promises;
    private PMMLEditorMarshallerApi pmmlEditorMarshallerApi;

    public PMMLMarshallerService() {
        // CDI
    }

    @Inject
    public PMMLMarshallerService(final Promises promises,
                                 final PMMLEditorMarshallerApi pmmlEditorMarshallerApi) {
        this.promises = promises;
        this.pmmlEditorMarshallerApi = pmmlEditorMarshallerApi;
    }

    public Promise<PMMLDocumentMetadata> getDocumentMetadata(final String pmmlFilePath,
                                                             final String pmmlFileContent) {
        if (StringUtils.isEmpty(pmmlFilePath)) {
            return promises.reject("PMML file required to be marshalled is empty or null");
        }
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
    }
}
