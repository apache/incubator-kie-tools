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


package org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.shortcut;

import java.util.Set;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.GeneralCreateNodeAction;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ToolboxDomainLookups;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.lookup.domain.CommonDomainLookups;
import org.kie.workbench.common.stunner.core.registry.impl.DefinitionsCacheRegistry;

public abstract class AbstractAppendNodeShortcut implements KeyboardShortcut<AbstractCanvasHandler> {

    private ToolboxDomainLookups toolboxDomainLookups;

    private DefinitionsCacheRegistry definitionsCacheRegistry;

    private GeneralCreateNodeAction generalCreateNodeAction;

    public AbstractAppendNodeShortcut(final ToolboxDomainLookups toolboxDomainLookups,
                                      final DefinitionsCacheRegistry definitionsCacheRegistry,
                                      final GeneralCreateNodeAction generalCreateNodeAction) {
        this.toolboxDomainLookups = toolboxDomainLookups;
        this.definitionsCacheRegistry = definitionsCacheRegistry;
        this.generalCreateNodeAction = generalCreateNodeAction;
    }

    @Override
    public void executeAction(final AbstractCanvasHandler canvasHandler,
                              final String selectedNodeId) {
        final Node sourceNode = CanvasLayoutUtils.getElement(canvasHandler, selectedNodeId).asNode();

        final String definitionSetId = canvasHandler.getDiagram().getMetadata().getDefinitionSetId();
        final CommonDomainLookups commonDomainLookups = toolboxDomainLookups.get(definitionSetId);

        final Set<String> connectorDefinitionIds = commonDomainLookups.lookupTargetConnectors(sourceNode);

        for (final String connectorDefinitionId : connectorDefinitionIds) {
            final Set<String> targetNodesDefinitionIds =
                    commonDomainLookups.lookupTargetNodes(canvasHandler.getDiagram().getGraph(),
                                                          sourceNode,
                                                          connectorDefinitionId);

            for (final String targetNodeDefinitionId : targetNodesDefinitionIds) {
                final Object definition = definitionsCacheRegistry.getDefinitionById(targetNodeDefinitionId);
                if (canAppendNodeOfDefinition(definition)) {
                    generalCreateNodeAction.executeAction(canvasHandler,
                                                          selectedNodeId,
                                                          targetNodeDefinitionId,
                                                          connectorDefinitionId);

                    break;
                }
            }
        }
    }

    public abstract boolean canAppendNodeOfDefinition(final Object definition);
}
