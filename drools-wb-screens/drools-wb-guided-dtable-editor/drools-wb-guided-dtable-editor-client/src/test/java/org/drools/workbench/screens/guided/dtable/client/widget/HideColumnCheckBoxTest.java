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

package org.drools.workbench.screens.guided.dtable.client.widget;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.guided.dtable.shared.model.DTColumnConfig52;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class HideColumnCheckBoxTest {

    HideColumnCheckBox box;

    @Mock
    DTColumnConfig52 column;

    @Captor
    ArgumentCaptor<ClickHandler> clickCaptor;

    @Before
    public void setUp() throws Exception {
        box = spy(new HideColumnCheckBox());
    }

    @Test
    public void testInitChecked() throws Exception {
        when(column.isHideColumn()).thenReturn(true);
        box.init(column);
        verify(box).setValue(true);
        verify(box,
               never()).setValue(false);
    }

    @Test
    public void testInitUnchecked() throws Exception {
        when(column.isHideColumn()).thenReturn(false);
        box.init(column);
        verify(box).setValue(false);
        verify(box,
               never()).setValue(true);
    }

    @Test
    public void testClickHandlerChecked() throws Exception {
        when(box.getValue()).thenReturn(true);
        box.init(column);
        verify(box).addClickHandler(clickCaptor.capture());
        clickCaptor.getValue().onClick(null);
        verify(column).setHideColumn(true);
        verify(column,
               never()).setHideColumn(false);
    }

    @Test
    public void testClickHandlerUnchecked() throws Exception {
        when(box.getValue()).thenReturn(false);
        box.init(column);
        verify(box).addClickHandler(clickCaptor.capture());
        clickCaptor.getValue().onClick(null);
        verify(column).setHideColumn(false);
        verify(column,
               never()).setHideColumn(true);
    }
}
