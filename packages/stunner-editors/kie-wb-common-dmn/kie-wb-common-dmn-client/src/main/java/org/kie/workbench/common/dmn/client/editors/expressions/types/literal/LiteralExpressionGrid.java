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

package org.kie.workbench.common.dmn.client.editors.expressions.types.literal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.enterprise.event.Event;

import com.ait.lienzo.shared.core.types.EventPropagationMode;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverView;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseDelegatingExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGridRenderer;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.handlers.DelegatingGridWidgetCellSelectorMouseEventHandler;
import org.kie.workbench.common.dmn.client.widgets.grid.handlers.DelegatingGridWidgetEditCellMouseEventHandler;
import org.kie.workbench.common.dmn.client.widgets.grid.handlers.EditableHeaderGridWidgetEditCellMouseEventHandler;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.grid.model.LiteralExpressionGridRow;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.ReadOnlyProvider;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.NodeMouseEventHandler;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridSelectionManager;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.GridPinnedModeManager;

import static org.kie.workbench.common.dmn.client.editors.expressions.util.RendererUtils.getExpressionTextLineHeight;

public class LiteralExpressionGrid extends BaseDelegatingExpressionGrid<LiteralExpression, DMNGridData, LiteralExpressionUIModelMapper> implements HasListSelectorControl {

    /** MUST BE SYNCHRONIZED WITH WidthConstants.ts */
    public static final double LITERAL_EXPRESSION_DEFAULT_WIDTH = 190d;

    private final ValueAndDataTypePopoverView.Presenter headerEditor;

    public LiteralExpressionGrid(final GridCellTuple parent,
                                 final Optional<String> nodeUUID,
                                 final HasExpression hasExpression,
                                 final Optional<HasName> hasName,
                                 final DMNGridPanel gridPanel,
                                 final DMNGridLayer gridLayer,
                                 final DMNGridData gridData,
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
                                 final ValueAndDataTypePopoverView.Presenter headerEditor,
                                 final ReadOnlyProvider readOnlyProvider) {
        super(parent,
              nodeUUID,
              hasExpression,
              hasName,
              gridPanel,
              gridLayer,
              gridData,
              new BaseExpressionGridRenderer(gridData),
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
        this.headerEditor = headerEditor;

        setEventPropagationMode(EventPropagationMode.NO_ANCESTORS);

        super.doInitialisation();
    }

    @Override
    public List<NodeMouseEventHandler> getNodeMouseClickEventHandlers(final GridSelectionManager selectionManager) {
        final List<NodeMouseEventHandler> handlers = new ArrayList<>();
        handlers.add(new DelegatingGridWidgetCellSelectorMouseEventHandler(selectionManager,
                                                                           this::getParentInformation,
                                                                           () -> nesting));
        handlers.add(new EditableHeaderGridWidgetEditCellMouseEventHandler());
        return handlers;
    }

    @Override
    public List<NodeMouseEventHandler> getNodeMouseDoubleClickEventHandlers(final GridSelectionManager selectionManager,
                                                                            final GridPinnedModeManager pinnedModeManager) {
        return Collections.singletonList(new DelegatingGridWidgetEditCellMouseEventHandler(this::getParentInformation,
                                                                                           () -> nesting));
    }

    @Override
    protected void doInitialisation() {
        // Defer initialisation until after the constructor completes as
        // LiteralExpressionUIModelMapper needs ListSelector to have been set
    }

    @Override
    public LiteralExpressionUIModelMapper makeUiModelMapper() {
        return new LiteralExpressionUIModelMapper(this::getModel,
                                                  getExpression(),
                                                  listSelector,
                                                  getExpressionTextLineHeight(getRenderer().getTheme()));
    }

    @Override
    protected void initialiseUiColumns() {
        final List<GridColumn.HeaderMetaData> headerMetaData = new ArrayList<>();
        if (nesting == 0) {
            headerMetaData.add(new LiteralExpressionColumnHeaderMetaData(hasExpression,
                                                                         hasName,
                                                                         clearValueConsumer(true, new Name()),
                                                                         setValueConsumer(true),
                                                                         setTypeRefConsumer(),
                                                                         translationService,
                                                                         cellEditorControls,
                                                                         headerEditor));
        }

        final GridColumn literalExpressionColumn = new LiteralExpressionColumn(headerMetaData,
                                                                               getAutocompleteTextareaFactory(),
                                                                               getAndSetInitialWidth(0, LITERAL_EXPRESSION_DEFAULT_WIDTH),
                                                                               this);

        model.appendColumn(literalExpressionColumn);
    }

    @Override
    public void initialiseUiRows() {
        getExpression().get().ifPresent(e -> model.appendRow(new LiteralExpressionGridRow()));
    }

    @Override
    public void initialiseUiCells() {
        getExpression().get().ifPresent(e -> uiModelMapper.fromDMNModel(0, 0));
    }

    @Override
    public double getPadding() {
        return findParentGrid().isPresent() ? PADDING : DEFAULT_PADDING;
    }

    @Override
    @SuppressWarnings("unused")
    public void doAfterSelectionChange(final int uiRowIndex,
                                       final int uiColumnIndex) {
        getExpression().get().ifPresent(this::fireDomainObjectSelectionEvent);
    }

    @Override
    public void doAfterHeaderSelectionChange(final int uiHeaderRowIndex,
                                             final int uiHeaderColumnIndex) {
        if (uiHeaderRowIndex == 0 && uiHeaderColumnIndex == 0) {
            final DMNModelInstrumentedBase base = hasExpression.asDMNModelInstrumentedBase();
            if (base instanceof DomainObject) {
                fireDomainObjectSelectionEvent((DomainObject) base);
                return;
            }
        }
        super.doAfterHeaderSelectionChange(uiHeaderRowIndex, uiHeaderColumnIndex);
    }
}
