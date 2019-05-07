/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.wires.core.grids.client.widget.grid.keyboard;

import java.util.Optional;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.widget.dom.single.HasSingletonDOMElementResource;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.GridColumnRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
abstract class BaseKeyDownHandlerCommonTest {

    @Mock
    protected GridLayer gridLayer;

    @Mock
    protected GridLienzoPanel gridPanel;

    @Mock
    protected GridWidget gridWidget;

    @Mock
    protected HasSingletonDOMElementResource gridCell;

    @Mock
    protected GridColumn.HeaderMetaData headerMetaData;

    @Mock
    protected GridColumnRenderer<?> columnRenderer;

    protected GridData uiModel;

    private KeyDownHandlerCommon handler;

    @Before
    public void setUpHandler() {
        uiModel = new BaseGridData();
        handler = new KeyDownHandlerCommon(gridPanel,
                                           gridLayer,
                                           gridWidget,
                                           gridCell,
                                           isTabKeyHandled(),
                                           isEnterKeyHandled(),
                                           isEscapeKeyHandled());

        for (int size = 0; size < 3; size++) {
            uiModel.appendRow(new BaseGridRow());
            uiModel.appendColumn(new BaseGridColumn<>(headerMetaData,
                                                      columnRenderer,
                                                      100.0));
        }

        when(gridWidget.getModel()).thenReturn(uiModel);
    }

    protected abstract boolean isTabKeyHandled();

    protected abstract boolean isEnterKeyHandled();

    protected abstract boolean isEscapeKeyHandled();

    @Test
    public void tabKeyCanvasActions() {
        final KeyDownEvent e = mockKeyDownEvent(Optional.of(KeyCodes.KEY_TAB),
                                                Optional.of(false),
                                                Optional.of(false));

        handler.onKeyDown(e);

        if (isTabKeyHandled()) {
            verify(gridCell).flush();
            verify(e).preventDefault();
            verifyCommonActions();
        } else {
            verifyNoMoreInteractions(gridCell);
        }
    }

    @Test
    public void enterKeyCanvasActions() {
        final KeyDownEvent e = mockKeyDownEvent(Optional.of(KeyCodes.KEY_ENTER),
                                                Optional.of(false),
                                                Optional.of(false));

        handler.onKeyDown(e);

        if (isEnterKeyHandled()) {
            verify(gridCell).flush();
            verify(e).preventDefault();
            verifyCommonActions();
        } else {
            verifyNoMoreInteractions(gridCell);
        }
    }

    @Test
    public void escapeKeyCanvasActions() {
        final KeyDownEvent e = mockKeyDownEvent(Optional.of(KeyCodes.KEY_ESCAPE),
                                                Optional.of(false),
                                                Optional.of(false));

        handler.onKeyDown(e);

        if (isEscapeKeyHandled()) {
            verify(gridCell, never()).flush();
            verifyCommonActions();
        }
    }

    private void verifyCommonActions() {
        verify(gridCell).destroyResources();
        verify(gridPanel).setFocus(eq(true));
        verify(gridLayer).batch();
    }

    private KeyDownEvent mockKeyDownEvent(final Optional<Integer> keyCode,
                                          final Optional<Boolean> isShiftKeyDown,
                                          final Optional<Boolean> isControlKeyDown) {
        final KeyDownEvent e = mock(KeyDownEvent.class);
        keyCode.ifPresent((c) -> when(e.getNativeKeyCode()).thenReturn(c));
        isShiftKeyDown.ifPresent((c) -> when(e.isShiftKeyDown()).thenReturn(c));
        isControlKeyDown.ifPresent((c) -> when(e.isControlKeyDown()).thenReturn(c));
        return e;
    }
}