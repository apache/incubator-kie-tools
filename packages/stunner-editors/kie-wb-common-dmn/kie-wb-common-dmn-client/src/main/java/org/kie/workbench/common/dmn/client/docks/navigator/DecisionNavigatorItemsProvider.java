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

package org.kie.workbench.common.dmn.client.docks.navigator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.TextAnnotation;
import org.kie.workbench.common.dmn.api.graph.DMNDiagramUtils;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramTuple;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.docks.navigator.factories.DecisionNavigatorItemFactory;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;

@ApplicationScoped
public class DecisionNavigatorItemsProvider {

    static final String DRG = "DRG";

    static final String DRDs = "DRDs";

    private final DecisionNavigatorItemFactory itemFactory;

    private final DMNDiagramsSession dmnDiagramsSession;

    private final DMNDiagramUtils dmnDiagramUtils;

    @Inject
    public DecisionNavigatorItemsProvider(final DecisionNavigatorItemFactory itemFactory,
                                          final DMNDiagramsSession dmnDiagramsSession,
                                          final DMNDiagramUtils dmnDiagramUtils) {
        this.itemFactory = itemFactory;
        this.dmnDiagramsSession = dmnDiagramsSession;
        this.dmnDiagramUtils = dmnDiagramUtils;
    }

    public List<DecisionNavigatorItem> getItems() {

        final List<DecisionNavigatorItem> items = new ArrayList<>();

        dmnDiagramsSession.getDMNDiagrams().stream()
                .sorted((e1, e2) -> {
                    final String dmnElementName1 = e1.getDMNDiagram().getName().getValue();
                    final String dmnElementName2 = e2.getDMNDiagram().getName().getValue();
                    return DRG.equals(dmnElementName1) ? -1 : DRG.equals(dmnElementName2) ? 1 : 0;
                })
                .forEach(diagramTuple -> {

                    final Diagram stunner = diagramTuple.getStunnerDiagram();
                    final DecisionNavigatorItem root = makeRoot(diagramTuple);

                    items.add(root);

                    getNodes(stunner).forEach(node -> makeItem(root, node));
                });

        if (items.size() > 1) {
            items.add(0, itemFactory.makeSeparator(DRG));
            items.add(2, itemFactory.makeSeparator(DRDs));
        }

        return items;
    }

    private List<Node> getNodes(final Diagram stunner) {
        return dmnDiagramUtils
                .getNodeStream(stunner)
                .filter(this::allowedNode)
                .collect(Collectors.toList());
    }

    private boolean allowedNode(final Node node) {

        final Object content = node.getContent();
        if (!(content instanceof Definition)) {
            return false;
        }

        final Object definition = ((Definition) content).getDefinition();
        return definition instanceof DRGElement || definition instanceof TextAnnotation;
    }

    @SuppressWarnings("unchecked")
    private void makeItem(final DecisionNavigatorItem root,
                          final Node node) {
        final DecisionNavigatorItem item = itemFactory.makeItem(node);
        root.addChild(item);
    }

    private DecisionNavigatorItem makeRoot(final DMNDiagramTuple diagramTuple) {
        return itemFactory.makeRoot(diagramTuple);
    }
}
