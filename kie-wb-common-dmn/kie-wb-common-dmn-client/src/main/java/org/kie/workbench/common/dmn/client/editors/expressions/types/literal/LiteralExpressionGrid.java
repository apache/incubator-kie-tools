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
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.event.Event;

import com.ait.lienzo.client.core.event.NodeMouseClickHandler;
import com.ait.lienzo.shared.core.types.EventPropagationMode;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.HasVariable;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.commands.general.SetTypeRefCommand;
import org.kie.workbench.common.dmn.client.editors.types.HasNameAndDataTypeControl;
import org.kie.workbench.common.dmn.client.editors.types.NameAndDataTypeEditorView;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.HasCellEditorControls;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridRow;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellValueTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormProperties;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridSelectionManager;

public class LiteralExpressionGrid extends BaseExpressionGrid<LiteralExpression, DMNGridData, LiteralExpressionUIModelMapper> implements HasListSelectorControl,
                                                                                                                                         HasNameAndDataTypeControl {

    public static final double PADDING = 0.0;

    private final NameAndDataTypeEditorView.Presenter headerEditor;

    public LiteralExpressionGrid(final GridCellTuple parent,
                                 final Optional<String> nodeUUID,
                                 final HasExpression hasExpression,
                                 final Optional<LiteralExpression> expression,
                                 final Optional<HasName> hasName,
                                 final DMNGridPanel gridPanel,
                                 final DMNGridLayer gridLayer,
                                 final DMNGridData gridData,
                                 final DefinitionUtils definitionUtils,
                                 final SessionManager sessionManager,
                                 final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                 final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory,
                                 final Event<ExpressionEditorChanged> editorSelectedEvent,
                                 final Event<RefreshFormProperties> refreshFormPropertiesEvent,
                                 final CellEditorControlsView.Presenter cellEditorControls,
                                 final ListSelectorView.Presenter listSelector,
                                 final TranslationService translationService,
                                 final int nesting,
                                 final NameAndDataTypeEditorView.Presenter headerEditor) {
        super(parent,
              nodeUUID,
              hasExpression,
              expression,
              hasName,
              gridPanel,
              gridLayer,
              gridData,
              new LiteralExpressionGridRenderer(nesting > 0),
              definitionUtils,
              sessionManager,
              sessionCommandManager,
              canvasCommandFactory,
              editorSelectedEvent,
              refreshFormPropertiesEvent,
              cellEditorControls,
              listSelector,
              translationService,
              nesting);
        this.headerEditor = headerEditor;

        setEventPropagationMode(EventPropagationMode.NO_ANCESTORS);

        super.doInitialisation();
    }

    @Override
    protected NodeMouseClickHandler getGridMouseClickHandler(final GridSelectionManager selectionManager) {
        return (event) -> gridLayer.select(parent.getGridWidget());
    }

    @Override
    public void selectFirstCell() {
        final GridData uiModel = parent.getGridWidget().getModel();
        uiModel.clearSelections();
        uiModel.selectCell(parent.getRowIndex(),
                           parent.getColumnIndex());
    }

    @Override
    protected void doInitialisation() {
        // Defer initialisation until after the constructor completes as
        // LiteralExpressionUIModelMapper needs ListSelector to have been set
    }

    @Override
    public LiteralExpressionUIModelMapper makeUiModelMapper() {
        return new LiteralExpressionUIModelMapper(this::getModel,
                                                  () -> expression,
                                                  listSelector,
                                                  parent);
    }

    @Override
    protected void initialiseUiColumns() {
        final GridColumn literalExpressionColumn = new LiteralExpressionColumn(new LiteralExpressionColumnHeaderMetaData(() -> hasName.orElse(HasName.NOP).getName().getValue(),
                                                                                                                         (s) -> hasName.orElse(HasName.NOP).getName().setValue(s),
                                                                                                                         this::getTypeRef,
                                                                                                                         cellEditorControls,
                                                                                                                         headerEditor,
                                                                                                                         this),
                                                                               getBodyTextAreaFactory(),
                                                                               this);

        model.appendColumn(literalExpressionColumn);
    }

    @Override
    protected void initialiseUiModel() {
        expression.ifPresent(e -> {
            model.appendRow(new DMNGridRow());
            uiModelMapper.fromDMNModel(0,
                                       0);
        });
    }

    @Override
    protected boolean isHeaderHidden() {
        return nesting > 0;
    }

    @Override
    public double getPadding() {
        return findParentGrid().isPresent() ? PADDING : DEFAULT_PADDING;
    }

    @Override
    @SuppressWarnings("unused")
    public List<ListSelectorItem> getItems(final int uiRowIndex,
                                           final int uiColumnIndex) {
        final List<ListSelectorItem> items = new ArrayList<>();
        final Optional<BaseExpressionGrid> parent = findParentGrid();
        parent.ifPresent(grid -> {
            if (grid instanceof HasListSelectorControl) {
                final int parentUiRowIndex = getParentInformation().getRowIndex();
                final int parentUiColumnIndex = getParentInformation().getColumnIndex();
                final GridCell<?> parentCell = getParentInformation().getGridWidget().getModel().getCell(parentUiRowIndex, parentUiColumnIndex);

                if (parentCell instanceof HasCellEditorControls) {
                    final List<ListSelectorItem> parentItems = ((HasListSelectorControl) grid).getItems(parentUiRowIndex,
                                                                                                        parentUiColumnIndex);
                    if (!parentItems.isEmpty()) {
                        items.addAll(parentItems);
                    }
                }
            }
        });

        return items;
    }

    @Override
    public void onItemSelected(final ListSelectorItem item) {
        final ListSelectorTextItem li = (ListSelectorTextItem) item;
        li.getCommand().execute();
    }

    @Override
    public String getDisplayName() {
        return hasName.orElse(HasName.NOP).getName().getValue();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setDisplayName(final String name) {
        if (Objects.equals(name, getDisplayName())) {
            return;
        }

        if (name == null || name.trim().isEmpty()) {
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          newHeaderHasNameHasNoValueCommand().apply(new GridCellTuple(0,
                                                                                                      0,
                                                                                                      this)));
        } else {
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          newHeaderHasNameHasValueCommand().apply(new GridCellValueTuple<>(0,
                                                                                                           0,
                                                                                                           this,
                                                                                                           new BaseGridCellValue<>(name))));
        }
    }

    @Override
    public QName getTypeRef() {
        final DMNModelInstrumentedBase model = hasExpression.asDMNModelInstrumentedBase();
        if (model instanceof HasVariable) {
            final HasVariable hasVariable = (HasVariable) model;
            return hasVariable.getVariable().getTypeRef();
        }
        return null;
    }

    @Override
    public void setTypeRef(final QName typeRef) {
        final DMNModelInstrumentedBase model = hasExpression.asDMNModelInstrumentedBase();
        if (model instanceof HasVariable) {
            final HasVariable hasVariable = (HasVariable) model;
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new SetTypeRefCommand(hasVariable,
                                                                typeRef,
                                                                () -> {
                                                                    gridLayer.batch();
                                                                    nodeUUID.ifPresent(uuid -> refreshFormPropertiesEvent.fire(new RefreshFormProperties(sessionManager.getCurrentSession(), uuid)));
                                                                }));
        }
    }

    @Override
    public DMNModelInstrumentedBase asDMNModelInstrumentedBase() {
        return expression.get();
    }
}
