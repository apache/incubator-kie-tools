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

package org.kie.workbench.common.dmn.client.canvas.controls.toolbox;

import java.util.Collections;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.DefaultCanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.canvas.controls.DeleteNodeConfirmation;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.DeleteNodeToolboxAction;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ToolboxAction;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseClickEvent;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

import static org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils.getElement;

@DMNEditor
@Dependent
public class DMNDeleteNodeToolboxAction extends DeleteNodeToolboxAction {

    private final DeleteNodeConfirmation deleteNodeConfirmation;

    @Inject
    public DMNDeleteNodeToolboxAction(final ClientTranslationService translationService,
                                      final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                      final @DMNEditor ManagedInstance<DefaultCanvasCommandFactory> commandFactories,
                                      final DefinitionUtils definitionUtils,
                                      final Event<CanvasClearSelectionEvent> clearSelectionEvent,
                                      final DeleteNodeConfirmation deleteNodeConfirmation) {
        super(translationService, sessionCommandManager, commandFactories, definitionUtils, clearSelectionEvent);
        this.deleteNodeConfirmation = deleteNodeConfirmation;
    }

    @Override
    public ToolboxAction<AbstractCanvasHandler> onMouseClick(final AbstractCanvasHandler canvasHandler,
                                                             final String uuid,
                                                             final MouseClickEvent event) {
        final Node<?, Edge> node = getElement(canvasHandler, uuid).asNode();
        if (deleteNodeConfirmation.requiresDeletionConfirmation(Collections.singleton(node))) {
            deleteNodeConfirmation.confirmDeletion(() -> superOnMouseClick(canvasHandler, uuid, event),
                                                   () -> {
                                                   },
                                                   Collections.singleton(node));
        } else {
            superOnMouseClick(canvasHandler, uuid, event);
        }
        return this;
    }

    void superOnMouseClick(final AbstractCanvasHandler canvasHandler,
                           final String uuid,
                           final MouseClickEvent event) {
        super.onMouseClick(canvasHandler, uuid, event);
    }
}
