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
package org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.kie.workbench.common.dmn.api.editors.included.PMMLDocumentMetadata;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDRGElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDefinitions;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITImport;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITItemDefinition;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.uberfire.backend.vfs.Path;

@ApplicationScoped
public class DMNMarshallerImportsHelperKogitoImpl implements DMNMarshallerImportsHelperKogito {

    @Override
    public Map<JSITImport, JSITDefinitions> getImportDefinitions(final Metadata metadata,
                                                                 final List<JSITImport> imports) {
        return Collections.emptyMap();
    }

    @Override
    public Map<JSITImport, PMMLDocumentMetadata> getPMMLDocuments(final Metadata metadata,
                                                                  final List<JSITImport> imports) {
        return Collections.emptyMap();
    }

    @Override
    public Map<JSITImport, String> getImportXML(final Metadata metadata,
                                                final List<JSITImport> imports) {
        return Collections.emptyMap();
    }

    @Override
    public List<JSITDRGElement> getImportedDRGElements(final Map<JSITImport, JSITDefinitions> importDefinitions) {
        return Collections.emptyList();
    }

    @Override
    public List<JSITItemDefinition> getImportedItemDefinitions(final Map<JSITImport, JSITDefinitions> importDefinitions) {
        return Collections.emptyList();
    }

    @Override
    public List<JSITItemDefinition> getImportedItemDefinitionsByNamespace(final WorkspaceProject workspaceProject,
                                                                          final String modelName,
                                                                          final String namespace) {
        return Collections.emptyList();
    }

    @Override
    public Path getDMNModelPath(final Metadata metadata,
                                final String modelNamespace,
                                final String modelName) {
        throw new UnsupportedOperationException("Imports are not supported in the kogito-based editors.");
    }

    @Override
    public Optional<InputStream> loadPath(final Path path) {
        return Optional.empty();
    }
}
