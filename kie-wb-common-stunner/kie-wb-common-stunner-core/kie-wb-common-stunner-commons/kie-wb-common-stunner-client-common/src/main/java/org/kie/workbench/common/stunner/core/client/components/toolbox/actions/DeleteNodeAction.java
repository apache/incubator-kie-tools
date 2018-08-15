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

package org.kie.workbench.common.stunner.core.client.components.toolbox.actions;

import java.util.function.Predicate;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.resources.StunnerCommonIconsGlyphFactory;
import org.kie.workbench.common.stunner.core.client.session.Session;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseClickEvent;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.i18n.CoreTranslationMessages;

/**
 * A toolbox action/operation for deleting an Element.
 */
@Dependent
public class DeleteNodeAction implements ToolboxAction<AbstractCanvasHandler> {

    private final ClientTranslationService translationService;
    private final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    private final CanvasCommandFactory<AbstractCanvasHandler> commandFactory;
    private final Predicate<DeleteNodeAction> confirmDelete;
    private final Event<CanvasClearSelectionEvent> clearSelectionEvent;

    @Inject
    public DeleteNodeAction(final ClientTranslationService translationService,
                            final @Session SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                            final CanvasCommandFactory<AbstractCanvasHandler> commandFactory,
                            final Event<CanvasClearSelectionEvent> clearSelectionEvent) {
        this(translationService,
             sessionCommandManager,
             commandFactory,
             deleteNodeAction -> true,
             clearSelectionEvent);
    }

    DeleteNodeAction(final ClientTranslationService translationService,
                     final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                     final CanvasCommandFactory<AbstractCanvasHandler> commandFactory,
                     final Predicate<DeleteNodeAction> confirmDelete,
                     final Event<CanvasClearSelectionEvent> clearSelectionEvent) {
        this.translationService = translationService;
        this.sessionCommandManager = sessionCommandManager;
        this.commandFactory = commandFactory;
        this.confirmDelete = confirmDelete;
        this.clearSelectionEvent = clearSelectionEvent;
    }

    @Override
    public Glyph getGlyph(final AbstractCanvasHandler canvasHandler,
                          final String uuid) {
        return StunnerCommonIconsGlyphFactory.DELETE;
    }

    @Override
    public String getTitle(final AbstractCanvasHandler canvasHandler,
                           final String uuid) {
        return translationService.getValue(CoreTranslationMessages.DELETE);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ToolboxAction<AbstractCanvasHandler> onMouseClick(final AbstractCanvasHandler canvasHandler,
                                                             final String uuid,
                                                             final MouseClickEvent event) {
        if (confirmDelete.test(this)) {
            final Node<?, Edge> node = AbstractToolboxAction.getElement(canvasHandler, uuid).asNode();

            clearSelectionEvent.fire(new CanvasClearSelectionEvent(canvasHandler));

            sessionCommandManager.execute(canvasHandler, commandFactory.deleteNode(node));
        }
        return this;
    }
}