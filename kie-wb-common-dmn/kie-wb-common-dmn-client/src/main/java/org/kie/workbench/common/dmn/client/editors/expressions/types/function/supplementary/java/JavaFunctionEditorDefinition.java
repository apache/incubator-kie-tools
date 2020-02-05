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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.java;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.FunctionGridSupplementaryEditor;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.BaseSupplementaryFunctionEditorDefinition;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;

@Dependent
@FunctionGridSupplementaryEditor
public class JavaFunctionEditorDefinition extends BaseSupplementaryFunctionEditorDefinition {

    public static final String VARIABLE_CLASS = "class";

    public static final String VARIABLE_METHOD_SIGNATURE = "method signature";

    public JavaFunctionEditorDefinition() {
        //CDI proxy
    }

    @Inject
    public JavaFunctionEditorDefinition(final DefinitionUtils definitionUtils,
                                        final SessionManager sessionManager,
                                        final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                        final @DMNEditor DefaultCanvasCommandFactory canvasCommandFactory,
                                        final Event<ExpressionEditorChanged> editorSelectedEvent,
                                        final Event<RefreshFormPropertiesEvent> refreshFormPropertiesEvent,
                                        final Event<DomainObjectSelectionEvent> domainObjectSelectionEvent,
                                        final ListSelectorView.Presenter listSelector,
                                        final TranslationService translationService,
                                        final @DMNEditor Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier) {
        super(definitionUtils,
              sessionManager,
              sessionCommandManager,
              canvasCommandFactory,
              editorSelectedEvent,
              refreshFormPropertiesEvent,
              domainObjectSelectionEvent,
              listSelector,
              translationService,
              expressionEditorDefinitionsSupplier);
    }

    @Override
    public ExpressionType getType() {
        return ExpressionType.FUNCTION_JAVA;
    }

    @Override
    public String getName() {
        return translationService.format(DMNEditorConstants.ExpressionEditor_JavaFunctionType);
    }

    @Override
    protected List<String> getVariableNames() {
        return Arrays.asList(VARIABLE_CLASS, VARIABLE_METHOD_SIGNATURE);
    }
}
