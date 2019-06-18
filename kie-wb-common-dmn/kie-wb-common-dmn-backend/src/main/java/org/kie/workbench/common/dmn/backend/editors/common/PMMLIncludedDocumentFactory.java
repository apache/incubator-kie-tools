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

package org.kie.workbench.common.dmn.backend.editors.common;

import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.kie.dmn.core.pmml.PMMLInfo;
import org.kie.dmn.core.pmml.PMMLModelInfo;
import org.kie.workbench.common.dmn.api.editors.included.DMNImportTypes;
import org.kie.workbench.common.dmn.api.editors.included.PMMLDocumentMetadata;
import org.kie.workbench.common.dmn.api.editors.included.PMMLIncludedModel;
import org.kie.workbench.common.dmn.api.editors.included.PMMLModelMetadata;
import org.kie.workbench.common.dmn.api.editors.included.PMMLParameterMetadata;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

@ApplicationScoped
public class PMMLIncludedDocumentFactory {

    private IOService ioService;

    public PMMLIncludedDocumentFactory() {
        //CDI proxy
    }

    @Inject
    public PMMLIncludedDocumentFactory(final @Named("ioStrategy") IOService ioService) {
        this.ioService = ioService;
    }

    public PMMLDocumentMetadata getDocumentByPath(final Path path) {
        return Optional.ofNullable(loadPMMLInfo(path)).map(pmml -> convertPMMLInfo(path, pmml)).orElse(emptyPMMLDocumentMetadata(path));
    }

    public PMMLDocumentMetadata getDocumentByPath(final Path path,
                                                  final PMMLIncludedModel includeModel) {
        final String modelName = includeModel.getModelName();
        return Optional.ofNullable(loadPMMLInfo(path)).map(pmml -> convertPMMLInfo(path, pmml, modelName)).orElse(emptyPMMLDocumentMetadata(path, modelName));
    }

    PMMLInfo<PMMLModelInfo> loadPMMLInfo(final Path path) {
        try (InputStream io = ioService.newInputStream(Paths.convert(path))) {
            return PMMLInfo.from(io);
        } catch (Exception e) {
            return null;
        }
    }

    private PMMLDocumentMetadata emptyPMMLDocumentMetadata(final Path path) {
        return new PMMLDocumentMetadata(path.toURI(),
                                        DMNImportTypes.PMML.getDefaultNamespace(),
                                        Collections.emptyList());
    }

    private PMMLDocumentMetadata emptyPMMLDocumentMetadata(final Path path,
                                                           final String modelName) {
        return new PMMLDocumentMetadata(path.toURI(),
                                        modelName,
                                        DMNImportTypes.PMML.getDefaultNamespace(),
                                        Collections.emptyList());
    }

    private PMMLDocumentMetadata convertPMMLInfo(final Path path,
                                                 final PMMLInfo<PMMLModelInfo> pmml) {
        return new PMMLDocumentMetadata(path.toURI(),
                                        pmml.getHeader().getPmmlNSURI(),
                                        convertPMMLInfoModels(pmml.getModels()));
    }

    private PMMLDocumentMetadata convertPMMLInfo(final Path path,
                                                 final PMMLInfo<PMMLModelInfo> pmml,
                                                 final String modelName) {
        return new PMMLDocumentMetadata(path.toURI(),
                                        modelName,
                                        pmml.getHeader().getPmmlNSURI(),
                                        convertPMMLInfoModels(pmml.getModels()));
    }

    private List<PMMLModelMetadata> convertPMMLInfoModels(final Collection<PMMLModelInfo> pmml) {
        return pmml.stream().map(this::convertPMMLModelInfo).collect(Collectors.toList());
    }

    private PMMLModelMetadata convertPMMLModelInfo(final PMMLModelInfo pmml) {
        return new PMMLModelMetadata(pmml.getName(),
                                     convertInputFieldNames(pmml.getInputFieldNames()));
    }

    private Set<PMMLParameterMetadata> convertInputFieldNames(final Collection<String> pmml) {
        return pmml.stream().map(this::convertInputFieldName).collect(Collectors.toSet());
    }

    private PMMLParameterMetadata convertInputFieldName(final String name) {
        return new PMMLParameterMetadata(name);
    }
}
