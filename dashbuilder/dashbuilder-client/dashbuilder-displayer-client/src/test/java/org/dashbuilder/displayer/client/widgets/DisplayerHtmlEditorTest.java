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
package org.dashbuilder.displayer.client.widgets;

import java.util.HashSet;
import java.util.Set;

import org.dashbuilder.displayer.DisplayerAttributeDef;
import org.dashbuilder.displayer.DisplayerConstraints;
import org.dashbuilder.displayer.client.Displayer;
import org.dashbuilder.displayer.client.widgets.sourcecode.SourceCodeEditor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DisplayerHtmlEditorTest {

    @Mock
    DisplayerHtmlEditor.View view;

    @Mock
    SourceCodeEditor codeEditor;

    @Mock
    DisplayerConstraints constraints;

    @Mock
    Displayer displayer;

    DisplayerHtmlEditor presenter;

    @Before
    public void init() throws Exception {
        Set<DisplayerAttributeDef> supportedAttrs = new HashSet<>();
        supportedAttrs.add(DisplayerAttributeDef.HTML_TEMPLATE);
        supportedAttrs.add(DisplayerAttributeDef.JS_TEMPLATE);
        when(constraints.getSupportedAttributes()).thenReturn(supportedAttrs);
        when(displayer.getDisplayerConstraints()).thenReturn(constraints);

        presenter = new DisplayerHtmlEditor(view, codeEditor);
        presenter.setDisplayer(displayer);
    }

    @Test
    public void testInit() {
        verify(view).showDisplayer(displayer);
        verify(codeEditor, never()).init(any(), any(), any(), any());
    }

    @Test
    public void testSourceCodeItems() {
        verify(view).addSourceCodeItem("html");
        verify(view).addSourceCodeItem("javascript");
   }
}