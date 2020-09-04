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
package org.kie.workbench.common.dmn.webapp.kogito.common.client.converters;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.appformer.kogito.bridge.client.pmmleditor.marshaller.model.PMMLDocumentData;
import org.kie.workbench.common.dmn.api.editors.included.DMNImportTypes;
import org.kie.workbench.common.dmn.api.editors.included.PMMLDocumentMetadata;
import org.kie.workbench.common.dmn.api.editors.included.PMMLModelMetadata;
import org.kie.workbench.common.dmn.api.editors.included.PMMLParameterMetadata;

public class PMMLMarshallerConverter {

    private PMMLMarshallerConverter() {
        // Utils class with static methods.
    }

    public static PMMLDocumentMetadata fromJSInteropToMetadata(final String pmmlFilePath,
                                                               final PMMLDocumentData pmmlDocumentData) {
        final List<PMMLModelMetadata> models = new ArrayList<>();
        pmmlDocumentData.getModels().stream().forEach(pmmlModelData -> {
            final String modelName = pmmlModelData.getModelName();
            final Set<PMMLParameterMetadata> fields = new HashSet<>();

            for (final String field : pmmlModelData.getFields()) {
                fields.add(new PMMLParameterMetadata(field));
            }

            models.add(new PMMLModelMetadata(modelName, fields));
        });
        return new PMMLDocumentMetadata(pmmlFilePath,
                                        DMNImportTypes.PMML.getDefaultNamespace(),
                                        models);
    }

}
