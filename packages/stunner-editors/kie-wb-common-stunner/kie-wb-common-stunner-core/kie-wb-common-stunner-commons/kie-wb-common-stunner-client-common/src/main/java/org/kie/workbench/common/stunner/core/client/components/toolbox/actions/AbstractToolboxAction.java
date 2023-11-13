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


package org.kie.workbench.common.stunner.core.client.components.toolbox.actions;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

public abstract class AbstractToolboxAction implements ToolboxAction<AbstractCanvasHandler> {

    public interface ToolboxGlyphConsumer extends ShapeFactory.GlyphConsumer {
        //Marker interface
    }

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

    protected DefinitionUtils getDefinitionUtils() {
        return definitionUtils;
    }

    @Override
    public Glyph getGlyph(final AbstractCanvasHandler canvasHandler,
                          final String uuid) {
        final String ssid = canvasHandler.getDiagram().getMetadata().getShapeSetId();
        final ShapeFactory shapeFactory = canvasHandler.getShapeFactory(ssid);
        return shapeFactory.getGlyph(getGlyphId(canvasHandler, uuid),
                                     ToolboxGlyphConsumer.class);
    }
}
