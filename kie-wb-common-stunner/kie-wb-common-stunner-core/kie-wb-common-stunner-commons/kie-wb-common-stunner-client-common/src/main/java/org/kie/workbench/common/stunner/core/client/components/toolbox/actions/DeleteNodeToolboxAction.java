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

import java.lang.annotation.Annotation;
import java.util.function.Predicate;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.DefaultCanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.resources.StunnerCommonIconsGlyphFactory;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseClickEvent;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.i18n.CoreTranslationMessages;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

import static org.kie.workbench.common.stunner.core.client.session.impl.InstanceUtils.lookup;

/**
 * A toolbox action/operation for deleting an Element.
 */
@Dependent
@Default
public class DeleteNodeToolboxAction implements ToolboxAction<AbstractCanvasHandler> {

    private final ClientTranslationService translationService;
    private final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    private final ManagedInstance<DefaultCanvasCommandFactory> commandFactories;
    private final DefinitionUtils definitionUtils;
    private final Predicate<DeleteNodeToolboxAction> confirmDelete;
    private final Event<CanvasClearSelectionEvent> clearSelectionEvent;

    @Inject
    public DeleteNodeToolboxAction(final ClientTranslationService translationService,
                                   final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                   final @Any ManagedInstance<DefaultCanvasCommandFactory> commandFactories,
                                   final DefinitionUtils definitionUtils,
                                   final Event<CanvasClearSelectionEvent> clearSelectionEvent) {
        this(translationService,
             sessionCommandManager,
             commandFactories,
             definitionUtils,
             deleteNodeAction -> true,
             clearSelectionEvent);
    }

    DeleteNodeToolboxAction(final ClientTranslationService translationService,
                            final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                            final ManagedInstance<DefaultCanvasCommandFactory> commandFactories,
                            final DefinitionUtils definitionUtils,
                            final Predicate<DeleteNodeToolboxAction> confirmDelete,
                            final Event<CanvasClearSelectionEvent> clearSelectionEvent) {
        this.translationService = translationService;
        this.sessionCommandManager = sessionCommandManager;
        this.commandFactories = commandFactories;
        this.definitionUtils = definitionUtils;
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

            final Metadata metadata = canvasHandler.getDiagram().getMetadata();
            final Annotation qualifier = definitionUtils.getQualifier(metadata.getDefinitionSetId());
            final CanvasCommandFactory<AbstractCanvasHandler> commandFactory = lookup(commandFactories, qualifier);

            final Node<?, Edge> node = CanvasLayoutUtils.getElement(canvasHandler, uuid).asNode();

            clearSelectionEvent.fire(new CanvasClearSelectionEvent(canvasHandler));

            sessionCommandManager.execute(canvasHandler, commandFactory.deleteNode(node));
        }
        return this;
    }

    @PreDestroy
    public void destroy() {
        commandFactories.destroyAll();
    }
}