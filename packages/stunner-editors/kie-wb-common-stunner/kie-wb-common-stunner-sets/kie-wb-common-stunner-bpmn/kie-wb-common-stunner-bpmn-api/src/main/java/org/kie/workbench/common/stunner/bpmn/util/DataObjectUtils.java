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

package org.kie.workbench.common.stunner.bpmn.util;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.kie.workbench.common.stunner.bpmn.definition.BPMNDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.DataObject;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;

public class DataObjectUtils {

    private static boolean isBPMNDefinition(Node node) {
        return node.getContent() instanceof View &&
                ((View) node.getContent()).getDefinition() instanceof BPMNDefinition;
    }

    public static Set<DataObject> findDataObjects(ClientSession session, GraphUtils graphUtils, Node selectedElement, Set<String> parentIds) {
        Iterable<Node> nodes = session
                .getCanvasHandler()
                .getDiagram()
                .getGraph()
                .nodes();
        // Only return Data Objects that have the same id as the current as the parent and its parent recursively
        return StreamSupport.stream(nodes.spliterator(), false)
                .filter(DataObjectUtils::isBPMNDefinition)
                .map(elm -> (Node<View<BPMNDefinition>, Edge>) elm)
                .filter(elm -> {
                    if (elm.getContent().getDefinition() instanceof DataObject) {
                        final Element parent = graphUtils.getParent(elm);
                        if (parent == null) { // test
                            return true;
                        }

                        return parentIds.contains(parent.getUUID());
                    }
                    return false;
                })
                .map(elm -> ((DataObject) elm.getContent().getDefinition()))
                .collect(Collectors.toSet());
    }
}
