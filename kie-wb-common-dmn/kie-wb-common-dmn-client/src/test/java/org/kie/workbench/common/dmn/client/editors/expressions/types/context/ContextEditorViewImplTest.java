/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.dmn.client.editors.expressions.types.context;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.Context;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.DefaultGridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;

import static org.junit.Assert.*;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.BaseExpressionEditorViewImpl.LIENZO_PANEL_HEIGHT;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.BaseExpressionEditorViewImpl.LIENZO_PANEL_WIDTH;
import static org.mockito.Mockito.*;

@RunWith(LienzoMockitoTestRunner.class)
public class ContextEditorViewImplTest {

    private ContextEditorViewImpl view;

    @Before
    public void setup() {
        view = spy(new ContextEditorViewImpl());
    }

    @Test
    public void testSetExpression() throws Exception {

        final Context expectedExpression = mock(Context.class);
        final DefaultGridLayer gridLayer = mock(DefaultGridLayer.class);
        GridLienzoPanel lienzoPanel = mock(GridLienzoPanel.class);

        doReturn(gridLayer).when(view).getGridLayer();
        doReturn(lienzoPanel).when(view).getGridPanel();

        view.setExpression(expectedExpression);

        final Context actualExpression = view.getExpression().orElseThrow(UnsupportedOperationException::new);

        verify(gridLayer).batch();
        verify(lienzoPanel).updatePanelSize(LIENZO_PANEL_WIDTH, LIENZO_PANEL_HEIGHT);
        assertEquals(expectedExpression, actualExpression);
    }
}
