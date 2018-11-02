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

package org.kie.workbench.common.dmn.client.canvas.controls.toolbox;

import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.v1_1.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.client.events.EditExpressionEvent;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ToolboxAction;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.resources.StunnerCommonImageResources;
import org.kie.workbench.common.stunner.core.client.shape.ImageDataUriGlyph;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseClickEvent;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.i18n.CoreTranslationMessages;

@Dependent
public class DMNEditBusinessKnowledgeModelToolboxAction implements ToolboxAction<AbstractCanvasHandler> {

    private static final ImageDataUriGlyph GLYPH =
            ImageDataUriGlyph.create(StunnerCommonImageResources.INSTANCE.edit().getSafeUri());

    private final SessionManager sessionManager;
    private final ClientTranslationService translationService;
    private final Event<EditExpressionEvent> editExpressionEvent;

    @Inject
    public DMNEditBusinessKnowledgeModelToolboxAction(final SessionManager sessionManager,
                                                      final ClientTranslationService translationService,
                                                      final Event<EditExpressionEvent> editExpressionEvent) {
        this.sessionManager = sessionManager;
        this.translationService = translationService;
        this.editExpressionEvent = editExpressionEvent;
    }

    @Override
    public Glyph getGlyph(final AbstractCanvasHandler canvasHandler,
                          final String uuid) {
        return GLYPH;
    }

    @Override
    public String getTitle(final AbstractCanvasHandler canvasHandler,
                           final String uuid) {
        return translationService.getValue(CoreTranslationMessages.EDIT);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ToolboxAction<AbstractCanvasHandler> onMouseClick(final AbstractCanvasHandler canvasHandler,
                                                             final String uuid,
                                                             final MouseClickEvent event) {
        // Notice the toolbox factory ensure this action is only being included
        // for BusinessKnowledgeModel definitions, next cast is safe.
        final Node<View<? extends BusinessKnowledgeModel>, Edge> bkmNode
                = (Node<View<? extends BusinessKnowledgeModel>, Edge>) CanvasLayoutUtils.getElement(canvasHandler,
                                                                                                    uuid)
                .asNode();
        final BusinessKnowledgeModel bkm = bkmNode.getContent().getDefinition();
        editExpressionEvent.fire(new EditExpressionEvent(sessionManager.getCurrentSession(),
                                                         uuid,
                                                         bkm.asHasExpression(),
                                                         Optional.of(bkm)));

        return this;
    }
}
