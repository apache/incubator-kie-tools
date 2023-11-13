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


package org.kie.workbench.common.stunner.client.widgets.presenters.session.impl;

import java.util.Objects;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoCanvasView;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoLayer;
import org.kie.workbench.common.stunner.client.widgets.resources.i18n.StunnerWidgetsConstants;
import org.kie.workbench.common.stunner.client.widgets.views.session.EmptyStateView;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandUndoneEvent;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils.CardinalityCountState;

import static org.kie.workbench.common.stunner.core.graph.util.GraphUtils.computeCardinalityState;

@Dependent
public class SessionCardinalityStateHandler {

    private static final String EMPTY_STATE_CAPTION_KEY = "EmptyStateCaption";
    private static final String EMPTY_STATE_MESSAGE_KEY = "EmptyStateMessage";

    private final ClientTranslationService translationService;
    private final EmptyStateView emptyStateView;
    private EditorSession session;

    @Inject
    public SessionCardinalityStateHandler(final ClientTranslationService translationService,
                                          final EmptyStateView emptyStateView) {
        this.translationService = translationService;
        this.emptyStateView = emptyStateView;
    }

    public void bind(final EditorSession session) {
        this.session = session;
        final String definitionSetId = session.getCanvasHandler().getDiagram().getMetadata().getDefinitionSetId();
        final String captionText = getEmptyStateCaption(definitionSetId);
        final String messageText = getEmptyStateMessage(definitionSetId);
        this.emptyStateView.init(getLienzoLayer(), captionText, messageText);
        refresh();
    }

    public void refresh() {
        final CardinalityCountState cardinalityState = computeCardinalityState(session.getCanvasHandler().getDiagram());
        if (Objects.equals(CardinalityCountState.EMPTY, cardinalityState)) {
            emptyStateView.show();
        } else {
            emptyStateView.hide();
        }
    }

    @PreDestroy
    public void destroy() {
        session = null;
    }

    @SuppressWarnings("unchecked")
    void onCommandUndoExecuted(final @Observes CanvasCommandUndoneEvent commandUndoExecutedEvent) {
        if (null != session &&
                Objects.equals(session.getCanvasHandler(), commandUndoExecutedEvent.getCanvasHandler())) {
            refresh();
        }
    }

    private String getEmptyStateCaption(final String definitionSetId) {
        String captionText = translationService.getValue(definitionSetId + "." + EMPTY_STATE_CAPTION_KEY);
        if (captionText == null) {
            captionText = translationService.getValue(StunnerWidgetsConstants.SessionCardinalityStateHandler_EmptyStateCaption);
        }
        return captionText;
    }

    private String getEmptyStateMessage(final String definitionSetId) {
        String messageText = translationService.getValue(definitionSetId + "." + EMPTY_STATE_MESSAGE_KEY);
        if (messageText == null) {
            messageText = translationService.getValue(StunnerWidgetsConstants.SessionCardinalityStateHandler_EmptyStateMessage);
        }
        return messageText;
    }

    private LienzoLayer getLienzoLayer() {
        return getCanvasView().getLayer();
    }

    private LienzoCanvasView getCanvasView() {
        return (LienzoCanvasView) getCanvas().getView();
    }

    private LienzoCanvas getCanvas() {
        return (LienzoCanvas) session.getCanvas();
    }
}
