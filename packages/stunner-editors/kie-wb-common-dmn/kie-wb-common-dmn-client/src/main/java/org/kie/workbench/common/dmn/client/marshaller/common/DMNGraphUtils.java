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
package org.kie.workbench.common.dmn.client.marshaller.common;

import java.util.stream.StreamSupport;

import org.kie.workbench.common.dmn.api.definition.model.DMNDiagram;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

public class DMNGraphUtils {

    private DMNGraphUtils() {
        // Private constructor to prevent instantiation
    }

    public static Node<?, ?> findDMNDiagramRoot(final Graph<?, Node<View, ?>> graph) {
        return StreamSupport.stream(graph.nodes().spliterator(), false)
                .filter(n -> DefinitionUtils.getElementDefinition(n) instanceof DMNDiagram)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("DMN Diagram root could not be found."));
    }
}
