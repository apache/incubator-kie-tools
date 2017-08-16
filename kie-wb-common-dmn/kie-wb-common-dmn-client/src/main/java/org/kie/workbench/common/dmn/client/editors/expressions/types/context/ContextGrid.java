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

package org.kie.workbench.common.dmn.client.editors.expressions.types.context;

import java.util.Optional;
import java.util.function.Supplier;

import javax.enterprise.event.Event;

import com.ait.lienzo.shared.core.types.EventPropagationMode;
import org.jboss.errai.common.client.api.IsElement;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.Context;
import org.kie.workbench.common.dmn.api.definition.v1_1.ContextEntry;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.client.commands.expressions.types.context.AddContextEntryCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.events.ExpressionEditorSelectedEvent;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextBoxSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridRow;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseHeaderMetaData;

public class ContextGrid extends BaseExpressionGrid<Context, ContextUIModelMapper> implements ContextGridControls.Presenter {

    private static final String EXPRESSION_COLUMN_GROUP = "ContextGrid$ExpressionColumn1";

    private Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;

    private ContextGridControls controls;

    public ContextGrid(final GridCellTuple parent,
                       final HasExpression hasExpression,
                       final Optional<Context> expression,
                       final Optional<HasName> hasName,
                       final DMNGridPanel gridPanel,
                       final DMNGridLayer gridLayer,
                       final SessionManager sessionManager,
                       final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                       final Event<ExpressionEditorSelectedEvent> editorSelectedEvent,
                       final Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier,
                       final ContextGridControls controls,
                       final boolean nested) {
        super(parent,
              hasExpression,
              expression,
              hasName,
              gridPanel,
              gridLayer,
              new ContextGridData(new DMNGridData(gridLayer),
                                  sessionManager,
                                  sessionCommandManager,
                                  expression,
                                  gridLayer::batch),
              new ContextGridRenderer(nested),
              sessionManager,
              sessionCommandManager,
              editorSelectedEvent);
        this.expressionEditorDefinitionsSupplier = expressionEditorDefinitionsSupplier;
        this.controls = controls;

        setEventPropagationMode(EventPropagationMode.NO_ANCESTORS);

        super.doInitialisation();

        controls.init(this);
    }

    @Override
    protected void doInitialisation() {
        // Defer initialisation until after the constructor completes as
        // makeUiModelMapper needs expressionEditorDefinitionsSupplier to have been set
    }

    @Override
    public ContextUIModelMapper makeUiModelMapper() {
        return new ContextUIModelMapper(this::getModel,
                                        () -> expression,
                                        expressionEditorDefinitionsSupplier);
    }

    @Override
    public void initialiseUiColumns() {
        final TextBoxSingletonDOMElementFactory factory = new TextBoxSingletonDOMElementFactory(gridPanel,
                                                                                                gridLayer,
                                                                                                this,
                                                                                                sessionManager,
                                                                                                sessionCommandManager,
                                                                                                newCellHasNoValueCommand(),
                                                                                                newCellHasValueCommand());
        final TextBoxSingletonDOMElementFactory headerFactory = new TextBoxSingletonDOMElementFactory(gridPanel,
                                                                                                      gridLayer,
                                                                                                      this,
                                                                                                      sessionManager,
                                                                                                      sessionCommandManager,
                                                                                                      newHeaderHasNoValueCommand(),
                                                                                                      newHeaderHasValueCommand());

        final NameColumn nameColumn = new NameColumn(new NameColumnHeaderMetaData(() -> hasName.orElse(HasName.NOP).getName().getValue(),
                                                                                  (s) -> hasName.orElse(HasName.NOP).getName().setValue(s),
                                                                                  headerFactory),
                                                     factory,
                                                     this);
        model.appendColumn(nameColumn);

        final ExpressionEditorColumn expressionColumn = new ExpressionEditorColumn(new BaseHeaderMetaData("",
                                                                                                          EXPRESSION_COLUMN_GROUP),
                                                                                   this);
        model.appendColumn(expressionColumn);

        getRenderer().setColumnRenderConstraint((isSelectionLayer, gridColumn) -> !isSelectionLayer || gridColumn.equals(expressionColumn));
    }

    @Override
    public void initialiseUiModel() {
        expression.ifPresent(c -> {
            c.getContextEntry().stream().forEach(ce -> {
                model.appendRow(new DMNGridRow());
                uiModelMapper.fromDMNModel(model.getRowCount() - 1,
                                           0);
                uiModelMapper.fromDMNModel(model.getRowCount() - 1,
                                           1);
            });
        });
    }

    @Override
    public Optional<IsElement> getEditorControls() {
        return Optional.of(controls);
    }

    @Override
    public void addContextEntry() {
        expression.ifPresent(c -> {
            final ContextEntry ce = new ContextEntry();
            ce.setVariable(new InformationItem());
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new AddContextEntryCommand(c,
                                                                     ce,
                                                                     model,
                                                                     new DMNGridRow(),
                                                                     uiModelMapper,
                                                                     () -> {
                                                                         gridPanel.refreshScrollPosition();
                                                                         gridPanel.updatePanelSize();
                                                                         gridLayer.batch();
                                                                     }));
        });
    }
}
