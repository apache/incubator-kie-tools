/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.displayer.client.widgets.filter;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MultipleTextParameterEditorTest {

    MultipleTextParameterEditor presenter;

    @Mock
    MultipleTextParameterEditor.View view;

    @Mock
    Command changedEvent;

    @Before
    public void init() {
        presenter = new MultipleTextParameterEditor(view);
    }

    @Test
    public void testShowSingleValue() {
        presenter.setValues(Arrays.asList("val"));
        verify(view).setValue("val");
    }

    @Test
    public void testShowMultipleValues() {
        presenter.setValues(Arrays.asList("val1", "val2", "val3"));
        verify(view).setValue("val1 | val2 | val3");
    }

    @Test
    public void testParseVoidInput() {
        when(view.getValue()).thenReturn("");
        presenter.valueChanged();
        assertTrue(presenter.getValues().isEmpty());
        verify(view).error();
    }

    @Test
    public void testParseSingleInput() {
        when(view.getValue()).thenReturn("val");
        presenter.valueChanged();
        assertEquals(presenter.getValues().size(), 1);
        assertEquals(presenter.getValues().get(0), "val");
    }

    @Test
    public void testMultipleInput() {
        when(view.getValue()).thenReturn("|1| 2 | 3|4|  ");
        presenter.valueChanged();
        assertEquals(presenter.getValues().size(), 4);
        assertEquals(presenter.getValues().get(0), "1");
        assertEquals(presenter.getValues().get(1), "2");
        assertEquals(presenter.getValues().get(2), "3");
        assertEquals(presenter.getValues().get(3), "4");

        // Endure values are cleared on every change
        presenter.valueChanged();
        assertEquals(presenter.getValues().size(), 4);
    }

    @Test
    public void testMultipleInput2() {
        when(view.getValue()).thenReturn(",1, 2 , 3,4,  ");
        presenter.valueChanged();
        assertEquals(presenter.getValues().size(), 4);
        assertEquals(presenter.getValues().get(0), "1");
        assertEquals(presenter.getValues().get(1), "2");
        assertEquals(presenter.getValues().get(2), "3");
        assertEquals(presenter.getValues().get(3), "4");
    }
}