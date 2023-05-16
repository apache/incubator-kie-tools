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
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.enterprise.event.Event;

import com.ait.lienzo.shared.core.types.EventPropagationMode;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.definition.model.BuiltinAggregator;
import org.kie.workbench.common.dmn.api.definition.model.ContextEntry;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.model.DecisionRule;
import org.kie.workbench.common.dmn.api.definition.model.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.model.HitPolicy;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.definition.model.InputClause;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.OutputClause;
import org.kie.workbench.common.dmn.api.definition.model.RuleAnnotationClause;
import org.kie.workbench.common.dmn.api.definition.model.UnaryTests;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.AddDecisionRuleCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.AddInputClauseCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.AddOutputClauseCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.AddRuleAnnotationClauseCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.DeleteDecisionRuleCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.DeleteInputClauseCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.DeleteOutputClauseCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.DeleteRuleAnnotationClauseCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.SetBuiltinAggregatorCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.SetHitPolicyCommand;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.dmn.client.commands.general.DeleteHasValueCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetHasValueCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetTypeRefCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ContextUIModelMapper;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.hitpolicy.HasHitPolicyControl;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.hitpolicy.HitPolicyPopoverView;
import org.kie.workbench.common.dmn.client.editors.expressions.util.SelectionUtils;
import org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverView;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextAreaSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridColumn;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.grid.model.LiteralExpressionGridRow;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.ReadOnlyProvider;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.util.CellContextUtilities;
import org.uberfire.mvp.Command;

import static org.kie.workbench.common.dmn.client.editors.expressions.util.RendererUtils.getExpressionTextLineHeight;
import static org.uberfire.ext.wires.core.grids.client.util.ColumnIndexUtilities.getHeaderBlockEndColumnIndex;
import static org.uberfire.ext.wires.core.grids.client.util.ColumnIndexUtilities.getHeaderBlockStartColumnIndex;

public class DecisionTableGrid extends BaseExpressionGrid<DecisionTable, DecisionTableGridData, DecisionTableUIModelMapper> implements HasListSelectorControl,
                                                                                                                                       HasHitPolicyControl {

    private final HitPolicyPopoverView.Presenter hitPolicyEditor;
    private final ManagedInstance<ValueAndDataTypePopoverView.Presenter> headerEditors;

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
                             final Optional<HasName> hasName,
                             final DMNGridPanel gridPanel,
                             final DMNGridLayer gridLayer,
                             final DecisionTableGridData gridData,
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
                             final HitPolicyPopoverView.Presenter hitPolicyEditor,
                             final ManagedInstance<ValueAndDataTypePopoverView.Presenter> headerEditors,
                             final ReadOnlyProvider readOnlyProvider) {
        super(parent,
              nodeUUID,
              hasExpression,
              hasName,
              gridPanel,
              gridLayer,
              gridData,
              new DecisionTableGridRenderer(gridData),
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
        this.hitPolicyEditor = hitPolicyEditor;
        this.headerEditors = headerEditors;

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
                                              getExpression(),
                                              listSelector,
                                              getExpressionTextLineHeight(getRenderer().getTheme()));
    }

    @Override
    public void initialiseUiColumns() {
        int uiColumnIndex = 0;
        if (getExpression().get().isPresent()) {
            final DecisionTable e = getExpression().get().get();
            model.appendColumn(new DecisionTableRowNumberColumn(e::getHitPolicy,
                                                                e::getAggregation,
                                                                cellEditorControls,
                                                                hitPolicyEditor,
                                                                getAndSetInitialWidth(uiColumnIndex++, DecisionTableRowNumberColumn.DEFAULT_WIDTH),
                                                                this));
            for (int index = 0; index < e.getInput().size(); index++) {
                model.appendColumn(makeInputClauseColumn(uiColumnIndex++, e.getInput().get(index)));
            }
            for (int index = 0; index < e.getOutput().size(); index++) {
                model.appendColumn(makeOutputClauseColumn(uiColumnIndex++, e.getOutput().get(index)));
            }
            for (int index = 0; index < e.getAnnotations().size(); index++) {
                model.appendColumn(makeRuleAnnotationClauseColumn(uiColumnIndex++, e.getAnnotations().get(index)));
            }
        }

        getRenderer().setColumnRenderConstraint((isSelectionLayer, gridColumn) -> !isSelectionLayer);
    }

    private RuleAnnotationClauseColumn makeRuleAnnotationClauseColumn(final int index,
                                                                      final RuleAnnotationClause ruleAnnotationClause) {
        final RuleAnnotationClauseColumn column = new RuleAnnotationClauseColumn(ruleAnnotationClauseHeaderMetaData(ruleAnnotationClause),
                                                                                 textAreaFactory,
                                                                                 getAndSetInitialWidth(index, DMNGridColumn.DEFAULT_WIDTH), // MUST BE SYNCHRONIZED WITH WidthConstants.ts
                                                                                 this);
        return column;
    }

    private Supplier<List<GridColumn.HeaderMetaData>> ruleAnnotationClauseHeaderMetaData(final RuleAnnotationClause ruleAnnotationClause) {
        return () -> {
            final List<GridColumn.HeaderMetaData> metaData = new ArrayList<>();
            metaData.add(new RuleAnnotationClauseColumnHeaderMetaData(() -> ruleAnnotationClause.getName().getValue(),
                                                                      (value) -> ruleAnnotationClause.getName().setValue(value),
                                                                      getHeaderTextBoxFactory(),
                                                                      Optional.of(translationService.getTranslation(DMNEditorConstants.DecisionTableEditor_EnterAnnotation)),
                                                                      this::getHeaderItems,
                                                                      listSelector,
                                                                      this::onItemSelected));
            return metaData;
        };
    }

    private InputClauseColumn makeInputClauseColumn(final int index,
                                                    final InputClause ic) {
        final InputClauseColumn column = new InputClauseColumn(new InputClauseColumnHeaderMetaData(ic.getInputExpression(),
                                                                                                   ic::getInputExpression,
                                                                                                   clearValueConsumer(false, new Text()),
                                                                                                   setValueConsumer(false),
                                                                                                   setTypeRefConsumer(),
                                                                                                   translationService,
                                                                                                   cellEditorControls,
                                                                                                   headerEditors.get(),
                                                                                                   listSelector,
                                                                                                   this::getHeaderItems,
                                                                                                   this::onItemSelected),
                                                               textAreaFactory,
                                                               getAndSetInitialWidth(index, DMNGridColumn.DEFAULT_WIDTH), // MUST BE SYNCHRONIZED WITH WidthConstants.ts
                                                               this);
        return column;
    }

    private OutputClauseColumn makeOutputClauseColumn(final int index,
                                                      final OutputClause oc) {
        final OutputClauseColumn column = new OutputClauseColumn(outputClauseHeaderMetaData(oc),
                                                                 textAreaFactory,
                                                                 getAndSetInitialWidth(index, DMNGridColumn.DEFAULT_WIDTH), // MUST BE SYNCHRONIZED WITH WidthConstants.ts
                                                                 this);
        return column;
    }

    private Supplier<List<GridColumn.HeaderMetaData>> outputClauseHeaderMetaData(final OutputClause oc) {
        final ValueAndDataTypePopoverView.Presenter headerEditor = headerEditors.get();
        return () -> {
            final List<GridColumn.HeaderMetaData> metaData = new ArrayList<>();
            getExpression().get().ifPresent(dtable -> {
                if (hasName.isPresent()) {
                    metaData.add(new OutputClauseColumnExpressionNameHeaderMetaData(hasExpression,
                                                                                    hasName,
                                                                                    clearValueConsumer(oc, dtable),
                                                                                    setValueConsumer(oc, dtable),
                                                                                    setTypeRefConsumer(oc, dtable),
                                                                                    translationService,
                                                                                    cellEditorControls,
                                                                                    headerEditor,
                                                                                    listSelector,
                                                                                    this::getHeaderItems,
                                                                                    this::onItemSelected));
                } else {
                    final HasName hn = new HasName() {
                        @Override public Name getName() {
                            return new Name(ContextUIModelMapper.DEFAULT_ROW_CAPTION);
                        }

                        @Override public void setName(Name name) {
                            // name can not be changed
                        }
                    };

                    final OutputClauseColumnHeaderMetaData hm = new OutputClauseColumnHeaderMetaData(hn,
                            () -> dtable,
                            hasName -> {
                                // name can not be cleared
                            },
                            (hasName, name) -> {
                                // name can not be changed
                            },
                            setTypeRefConsumer(),
                            translationService,
                            cellEditorControls,
                            headerEditor,
                            listSelector,
                            this::getHeaderItems,
                            this::onItemSelected);
                    hm.setHeaderPlacement(OutputClauseColumnHeaderMetaData.OutputClauseColumnHeaderMetaDataPlacement.TOP);

                    metaData.add(hm);
                }
                if (dtable.getOutput().size() > 1) {
                    final OutputClauseColumnHeaderMetaData hm = new OutputClauseColumnHeaderMetaData(wrapOutputClauseIntoHasName(oc),
                            () -> oc,
                            clearValueConsumer(false, new Name()),
                            setValueConsumer(false),
                            setTypeRefConsumer(),
                            translationService,
                            cellEditorControls,
                            headerEditor,
                            listSelector,
                            this::getHeaderItems,
                            this::onItemSelected);
                    hm.setHeaderPlacement(OutputClauseColumnHeaderMetaData.OutputClauseColumnHeaderMetaDataPlacement.BOTTOM);
                    metaData.add(hm);
                }
            });
            return metaData;
        };
    }

    private Consumer<HasName> clearValueConsumer(final OutputClause oc,
                                                 final DecisionTable dtable) {
        if (dtable.getOutput().size() == 1) {
            return clearDisplayNameOnHasExpressionAndOutputClauseConsumer(oc);
        }
        return clearValueConsumer(true, new Name());
    }

    @SuppressWarnings("unchecked")
    private Consumer<HasName> clearDisplayNameOnHasExpressionAndOutputClauseConsumer(final OutputClause oc) {
        return (hv) -> {
            final CompositeCommand.Builder commandBuilder = newHasValueHasNoValueCommand(hv, new Name());
            commandBuilder.addCommand(new DeleteHasValueCommand<>(wrapOutputClauseIntoHasName(oc),
                                                                  new Name(),
                                                                  () -> {/*Nothing*/}));
            getUpdateStunnerTitleCommand(new Name()).ifPresent(commandBuilder::addCommand);
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          commandBuilder.build());
        };
    }

    private BiConsumer<HasName, Name> setValueConsumer(final OutputClause oc,
                                                       final DecisionTable dtable) {
        if (dtable.getOutput().size() == 1) {
            return setDisplayNameOnHasExpressionAndOutputClauseConsumer(oc);
        }
        return setValueConsumer(true);
    }

    @SuppressWarnings("unchecked")
    private BiConsumer<HasName, Name> setDisplayNameOnHasExpressionAndOutputClauseConsumer(final OutputClause oc) {
        return (hv, value) -> {
            final CompositeCommand.Builder commandBuilder = newHasValueHasValueCommand(hv, value);
            commandBuilder.addCommand(new SetHasValueCommand<>(wrapOutputClauseIntoHasName(oc),
                                                               value,
                                                               () -> {/*Nothing*/}));
            getUpdateStunnerTitleCommand(value).ifPresent(commandBuilder::addCommand);
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
    public void initialiseUiRows() {
        getExpression().get().ifPresent(e -> {
            e.getRule().forEach(r -> model.appendRow(new LiteralExpressionGridRow()));
        });
    }

    @Override
    public void initialiseUiCells() {
        getExpression().get().ifPresent(e -> {
            for (int rowIndex = 0; rowIndex < e.getRule().size(); rowIndex++) {
                int columnIndex = 0;
                uiModelMapper.fromDMNModel(rowIndex,
                                           columnIndex++);
                for (int ici = 0; ici < e.getInput().size(); ici++) {
                    uiModelMapper.fromDMNModel(rowIndex,
                                               columnIndex++);
                }
                for (int oci = 0; oci < e.getOutput().size(); oci++) {
                    uiModelMapper.fromDMNModel(rowIndex,
                                               columnIndex++);
                }
                for (int index = 0; index < e.getAnnotations().size(); index++) {
                    uiModelMapper.fromDMNModel(rowIndex,
                                               columnIndex++);
                }
                uiModelMapper.fromDMNModel(rowIndex,
                                           columnIndex);
            }
        });
    }

    @SuppressWarnings("unused")
    List<ListSelectorItem> getHeaderItems(final int uiHeaderRowIndex,
                                          final int uiHeaderColumnIndex) {
        final List<ListSelectorItem> items = new ArrayList<>();

        getExpression().get().ifPresent(dtable -> {
            final boolean isMultiHeaderColumn = SelectionUtils.isMultiHeaderColumn(model);
            final DecisionTableUIModelMapperHelper.DecisionTableSection section = DecisionTableUIModelMapperHelper.getSection(dtable, uiHeaderColumnIndex);
            switch (section) {
                case INPUT_CLAUSES:
                    addInputClauseItems(items,
                                        dtable,
                                        uiHeaderColumnIndex,
                                        isMultiHeaderColumn);
                    break;

                case OUTPUT_CLAUSES:
                    final List<GridColumn<?>> allColumns = model.getColumns();
                    final List<GridColumn.HeaderMetaData> headerMetaData = allColumns.get(uiHeaderColumnIndex).getHeaderMetaData();
                    final GridColumn.HeaderMetaData cellHeaderMetaData = headerMetaData.get(uiHeaderRowIndex);
                    if (headerMetaData.size() - 1 == uiHeaderRowIndex) {
                        addOutputClauseItems(items,
                                             dtable,
                                             uiHeaderColumnIndex,
                                             isMultiHeaderColumn);
                    } else {
                        items.add(ListSelectorHeaderItem.build(translationService.format(DMNEditorConstants.DecisionTableEditor_OutputClauseHeader)));
                        items.add(ListSelectorTextItem.build(translationService.format(DMNEditorConstants.DecisionTableEditor_InsertOutputClauseLeft),
                                                             true,
                                                             () -> addOutputClause(getHeaderBlockStartColumnIndex(allColumns,
                                                                                                                  cellHeaderMetaData,
                                                                                                                  uiHeaderRowIndex,
                                                                                                                  uiHeaderColumnIndex))));
                        items.add(ListSelectorTextItem.build(translationService.format(DMNEditorConstants.DecisionTableEditor_InsertOutputClauseRight),
                                                             true,
                                                             () -> addOutputClause(getHeaderBlockEndColumnIndex(allColumns,
                                                                                                                cellHeaderMetaData,
                                                                                                                uiHeaderRowIndex,
                                                                                                                uiHeaderColumnIndex) + 1)));
                    }
                    break;
                case ANNOTATION_CLAUSES:
                    addRuleAnnotationClauseItems(items,
                                                 uiHeaderColumnIndex,
                                                 dtable.getAnnotations().size() <= 1);
            }
        });

        return items;
    }

    @Override
    @SuppressWarnings("unused")
    public List<ListSelectorItem> getItems(final int uiRowIndex,
                                           final int uiColumnIndex) {
        final List<ListSelectorItem> items = new ArrayList<>();

        getExpression().get().ifPresent(dtable -> {
            final boolean isMultiColumn = SelectionUtils.isMultiColumn(model);
            final DecisionTableUIModelMapperHelper.DecisionTableSection section = DecisionTableUIModelMapperHelper.getSection(dtable, uiColumnIndex);
            switch (section) {
                case INPUT_CLAUSES:
                    addInputClauseItems(items,
                                        dtable,
                                        uiColumnIndex,
                                        isMultiColumn);
                    items.add(new ListSelectorDividerItem());
                    break;

                case OUTPUT_CLAUSES:
                    addOutputClauseItems(items,
                                         dtable,
                                         uiColumnIndex,
                                         isMultiColumn);
                    items.add(new ListSelectorDividerItem());
                    break;

                case ANNOTATION_CLAUSES:
                    addRuleAnnotationClauseItems(items,
                                                 uiColumnIndex,
                                                 dtable.getAnnotations().size() <= 1);
                    items.add(new ListSelectorDividerItem());
                    break;
            }

            addDecisionRuleItems(dtable,
                                 items,
                                 uiRowIndex);
        });

        return items;
    }

    private void addInputClauseItems(final List<ListSelectorItem> items,
                                     final DecisionTable dtable,
                                     final int uiColumnIndex,
                                     final boolean isMultiColumn) {
        items.add(ListSelectorHeaderItem.build(translationService.format(DMNEditorConstants.DecisionTableEditor_InputClauseHeader)));
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
    }

    private void addRuleAnnotationClauseItems(final List<ListSelectorItem> items,
                                              final int uiColumnIndex,
                                              final boolean isSingleAnnotation) {
        items.add(ListSelectorHeaderItem.build(translationService.format(DMNEditorConstants.DecisionTableEditor_RuleAnnotationClauseHeader)));
        addItems(items,
                 new ListSelectorItemDefinition(translationService.format(DMNEditorConstants.DecisionTableEditor_InsertRuleAnnotationClauseLeft),
                                                true,
                                                () -> addRuleAnnotationClause(uiColumnIndex)),
                 new ListSelectorItemDefinition(translationService.format(DMNEditorConstants.DecisionTableEditor_InsertRuleAnnotationClauseRight),
                                                true,
                                                () -> addRuleAnnotationClause(uiColumnIndex + 1)),
                 new ListSelectorItemDefinition(translationService.format(DMNEditorConstants.DecisionTableEditor_DeleteRuleAnnotationClause),
                                                !isSingleAnnotation,
                                                () -> deleteRuleAnnotationClause(uiColumnIndex)));
    }

    private void addOutputClauseItems(final List<ListSelectorItem> items,
                                      final DecisionTable dtable,
                                      final int uiColumnIndex,
                                      final boolean isMultiColumn) {
        items.add(ListSelectorHeaderItem.build(translationService.format(DMNEditorConstants.DecisionTableEditor_OutputClauseHeader)));
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
    }

    void addItems(final List<ListSelectorItem> items,
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
                              final List<ListSelectorItem> items,
                              final int uiRowIndex) {
        final boolean isMultiRow = SelectionUtils.isMultiRow(model);

        items.add(ListSelectorHeaderItem.build(translationService.format(DMNEditorConstants.DecisionTableEditor_DecisionRuleHeader)));
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

        items.add(ListSelectorTextItem.build(translationService.format(DMNEditorConstants.DecisionTableEditor_DuplicateDecisionRule),
                                             !isMultiRow,
                                             () -> {
                                                 cellEditorControls.hide();
                                                 duplicateDecisionRule(uiRowIndex);
                                             }));
    }

    @Override
    public void onItemSelected(final ListSelectorItem item) {
        final ListSelectorTextItem li = (ListSelectorTextItem) item;
        li.getCommand().execute();
    }

    void addInputClause(final int index) {
        getExpression().get().ifPresent(dtable -> {
            final InputClause clause = new InputClause();

            final CommandResult<CanvasViolation> result = sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                                                                        new AddInputClauseCommand(dtable,
                                                                                                                  clause,
                                                                                                                  model,
                                                                                                                  () -> makeInputClauseColumn(index, clause),
                                                                                                                  index,
                                                                                                                  uiModelMapper,
                                                                                                                  () -> resize(BaseExpressionGrid.RESIZE_EXISTING),
                                                                                                                  () -> resize(BaseExpressionGrid.RESIZE_EXISTING_MINIMUM)));

            if (!CommandUtils.isError(result)) {
                selectHeaderCell(0, index, false, false);
                CellContextUtilities.editSelectedCell(this);
            }
        });
    }

    void deleteInputClause(final int index) {
        getExpression().get().ifPresent(dtable -> {
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new DeleteInputClauseCommand(dtable,
                                                                       model,
                                                                       index,
                                                                       uiModelMapper,
                                                                       () -> resize(BaseExpressionGrid.RESIZE_EXISTING_MINIMUM),
                                                                       () -> resize(BaseExpressionGrid.RESIZE_EXISTING)));
        });
    }

    void addRuleAnnotationClause(final int index) {
        getExpression().get().ifPresent(dtable -> {
            final RuleAnnotationClause clause = new RuleAnnotationClause();

            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new AddRuleAnnotationClauseCommand(dtable,
                                                                             clause,
                                                                             model,
                                                                             () -> makeRuleAnnotationClauseColumn(index, clause),
                                                                             index,
                                                                             uiModelMapper,
                                                                             () -> resize(BaseExpressionGrid.RESIZE_EXISTING),
                                                                             () -> resize(BaseExpressionGrid.RESIZE_EXISTING_MINIMUM)));
        });
    }

    void deleteRuleAnnotationClause(final int index) {
        getExpression().get().ifPresent(decisionTable ->
                                                sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                                                              new DeleteRuleAnnotationClauseCommand(decisionTable,
                                                                                                                    model,
                                                                                                                    index,
                                                                                                                    uiModelMapper,
                                                                                                                    () -> resize(BaseExpressionGrid.RESIZE_EXISTING_MINIMUM),
                                                                                                                    () -> resize(BaseExpressionGrid.RESIZE_EXISTING))));
    }

    void addOutputClause(final int index) {
        getExpression().get().ifPresent(dtable -> {
            final OutputClause clause = new OutputClause();

            final CommandResult<CanvasViolation> result = sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                                                                        new AddOutputClauseCommand(dtable,
                                                                                                                   clause,
                                                                                                                   model,
                                                                                                                   () -> makeOutputClauseColumn(index, clause),
                                                                                                                   index,
                                                                                                                   uiModelMapper,
                                                                                                                   () -> resize(BaseExpressionGrid.RESIZE_EXISTING),
                                                                                                                   () -> resize(BaseExpressionGrid.RESIZE_EXISTING_MINIMUM)));

            if (!CommandUtils.isError(result)) {
                selectHeaderCell(1, index, false, false);
                CellContextUtilities.editSelectedCell(this);
            }
        });
    }

    void deleteOutputClause(final int index) {
        getExpression().get().ifPresent(dtable -> {
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
        getExpression().get().ifPresent(dtable -> {
            final GridRow decisionTableRow = new LiteralExpressionGridRow();
            final DecisionRule decisionRule = DecisionRuleFactory.makeDecisionRule(dtable);
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new AddDecisionRuleCommand(dtable,
                                                                     decisionRule,
                                                                     model,
                                                                     decisionTableRow,
                                                                     index,
                                                                     uiModelMapper,
                                                                     () -> resize(BaseExpressionGrid.RESIZE_EXISTING)));
        });
    }

    void deleteDecisionRule(final int index) {
        getExpression().get().ifPresent(dtable -> {
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new DeleteDecisionRuleCommand(dtable,
                                                                        model,
                                                                        index,
                                                                        () -> resize(BaseExpressionGrid.RESIZE_EXISTING)));
        });
    }

    void duplicateDecisionRule(final int index) {
        getExpression().get().ifPresent(dtable -> {
            final GridRow decisionTableRow = new LiteralExpressionGridRow();
            final DecisionRule decisionRule = DecisionRuleFactory.duplicateDecisionRule(index, dtable);
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new AddDecisionRuleCommand(dtable,
                                                                     decisionRule,
                                                                     model,
                                                                     decisionTableRow,
                                                                     index,
                                                                     uiModelMapper,
                                                                     () -> resize(BaseExpressionGrid.RESIZE_EXISTING)));
        });
    }

    @Override
    public HitPolicy getHitPolicy() {
        return getExpression().get().orElseThrow(() -> new IllegalArgumentException("DecisionTable has not been set.")).getHitPolicy();
    }

    @Override
    public BuiltinAggregator getBuiltinAggregator() {
        return getExpression().get().orElseThrow(() -> new IllegalArgumentException("DecisionTable has not been set.")).getAggregation();
    }

    @Override
    public void setHitPolicy(final HitPolicy hitPolicy,
                             final Command onSuccess) {
        getExpression().get().ifPresent(dtable -> {
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
        getExpression().get().ifPresent(dtable -> {
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new SetBuiltinAggregatorCommand(dtable,
                                                                          aggregator,
                                                                          gridLayer::batch));
        });
    }

    @Override
    public void doAfterSelectionChange(final int uiRowIndex,
                                       final int uiColumnIndex) {
        if (hasAnyHeaderCellSelected() || hasMultipleCellsSelected()) {
            super.doAfterSelectionChange(uiRowIndex, uiColumnIndex);
            return;
        }

        if (getExpression().get().isPresent()) {
            final DecisionTable dtable = getExpression().get().get();
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
    public void doAfterHeaderSelectionChange(final int uiHeaderRowIndex,
                                             final int uiHeaderColumnIndex) {
        if (getExpression().get().isPresent()) {
            final DecisionTable dtable = getExpression().get().get();
            final DecisionTableUIModelMapperHelper.DecisionTableSection section = DecisionTableUIModelMapperHelper.getSection(dtable, uiHeaderColumnIndex);
            switch (section) {
                case INPUT_CLAUSES:
                    doAfterHeaderInputClauseSelectionChange(dtable, uiHeaderColumnIndex);
                    return;
                case OUTPUT_CLAUSES:
                    doAfterHeaderOutputClauseSelectionChange(dtable, uiHeaderRowIndex, uiHeaderColumnIndex);
                    return;
            }
        }
        super.doAfterHeaderSelectionChange(uiHeaderRowIndex, uiHeaderColumnIndex);
    }

    protected void doAfterHeaderInputClauseSelectionChange(final DecisionTable dtable,
                                                           final int uiHeaderColumnIndex) {
        final int icIndex = DecisionTableUIModelMapperHelper.getInputEntryIndex(dtable, uiHeaderColumnIndex);
        final InputClause inputClause = dtable.getInput().get(icIndex);
        fireDomainObjectSelectionEvent(inputClause);
    }

    protected void doAfterHeaderOutputClauseSelectionChange(final DecisionTable dtable,
                                                            final int uiHeaderRowIndex,
                                                            final int uiHeaderColumnIndex) {
        final List<GridColumn.HeaderMetaData> headerMetaData = model.getColumns().get(uiHeaderColumnIndex).getHeaderMetaData();
        if (uiHeaderRowIndex == 0 && headerMetaData.size() > 1) {
            if (doFireDomainObjectSelectionEventForHasExpression()) {
                return;
            }
        }
        final int ocIndex = DecisionTableUIModelMapperHelper.getOutputEntryIndex(dtable, uiHeaderColumnIndex);
        final OutputClause outputClause = dtable.getOutput().get(ocIndex);
        fireDomainObjectSelectionEvent(outputClause);
    }

    protected boolean doFireDomainObjectSelectionEventForHasExpression() {
        final DMNModelInstrumentedBase base = hasExpression.asDMNModelInstrumentedBase();
        if (base instanceof DomainObject) {
            fireDomainObjectSelectionEvent((DomainObject) base);
            return true;
        } else if (base instanceof ContextEntry) {
            final ContextEntry contextEntry = (ContextEntry) base;
            final InformationItem variable = contextEntry.getVariable();
            if (Objects.nonNull(variable)) {
                fireDomainObjectSelectionEvent(variable);
                return true;
            }
        }
        return false;
    }
}
