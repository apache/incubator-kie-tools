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

import javax.enterprise.event.Event;

import com.google.gwt.user.client.Timer;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

public abstract class AbstractToolboxAction implements ToolboxAction<AbstractCanvasHandler> {

    private final DefinitionUtils definitionUtils;
    private final ClientTranslationService translationService;

    protected AbstractToolboxAction(final DefinitionUtils definitionUtils,
                                    final ClientTranslationService translationService) {
        this.definitionUtils = definitionUtils;
        this.translationService = translationService;
    }

    protected abstract String getTitleKey(AbstractCanvasHandler canvasHandler,
                                          String uuid);

    protected abstract String getTitleDefinitionId(AbstractCanvasHandler canvasHandler,
                                                   String uuid);

    protected abstract String getGlyphId(AbstractCanvasHandler canvasHandler,
                                         String uuid);

    @Override
    public String getTitle(final AbstractCanvasHandler canvasHandler,
                           final String uuid) {
        final String titleKey = getTitleKey(canvasHandler,
                                            uuid);
        final String titleDefinitionId = getTitleDefinitionId(canvasHandler,
                                                              uuid);
        return translationService.getValue(titleKey) + " " +
                definitionUtils.getTitle(titleDefinitionId);
    }

    @Override
    public Glyph getGlyph(final AbstractCanvasHandler canvasHandler,
                          final String uuid) {
        final String ssid = canvasHandler.getDiagram().getMetadata().getShapeSetId();
        final ShapeFactory shapeFactory = canvasHandler.getShapeFactory(ssid);
        return shapeFactory.getGlyph(getGlyphId(canvasHandler,
                                                uuid));
    }

    public static Element<?> getElement(final AbstractCanvasHandler canvasHandler,
                                        final String uuid) {
        return canvasHandler.getGraphIndex().get(uuid);
    }

    // TODO: This is a work around. If enabling canvas handlers just here ( without using the timer )
    //       the layer receives a click event, so it fires a clear selection event and it results
    //       on the element just added not being selected.
    public static void fireElementSelectedEvent(final Event<CanvasSelectionEvent> selectionEvent,
                                                final AbstractCanvasHandler canvasHandler,
                                                final String uuid) {
        canvasHandler.getCanvas().getLayer().disableHandlers();
        selectionEvent.fire(new CanvasSelectionEvent(canvasHandler,
                                                     uuid));
        final Timer t = new Timer() {
            @Override
            public void run() {
                canvasHandler.getCanvas().getLayer().enableHandlers();
            }
        };
        t.schedule(500);
    }
}
