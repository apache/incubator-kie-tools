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

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.parameters.ParametersEditorView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControls;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellEditContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class FunctionColumnParametersHeaderMetaDataTest {

    private static final double ABSOLUTE_CELL_X = 10.0;
    private static final double ABSOLUTE_CELL_Y = 20.0;
    private static final double CELL_WIDTH = 100.0;
    private static final double CELL_HEIGHT = 32.0;
    private static final double CLIP_MIN_Y = 5.0;
    private static final double CLIP_MIN_X = 15.0;
    private static final int ROW_INDEX = 1;
    private static final int COLUMN_INDEX = 2;
    private static final boolean IS_FLOATING = false;
    private static final double RELATIVE_X = 38.0;
    private static final double RELATIVE_Y = 16.0;

    @Mock
    private CellEditorControls cellEditorControls;

    @Mock
    private ParametersEditorView.Presenter parametersEditor;

    @Mock
    private FunctionGrid gridWidget;

    @Mock
    private Transform transform;

    @Mock
    private GridRenderer renderer;

    private FunctionDefinition function;

    private Supplier<FunctionDefinition> functionSupplier;

    private FunctionColumnParametersHeaderMetaData header;

    @Before
    public void setup() {
        this.function = new FunctionDefinition();
        this.functionSupplier = () -> function;
        this.header = new FunctionColumnParametersHeaderMetaData(functionSupplier,
                                                                 cellEditorControls,
                                                                 parametersEditor,
                                                                 gridWidget);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetColumnGroup() {
        header.setColumnGroup("group");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetTitle() {
        header.setTitle("title");
    }

    @Test
    public void testGetExpressionLanguageTitle() {
        KindUtilities.setKind(this.function,
                              FunctionDefinition.Kind.FEEL);

        assertEquals(FunctionDefinition.Kind.FEEL.code(),
                     header.getExpressionLanguageTitle());
    }

    @Test
    public void testGetFormalParametersTitleOneParameter() {
        setupFormalParameters("p0");

        assertEquals("(p0)",
                     header.getFormalParametersTitle());
    }

    @Test
    public void testGetFormalParametersTitleMultipleParameters() {
        setupFormalParameters("p0", "p1");

        assertEquals("(p0, p1)",
                     header.getFormalParametersTitle());
    }

    @Test
    public void testGetColumnGroup() {
        assertEquals(FunctionColumnParametersHeaderMetaData.PARAMETER_COLUMN_GROUP,
                     header.getColumnGroup());
    }

    @Test
    public void testGetTitle() {
        KindUtilities.setKind(this.function,
                              FunctionDefinition.Kind.FEEL);

        setupFormalParameters("p0", "p1");

        assertEquals("F : (p0, p1)",
                     header.getTitle());
    }

    @Test
    public void testEditWithRelationLocation() {
        final GridBodyCellEditContext context = new GridBodyCellEditContext(ABSOLUTE_CELL_X,
                                                                            ABSOLUTE_CELL_Y,
                                                                            CELL_WIDTH,
                                                                            CELL_HEIGHT,
                                                                            CLIP_MIN_Y,
                                                                            CLIP_MIN_X,
                                                                            ROW_INDEX,
                                                                            COLUMN_INDEX,
                                                                            IS_FLOATING,
                                                                            transform,
                                                                            renderer,
                                                                            Optional.of(new Point2D(RELATIVE_X, RELATIVE_Y)));

        header.edit(context);

        verify(parametersEditor).bind(eq(gridWidget),
                                      eq(ROW_INDEX),
                                      eq(COLUMN_INDEX));
        verify(cellEditorControls).show(eq(parametersEditor),
                                        eq((int) (ABSOLUTE_CELL_X + RELATIVE_X)),
                                        eq((int) (ABSOLUTE_CELL_Y + (RELATIVE_Y - CELL_HEIGHT * ROW_INDEX))));
    }

    @Test
    public void testEditWithNoRelationLocation() {
        final GridBodyCellEditContext context = new GridBodyCellEditContext(ABSOLUTE_CELL_X,
                                                                            ABSOLUTE_CELL_Y,
                                                                            CELL_WIDTH,
                                                                            CELL_HEIGHT,
                                                                            CLIP_MIN_Y,
                                                                            CLIP_MIN_X,
                                                                            ROW_INDEX,
                                                                            COLUMN_INDEX,
                                                                            IS_FLOATING,
                                                                            transform,
                                                                            renderer,
                                                                            Optional.empty());

        header.edit(context);

        verify(parametersEditor).bind(eq(gridWidget),
                                      eq(ROW_INDEX),
                                      eq(COLUMN_INDEX));
        verify(cellEditorControls).show(eq(parametersEditor),
                                        eq((int) ABSOLUTE_CELL_X),
                                        eq((int) ABSOLUTE_CELL_Y));
    }

    @Test
    public void testDestroyResources() {
        header.destroyResources();

        verify(parametersEditor).hide();
    }

    private void setupFormalParameters(final String... parameters) {
        Arrays.asList(parameters).forEach(p -> this.function.getFormalParameter().add(new InformationItem() {{
            setName(new Name(p));
        }}));
    }
}
