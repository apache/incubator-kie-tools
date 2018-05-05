/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.cm.client.canvas.controls.builder;

import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.cm.qualifiers.CaseManagementEditor;
import org.kie.workbench.common.stunner.core.client.api.ClientDefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl.Element;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl.ElementBuilderControlImpl;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.GraphBoundsIndexer;
import org.kie.workbench.common.stunner.core.rule.RuleManager;

@Element
@Dependent
@CaseManagementEditor
public class CaseManagementElementBuilderControl extends ElementBuilderControlImpl {

    protected CaseManagementElementBuilderControl() {
        this(null,
             null,
             null,
             null,
             null);
    }

    @Inject
    public CaseManagementElementBuilderControl(final ClientDefinitionManager clientDefinitionManager,
                                               final ClientFactoryService clientFactoryServices,
                                               final RuleManager ruleManager,
                                               final @CaseManagementEditor CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory,
                                               final GraphBoundsIndexer graphBoundsIndexer) {
        super(clientDefinitionManager,
              clientFactoryServices,
              ruleManager,
              canvasCommandFactory,
              graphBoundsIndexer);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Node<View<?>, Edge> getParent(final double x,
                                         final double y) {
        // The default implementation uses GraphBoundsIndexer that finds the top (z-index) Node
        // that contains the (x, y) location. However Case Management does not update Graph's Node's
        // locations to represent the visual layout. Consequentially we find the Node by examining
        // the  actual rendered Canvas.
        final Optional<Node<View<?>, Edge>> parent = canvasHandler.getElementAt(x,
                                                                                y);
        return parent.orElse(null);
    }
}
