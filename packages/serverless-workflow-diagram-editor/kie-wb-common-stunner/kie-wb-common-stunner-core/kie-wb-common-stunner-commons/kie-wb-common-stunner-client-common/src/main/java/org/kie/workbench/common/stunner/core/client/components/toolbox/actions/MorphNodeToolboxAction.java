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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseClickEvent;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.definition.morph.MorphDefinition;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.core.util.HashUtil;

/**
 * A toolbox action/operation for an Element in order to morph it into another one.
 */
@Dependent
public class MorphNodeToolboxAction extends AbstractToolboxAction {

    private static Logger LOGGER = Logger.getLogger(MorphNodeToolboxAction.class.getName());
    static final String KEY_TITLE = "org.kie.workbench.common.stunner.core.client.toolbox.morphInto";

    private final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    private final CanvasCommandFactory<AbstractCanvasHandler> commandFactory;
    private final Event<CanvasSelectionEvent> selectionEvent;
    private final Event<CanvasClearSelectionEvent> clearSelectionEventEvent;

    private MorphDefinition morphDefinition;
    private String targetDefinitionId;

    @Inject
    public MorphNodeToolboxAction(final DefinitionUtils definitionUtils,
                                  final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                  final CanvasCommandFactory<AbstractCanvasHandler> commandFactory,
                                  final ClientTranslationService translationService,
                                  final Event<CanvasSelectionEvent> selectionEvent,
                                  final Event<CanvasClearSelectionEvent> clearSelectionEventEvent) {
        super(definitionUtils,
              translationService);
        this.sessionCommandManager = sessionCommandManager;
        this.commandFactory = commandFactory;
        this.selectionEvent = selectionEvent;
        this.clearSelectionEventEvent = clearSelectionEventEvent;
    }

    public MorphNodeToolboxAction setMorphDefinition(final MorphDefinition morphDefinition) {
        this.morphDefinition = morphDefinition;
        return this;
    }

    public MorphNodeToolboxAction setTargetDefinitionId(final String targetDefinitionId) {
        this.targetDefinitionId = targetDefinitionId;
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ToolboxAction<AbstractCanvasHandler> onMouseClick(final AbstractCanvasHandler canvasHandler,
                                                             final String uuid,
                                                             final MouseClickEvent event) {
        final String ssid = canvasHandler.getDiagram().getMetadata().getShapeSetId();
        final Node<View<?>, Edge> sourceNode = (Node<View<?>, Edge>) CanvasLayoutUtils.getElement(canvasHandler,
                                                                                                  uuid).asNode();

        //deselect node to be morphed, to avoid showing toolbar while morphing
        clearSelectionEventEvent.fire(new CanvasClearSelectionEvent(canvasHandler));

        //delay is used to overcome the toolbar animation while morphing the node
        final CommandResult<CanvasViolation> result =
                sessionCommandManager.execute(canvasHandler,
                                              commandFactory.morphNode(sourceNode,
                                                                       morphDefinition,
                                                                       targetDefinitionId,
                                                                       ssid));
        if (CommandUtils.isError(result)) {
            LOGGER.log(Level.SEVERE,
                       result.toString());
        } else {
            CanvasLayoutUtils.fireElementSelectedEvent(selectionEvent,
                                                       canvasHandler,
                                                       uuid);
        }

        return this;
    }

    @Override
    protected String getTitleKey(final AbstractCanvasHandler canvasHandler,
                                 final String uuid) {
        return KEY_TITLE;
    }

    @Override
    protected String getTitleDefinitionId(final AbstractCanvasHandler canvasHandler,
                                          final String uuid) {
        return targetDefinitionId;
    }

    @Override
    protected String getGlyphId(final AbstractCanvasHandler canvasHandler,
                                final String uuid) {
        return targetDefinitionId;
    }

    @PreDestroy
    public void destroy() {
        morphDefinition = null;
        targetDefinitionId = null;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(morphDefinition.hashCode(),
                                         targetDefinitionId.hashCode());
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof MorphNodeToolboxAction) {
            MorphNodeToolboxAction other = (MorphNodeToolboxAction) o;
            return other.morphDefinition.equals(morphDefinition) &&
                    other.targetDefinitionId.equals(targetDefinitionId);
        }
        return false;
    }
}
