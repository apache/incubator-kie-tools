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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.pmml;

import java.util.Optional;
import java.util.function.Supplier;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.Context;
import org.kie.workbench.common.dmn.api.definition.v1_1.ContextEntry;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.editors.expressions.types.BaseEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.FunctionGridSupplementaryEditor;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.FunctionSupplementaryGrid;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.FunctionSupplementaryGridData;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.Session;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

@Dependent
@FunctionGridSupplementaryEditor
public class PMMLFunctionEditorDefinition extends BaseEditorDefinition<Context, FunctionSupplementaryGridData> {

    public static final String VARIABLE_DOCUMENT = "document";

    public static final String VARIABLE_MODEL = "model";

    private Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;

    public PMMLFunctionEditorDefinition() {
        //CDI proxy
    }

    @Inject
    public PMMLFunctionEditorDefinition(final @DMNEditor DMNGridPanel gridPanel,
                                        final @DMNEditor DMNGridLayer gridLayer,
                                        final DefinitionUtils definitionUtils,
                                        final SessionManager sessionManager,
                                        final @Session SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                        final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory,
                                        final Event<ExpressionEditorChanged> editorSelectedEvent,
                                        final CellEditorControlsView.Presenter cellEditorControls,
                                        final ListSelectorView.Presenter listSelector,
                                        final TranslationService translationService,
                                        final @DMNEditor Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier) {
        super(gridPanel,
              gridLayer,
              definitionUtils,
              sessionManager,
              sessionCommandManager,
              canvasCommandFactory,
              editorSelectedEvent,
              cellEditorControls,
              listSelector,
              translationService);
        this.expressionEditorDefinitionsSupplier = expressionEditorDefinitionsSupplier;
    }

    @Override
    public ExpressionType getType() {
        return ExpressionType.FUNCTION_PMML;
    }

    @Override
    public String getName() {
        return translationService.format(DMNEditorConstants.ExpressionEditor_PMMLFunctionType);
    }

    @Override
    public Optional<Context> getModelClass() {
        final Context context = new Context();
        final ContextEntry documentEntry = new ContextEntry();
        final InformationItem documentEntryVariable = new InformationItem();
        documentEntryVariable.setName(new Name(VARIABLE_DOCUMENT));
        documentEntry.setVariable(documentEntryVariable);
        documentEntry.setExpression(new LiteralExpression());
        context.getContextEntry().add(documentEntry);

        final ContextEntry modelEntry = new ContextEntry();
        final InformationItem modelEntryVariable = new InformationItem();
        modelEntryVariable.setName(new Name(VARIABLE_MODEL));
        modelEntry.setVariable(modelEntryVariable);
        modelEntry.setExpression(new LiteralExpression());
        context.getContextEntry().add(modelEntry);
        return Optional.of(context);
    }

    @Override
    public Optional<BaseExpressionGrid> getEditor(final GridCellTuple parent,
                                                  final Optional<String> nodeUUID,
                                                  final HasExpression hasExpression,
                                                  final Optional<Context> expression,
                                                  final Optional<HasName> hasName,
                                                  final int nesting) {
        return Optional.of(new FunctionSupplementaryGrid(parent,
                                                         nodeUUID,
                                                         hasExpression,
                                                         expression,
                                                         hasName,
                                                         gridPanel,
                                                         gridLayer,
                                                         makeGridData(expression),
                                                         definitionUtils,
                                                         sessionManager,
                                                         sessionCommandManager,
                                                         canvasCommandFactory,
                                                         editorSelectedEvent,
                                                         cellEditorControls,
                                                         listSelector,
                                                         translationService,
                                                         nesting,
                                                         expressionEditorDefinitionsSupplier));
    }

    @Override
    protected FunctionSupplementaryGridData makeGridData(final Optional<Context> expression) {
        return new FunctionSupplementaryGridData(new DMNGridData(),
                                                 sessionManager,
                                                 sessionCommandManager,
                                                 expression,
                                                 gridLayer::batch);
    }
}
