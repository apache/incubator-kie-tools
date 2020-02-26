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
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.SelectionExtension;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;

import static org.junit.Assert.assertEquals;

@RunWith(LienzoMockitoTestRunner.class)
public class KeyboardOperationMoveLeftTest {

    @Mock
    private GridLayer gridLayer;

    @Mock
    private GridLienzoPanel panel;

    private KeyboardOperationMoveLeft keyboardOperation;

    @Before
    public void setup() {
        keyboardOperation = new KeyboardOperationMoveLeft(gridLayer, panel);
    }

    @Test
    public void testGetSelectionExtension() {

        final SelectionExtension actual = keyboardOperation.getSelectionExtension();
        assertEquals(SelectionExtension.LEFT, actual);
    }
}