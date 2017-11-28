/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LikeToFunctionEditorTest {

    LikeToFunctionEditor presenter;

    @Mock
    LikeToFunctionEditor.View view;

    @Mock
    Command changedEvent;

    @Before
    public void init() {
        presenter = new LikeToFunctionEditor(view);
    }

    @Test
    public void testShowValue() {
        presenter.setPattern("%val%");
        verify(view).setPattern("%val%");
    }

    @Test
    public void testParseVoidInput() {
        when(view.getPattern()).thenReturn("");
        presenter.viewUpdated();
        verify(view).error();
        assertNull(presenter.getPattern());
    }

    @Test
    public void testParseInput() {
        when(view.getPattern()).thenReturn("val");
        presenter.viewUpdated();
        assertEquals(presenter.getPattern(), "val");
    }
}