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

import java.util.Optional;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.dom.single.HasSingletonDOMElementResource;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.SelectionExtension;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;

import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public abstract class BaseKeyDownHandlerTest extends BaseKeyboardTest {

    @Mock
    protected GridLienzoPanel gridPanel;

    @Mock
    protected HasSingletonDOMElementResource gridCell;

    @Mock
    protected GridBodyCellRenderContext context;

    protected KeyDownHandler handler;

    @Before
    public void setup() {
        super.setup();
        this.handler = getHandler();
    }

    protected abstract KeyDownHandler getHandler();

    @Test
    public void keyPressStopsPropagation() {
        final KeyDownEvent e = mockKeyDownEvent(Optional.empty(),
                                                Optional.empty(),
                                                Optional.empty());

        handler.onKeyDown(e);

        verify(e).stopPropagation();
    }

    @Test
    public void tabKeyWithShiftMovesLeft() {
        when(context.getRowIndex()).thenReturn(0);
        when(context.getColumnIndex()).thenReturn(1);
        final KeyDownEvent e = mockKeyDownEvent(Optional.of(KeyCodes.KEY_TAB),
                                                Optional.of(true),
                                                Optional.of(false));

        handler.onKeyDown(e);

        verify(gridWidget).adjustSelection(eq(SelectionExtension.LEFT),
                                           eq(false));
    }

    @Test
    public void tabKeyWithoutShiftMovesRight() {
        when(context.getRowIndex()).thenReturn(0);
        when(context.getColumnIndex()).thenReturn(1);
        final KeyDownEvent e = mockKeyDownEvent(Optional.of(KeyCodes.KEY_TAB),
                                                Optional.of(false),
                                                Optional.of(false));

        handler.onKeyDown(e);

        verify(gridWidget).adjustSelection(eq(SelectionExtension.RIGHT),
                                           eq(false));
    }

    @Test
    public void enterKeyWithShiftMovesUp() {
        when(context.getRowIndex()).thenReturn(1);
        when(context.getColumnIndex()).thenReturn(0);
        final KeyDownEvent e = mockKeyDownEvent(Optional.of(KeyCodes.KEY_ENTER),
                                                Optional.of(true),
                                                Optional.of(false));

        handler.onKeyDown(e);

        verify(gridWidget).adjustSelection(eq(SelectionExtension.UP),
                                           eq(false));
    }

    @Test
    public void enterKeyWithoutShiftMovesDown() {
        when(context.getRowIndex()).thenReturn(1);
        when(context.getColumnIndex()).thenReturn(0);
        final KeyDownEvent e = mockKeyDownEvent(Optional.of(KeyCodes.KEY_ENTER),
                                                Optional.of(false),
                                                Optional.of(false));

        handler.onKeyDown(e);

        verify(gridWidget).adjustSelection(eq(SelectionExtension.DOWN),
                                           eq(false));
    }

    @Test
    public void escapeKeyDoesNotMoveAnywhere() {
        final KeyDownEvent e = mockKeyDownEvent(Optional.of(KeyCodes.KEY_ESCAPE),
                                                Optional.of(false),
                                                Optional.of(false));

        handler.onKeyDown(e);

        verify(gridWidget,
               never()).selectCell(anyInt(),
                                   anyInt(),
                                   anyBoolean(),
                                   anyBoolean());
    }

    protected KeyDownEvent mockKeyDownEvent(final Optional<Integer> keyCode,
                                            final Optional<Boolean> isShiftKeyDown,
                                            final Optional<Boolean> isControlKeyDown) {
        final KeyDownEvent e = mock(KeyDownEvent.class);
        keyCode.ifPresent((c) -> when(e.getNativeKeyCode()).thenReturn(c));
        isShiftKeyDown.ifPresent((c) -> when(e.isShiftKeyDown()).thenReturn(c));
        isControlKeyDown.ifPresent((c) -> when(e.isControlKeyDown()).thenReturn(c));
        return e;
    }
}
