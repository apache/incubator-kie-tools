/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.graph;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.v1_1.DMNDiagram;
import org.kie.workbench.common.dmn.api.definition.v1_1.Definitions;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.AbstractTreeTraverseCallback;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessor;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessorImpl;

@Dependent
public class DMNGraphUtils {

    private SessionManager sessionManager;

    public DMNGraphUtils() {
        //CDI proxy
    }

    @Inject
    public DMNGraphUtils(final SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @SuppressWarnings("unchecked")
    public Definitions getDefinitions() {
        final List<Definitions> definitions = new ArrayList<>();
        final ClientSession session = sessionManager.getCurrentSession();
        if (session == null) {
            return null;
        }
        final CanvasHandler canvasHandler = session.getCanvasHandler();
        if (canvasHandler == null) {
            return null;
        }
        final Graph<?, Node> graph = canvasHandler.getDiagram().getGraph();

        final TreeWalkTraverseProcessor walker = new TreeWalkTraverseProcessorImpl();
        walker.traverse(graph,
                        new AbstractTreeTraverseCallback<Graph, Node, Edge>() {

                            @Override
                            public boolean startNodeTraversal(final Node node) {
                                final Object c = node.getContent();
                                if (c instanceof View) {
                                    final View v = (View) c;
                                    final Object d = v.getDefinition();
                                    if (d instanceof DMNDiagram) {
                                        final DMNDiagram diagram = (DMNDiagram) d;
                                        definitions.add(diagram.getDefinitions());
                                        return false;
                                    }
                                }
                                return true;
                            }
                        });

        return definitions.isEmpty() ? null : definitions.get(0);
    }
}
