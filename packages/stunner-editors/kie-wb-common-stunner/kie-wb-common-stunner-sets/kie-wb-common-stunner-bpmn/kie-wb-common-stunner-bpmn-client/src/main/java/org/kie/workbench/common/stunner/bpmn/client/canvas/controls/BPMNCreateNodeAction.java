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

package org.kie.workbench.common.stunner.bpmn.client.canvas.controls;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.bpmn.qualifiers.BPMN;
import org.kie.workbench.common.stunner.core.client.api.ClientFactoryManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.DefaultCanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.canvas.controls.inlineeditor.InlineTextEditEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.GeneralCreateNodeAction;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

@Dependent
@BPMN
public class BPMNCreateNodeAction extends GeneralCreateNodeAction {

    @Inject
    public BPMNCreateNodeAction(final DefinitionUtils definitionUtils,
                                final ClientFactoryManager clientFactoryManager,
                                final CanvasLayoutUtils canvasLayoutUtils,
                                final Event<CanvasSelectionEvent> selectionEvent,
                                final Event<InlineTextEditEvent> inlineTextEditEventEvent,
                                final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                final @Any ManagedInstance<DefaultCanvasCommandFactory> canvasCommandFactories) {
        super(definitionUtils,
              clientFactoryManager,
              canvasLayoutUtils,
              selectionEvent,
              inlineTextEditEventEvent,
              sessionCommandManager,
              canvasCommandFactories);
    }

    @Override
    protected MagnetConnection buildSourceConnectionBetween(Node<View<?>, Edge> sourceNode, Node<View<?>, Edge> targetNode) {
        MagnetConnection connection = super.buildSourceConnectionBetween(sourceNode, targetNode);
        connection.setAuto(true);
        return connection;
    }

    @Override
    protected MagnetConnection buildTargetBetween(Node<View<?>, Edge> sourceNode, Node<View<?>, Edge> targetNode) {
        MagnetConnection connection = super.buildTargetBetween(sourceNode, targetNode);
        connection.setAuto(true);
        return connection;
    }
}
