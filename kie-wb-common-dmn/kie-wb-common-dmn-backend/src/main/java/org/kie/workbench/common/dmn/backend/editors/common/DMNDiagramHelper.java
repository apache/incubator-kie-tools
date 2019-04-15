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

import java.util.List;

import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.v1_1.DRGElement;
import org.kie.workbench.common.dmn.api.graph.DMNDiagramUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.service.DiagramService;
import org.uberfire.backend.vfs.Path;

public class DMNDiagramHelper {

    private final DiagramService diagramService;

    private final DMNDiagramUtils dmnDiagramUtils;

    @Inject
    public DMNDiagramHelper(final DiagramService diagramService,
                            final DMNDiagramUtils dmnDiagramUtils) {
        this.diagramService = diagramService;
        this.dmnDiagramUtils = dmnDiagramUtils;
    }

    public List<DRGElement> getNodes(final Diagram diagram) {
        return dmnDiagramUtils.getNodes(diagram);
    }

    public String getNamespace(final Path path) {
        final Diagram<Graph, Metadata> diagram = getDiagramByPath(path);
        return dmnDiagramUtils.getNamespace(diagram);
    }

    public String getNamespace(final Diagram diagram) {
        return dmnDiagramUtils.getNamespace(diagram);
    }

    public Diagram<Graph, Metadata> getDiagramByPath(final Path path) {
        return diagramService.getDiagramByPath(path);
    }
}
