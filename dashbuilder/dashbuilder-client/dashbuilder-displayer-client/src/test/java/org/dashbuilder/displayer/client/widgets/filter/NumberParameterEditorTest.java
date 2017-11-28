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
public class NumberParameterEditorTest {

    NumberParameterEditor presenter;

    @Mock
    NumberParameterEditor.View view;

    @Mock
    Command changedEvent;

    @Before
    public void init() {
        presenter = new NumberParameterEditor(view);
    }

    @Test
    public void testShowValue() {
        presenter.setValue(10d);
        verify(view).setValue("10.0");
    }

    @Test
    public void testParseVoidInput() {
        when(view.getValue()).thenReturn("");
        presenter.valueChanged();
        assertNull(presenter.getValue());
        verify(view).error();;
    }

    @Test
    public void testParseSingleInput() {
        when(view.getValue()).thenReturn("3");
        presenter.valueChanged();
        verify(view, never()).error();;
        assertEquals(presenter.getValue(), 3d);
    }

    @Test
    public void testInputError() {
        when(view.getValue()).thenReturn("a");
        presenter.valueChanged();
        assertNull(presenter.getValue());
        verify(view).error();
    }
}