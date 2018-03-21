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

package org.kie.workbench.common.dmn.client.editors.expressions.types.invocation;

import java.util.Optional;
import java.util.function.Supplier;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.Binding;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.api.definition.v1_1.Invocation;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.editors.expressions.types.BaseEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.Session;

@ApplicationScoped
public class InvocationEditorDefinition extends BaseEditorDefinition<Invocation> {

    private Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;
    private ListSelectorView.Presenter listSelector;

    public InvocationEditorDefinition() {
        //CDI proxy
    }

    @Inject
    public InvocationEditorDefinition(final @DMNEditor DMNGridPanel gridPanel,
                                      final @DMNEditor DMNGridLayer gridLayer,
                                      final SessionManager sessionManager,
                                      final @Session SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                      final @DMNEditor Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier,
                                      final CellEditorControlsView.Presenter cellEditorControls,
                                      final TranslationService translationService,
                                      final ListSelectorView.Presenter listSelector) {
        super(gridPanel,
              gridLayer,
              sessionManager,
              sessionCommandManager,
              cellEditorControls,
              translationService);
        this.expressionEditorDefinitionsSupplier = expressionEditorDefinitionsSupplier;
        this.listSelector = listSelector;
    }

    @Override
    public ExpressionType getType() {
        return ExpressionType.INVOCATION;
    }

    @Override
    public String getName() {
        return translationService.format(DMNEditorConstants.ExpressionEditor_InvocationType);
    }

    @Override
    public Optional<Invocation> getModelClass() {
        final Invocation invocation = new Invocation();
        invocation.setExpression(new LiteralExpression());
        final InformationItem parameter = new InformationItem();
        parameter.setName(new Name("p0"));
        final Binding binding = new Binding();
        binding.setParameter(parameter);
        invocation.getBinding().add(binding);
        return Optional.of(invocation);
    }

    @Override
    public Optional<BaseExpressionGrid> getEditor(final GridCellTuple parent,
                                                  final HasExpression hasExpression,
                                                  final Optional<Invocation> expression,
                                                  final Optional<HasName> hasName,
                                                  final int nesting) {
        return Optional.of(new InvocationGrid(parent,
                                              hasExpression,
                                              expression,
                                              hasName,
                                              gridPanel,
                                              gridLayer,
                                              sessionManager,
                                              sessionCommandManager,
                                              expressionEditorDefinitionsSupplier,
                                              cellEditorControls,
                                              translationService,
                                              listSelector,
                                              nesting));
    }
}
