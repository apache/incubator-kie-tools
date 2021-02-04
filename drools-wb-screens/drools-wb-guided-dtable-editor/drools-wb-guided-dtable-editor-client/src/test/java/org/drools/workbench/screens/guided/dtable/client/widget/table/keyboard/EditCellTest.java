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

import com.google.gwt.event.dom.client.KeyCodes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.KeyboardOperation.TriStateBoolean;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.Silent.class)
public class EditCellTest extends BaseKeyboardOperationTest<EditCell> {

    @Override
    protected EditCell getHandler(final GridLayer layer) {
        return new EditCell(layer);
    }

    @Override
    protected int getExpectedKeyCode() {
        return KeyCodes.KEY_F2;
    }

    @Override
    protected TriStateBoolean getExpectedShiftKeyState() {
        return TriStateBoolean.DONT_CARE;
    }

    @Override
    protected TriStateBoolean getExpectedControlKeyState() {
        return TriStateBoolean.DONT_CARE;
    }

    @Test
    public void checkPerformanceWithSelectedCell() {
        uiModel.selectCell(1,
                           2);

        handler.perform(gridWidget,
                        true,
                        true);

        verify(gridWidget,
               times(1)).startEditingCell(eq(1),
                                          eq(2));
    }

    @Test
    public void checkPerformanceWithoutSelectedCell() {
        handler.perform(gridWidget,
                        true,
                        true);

        verify(gridWidget,
               never()).startEditingCell(anyInt(),
                                         anyInt());
    }
}
