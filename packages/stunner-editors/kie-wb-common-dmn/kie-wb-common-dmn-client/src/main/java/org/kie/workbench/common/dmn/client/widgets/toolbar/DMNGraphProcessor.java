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

package org.kie.workbench.common.dmn.client.widgets.toolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.kie.workbench.common.dmn.api.definition.model.DecisionService;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.processing.layout.GraphProcessor;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;

@DMNEditor
public class DMNGraphProcessor implements GraphProcessor {

    private final HashMap<String, String> replacedNodes;

    public DMNGraphProcessor() {
        replacedNodes = new HashMap<>();
    }

    @Override
    public Iterable<? extends Node> getNodes(final Graph<?, ?> graph) {

        final List<Node> nodes = new ArrayList();
        graph.nodes().forEach(nodes::add);

        for (final Node n : graph.nodes()) {
            final Definition def = (Definition) n.getContent();
            if (def.getDefinition() instanceof DecisionService) {
                final List<Node> childNodes = getChildNodes(n);
                nodes.removeAll(childNodes);
                // Remove all nodes inside DS
                // All edges that points to nodes inside DS now points to DS
                for (final Node o : childNodes) {
                    replacedNodes.put(o.getUUID(), n.getUUID());
                }
            }
        }

        return nodes;
    }

    protected List<Node> getChildNodes(final Node node) {
        return GraphUtils.getChildNodes(node);
    }

    @Override
    public boolean isReplacedByAnotherNode(final String uuid) {
        return replacedNodes.containsKey(uuid);
    }

    @Override
    public String getReplaceNodeId(final String uuid) {
        return replacedNodes.get(uuid);
    }
}
