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
public class TextParameterEditorTest {

    TextParameterEditor presenter;

    @Mock
    TextParameterEditor.View view;

    @Mock
    Command changedEvent;

    @Before
    public void init() {
        presenter = new TextParameterEditor(view);
    }

    @Test
    public void testShowValue() {
        presenter.setValue("val");
        verify(view).setValue("val");
    }

    @Test
    public void testParseVoidInput() {
        when(view.getValue()).thenReturn("");
        presenter.valueChanged();
        verify(view).error();
        assertNull(presenter.getValue());
    }

    @Test
    public void testParseInput() {
        when(view.getValue()).thenReturn("val");
        presenter.valueChanged();
        assertEquals(presenter.getValue(), "val");
    }
}