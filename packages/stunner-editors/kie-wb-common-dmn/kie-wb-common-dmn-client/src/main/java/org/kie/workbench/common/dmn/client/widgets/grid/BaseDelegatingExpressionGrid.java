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

package org.kie.workbench.common.dmn.client.widgets.grid;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.enterprise.event.Event;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.HasCellEditorControls;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.ReadOnlyProvider;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;

public abstract class BaseDelegatingExpressionGrid<E extends Expression, D extends GridData, M extends BaseUIModelMapper<E>> extends BaseExpressionGrid<E, D, M> implements HasListSelectorControl {

    public static final double PADDING = 0.0;

    public BaseDelegatingExpressionGrid(final GridCellTuple parent,
                                        final Optional<String> nodeUUID,
                                        final HasExpression hasExpression,
                                        final Optional<HasName> hasName,
                                        final DMNGridPanel gridPanel,
                                        final DMNGridLayer gridLayer,
                                        final D gridData,
                                        final GridRenderer gridRenderer,
                                        final DefinitionUtils definitionUtils,
                                        final SessionManager sessionManager,
                                        final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                        final DefaultCanvasCommandFactory canvasCommandFactory,
                                        final Event<ExpressionEditorChanged> editorSelectedEvent,
                                        final Event<RefreshFormPropertiesEvent> refreshFormPropertiesEvent,
                                        final Event<DomainObjectSelectionEvent> domainObjectSelectionEvent,
                                        final CellEditorControlsView.Presenter cellEditorControls,
                                        final ListSelectorView.Presenter listSelector,
                                        final TranslationService translationService,
                                        final boolean isOnlyVisualChangeAllowed,
                                        final int nesting,
                                        final ReadOnlyProvider readOnlyProvider) {
        super(parent,
              nodeUUID,
              hasExpression,
              hasName,
              gridPanel,
              gridLayer,
              gridData,
              gridRenderer,
              definitionUtils,
              sessionManager,
              sessionCommandManager,
              canvasCommandFactory,
              editorSelectedEvent,
              refreshFormPropertiesEvent,
              domainObjectSelectionEvent,
              cellEditorControls,
              listSelector,
              translationService,
              isOnlyVisualChangeAllowed,
              nesting,
              readOnlyProvider);
    }

    @Override
    public void selectFirstCell() {
        if (nesting == 0) {
            super.selectFirstCell();
            return;
        }

        final GridCellTuple parent = getParentInformation();
        final GridWidget parentGridWidget = parent.getGridWidget();
        final GridData parentGridData = parentGridWidget.getModel();
        parentGridData.clearSelections();
        parentGridData.selectCell(parent.getRowIndex(),
                                  parent.getColumnIndex());

        doAfterSelectionChange(0, 0);

        final DMNGridLayer gridLayer = (DMNGridLayer) getLayer();
        gridLayer.select(parentGridWidget);
    }

    @Override
    @SuppressWarnings("unused")
    public List<ListSelectorItem> getItems(final int uiRowIndex,
                                           final int uiColumnIndex) {
        final List<ListSelectorItem> items = new ArrayList<>();
        final Optional<BaseExpressionGrid> parent = findParentGrid();
        parent.ifPresent(grid -> {
            final int parentUiRowIndex = getParentInformation().getRowIndex();
            final int parentUiColumnIndex = getParentInformation().getColumnIndex();
            final GridCell<?> parentCell = getParentInformation().getGridWidget().getModel().getCell(parentUiRowIndex, parentUiColumnIndex);

            if (parentCell instanceof HasCellEditorControls) {
                final List<ListSelectorItem> parentItems = grid.getItems(parentUiRowIndex,
                                                                         parentUiColumnIndex);
                if (!parentItems.isEmpty()) {
                    items.addAll(parentItems);
                }
            }
        });

        return items;
    }

    @Override
    public void onItemSelected(final ListSelectorItem item) {
        if (item instanceof ListSelectorTextItem) {
            final ListSelectorTextItem li = (ListSelectorTextItem) item;
            li.getCommand().execute();
        }
    }
}
