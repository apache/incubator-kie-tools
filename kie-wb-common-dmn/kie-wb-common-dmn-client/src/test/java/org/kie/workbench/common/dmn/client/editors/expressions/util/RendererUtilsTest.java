/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.expressions.util;

import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGridTheme;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCell;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.GridRendererTheme;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class RendererUtilsTest {

    private static final String VALUE = "some text value";
    private static final double WIDTH = 200;
    private static final double HEIGHT = 80;

    private Text text;

    @Mock
    private GridRenderer gridRenderer;

    @Mock
    private GridRendererTheme gridTheme;

    @Mock
    private GridBodyCellRenderContext context;

    @Before
    public void setUp() throws Exception {
        text = spy(new Text(""));

        when(context.getRenderer()).thenReturn(gridRenderer);
        when(gridRenderer.getTheme()).thenReturn(gridTheme);
        when(gridTheme.getBodyText()).thenReturn(text);
        when(context.getCellWidth()).thenReturn(WIDTH);
        when(context.getCellHeight()).thenReturn(HEIGHT);
    }

    @Test
    public void testCenteredText() throws Exception {
        final BaseGridCell<String> cell = new BaseGridCell<>(new BaseGridCellValue<>(VALUE));

        RendererUtils.getCenteredCellText(context, cell);

        verify(text).setText(VALUE);
        verify(text).setListening(false);
        verify(text).setX(WIDTH / 2);
        verify(text).setY(HEIGHT / 2);
    }

    @Test
    public void testLeftAlignTest() throws Exception {
        final BaseGridCell<String> cell = new BaseGridCell<>(new BaseGridCellValue<>(VALUE));

        RendererUtils.getExpressionCellText(context, cell);

        verify(text).setText(VALUE);
        verify(text).setListening(false);
        verify(text).setX(5);
        verify(text).setY(5);
        verify(text).setFontFamily(BaseExpressionGridTheme.FONT_FAMILY_EXPRESSION);
        verify(text).setTextAlign(TextAlign.LEFT);
    }
}
