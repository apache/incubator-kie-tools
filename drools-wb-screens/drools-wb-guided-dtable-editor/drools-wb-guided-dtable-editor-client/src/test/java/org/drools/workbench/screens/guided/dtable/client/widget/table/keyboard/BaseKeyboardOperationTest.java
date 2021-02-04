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
package org.drools.workbench.screens.guided.dtable.client.widget.table.keyboard;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.KeyboardOperation;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.KeyboardOperation.TriStateBoolean;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.Silent.class)
public abstract class BaseKeyboardOperationTest<H extends KeyboardOperation> extends BaseKeyboardTest {

    protected H handler;

    @Before
    public void setup() {
        super.setup();
        this.handler = getHandler(gridLayer);
    }

    protected abstract H getHandler(final GridLayer layer);

    protected abstract int getExpectedKeyCode();

    protected abstract TriStateBoolean getExpectedShiftKeyState();

    protected abstract TriStateBoolean getExpectedControlKeyState();

    @Test
    public void checkConfiguration() {
        assertEquals(getExpectedKeyCode(),
                     handler.getKeyCode());
        assertEquals(getExpectedShiftKeyState(),
                     handler.isShiftKeyDown());
        assertEquals(getExpectedControlKeyState(),
                     handler.isControlKeyDown());
    }
}


