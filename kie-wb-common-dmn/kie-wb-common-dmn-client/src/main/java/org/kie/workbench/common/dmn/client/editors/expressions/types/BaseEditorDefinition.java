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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridDataCache;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.uberfire.ext.wires.core.grids.client.model.GridData;

public abstract class BaseEditorDefinition<E extends Expression, D extends GridData> implements ExpressionEditorDefinition<E>,
                                                                                                GridDataCache<E, D> {

    protected DMNGridPanel gridPanel;
    protected DMNGridLayer gridLayer;
    protected DefinitionUtils definitionUtils;
    protected SessionManager sessionManager;
    protected SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    protected CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;
    protected CellEditorControlsView.Presenter cellEditorControls;
    protected ListSelectorView.Presenter listSelector;
    protected TranslationService translationService;

    protected Map<String, D> cache = new HashMap<>();

    public BaseEditorDefinition() {
        //CDI proxy
    }

    public BaseEditorDefinition(final DMNGridPanel gridPanel,
                                final DMNGridLayer gridLayer,
                                final DefinitionUtils definitionUtils,
                                final SessionManager sessionManager,
                                final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory,
                                final CellEditorControlsView.Presenter cellEditorControls,
                                final ListSelectorView.Presenter listSelector,
                                final TranslationService translationService) {
        this.gridPanel = gridPanel;
        this.gridLayer = gridLayer;
        this.definitionUtils = definitionUtils;
        this.sessionManager = sessionManager;
        this.sessionCommandManager = sessionCommandManager;
        this.canvasCommandFactory = canvasCommandFactory;
        this.cellEditorControls = cellEditorControls;
        this.listSelector = listSelector;
        this.translationService = translationService;
    }

    @Override
    public CacheResult<D> getData(final Optional<String> nodeUUID,
                                  final Optional<E> expression) {
        if (!nodeUUID.isPresent()) {
            return new CacheResult<>(makeGridData(expression), false);
        }
        if (!cache.containsKey(nodeUUID.get())) {
            final D gridData = makeGridData(expression);
            cache.put(nodeUUID.get(),
                      gridData);
            return new CacheResult<>(gridData, false);
        }
        return new CacheResult<>(cache.get(nodeUUID.get()), true);
    }

    protected abstract D makeGridData(final Optional<E> expression);
}
