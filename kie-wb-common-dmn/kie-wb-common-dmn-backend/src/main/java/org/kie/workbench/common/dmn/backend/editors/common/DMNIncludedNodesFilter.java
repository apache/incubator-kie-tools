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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedModel;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedNode;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.uberfire.backend.vfs.Path;

public class DMNIncludedNodesFilter {

    private final DMNDiagramHelper diagramHelper;

    private final DMNIncludedNodeFactory factory;

    @Inject
    public DMNIncludedNodesFilter(final DMNDiagramHelper diagramHelper,
                                  final DMNIncludedNodeFactory factory) {
        this.diagramHelper = diagramHelper;
        this.factory = factory;
    }

    public List<DMNIncludedNode> getNodesFromImports(final Path path,
                                                     final List<DMNIncludedModel> includedModels) {

        try {

            final Diagram<Graph, Metadata> diagram = diagramHelper.getDiagramByPath(path);
            final Optional<DMNIncludedModel> diagramImport = getDiagramImport(diagram, includedModels);
            final boolean isDiagramImported = diagramImport.isPresent();

            if (isDiagramImported) {
                return diagramHelper
                        .getNodes(diagram)
                        .stream()
                        .map(node -> factory.makeDMNIncludeModel(path, diagramImport.get(), node))
                        .collect(Collectors.toList());
            }
        } catch (final Exception e) {
            // Ignore when 'path' cannot be reached.
        }

        return new ArrayList<>();
    }

    private Optional<DMNIncludedModel> getDiagramImport(final Diagram<Graph, Metadata> diagram,
                                                        final List<DMNIncludedModel> includedModels) {
        final String pathNamespace = diagramHelper.getNamespace(diagram);
        return includedModels
                .stream()
                .filter(i -> Objects.equals(pathNamespace, i.getNamespace()))
                .findAny();
    }
}
