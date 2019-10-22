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

package org.kie.workbench.common.dmn.webapp.kogito.common.backend.workarounds;

import java.util.Collections;
import java.util.List;

import javax.enterprise.inject.Specializes;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedModel;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedNode;
import org.kie.workbench.common.dmn.api.editors.included.IncludedModel;
import org.kie.workbench.common.dmn.api.editors.included.PMMLDocumentMetadata;
import org.kie.workbench.common.dmn.api.editors.included.PMMLIncludedModel;
import org.kie.workbench.common.dmn.backend.editors.included.DMNIncludedModelsServiceImpl;
import org.uberfire.backend.vfs.Path;

/**
 * kogito lacks the ability to query the environment (VSCode etc) for additional files.
 * Therefore this implementation of the service returns empty collections for all methods.
 */
@Service
@Specializes
public class MockDMNIncludedModelsServiceImpl extends DMNIncludedModelsServiceImpl {

    public MockDMNIncludedModelsServiceImpl() {
        super(null, null, null, null, null);
    }

    @Override
    public List<IncludedModel> loadModels(final Path path,
                                          final WorkspaceProject workspaceProject) {
        return Collections.emptyList();
    }

    @Override
    public List<DMNIncludedNode> loadNodesFromImports(final WorkspaceProject workspaceProject,
                                                      final List<DMNIncludedModel> includedModels) {
        return Collections.emptyList();
    }

    @Override
    public List<PMMLDocumentMetadata> loadPMMLDocumentsFromImports(final Path path,
                                                                   final WorkspaceProject workspaceProject,
                                                                   final List<PMMLIncludedModel> includedModels) {
        return Collections.emptyList();
    }

    @Override
    public List<ItemDefinition> loadItemDefinitionsByNamespace(final WorkspaceProject workspaceProject,
                                                               final String modelName,
                                                               final String namespace) {
        return Collections.emptyList();
    }
}
