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

package org.kie.workbench.common.dmn.client.editors.expressions;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import javax.enterprise.event.Event;

import com.ait.lienzo.client.core.event.INodeXYEvent;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.EventPropagationMode;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.NOPDomainObject;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.commands.general.ClearExpressionTypeCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetHasNameCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionEditorColumn;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.ExpressionGridCache;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridColumn;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridRow;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseHeaderMetaData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.BaseGridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;
import org.uberfire.mvp.ParameterizedCommand;

public class ExpressionContainerGrid extends BaseGridWidget implements HasListSelectorControl {

    private static final String COLUMN_GROUP = "ExpressionContainerGrid$Expression0";

    private final GridLayer gridLayer;
    private final CellEditorControlsView.Presenter cellEditorControls;
    private final TranslationService translationService;

    private final SessionManager sessionManager;
    private final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    private final Supplier<ExpressionGridCache> expressionGridCache;
    private final GridCellTuple parent = new GridCellTuple(0, 0, this);
    private final GridColumn expressionColumn;

    private String nodeUUID;
    private Optional<HasName> hasName = Optional.empty();
    private HasExpression hasExpression;

    private final ParameterizedCommand<Optional<Expression>> onHasExpressionChanged;
    private final ParameterizedCommand<Optional<HasName>> onHasNameChanged;
    private final Event<DomainObjectSelectionEvent> domainObjectSelectionEvent;

    private ExpressionContainerUIModelMapper uiModelMapper;

    public ExpressionContainerGrid(final DMNGridLayer gridLayer,
                                   final CellEditorControlsView.Presenter cellEditorControls,
                                   final TranslationService translationService,
                                   final ListSelectorView.Presenter listSelector,
                                   final SessionManager sessionManager,
                                   final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                   final Supplier<ExpressionEditorDefinitions> expressionEditorDefinitions,
                                   final Supplier<ExpressionGridCache> expressionGridCache,
                                   final ParameterizedCommand<Optional<Expression>> onHasExpressionChanged,
                                   final ParameterizedCommand<Optional<HasName>> onHasNameChanged,
                                   final Event<DomainObjectSelectionEvent> domainObjectSelectionEvent) {
        super(new DMNGridData(),
              gridLayer,
              gridLayer,
              new ExpressionContainerRenderer());
        this.gridLayer = gridLayer;
        this.cellEditorControls = cellEditorControls;
        this.translationService = translationService;
        this.sessionManager = sessionManager;
        this.sessionCommandManager = sessionCommandManager;
        this.expressionGridCache = expressionGridCache;

        this.onHasExpressionChanged = onHasExpressionChanged;
        this.onHasNameChanged = onHasNameChanged;
        this.domainObjectSelectionEvent = domainObjectSelectionEvent;

        this.uiModelMapper = new ExpressionContainerUIModelMapper(parent,
                                                                  this::getModel,
                                                                  () -> Optional.ofNullable(hasExpression.getExpression()),
                                                                  () -> nodeUUID,
                                                                  () -> hasExpression,
                                                                  () -> hasName,
                                                                  expressionEditorDefinitions,
                                                                  expressionGridCache,
                                                                  listSelector);

        setEventPropagationMode(EventPropagationMode.NO_ANCESTORS);

        expressionColumn = new ExpressionEditorColumn(gridLayer,
                                                      new BaseHeaderMetaData(COLUMN_GROUP),
                                                      this);
        expressionColumn.setMovable(false);
        expressionColumn.setResizable(true);

        model.appendColumn(expressionColumn);
        model.appendRow(new DMNGridRow());

        getRenderer().setColumnRenderConstraint((isSelectionLayer, gridColumn) -> !isSelectionLayer || gridColumn.equals(expressionColumn));
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
                              final Optional<HasName> hasName) {
        this.nodeUUID = nodeUUID;
        this.hasExpression = spyHasExpression(hasExpression);
        this.hasName = spyHasName(hasName);

        uiModelMapper.fromDMNModel(0, 0);

        expressionColumn.setWidth(getExistingEditorWidth());
        selectExpressionEditorFirstCell();
    }

    double getExistingEditorWidth() {
        double existingWidth = DMNGridColumn.DEFAULT_WIDTH;
        final GridCell<?> cell = model.getRow(0).getCells().get(0);
        if (cell != null) {
            final GridCellValue<?> value = cell.getValue();
            if (value instanceof ExpressionCellValue) {
                final ExpressionCellValue ecv = (ExpressionCellValue) value;
                final Optional<BaseExpressionGrid> editor = ecv.getValue();
                if (editor.isPresent()) {
                    final BaseExpressionGrid beg = editor.get();
                    existingWidth = Math.max(existingWidth,
                                             beg.getWidth() + beg.getPadding() * 2);
                }
            }
        }
        return existingWidth;
    }

    /**
     * Proxy {@link HasExpression} to be able intercept interactions with the original
     * to update the expression label in {@link ExpressionEditorView} when the {@link Expression} changes.
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
     * navigation label in {@link ExpressionEditorView#setReturnToDRGText(Optional)} when the {@link Name}
     * changes. The {@link Name} changes by a {@link SetHasNameCommand#execute(AbstractCanvasHandler)} or
     * {@link SetHasNameCommand#undo(AbstractCanvasHandler)} that ensures the {@link HasName#setName(Name)}
     * method is called.
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

    void clearExpressionType() {
        sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                      new ClearExpressionTypeCommand(parent,
                                                                     nodeUUID,
                                                                     hasExpression,
                                                                     uiModelMapper,
                                                                     expressionGridCache.get(),
                                                                     () -> {
                                                                         expressionColumn.setWidth(getExistingEditorWidth());
                                                                         selectExpressionEditorFirstCell();
                                                                     },
                                                                     () -> {
                                                                         expressionColumn.setWidth(getExistingEditorWidth());
                                                                         selectExpressionEditorFirstCell();
                                                                     }));
    }

    void selectExpressionEditorFirstCell() {
        final GridCellValue<?> value = model.getCell(0, 0).getValue();
        final Optional<BaseExpressionGrid> grid = ((ExpressionCellValue) value).getValue();
        grid.ifPresent(beg -> {
            //It's not possible to set-up GridLayer for ExpressionContainerGrid in Unit Tests so defensively handle nulls
            Optional.ofNullable(getLayer()).ifPresent(layer -> ((DMNGridLayer) layer).select(beg));
            beg.selectFirstCell();
        });
    }

    @Override
    public boolean selectCell(final Point2D ap,
                              final boolean isShiftKeyDown,
                              final boolean isControlKeyDown) {
        gridLayer.select(this);
        fireRefreshFormPropertiesEvent();
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
        fireRefreshFormPropertiesEvent();
        return super.selectCell(uiRowIndex,
                                uiColumnIndex,
                                isShiftKeyDown,
                                isControlKeyDown);
    }

    protected void fireRefreshFormPropertiesEvent() {
        final ClientSession session = sessionManager.getCurrentSession();
        if (session != null) {
            final CanvasHandler canvasHandler = session.getCanvasHandler();
            if (canvasHandler != null) {
                final DMNModelInstrumentedBase base = hasExpression.asDMNModelInstrumentedBase();
                if (base instanceof DomainObject) {
                    domainObjectSelectionEvent.fire(new DomainObjectSelectionEvent(canvasHandler, (DomainObject) base));
                    return;
                }
                domainObjectSelectionEvent.fire(new DomainObjectSelectionEvent(canvasHandler, new NOPDomainObject()));
            }
        }
    }
}
