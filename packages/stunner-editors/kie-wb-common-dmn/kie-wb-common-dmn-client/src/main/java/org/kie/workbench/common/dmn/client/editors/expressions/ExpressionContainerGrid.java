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

package org.kie.workbench.common.dmn.client.editors.expressions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.enterprise.event.Event;

import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.EventPropagationMode;
import com.ait.lienzo.tools.client.event.INodeXYEvent;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.HasVariable;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.dmn.client.commands.general.ClearExpressionTypeCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetHasValueCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionEditorColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.UndefinedExpressionColumn;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.ExpressionGridCache;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridColumn;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorGridRow;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseHeaderMetaData;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridWidgetRegistry;
import org.uberfire.mvp.ParameterizedCommand;

public class ExpressionContainerGrid extends BaseGrid<Expression> {

    private static final String COLUMN_GROUP = "ExpressionContainerGrid$Expression0";

    private final Supplier<ExpressionGridCache> expressionGridCache;
    private final GridCellTuple parent = new GridCellTuple(0, 0, this);
    private final ExpressionEditorColumn expressionColumn;

    private final ParameterizedCommand<Optional<Expression>> onHasExpressionChanged;
    private final ParameterizedCommand<Optional<HasName>> onHasNameChanged;

    private ExpressionContainerUIModelMapper uiModelMapper;

    private Optional<Consumer> onUndoClear;

    private static class ExpressionEditorColumnWrapper extends ExpressionEditorColumn {

        public ExpressionEditorColumnWrapper(final GridWidgetRegistry registry,
                                             final HeaderMetaData headerMetaData,
                                             final double width,
                                             final BaseGrid<? extends Expression> gridWidget) {
            super(registry,
                  headerMetaData,
                  width,
                  gridWidget);
        }

        @Override
        protected void setComponentWidth(final double width) {
            //NOP. Synchronization with the HasComponentWidths is handled by the child grids.
        }
    }

    public ExpressionContainerGrid(final DMNGridLayer gridLayer,
                                   final CellEditorControlsView.Presenter cellEditorControls,
                                   final TranslationService translationService,
                                   final ListSelectorView.Presenter listSelector,
                                   final SessionManager sessionManager,
                                   final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                   final DefaultCanvasCommandFactory canvasCommandFactory,
                                   final Supplier<ExpressionEditorDefinitions> expressionEditorDefinitions,
                                   final Supplier<ExpressionGridCache> expressionGridCache,
                                   final ParameterizedCommand<Optional<Expression>> onHasExpressionChanged,
                                   final ParameterizedCommand<Optional<HasName>> onHasNameChanged,
                                   final Event<RefreshFormPropertiesEvent> refreshFormPropertiesEvent,
                                   final Event<DomainObjectSelectionEvent> domainObjectSelectionEvent) {
        super(Optional.empty(),
              HasExpression.NOP,
              Optional.empty(),
              gridLayer,
              new DMNGridData(),
              new ExpressionContainerRenderer(),
              sessionManager,
              sessionCommandManager,
              canvasCommandFactory,
              refreshFormPropertiesEvent,
              domainObjectSelectionEvent,
              cellEditorControls,
              translationService,
              false);
        this.expressionGridCache = expressionGridCache;
        this.onHasExpressionChanged = onHasExpressionChanged;
        this.onHasNameChanged = onHasNameChanged;
        this.onUndoClear = Optional.empty();

        this.uiModelMapper = new ExpressionContainerUIModelMapper(parent,
                                                                  this::getModel,
                                                                  getExpression(),
                                                                  () -> nodeUUID.get(),
                                                                  () -> hasExpression,
                                                                  () -> hasName,
                                                                  () -> isOnlyVisualChangeAllowed,
                                                                  expressionEditorDefinitions,
                                                                  expressionGridCache,
                                                                  listSelector);

        setEventPropagationMode(EventPropagationMode.NO_ANCESTORS);

        expressionColumn = new ExpressionEditorColumnWrapper(gridLayer,
                                                             new BaseHeaderMetaData(COLUMN_GROUP),
                                                             UndefinedExpressionColumn.DEFAULT_WIDTH,
                                                             this);
        expressionColumn.setMovable(false);
        expressionColumn.setResizable(true);

        model.appendColumn(expressionColumn);
        model.appendRow(new ExpressionEditorGridRow());

        getRenderer().setColumnRenderConstraint((isSelectionLayer, gridColumn) -> !isSelectionLayer || gridColumn.equals(expressionColumn));
    }

    public Optional<Consumer> getOnUndoClear() {
        return onUndoClear;
    }

    public void setOnUndoClear(final Optional<Consumer> onUndoClear) {
        this.onUndoClear = onUndoClear;
    }

    @Override
    public boolean onDragHandle(final INodeXYEvent event) {
        return false;
    }

    @Override
    public void deselect() {
        getModel().clearSelections();
        super.deselect();
    }

    public void setExpression(final String nodeUUID,
                              final HasExpression hasExpression,
                              final Optional<HasName> hasName,
                              final boolean isOnlyVisualChangeAllowed) {
        this.nodeUUID = Optional.of(nodeUUID);
        this.hasExpression = spyHasExpression(hasExpression);
        this.hasName = spyHasName(hasName);
        this.isOnlyVisualChangeAllowed = isOnlyVisualChangeAllowed;

        uiModelMapper.fromDMNModel(0, 0);

        expressionColumn.setWidthInternal(getExistingEditorWidth());
    }

    public void clearExpression(final String nodeUUID) {
        //Clear cache first as its content is used by the UIModelMapper to retrieve existing BaseExpressionGrid
        expressionGridCache.get().removeExpressionGrid(nodeUUID);

        //Use UIModelMapper to get cell value for null Expressions
        uiModelMapper.fromDMNModel(parent.getRowIndex(), parent.getColumnIndex());
    }

    double getExistingEditorWidth() {
        double existingWidth = DMNGridColumn.DEFAULT_WIDTH;
        final GridCell<?> cell = model.getRow(0).getCells().get(0);
        if (cell != null) {
            final GridCellValue<?> value = cell.getValue();
            if (value instanceof ExpressionCellValue) {
                final ExpressionCellValue ecv = (ExpressionCellValue) value;
                final Optional<BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper>> editor = ecv.getValue();
                if (editor.isPresent()) {
                    final BaseExpressionGrid beg = editor.get();
                    existingWidth = Collections.max(Arrays.asList(existingWidth,
                                                                  beg.getWidth() + beg.getPadding() * 2,
                                                                  beg.getMinimumWidth() + beg.getPadding() * 2));
                }
            }
        }
        return existingWidth;
    }

    /**
     * Proxy {@link HasExpression} to be able intercept interactions with the original
     * to update the expression label in {@link ExpressionEditorView} when the {@link Expression} changes.
     *
     * @param hasExpression A {@link HasExpression} to be proxied.
     * @return A proxy that intercepts interactions with the wrapped {@link HasExpression}
     */
    HasExpression spyHasExpression(final HasExpression hasExpression) {
        final HasExpression spy = new HasExpression() {
            @Override
            public Expression getExpression() {
                return hasExpression.getExpression();
            }

            @Override
            public void setExpression(final Expression expression) {
                hasExpression.setExpression(expression);
                onHasExpressionChanged.execute(Optional.ofNullable(expression));
            }

            @Override
            public DMNModelInstrumentedBase asDMNModelInstrumentedBase() {
                return hasExpression.asDMNModelInstrumentedBase();
            }

            @Override
            public boolean isClearSupported() {
                return hasExpression.isClearSupported();
            }
        };

        return spy;
    }

    /**
     * Proxy {@link HasName} to be able intercept interactions with the original to update the
     * navigation label in {@link ExpressionEditorView#setExpressionNameText(Optional)} when the {@link Name}
     * changes. The {@link Name} changes by a {@link SetHasValueCommand#execute(AbstractCanvasHandler)} or
     * {@link SetHasValueCommand#undo(AbstractCanvasHandler)} that ensures the {@link HasName#setName(Name)}
     * method is called.
     *
     * @param hasName A {@link HasName} to be proxied.
     * @return A proxy that intercepts interactions with the wrapped {@link HasName}
     */
    Optional<HasName> spyHasName(final Optional<HasName> hasName) {
        final HasName spy = new HasName() {
            @Override
            public Name getName() {
                return hasName.orElse(HasName.NOP).getName();
            }

            @Override
            public void setName(final Name name) {
                hasName.ifPresent(hn -> {
                    hn.setName(name);
                    if (hn instanceof HasVariable) {
                        final HasVariable hv = (HasVariable) hn;
                        hv.getVariable().setName(name);
                    }
                    onHasNameChanged.execute(hasName);
                });
            }
        };

        return Optional.of(spy);
    }

    @Override
    public List<ListSelectorItem> getItems(final int uiRowIndex,
                                           final int uiColumnIndex) {
        if (hasExpression.isClearSupported()) {
            return Collections.singletonList(ListSelectorTextItem
                                                     .build(translationService.format(DMNEditorConstants.ExpressionEditor_Clear),
                                                            true,
                                                            () -> {
                                                                cellEditorControls.hide();
                                                                clearExpressionType();
                                                            }));
        }
        return Collections.emptyList();
    }

    @Override
    public void onItemSelected(final ListSelectorItem item) {
        final ListSelectorTextItem li = (ListSelectorTextItem) item;
        li.getCommand().execute();
    }

    public void clearExpressionType() {
        sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                      new ClearExpressionTypeCommand(parent,
                                                                     nodeUUID.get(),
                                                                     hasExpression,
                                                                     uiModelMapper,
                                                                     expressionGridCache.get(),
                                                                     () -> {
                                                                         expressionColumn.setWidthInternal(getExistingEditorWidth());
                                                                         selectExpressionEditorFirstCell();
                                                                         getOnUndoClear().ifPresent(c -> c.accept(this));
                                                                     },
                                                                     () -> {
                                                                         expressionColumn.setWidthInternal(getExistingEditorWidth());
                                                                         selectExpressionEditorFirstCell();
                                                                         getOnUndoClear().ifPresent(c -> c.accept(this));
                                                                     }));
    }

    void selectExpressionEditorFirstCell() {
        final Optional<BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper>> grid = getBaseExpressionGrid();
        grid.ifPresent(beg -> {
            //It's not possible to set-up GridLayer for ExpressionContainerGrid in Unit Tests so defensively handle nulls
            Optional.ofNullable(getLayer()).ifPresent(layer -> ((DMNGridLayer) layer).select(beg));
            beg.selectFirstCell();
        });
    }

    public Optional<BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper>> getBaseExpressionGrid() {
        final GridCellValue<?> value = model.getCell(0, 0).getValue();
        return ((ExpressionCellValue) value).getValue();
    }

    @Override
    public boolean selectCell(final Point2D ap,
                              final boolean isShiftKeyDown,
                              final boolean isControlKeyDown) {
        gridLayer.select(this);
        fireDomainObjectSelectionEvent();
        return super.selectCell(ap,
                                isShiftKeyDown,
                                isControlKeyDown);
    }

    @Override
    public boolean selectCell(final int uiRowIndex,
                              final int uiColumnIndex,
                              final boolean isShiftKeyDown,
                              final boolean isControlKeyDown) {
        gridLayer.select(this);
        fireDomainObjectSelectionEvent();
        return super.selectCell(uiRowIndex,
                                uiColumnIndex,
                                isShiftKeyDown,
                                isControlKeyDown);
    }
}
