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
package org.dashbuilder.displayer.client.widgets.sourcecode;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyCollection;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SourceCodeEditorTest {

    @Mock
    SourceCodeEditor.View view;

    @Mock
    Command onChange;

    @Mock
    HtmlValidator htmlValidator;

    @Mock
    JsValidator jsValidator;

    SourceCodeEditor presenter;

    @Before
    public void init() {
        presenter = new SourceCodeEditor(view, htmlValidator, jsValidator);
        presenter.init(SourceCodeType.HTML, "Hi", new HashMap<>(), onChange);
    }

    @Test
    public void testInitialization() {
        verify(view).edit(SourceCodeType.HTML, "Hi");
    }

    @Test
    public void testEmpty() {
        reset(htmlValidator);
        presenter.init(SourceCodeType.HTML, null, new HashMap<>(), onChange);
        verify(htmlValidator, never()).validate(any());

        reset(htmlValidator);
        presenter.init(SourceCodeType.HTML, "", new HashMap<>(), onChange);
        verify(htmlValidator, never()).validate(any());

        reset(jsValidator);
        presenter.init(SourceCodeType.JAVASCRIPT, null, new HashMap<>(), onChange);
        verify(jsValidator, never()).validate(any(), anyCollection());

        reset(jsValidator);
        presenter.init(SourceCodeType.JAVASCRIPT, "", new HashMap<>(), onChange);
        verify(jsValidator, never()).validate(any(), anyCollection());
    }

    @Test
    public void testOnChange() {
        presenter.onSourceCodeChanged("howdy?");

        assertEquals(presenter.getCode(), "howdy?");
        assertEquals(presenter.hasErrors(), false);
        verify(htmlValidator).validate("howdy?");
        verify(onChange).execute();
        verify(view, never()).error(anyString());
    }

    @Test
    public void testError() {
        when(htmlValidator.validate("howdy?")).thenReturn("Error");
        boolean ok = presenter.onSourceCodeChanged("howdy?");

        assertFalse(ok);
        assertEquals(presenter.getCode(), "Hi");
        assertEquals(presenter.hasErrors(), true    );
        verify(htmlValidator).validate("howdy?");
        verify(onChange, never()).execute();
        verify(view).error("Error");
    }
}