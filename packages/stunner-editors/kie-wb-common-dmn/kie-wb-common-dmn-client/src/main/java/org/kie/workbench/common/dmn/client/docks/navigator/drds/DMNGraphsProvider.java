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

package org.kie.workbench.common.dmn.client.docks.navigator.drds;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.GraphsProvider;
import org.kie.workbench.common.stunner.core.graph.Graph;

@Alternative
@ApplicationScoped
@DMNEditor
public class DMNGraphsProvider implements GraphsProvider {

    private final DMNDiagramsSession diagramsSession;

    protected DMNGraphsProvider() {
        // CDI proxy
        this(null);
    }

    @Inject
    public DMNGraphsProvider(final DMNDiagramsSession diagramsSession) {
        this.diagramsSession = diagramsSession;
    }

    @Override
    public boolean isGlobalGraphSelected() {
        return diagramsSession.isGlobalGraphSelected();
    }

    @Override
    public List<Graph> getGraphs() {
        return diagramsSession.getGraphs();
    }

    @Override
    public List<Graph> getNonGlobalGraphs() {
        return diagramsSession.getNonGlobalGraphs();
    }

    @Override
    public Diagram getDiagram(final String diagramId) {
        return diagramsSession.getDiagram(diagramId);
    }

    @Override
    public String getCurrentDiagramId() {
        return diagramsSession.getCurrentDiagramId();
    }
}
