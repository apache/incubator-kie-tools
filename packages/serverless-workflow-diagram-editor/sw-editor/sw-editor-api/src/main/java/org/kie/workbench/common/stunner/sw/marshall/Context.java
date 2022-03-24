/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.sw.marshall;

import java.util.HashMap;

import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.kie.workbench.common.stunner.sw.definition.Workflow;

import static org.kie.workbench.common.stunner.sw.marshall.MarshallerUtils.getElementDefinition;

public class Context {

    private final HashMap<String, String> nameToUUIDBindings;

    // TODO: Need to keep a ref to the whole graph?
    private final Graph graph;
    private Node workflowRootNode;

    public Context(Graph graph) {
        this.graph = graph;
        this.nameToUUIDBindings = new HashMap<>();
    }

    public Node getWorkflowRootNode() {
        return workflowRootNode;
    }

    public void setWorkflowRootNode(Node workflowRootNode) {
        this.workflowRootNode = workflowRootNode;
    }

    public Node getNode(final String uuid) {
        return graph.getNode(uuid);
    }

    public Workflow getWorkflowRoot() {
        return getElementDefinition(workflowRootNode);
    }

    public String obtainUUID(String name) {
        if (null == name) {
            return generateUUID();
        }
        String uuid = nameToUUIDBindings.get(name);
        if (null == uuid) {
            uuid = generateUUID();
            nameToUUIDBindings.put(name, uuid);
        }
        return uuid;
    }

    static String generateUUID() {
        return UUID.uuid();
    }
}
