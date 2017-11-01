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
package org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.listbox;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Element;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.gwtbootstrap3.client.ui.ListBox;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class MultiValueDOMElementTest {

    @Mock
    private ListBox listBox;

    @Mock
    private Element listBoxElement;

    @Mock
    private GridLayer gridLayer;

    @Mock
    private GuidedDecisionTableView gridWidget;

    @Mock
    private Style style;

    @Before
    public void setup() {
        when(listBox.getElement()).thenReturn(listBoxElement);
        when(listBoxElement.getStyle()).thenReturn(style);
    }

    @Test
    public void checkSizedToParentWidth() {
        @SuppressWarnings("unused")
        final MultiValueDOMElementMock domElement = new MultiValueDOMElementMock(listBox,
                                                                                 gridLayer,
                                                                                 gridWidget,
                                                                                 true,
                                                                                 false);

        verify(style).setWidth(eq(100.0),
                               eq(Style.Unit.PCT));
        verify(style,
               never()).setHeight(anyDouble(),
                                  any(Style.Unit.class));
    }

    @Test
    public void checkSizedToParentHeight() {
        @SuppressWarnings("unused")
        final MultiValueDOMElementMock domElement = new MultiValueDOMElementMock(listBox,
                                                                                 gridLayer,
                                                                                 gridWidget,
                                                                                 false,
                                                                                 true);

        verify(style,
               never()).setWidth(anyDouble(),
                                 any(Style.Unit.class));
        verify(style).setHeight(eq(100.0),
                                eq(Style.Unit.PCT));
    }

    private class MultiValueDOMElementMock extends MultiValueDOMElement<String, ListBox> {

        public MultiValueDOMElementMock(final ListBox widget,
                                        final GridLayer gridLayer,
                                        final GridWidget gridWidget,
                                        final boolean restrictEditorWidthToCell,
                                        final boolean restrictEditorHeightToCell) {
            super(widget,
                  gridLayer,
                  gridWidget,
                  restrictEditorWidthToCell,
                  restrictEditorHeightToCell);
        }
    }
}
