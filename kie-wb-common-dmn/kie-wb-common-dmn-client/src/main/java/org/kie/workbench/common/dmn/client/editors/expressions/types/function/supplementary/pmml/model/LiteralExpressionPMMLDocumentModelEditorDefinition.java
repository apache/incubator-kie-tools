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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.pmml.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpressionPMMLDocument;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpressionPMMLDocumentModel;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.commands.expressions.types.function.SetParametersCommand;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.dmn.client.commands.general.SetCellValueCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.types.BaseEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.pmml.LiteralExpressionPMMLGrid;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.pmml.PMMLDocumentMetadataProvider;
import org.kie.workbench.common.dmn.client.editors.types.NameAndDataTypePopoverView;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellValueTuple;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.uberfire.ext.wires.core.grids.client.model.GridData;

@ApplicationScoped
public class LiteralExpressionPMMLDocumentModelEditorDefinition extends BaseEditorDefinition<LiteralExpressionPMMLDocumentModel, DMNGridData> {

    private NameAndDataTypePopoverView.Presenter headerEditor;

    private PMMLDocumentMetadataProvider pmmlDocumentMetadataProvider;

    public LiteralExpressionPMMLDocumentModelEditorDefinition() {
        //CDI proxy
    }

    @Inject
    public LiteralExpressionPMMLDocumentModelEditorDefinition(final DefinitionUtils definitionUtils,
                                                              final SessionManager sessionManager,
                                                              final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                                              final @DMNEditor DefaultCanvasCommandFactory canvasCommandFactory,
                                                              final Event<ExpressionEditorChanged> editorSelectedEvent,
                                                              final Event<RefreshFormPropertiesEvent> refreshFormPropertiesEvent,
                                                              final Event<DomainObjectSelectionEvent> domainObjectSelectionEvent,
                                                              final ListSelectorView.Presenter listSelector,
                                                              final TranslationService translationService,
                                                              final NameAndDataTypePopoverView.Presenter headerEditor,
                                                              final PMMLDocumentMetadataProvider pmmlDocumentMetadataProvider) {
        super(definitionUtils,
              sessionManager,
              sessionCommandManager,
              canvasCommandFactory,
              editorSelectedEvent,
              refreshFormPropertiesEvent,
              domainObjectSelectionEvent,
              listSelector,
              translationService);
        this.headerEditor = headerEditor;
        this.pmmlDocumentMetadataProvider = pmmlDocumentMetadataProvider;
    }

    @Override
    public boolean isUserSelectable() {
        return false;
    }

    @Override
    public ExpressionType getType() {
        return ExpressionType.LITERAL_EXPRESSION;
    }

    @Override
    public String getName() {
        return translationService.format(DMNEditorConstants.ExpressionEditor_LiteralExpressionType) + "-PMMLDocumentModel";
    }

    @Override
    public Optional<LiteralExpressionPMMLDocumentModel> getModelClass() {
        return Optional.of(new LiteralExpressionPMMLDocumentModel());
    }

    @Override
    public Optional<BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper>> getEditor(final GridCellTuple parent,
                                                                                                                         final Optional<String> nodeUUID,
                                                                                                                         final HasExpression hasExpression,
                                                                                                                         final Optional<HasName> hasName,
                                                                                                                         final boolean isOnlyVisualChangeAllowed,
                                                                                                                         final int nesting) {
        return Optional.of(new LiteralExpressionPMMLGrid(parent,
                                                         nodeUUID,
                                                         hasExpression,
                                                         hasName,
                                                         getGridPanel(),
                                                         getGridLayer(),
                                                         makeGridData(() -> Optional.ofNullable((LiteralExpressionPMMLDocumentModel) hasExpression.getExpression())),
                                                         definitionUtils,
                                                         sessionManager,
                                                         sessionCommandManager,
                                                         canvasCommandFactory,
                                                         editorSelectedEvent,
                                                         refreshFormPropertiesEvent,
                                                         domainObjectSelectionEvent,
                                                         getCellEditorControls(),
                                                         listSelector,
                                                         translationService,
                                                         isOnlyVisualChangeAllowed,
                                                         nesting,
                                                         headerEditor) {

            @Override
            protected String getPlaceHolder() {
                return translationService.getTranslation(DMNEditorConstants.LiteralExpressionPMMLDocumentModelEditorDefinition_Placeholder);
            }

            @Override
            protected void loadValues(final Consumer<List<String>> consumer) {
                final String pmmlDocumentName = getExpressionPMMLValue(LiteralExpressionPMMLDocument.VARIABLE_DOCUMENT);
                consumer.accept(pmmlDocumentMetadataProvider.getPMMLDocumentModels(pmmlDocumentName));
            }

            @Override
            public Function<GridCellValueTuple, Command> newCellHasValueCommand() {
                return (gridCellValueTuple) -> {
                    final CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation> builder = new CompositeCommand.Builder<>();
                    //Command to set the PMMLDocumentModel value
                    builder.addCommand(new SetCellValueCommand(gridCellValueTuple,
                                                               () -> uiModelMapper,
                                                               gridLayer::batch));

                    //Command to set PMMLDocumentModel parameters
                    getParentFunctionGrid().ifPresent(parentFunctionGrid -> {
                        final String pmmlDocumentName = getExpressionPMMLValue(LiteralExpressionPMMLDocument.VARIABLE_DOCUMENT);
                        final String pmmlDocumentModelName = StringUtils.createUnquotedConstant((String) gridCellValueTuple.getValue().getValue());
                        final List<String> parameters = pmmlDocumentMetadataProvider.getPMMLDocumentModelParameterNames(pmmlDocumentName,
                                                                                                                        pmmlDocumentModelName);

                        parentFunctionGrid.getExpression().get().ifPresent(function -> {
                            builder.addCommand(new SetParametersCommand(function,
                                                                        convertParametersToInformationItems(parameters),
                                                                        gridLayer::batch));
                        });
                    });

                    return builder.build();
                };
            }

            private List<InformationItem> convertParametersToInformationItems(final List<String> parameters) {
                final List<InformationItem> informationItems = new ArrayList<>();
                parameters.forEach(parameter -> informationItems.add(new InformationItem(new Id(),
                                                                                         new Description(),
                                                                                         new Name(parameter),
                                                                                         BuiltInType.ANY.asQName())));
                return informationItems;
            }
        });
    }

    @Override
    protected DMNGridData makeGridData(final Supplier<Optional<LiteralExpressionPMMLDocumentModel>> expression) {
        return new DMNGridData();
    }
}
