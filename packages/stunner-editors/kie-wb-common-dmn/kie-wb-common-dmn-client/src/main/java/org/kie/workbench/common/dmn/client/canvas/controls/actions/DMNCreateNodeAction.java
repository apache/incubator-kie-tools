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

package org.kie.workbench.common.dmn.client.canvas.controls.actions;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
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
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

import static org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils.DEFAULT_NEW_NODE_ORIENTATION;
import static org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils.Orientation;

@Dependent
@DMNEditor
public class DMNCreateNodeAction extends GeneralCreateNodeAction {

    @Inject
    public DMNCreateNodeAction(final DefinitionUtils definitionUtils,
                               final ClientFactoryManager clientFactoryManager,
                               final CanvasLayoutUtils canvasLayoutUtils,
                               final Event<CanvasSelectionEvent> selectionEvent,
                               final Event<InlineTextEditEvent> inlineTextEditEventEvent,
                               final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                               final @Any ManagedInstance<DefaultCanvasCommandFactory> canvasCommandFactories) {
        super(definitionUtils, clientFactoryManager, canvasLayoutUtils, selectionEvent, inlineTextEditEventEvent, sessionCommandManager, canvasCommandFactories);
    }

    @Override
    public Orientation getNodeOrientation(final Node<View<?>, Edge> targetNode) {

        final Object definition = DefinitionUtils.getElementDefinition(targetNode);
        if (definition instanceof Decision
                || definition instanceof BusinessKnowledgeModel) {
            return Orientation.UpRight;
        }

        return DEFAULT_NEW_NODE_ORIENTATION;
    }
}
