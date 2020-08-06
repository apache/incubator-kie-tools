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

import java.util.Collections;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.promise.Promise;
import org.kie.workbench.common.dmn.api.editors.included.DMNImportTypes;
import org.kie.workbench.common.dmn.api.editors.included.PMMLDocumentMetadata;
import org.kie.workbench.common.stunner.core.util.FileUtils;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.uberfire.client.promise.Promises;

/**
 * Scope of this bean is to provide an entry point to the PMML client marshaller available thought enveloper
 */
@Dependent
public class PMMLMarshallerService {

    private Promises promises;

    public PMMLMarshallerService() {
        // CDI
    }

    @Inject
    public PMMLMarshallerService(final Promises promises) {
        this.promises = promises;
    }

    public Promise<PMMLDocumentMetadata> getDocumentMetadata(final String pmmlFile, final String pmmlFileContent) {
        if (StringUtils.isEmpty(pmmlFile)) {
            return promises.reject("PMML fileName required to be marshalled is empty or null");
        }
        if (StringUtils.isEmpty(pmmlFileContent)) {
            return promises.reject("PMML file " + pmmlFile + " content required to be marshalled is empty or null");
        }

        /* Here, a JSInterop call through enveloper should be used passing pmmlFileContent */
        String pmmlFileName = FileUtils.getFileName(pmmlFile);
        PMMLDocumentMetadata documentMetadata = new PMMLDocumentMetadata(pmmlFile,
                                                                         pmmlFileName,
                                                                         DMNImportTypes.PMML.getDefaultNamespace(),
                                                                         Collections.emptyList());
        return promises.resolve(documentMetadata);
    }
}
