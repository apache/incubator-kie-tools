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

package org.kie.workbench.common.dmn.client.editors.expressions.types;

import java.util.Optional;

import javax.enterprise.event.Event;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.uberfire.ext.wires.core.grids.client.model.GridData;

public abstract class BaseEditorDefinition<E extends Expression, D extends GridData> implements ExpressionEditorDefinition<E> {

    protected DMNGridPanel gridPanel;
    protected DMNGridLayer gridLayer;
    protected DefinitionUtils definitionUtils;
    protected SessionManager sessionManager;
    protected SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    protected CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;
    protected Event<ExpressionEditorChanged> editorSelectedEvent;
    protected CellEditorControlsView.Presenter cellEditorControls;
    protected ListSelectorView.Presenter listSelector;
    protected TranslationService translationService;

    public BaseEditorDefinition() {
        //CDI proxy
    }

    public BaseEditorDefinition(final DMNGridPanel gridPanel,
                                final DMNGridLayer gridLayer,
                                final DefinitionUtils definitionUtils,
                                final SessionManager sessionManager,
                                final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory,
                                final Event<ExpressionEditorChanged> editorSelectedEvent,
                                final CellEditorControlsView.Presenter cellEditorControls,
                                final ListSelectorView.Presenter listSelector,
                                final TranslationService translationService) {
        this.gridPanel = gridPanel;
        this.gridLayer = gridLayer;
        this.definitionUtils = definitionUtils;
        this.sessionManager = sessionManager;
        this.sessionCommandManager = sessionCommandManager;
        this.canvasCommandFactory = canvasCommandFactory;
        this.editorSelectedEvent = editorSelectedEvent;
        this.cellEditorControls = cellEditorControls;
        this.listSelector = listSelector;
        this.translationService = translationService;
    }

    protected abstract D makeGridData(final Optional<E> expression);
}
