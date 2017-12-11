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

package org.kie.workbench.common.dmn.client.editors.expressions.types.relation;

import java.util.Optional;

import javax.enterprise.event.Event;

import com.ait.lienzo.shared.core.types.EventPropagationMode;
import org.jboss.errai.common.client.api.IsElement;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.api.definition.v1_1.List;
import org.kie.workbench.common.dmn.api.definition.v1_1.Relation;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.commands.expressions.types.relation.AddRelationColumnCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.relation.AddRelationRowCommand;
import org.kie.workbench.common.dmn.client.events.ExpressionEditorSelectedEvent;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextAreaSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextBoxSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridRow;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;

public class RelationGrid extends BaseExpressionGrid<Relation, RelationUIModelMapper> implements RelationGridControls.Presenter {

    private final RelationGridControls controls;

    private final TextAreaSingletonDOMElementFactory factory;
    private final TextBoxSingletonDOMElementFactory headerFactory;

    public RelationGrid(final GridCellTuple parent,
                        final HasExpression hasExpression,
                        final Optional<Relation> expression,
                        final Optional<HasName> hasName,
                        final DMNGridPanel gridPanel,
                        final DMNGridLayer gridLayer,
                        final SessionManager sessionManager,
                        final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                        final Event<ExpressionEditorSelectedEvent> editorSelectedEvent,
                        final RelationGridControls controls) {
        super(parent,
              hasExpression,
              expression,
              hasName,
              gridPanel,
              gridLayer,
              new RelationGridData(new DMNGridData(gridLayer),
                                   sessionManager,
                                   sessionCommandManager,
                                   expression,
                                   gridLayer::batch),
              new RelationGridRenderer(),
              sessionManager,
              sessionCommandManager,
              editorSelectedEvent);
        this.controls = controls;

        this.factory = new TextAreaSingletonDOMElementFactory(gridPanel,
                                                              gridLayer,
                                                              this,
                                                              sessionManager,
                                                              sessionCommandManager,
                                                              newCellHasNoValueCommand(),
                                                              newCellHasValueCommand());
        this.headerFactory = new TextBoxSingletonDOMElementFactory(gridPanel,
                                                                   gridLayer,
                                                                   this,
                                                                   sessionManager,
                                                                   sessionCommandManager,
                                                                   newHeaderHasNoValueCommand(),
                                                                   newHeaderHasValueCommand());

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
    public RelationUIModelMapper makeUiModelMapper() {
        return new RelationUIModelMapper(this::getModel,
                                         () -> expression);
    }

    @Override
    public void initialiseUiColumns() {
        expression.ifPresent(e -> {
            model.appendColumn(new RowNumberColumn());
            e.getColumn().forEach(ii -> {
                final GridColumn relationColumn = makeRelationColumn(ii);
                model.appendColumn(relationColumn);
            });
        });

        getRenderer().setColumnRenderConstraint((isSelectionLayer, gridColumn) -> true);
    }

    private RelationColumn makeRelationColumn(final InformationItem informationItem) {
        final RelationColumn relationColumn = new RelationColumn(new RelationColumnHeaderMetaData(() -> informationItem.getName().getValue(),
                                                                                                  (s) -> informationItem.getName().setValue(s),
                                                                                                  headerFactory),
                                                                 factory,
                                                                 this);
        return relationColumn;
    }

    @Override
    public void initialiseUiModel() {
        expression.ifPresent(e -> {
            e.getRow().forEach(r -> {
                int columnIndex = 0;
                model.appendRow(new DMNGridRow());
                uiModelMapper.fromDMNModel(model.getRowCount() - 1,
                                           columnIndex++);
                for (int ii = 0; ii < e.getColumn().size(); ii++) {
                    uiModelMapper.fromDMNModel(model.getRowCount() - 1,
                                               columnIndex++);
                }
            });
        });
    }

    @Override
    public Optional<IsElement> getEditorControls() {
        return Optional.of(controls);
    }

    @Override
    public void addColumn() {
        expression.ifPresent(relation -> {
            final InformationItem informationItem = new InformationItem();
            informationItem.setName(new Name("Column"));
            final RelationColumn relationColumn = makeRelationColumn(informationItem);

            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new AddRelationColumnCommand(relation,
                                                                       informationItem,
                                                                       model,
                                                                       relationColumn,
                                                                       uiModelMapper,
                                                                       () -> {
                                                                           relationColumn.updateWidthOfPeers();
                                                                           gridPanel.refreshScrollPosition();
                                                                           gridPanel.updatePanelSize();
                                                                           gridLayer.batch();
                                                                       }));
        });
    }

    @Override
    public void addRow() {
        expression.ifPresent(relation -> {
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new AddRelationRowCommand(relation,
                                                                    new List(),
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
