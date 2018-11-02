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

package org.kie.workbench.common.dmn.client.editors.expressions.types.dtable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.enterprise.event.Event;

import com.ait.lienzo.shared.core.types.EventPropagationMode;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.definition.v1_1.BuiltinAggregator;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionRule;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionTableOrientation;
import org.kie.workbench.common.dmn.api.definition.v1_1.HitPolicy;
import org.kie.workbench.common.dmn.api.definition.v1_1.InputClause;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.v1_1.OutputClause;
import org.kie.workbench.common.dmn.api.definition.v1_1.UnaryTests;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.AddDecisionRuleCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.AddInputClauseCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.AddOutputClauseCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.DeleteDecisionRuleCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.DeleteInputClauseCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.DeleteOutputClauseCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.SetBuiltinAggregatorCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.SetHitPolicyCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.SetOrientationCommand;
import org.kie.workbench.common.dmn.client.commands.general.DeleteHasNameCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetHasNameCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetTypeRefCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.hitpolicy.HasHitPolicyControl;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.hitpolicy.HitPolicyPopoverView;
import org.kie.workbench.common.dmn.client.editors.expressions.util.SelectionUtils;
import org.kie.workbench.common.dmn.client.editors.types.NameAndDataTypePopoverView;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextAreaSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextBoxSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridRow;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseHeaderMetaData;
import org.uberfire.mvp.Command;

public class DecisionTableGrid extends BaseExpressionGrid<DecisionTable, DecisionTableGridData, DecisionTableUIModelMapper> implements HasListSelectorControl,
                                                                                                                                       HasHitPolicyControl {

    public static final String DESCRIPTION_GROUP = "DecisionTable$Description";

    private final HitPolicyPopoverView.Presenter hitPolicyEditor;
    private final NameAndDataTypePopoverView.Presenter headerEditor;

    private final TextBoxSingletonDOMElementFactory textBoxFactory = getBodyTextBoxFactory();
    private final TextAreaSingletonDOMElementFactory textAreaFactory = getBodyTextAreaFactory();

    private class ListSelectorItemDefinition {

        private final String caption;
        private final boolean enabled;
        private final Command command;

        public ListSelectorItemDefinition(final String caption,
                                          final boolean enabled,
                                          final Command command) {
            this.caption = caption;
            this.enabled = enabled;
            this.command = command;
        }
    }

    public DecisionTableGrid(final GridCellTuple parent,
                             final Optional<String> nodeUUID,
                             final HasExpression hasExpression,
                             final Optional<DecisionTable> expression,
                             final Optional<HasName> hasName,
                             final DMNGridPanel gridPanel,
                             final DMNGridLayer gridLayer,
                             final DecisionTableGridData gridData,
                             final DefinitionUtils definitionUtils,
                             final SessionManager sessionManager,
                             final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                             final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory,
                             final Event<ExpressionEditorChanged> editorSelectedEvent,
                             final Event<DomainObjectSelectionEvent> domainObjectSelectionEvent,
                             final CellEditorControlsView.Presenter cellEditorControls,
                             final ListSelectorView.Presenter listSelector,
                             final TranslationService translationService,
                             final int nesting,
                             final HitPolicyPopoverView.Presenter hitPolicyEditor,
                             final NameAndDataTypePopoverView.Presenter headerEditor) {
        super(parent,
              nodeUUID,
              hasExpression,
              expression,
              hasName,
              gridPanel,
              gridLayer,
              gridData,
              new DecisionTableGridRenderer(),
              definitionUtils,
              sessionManager,
              sessionCommandManager,
              canvasCommandFactory,
              editorSelectedEvent,
              domainObjectSelectionEvent,
              cellEditorControls,
              listSelector,
              translationService,
              nesting);
        this.hitPolicyEditor = hitPolicyEditor;
        this.headerEditor = headerEditor;

        setEventPropagationMode(EventPropagationMode.NO_ANCESTORS);

        super.doInitialisation();
    }

    @Override
    protected void doInitialisation() {
        // Defer initialisation until after the constructor completes as
        // makeUiModelMapper needs expressionEditorDefinitionsSupplier to have been set
    }

    @Override
    public DecisionTableUIModelMapper makeUiModelMapper() {
        return new DecisionTableUIModelMapper(this::getModel,
                                              () -> expression,
                                              listSelector);
    }

    @Override
    public void initialiseUiColumns() {
        expression.ifPresent(e -> {
            model.appendColumn(new DecisionTableRowNumberColumn(e::getHitPolicy,
                                                                e::getAggregation,
                                                                cellEditorControls,
                                                                hitPolicyEditor,
                                                                Optional.of(translationService.getTranslation(DMNEditorConstants.DecisionTableEditor_EditHitPolicy)),
                                                                this));
            e.getInput().forEach(ic -> model.appendColumn(makeInputClauseColumn(ic)));
            e.getOutput().forEach(oc -> model.appendColumn(makeOutputClauseColumn(oc)));
            model.appendColumn(new DescriptionColumn(new BaseHeaderMetaData(translationService.format(DMNEditorConstants.DecisionTableEditor_DescriptionColumnHeader),
                                                                            DESCRIPTION_GROUP),
                                                     textBoxFactory,
                                                     this));
        });

        getRenderer().setColumnRenderConstraint((isSelectionLayer, gridColumn) -> !isSelectionLayer);
    }

    private InputClauseColumn makeInputClauseColumn(final InputClause ic) {
        final InputClauseColumn column = new InputClauseColumn(new InputClauseColumnHeaderMetaData(wrapInputClauseIntoHasName(ic),
                                                                                                   ic::getInputExpression,
                                                                                                   clearDisplayNameConsumer(false),
                                                                                                   setDisplayNameConsumer(false),
                                                                                                   setTypeRefConsumer(),
                                                                                                   cellEditorControls,
                                                                                                   headerEditor,
                                                                                                   Optional.of(translationService.getTranslation(DMNEditorConstants.DecisionTableEditor_EditInputClause))),
                                                               textAreaFactory,
                                                               this);
        return column;
    }

    private HasName wrapInputClauseIntoHasName(final InputClause inputClause) {
        return new HasName() {

            @Override
            public Name getName() {
                return new Name(inputClause.getInputExpression().getText().getValue());
            }

            @Override
            public void setName(final Name name) {
                inputClause.getInputExpression().getText().setValue(name.getValue());
            }
        };
    }

    private OutputClauseColumn makeOutputClauseColumn(final OutputClause oc) {
        final OutputClauseColumn column = new OutputClauseColumn(outputClauseHeaderMetaData(oc),
                                                                 textAreaFactory,
                                                                 this);
        return column;
    }

    private Supplier<List<GridColumn.HeaderMetaData>> outputClauseHeaderMetaData(final OutputClause oc) {
        return () -> {
            final List<GridColumn.HeaderMetaData> metaData = new ArrayList<>();
            expression.ifPresent(dtable -> {
                if (hasName.isPresent()) {
                    metaData.add(new OutputClauseColumnExpressionNameHeaderMetaData(hasExpression,
                                                                                    expression,
                                                                                    hasName,
                                                                                    clearDisplayNameConsumer(true, oc, dtable),
                                                                                    setDisplayNameConsumer(true, oc, dtable),
                                                                                    setTypeRefConsumer(oc, dtable),
                                                                                    cellEditorControls,
                                                                                    headerEditor,
                                                                                    Optional.of(translationService.getTranslation(DMNEditorConstants.DecisionTableEditor_EditOutputClause))));
                } else {
                    metaData.add(new BaseHeaderMetaData(translationService.format(DMNEditorConstants.DecisionTableEditor_OutputClauseHeader)));
                }
                if (dtable.getOutput().size() > 1) {
                    metaData.add(new OutputClauseColumnHeaderMetaData(wrapOutputClauseIntoHasName(oc),
                                                                      oc,
                                                                      clearDisplayNameConsumer(false),
                                                                      setDisplayNameConsumer(false),
                                                                      setTypeRefConsumer(),
                                                                      cellEditorControls,
                                                                      headerEditor,
                                                                      Optional.of(translationService.getTranslation(DMNEditorConstants.DecisionTableEditor_EditOutputClause))));
                }
            });
            return metaData;
        };
    }

    private Consumer<HasName> clearDisplayNameConsumer(final boolean updateStunnerTitle,
                                                       final OutputClause oc,
                                                       final DecisionTable dtable) {
        if (dtable.getOutput().size() == 1) {
            return clearDisplayNameOnHasExpressionAndOutputClauseConsumer(updateStunnerTitle, oc);
        }
        return clearDisplayNameConsumer(updateStunnerTitle);
    }

    @SuppressWarnings("unchecked")
    private Consumer<HasName> clearDisplayNameOnHasExpressionAndOutputClauseConsumer(final boolean updateStunnerTitle,
                                                                                     final OutputClause oc) {
        return (hn) -> {
            final CompositeCommand.Builder commandBuilder = newHasNameHasNoValueCommand(hn);
            commandBuilder.addCommand(new DeleteHasNameCommand(wrapOutputClauseIntoHasName(oc),
                                                               () -> {/*Nothing*/}));
            if (updateStunnerTitle) {
                getUpdateStunnerTitleCommand("").ifPresent(commandBuilder::addCommand);
            }
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          commandBuilder.build());
        };
    }

    private BiConsumer<HasName, Name> setDisplayNameConsumer(final boolean updateStunnerTitle,
                                                             final OutputClause oc,
                                                             final DecisionTable dtable) {
        if (dtable.getOutput().size() == 1) {
            return setDisplayNameOnHasExpressionAndOutputClauseConsumer(updateStunnerTitle, oc);
        }
        return setDisplayNameConsumer(updateStunnerTitle);
    }

    @SuppressWarnings("unchecked")
    private BiConsumer<HasName, Name> setDisplayNameOnHasExpressionAndOutputClauseConsumer(final boolean updateStunnerTitle,
                                                                                           final OutputClause oc) {
        return (hn, name) -> {
            final CompositeCommand.Builder commandBuilder = newHasNameHasValueCommand(hn, name);
            commandBuilder.addCommand(new SetHasNameCommand(wrapOutputClauseIntoHasName(oc),
                                                            name,
                                                            () -> {/*Nothing*/}));
            if (updateStunnerTitle) {
                getUpdateStunnerTitleCommand(name.getValue()).ifPresent(commandBuilder::addCommand);
            }
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          commandBuilder.build());
        };
    }

    private BiConsumer<HasTypeRef, QName> setTypeRefConsumer(final OutputClause oc,
                                                             final DecisionTable dtable) {
        if (dtable.getOutput().size() == 1) {
            return setTypeRefOnHasExpressionAndOutputClauseConsumer(oc);
        }
        return setTypeRefConsumer();
    }

    private BiConsumer<HasTypeRef, QName> setTypeRefOnHasExpressionAndOutputClauseConsumer(final OutputClause oc) {
        return (htr, typeRef) -> {
            final CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation> commandBuilder = new CompositeCommand.Builder<>();
            commandBuilder.addCommand(new SetTypeRefCommand(htr,
                                                            typeRef,
                                                            () -> {/*Nothing*/}));
            commandBuilder.addCommand(new SetTypeRefCommand(oc,
                                                            typeRef,
                                                            () -> {
                                                                gridLayer.batch();
                                                                selectedDomainObject.ifPresent(this::fireDomainObjectSelectionEvent);
                                                            }));

            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          commandBuilder.build());
        };
    }

    private HasName wrapOutputClauseIntoHasName(final OutputClause outputClause) {
        return new HasName() {

            @Override
            public Name getName() {
                return new Name(outputClause.getName());
            }

            @Override
            public void setName(final Name name) {
                outputClause.setName(name.getValue());
            }
        };
    }

    @Override
    public void initialiseUiModel() {
        expression.ifPresent(e -> {
            e.getRule().forEach(r -> {
                int columnIndex = 0;
                model.appendRow(new DMNGridRow());
                uiModelMapper.fromDMNModel(model.getRowCount() - 1,
                                           columnIndex++);
                for (int ici = 0; ici < e.getInput().size(); ici++) {
                    uiModelMapper.fromDMNModel(model.getRowCount() - 1,
                                               columnIndex++);
                }
                for (int oci = 0; oci < e.getOutput().size(); oci++) {
                    uiModelMapper.fromDMNModel(model.getRowCount() - 1,
                                               columnIndex++);
                }
                uiModelMapper.fromDMNModel(model.getRowCount() - 1,
                                           columnIndex);
            });
        });
    }

    @Override
    protected boolean isHeaderHidden() {
        return false;
    }

    @Override
    @SuppressWarnings("unused")
    public java.util.List<ListSelectorItem> getItems(final int uiRowIndex,
                                                     final int uiColumnIndex) {
        final java.util.List<ListSelectorItem> items = new ArrayList<>();
        final boolean isMultiColumn = SelectionUtils.isMultiColumn(model);

        getExpression().ifPresent(dtable -> {
            final DecisionTableUIModelMapperHelper.DecisionTableSection section = DecisionTableUIModelMapperHelper.getSection(dtable, uiColumnIndex);
            switch (section) {
                case INPUT_CLAUSES:
                    addItems(items,
                             new ListSelectorItemDefinition(translationService.format(DMNEditorConstants.DecisionTableEditor_InsertInputClauseLeft),
                                                            !isMultiColumn,
                                                            () -> addInputClause(uiColumnIndex)),
                             new ListSelectorItemDefinition(translationService.format(DMNEditorConstants.DecisionTableEditor_InsertInputClauseRight),
                                                            !isMultiColumn,
                                                            () -> addInputClause(uiColumnIndex + 1)),
                             new ListSelectorItemDefinition(translationService.format(DMNEditorConstants.DecisionTableEditor_DeleteInputClause),
                                                            !isMultiColumn && dtable.getInput().size() > 1,
                                                            () -> deleteInputClause(uiColumnIndex)));
                    items.add(new ListSelectorDividerItem());
                    addDecisionRuleItems(dtable,
                                         items,
                                         uiRowIndex);
                    break;

                case OUTPUT_CLAUSES:
                    addItems(items,
                             new ListSelectorItemDefinition(translationService.format(DMNEditorConstants.DecisionTableEditor_InsertOutputClauseLeft),
                                                            !isMultiColumn,
                                                            () -> addOutputClause(uiColumnIndex)),
                             new ListSelectorItemDefinition(translationService.format(DMNEditorConstants.DecisionTableEditor_InsertOutputClauseRight),
                                                            !isMultiColumn,
                                                            () -> addOutputClause(uiColumnIndex + 1)),
                             new ListSelectorItemDefinition(translationService.format(DMNEditorConstants.DecisionTableEditor_DeleteOutputClause),
                                                            !isMultiColumn && dtable.getOutput().size() > 1,
                                                            () -> deleteOutputClause(uiColumnIndex)));
                    items.add(new ListSelectorDividerItem());
                    addDecisionRuleItems(dtable,
                                         items,
                                         uiRowIndex);
                    break;

                default:
                    addDecisionRuleItems(dtable,
                                         items,
                                         uiRowIndex);
            }
        });

        return items;
    }

    void addItems(final java.util.List<ListSelectorItem> items,
                  final ListSelectorItemDefinition onBefore,
                  final ListSelectorItemDefinition onAfter,
                  final ListSelectorItemDefinition onDelete) {
        items.add(ListSelectorTextItem.build(onBefore.caption,
                                             onBefore.enabled,
                                             () -> {
                                                 cellEditorControls.hide();
                                                 onBefore.command.execute();
                                             }));
        items.add(ListSelectorTextItem.build(onAfter.caption,
                                             onAfter.enabled,
                                             () -> {
                                                 cellEditorControls.hide();
                                                 onAfter.command.execute();
                                             }));
        items.add(ListSelectorTextItem.build(onDelete.caption,
                                             onDelete.enabled,
                                             () -> {
                                                 cellEditorControls.hide();
                                                 onDelete.command.execute();
                                             }));
    }

    void addDecisionRuleItems(final DecisionTable dtable,
                              final java.util.List<ListSelectorItem> items,
                              final int uiRowIndex) {
        final boolean isMultiRow = SelectionUtils.isMultiRow(model);

        addItems(items,
                 new ListSelectorItemDefinition(translationService.format(DMNEditorConstants.DecisionTableEditor_InsertDecisionRuleAbove),
                                                !isMultiRow,
                                                () -> addDecisionRule(uiRowIndex)),
                 new ListSelectorItemDefinition(translationService.format(DMNEditorConstants.DecisionTableEditor_InsertDecisionRuleBelow),
                                                !isMultiRow,
                                                () -> addDecisionRule(uiRowIndex + 1)),
                 new ListSelectorItemDefinition(translationService.format(DMNEditorConstants.DecisionTableEditor_DeleteDecisionRule),
                                                !isMultiRow && dtable.getRule().size() > 1,
                                                () -> deleteDecisionRule(uiRowIndex)));
    }

    @Override
    public void onItemSelected(final ListSelectorItem item) {
        final ListSelectorTextItem li = (ListSelectorTextItem) item;
        li.getCommand().execute();
    }

    void addInputClause(final int index) {
        expression.ifPresent(dtable -> {
            final InputClause clause = new InputClause();
            final InputClauseColumn inputClauseColumn = makeInputClauseColumn(clause);

            final CommandResult<CanvasViolation> result = sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                                                                        new AddInputClauseCommand(dtable,
                                                                                                                  clause,
                                                                                                                  model,
                                                                                                                  inputClauseColumn,
                                                                                                                  index,
                                                                                                                  uiModelMapper,
                                                                                                                  () -> resize(BaseExpressionGrid.RESIZE_EXISTING),
                                                                                                                  () -> resize(BaseExpressionGrid.RESIZE_EXISTING_MINIMUM)));

            if (!CommandUtils.isError(result)) {
                selectHeaderCell(0, index, false, false);
                inputClauseColumn.startEditingHeaderCell(0);
            }
        });
    }

    void deleteInputClause(final int index) {
        expression.ifPresent(dtable -> {
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new DeleteInputClauseCommand(dtable,
                                                                       model,
                                                                       index,
                                                                       uiModelMapper,
                                                                       () -> resize(BaseExpressionGrid.RESIZE_EXISTING_MINIMUM),
                                                                       () -> resize(BaseExpressionGrid.RESIZE_EXISTING)));
        });
    }

    void addOutputClause(final int index) {
        expression.ifPresent(dtable -> {
            final OutputClause clause = new OutputClause();
            final OutputClauseColumn outputClauseColumn = makeOutputClauseColumn(clause);

            final CommandResult<CanvasViolation> result = sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                                                                        new AddOutputClauseCommand(dtable,
                                                                                                                   clause,
                                                                                                                   model,
                                                                                                                   outputClauseColumn,
                                                                                                                   index,
                                                                                                                   uiModelMapper,
                                                                                                                   () -> resize(BaseExpressionGrid.RESIZE_EXISTING),
                                                                                                                   () -> resize(BaseExpressionGrid.RESIZE_EXISTING_MINIMUM)));

            if (!CommandUtils.isError(result)) {
                selectHeaderCell(1, index, false, false);
                outputClauseColumn.startEditingHeaderCell(1);
            }
        });
    }

    void deleteOutputClause(final int index) {
        expression.ifPresent(dtable -> {
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new DeleteOutputClauseCommand(dtable,
                                                                        model,
                                                                        index,
                                                                        uiModelMapper,
                                                                        () -> resize(BaseExpressionGrid.RESIZE_EXISTING_MINIMUM),
                                                                        () -> resize(BaseExpressionGrid.RESIZE_EXISTING)));
        });
    }

    void addDecisionRule(final int index) {
        expression.ifPresent(dtable -> {
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new AddDecisionRuleCommand(dtable,
                                                                     new DecisionRule(),
                                                                     model,
                                                                     new DMNGridRow(),
                                                                     index,
                                                                     uiModelMapper,
                                                                     () -> resize(BaseExpressionGrid.RESIZE_EXISTING)));
        });
    }

    void deleteDecisionRule(final int index) {
        expression.ifPresent(dtable -> {
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new DeleteDecisionRuleCommand(dtable,
                                                                        model,
                                                                        index,
                                                                        () -> resize(BaseExpressionGrid.RESIZE_EXISTING)));
        });
    }

    @Override
    public HitPolicy getHitPolicy() {
        return expression.orElseThrow(() -> new IllegalArgumentException("DecisionTable has not been set.")).getHitPolicy();
    }

    @Override
    public BuiltinAggregator getBuiltinAggregator() {
        return expression.orElseThrow(() -> new IllegalArgumentException("DecisionTable has not been set.")).getAggregation();
    }

    @Override
    public DecisionTableOrientation getDecisionTableOrientation() {
        return expression.orElseThrow(() -> new IllegalArgumentException("DecisionTable has not been set.")).getPreferredOrientation();
    }

    @Override
    public void setHitPolicy(final HitPolicy hitPolicy,
                             final Command onSuccess) {
        expression.ifPresent(dtable -> {
            final CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation> commandBuilder = new CompositeCommand.Builder<>();
            commandBuilder.addCommand(new SetBuiltinAggregatorCommand(dtable,
                                                                      null,
                                                                      gridLayer::batch));
            commandBuilder.addCommand(new SetHitPolicyCommand(dtable,
                                                              hitPolicy,
                                                              () -> {
                                                                  gridLayer.batch();
                                                                  onSuccess.execute();
                                                              }));

            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          commandBuilder.build());
        });
    }

    @Override
    public void setBuiltinAggregator(final BuiltinAggregator aggregator) {
        expression.ifPresent(dtable -> {
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new SetBuiltinAggregatorCommand(dtable,
                                                                          aggregator,
                                                                          gridLayer::batch));
        });
    }

    @Override
    public void setDecisionTableOrientation(final DecisionTableOrientation orientation) {
        expression.ifPresent(dtable -> {
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new SetOrientationCommand(dtable,
                                                                    orientation,
                                                                    gridLayer::batch));
        });
    }

    @Override
    protected void doAfterSelectionChange(final int uiRowIndex,
                                          final int uiColumnIndex) {
        if (hasAnyHeaderCellSelected() || hasMultipleCellsSelected()) {
            super.doAfterSelectionChange(uiRowIndex, uiColumnIndex);
            return;
        }

        if (expression.isPresent()) {
            final DecisionTable dtable = expression.get();
            final DecisionTableUIModelMapperHelper.DecisionTableSection section = DecisionTableUIModelMapperHelper.getSection(dtable, uiColumnIndex);
            switch (section) {
                case INPUT_CLAUSES:
                    final int icIndex = DecisionTableUIModelMapperHelper.getInputEntryIndex(dtable, uiColumnIndex);
                    final UnaryTests unaryTests = dtable.getRule().get(uiRowIndex).getInputEntry().get(icIndex);
                    fireDomainObjectSelectionEvent(unaryTests);
                    return;
                case OUTPUT_CLAUSES:
                    final int ocIndex = DecisionTableUIModelMapperHelper.getOutputEntryIndex(dtable, uiColumnIndex);
                    final LiteralExpression literalExpression = dtable.getRule().get(uiRowIndex).getOutputEntry().get(ocIndex);
                    fireDomainObjectSelectionEvent(literalExpression);
                    return;
            }
        }
        super.doAfterSelectionChange(uiRowIndex, uiColumnIndex);
    }

    @Override
    protected void doAfterHeaderSelectionChange(final int uiHeaderRowIndex,
                                                final int uiHeaderColumnIndex) {
        if (expression.isPresent()) {
            final DecisionTable dtable = expression.get();
            final DecisionTableUIModelMapperHelper.DecisionTableSection section = DecisionTableUIModelMapperHelper.getSection(dtable, uiHeaderColumnIndex);
            switch (section) {
                case INPUT_CLAUSES:
                    final int icIndex = DecisionTableUIModelMapperHelper.getInputEntryIndex(dtable, uiHeaderColumnIndex);
                    final InputClause inputClause = dtable.getInput().get(icIndex);
                    fireDomainObjectSelectionEvent(inputClause);
                    return;
                case OUTPUT_CLAUSES:
                    final List<GridColumn.HeaderMetaData> headerMetaData = model.getColumns().get(uiHeaderColumnIndex).getHeaderMetaData();
                    if (headerMetaData.size() > 1) {
                        if (uiHeaderRowIndex == 0) {
                            final DMNModelInstrumentedBase base = hasExpression.asDMNModelInstrumentedBase();
                            if (base instanceof DomainObject) {
                                fireDomainObjectSelectionEvent((DomainObject) base);
                                return;
                            }
                        }
                    }
                    final int ocIndex = DecisionTableUIModelMapperHelper.getOutputEntryIndex(dtable, uiHeaderColumnIndex);
                    final OutputClause outputClause = dtable.getOutput().get(ocIndex);
                    fireDomainObjectSelectionEvent(outputClause);
                    return;
            }
        }
        super.doAfterHeaderSelectionChange(uiHeaderRowIndex, uiHeaderColumnIndex);
    }
}
