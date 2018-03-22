/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.forms.client.components.toolbox;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ToolboxAction;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.shape.SvgDataUriGlyph;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseClickEvent;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.forms.client.gen.ClientFormGenerationManager;
import org.kie.workbench.common.stunner.forms.client.resources.i18n.FormsClientConstants;
import org.kie.workbench.common.stunner.forms.client.resources.images.FormsImageResources;

@Dependent
public class FormGenerationToolboxAction implements ToolboxAction<AbstractCanvasHandler> {

    private final ClientTranslationService translationService;
    private final ClientFormGenerationManager formGenerationManager;

    private static final SvgDataUriGlyph GLYPH =
            SvgDataUriGlyph.Builder.build(FormsImageResources.INSTANCE.form().getSafeUri());

    @Inject
    public FormGenerationToolboxAction(final ClientTranslationService translationService,
                                       final ClientFormGenerationManager formGenerationManager) {
        this.translationService = translationService;
        this.formGenerationManager = formGenerationManager;
    }

    @Override
    public Glyph getGlyph(final AbstractCanvasHandler canvasHandler,
                          final String uuid) {
        return GLYPH;
    }

    @Override
    public String getTitle(final AbstractCanvasHandler canvasHandler,
                           final String uuid) {
        return translationService.getValue(FormsClientConstants.FormsGenerateTaskForm);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ToolboxAction<AbstractCanvasHandler> onMouseClick(final AbstractCanvasHandler canvasHandler,
                                                             final String uuid,
                                                             final MouseClickEvent event) {
        formGenerationManager.call(service -> service.generateSelectedForms(canvasHandler.getDiagram(),
                                                                            new String[]{uuid}));
        return this;
    }
}
