/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.sw.marshall;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.kie.workbench.common.stunner.sw.definition.Workflow;

import static org.kie.workbench.common.stunner.sw.marshall.MarshallerUtils.getElementDefinition;

public class Context {

    private final HashMap<String, String> nameToUUIDBindings;

    // TODO: Need to keep a ref to the whole graph?
    final Index<?, ?> graphIndex;
    private Node workflowRootNode;
    private final List<Message> messages = new ArrayList<>();

    public Context(Index<?, ?> graphIndex) {
        this.graphIndex = graphIndex;
        this.nameToUUIDBindings = new HashMap<>();
    }

    public HashMap<String, String> getNameToUUIDBindings() {
        return nameToUUIDBindings;
    }

    public Node getWorkflowRootNode() {
        return workflowRootNode;
    }

    public void setWorkflowRootNode(Node workflowRootNode) {
        nameToUUIDBindings.put(workflowRootNode.getUUID(), workflowRootNode.getUUID());

        this.workflowRootNode = workflowRootNode;
    }

    public Node getNode(final String uuid) {
        return graphIndex.getNode(uuid);
    }

    public Workflow getWorkflowRoot() {
        return getElementDefinition(workflowRootNode);
    }

    public Graph getGraph() {
        return graphIndex.getGraph();
    }

    public String resolveUUID(String name, HashMap<String, String> previousNameToUUIDBindings) {
        if (null != previousNameToUUIDBindings) {
            final String uuid = previousNameToUUIDBindings.get(name);
            if (null != uuid) {
                nameToUUIDBindings.put(name, uuid); //add to current context

                return previousNameToUUIDBindings.get(name);
            }
        }

        return obtainUUID(name);
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

    public Message[] getMessages() {
        return messages.toArray(new Message[0]);
    }

    public void addMessage(Message message) {
        messages.add(message);
    }
}
