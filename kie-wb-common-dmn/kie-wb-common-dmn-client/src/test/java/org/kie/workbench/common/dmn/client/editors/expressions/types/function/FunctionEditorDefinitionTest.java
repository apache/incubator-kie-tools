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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function;

import java.util.Optional;
import java.util.function.Supplier;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType;
import org.kie.workbench.common.dmn.client.events.ExpressionEditorSelectedEvent;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

@RunWith(LienzoMockitoTestRunner.class)
public class FunctionEditorDefinitionTest {

    @Mock
    private DMNGridPanel gridPanel;

    @Mock
    private DMNGridLayer gridLayer;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    private Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;

    @Mock
    private Supplier<ExpressionEditorDefinitions> supplementaryEditorDefinitionsSupplier;

    @Mock
    private EventSourceMock<ExpressionEditorSelectedEvent> editorSelectedEvent;

    @Mock
    private ManagedInstance<FunctionGridControls> controlsProvider;

    @Mock
    private FunctionGridControls controls;

    @Mock
    private GridCellTuple parent;

    @Mock
    private HasExpression hasExpression;

    private Optional<HasName> hasName = Optional.empty();

    private FunctionEditorDefinition definition;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        this.definition = new FunctionEditorDefinition(gridPanel,
                                                       gridLayer,
                                                       sessionManager,
                                                       sessionCommandManager,
                                                       expressionEditorDefinitionsSupplier,
                                                       supplementaryEditorDefinitionsSupplier,
                                                       editorSelectedEvent,
                                                       controlsProvider);
        final ExpressionEditorDefinitions expressionEditorDefinitions = new ExpressionEditorDefinitions();
        expressionEditorDefinitions.add((ExpressionEditorDefinition) definition);

        doReturn(controls).when(controlsProvider).get();
        doReturn(expressionEditorDefinitions).when(expressionEditorDefinitionsSupplier).get();
        doReturn(expressionEditorDefinitions).when(supplementaryEditorDefinitionsSupplier).get();
    }

    @Test
    public void testType() {
        assertEquals(ExpressionType.FUNCTION,
                     definition.getType());
    }

    @Test
    public void testName() {
        assertEquals(FunctionDefinition.class.getSimpleName(),
                     definition.getName());
    }

    @Test
    public void testModelDefinition() {
        final Optional<FunctionDefinition> oModel = definition.getModelClass();
        assertTrue(oModel.isPresent());

        final FunctionDefinition model = oModel.get();
        assertEquals(FunctionDefinition.Kind.FEEL.code(),
                     model.getOtherAttributes().get(FunctionDefinition.KIND_QNAME));
        assertTrue(model.getExpression() instanceof LiteralExpression);
    }

    @Test
    public void testEditor() {
        final Optional<FunctionDefinition> expression = definition.getModelClass();
        final Optional<BaseExpressionGrid> oEditor = definition.getEditor(parent,
                                                                          hasExpression,
                                                                          expression,
                                                                          hasName,
                                                                          false);

        assertTrue(oEditor.isPresent());

        final GridWidget editor = oEditor.get();
        assertTrue(editor instanceof FunctionGrid);
    }
}
