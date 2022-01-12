/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.wires.core.grids.client.widget.grid.impl;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.SelectionExtension;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.scrollbars.GridLienzoScrollable;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class KeyboardOperationMoveTest {

    @Mock
    private GridLayer gridLayer;

    @Mock
    private GridLienzoScrollable gridLienzoPanel;

    @Mock
    private SelectionExtension extension;

    private KeyboardOperationMove keyboardOperationMove;

    @Before
    public void setup() {

        keyboardOperationMove = spy(new KeyboardOperationMove(gridLayer,
                                                              gridLienzoPanel) {
            @Override
            public int getKeyCode() {
                return 0;
            }

            @Override
            SelectionExtension getSelectionExtension() {
                return extension;
            }
        });
    }

    @Test
    public void testPerform() {

        final GridWidget gridWidget = mock(GridWidget.class);
        doNothing().when(keyboardOperationMove).baseScrollSelectedCellIntoView(gridWidget);

        keyboardOperationMove.perform(gridWidget, true, true);

        verify(gridWidget).adjustSelection(extension, true);
        verify(keyboardOperationMove).baseScrollSelectedCellIntoView(gridWidget);
        verify(gridLienzoPanel).refreshScrollPosition();
    }
}