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

package org.kie.workbench.common.stunner.core.client.api;

import java.util.Collections;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;

import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.GraphsProvider;
import org.kie.workbench.common.stunner.core.graph.Graph;

@Default
@ApplicationScoped
public class DefaultGraphsProvider implements GraphsProvider {

    protected DefaultGraphsProvider() {
        // CDI proxy
    }

    @Override
    public boolean isGlobalGraphSelected() {
        return false;
    }

    @Override
    public List<Graph> getGraphs() {
        return Collections.emptyList();
    }

    @Override
    public List<Graph> getNonGlobalGraphs() {
        return Collections.emptyList();
    }

    @Override
    public Diagram getDiagram(final String diagramId) {
        return null;
    }

    @Override
    public String getCurrentDiagramId() {
        return null;
    }
}
