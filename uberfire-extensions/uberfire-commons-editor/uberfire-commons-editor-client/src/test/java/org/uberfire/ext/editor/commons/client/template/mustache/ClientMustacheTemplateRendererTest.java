/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.editor.commons.client.template.mustache;

import java.util.function.Function;

import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.resources.client.TextResource;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ClientMustacheTemplateRendererTest {

    private ClientMustacheTemplateRenderer tested;

    @Mock
    private MustacheSource mustacheSource;

    @Mock
    private ScriptInjector.FromString script;

    @Mock
    private TextResource source;

    private final String SOURCE_TXT = "source";

    @Mock
    private Function<String, ScriptInjector.FromString> injector;

    @Before
    public void setUp() throws Exception {
        tested = new ClientMustacheTemplateRenderer(() -> mustacheSource, injector);
        when(mustacheSource.mustache()).thenReturn(source);
        when(source.getText()).thenReturn(SOURCE_TXT);
        when(injector.apply(SOURCE_TXT)).thenReturn(script);
        when(script.setWindow(any())).thenReturn(script);
        when(script.setRemoveTag(anyBoolean())).thenReturn(script);
    }

    @Test
    public void init() {
        tested.init();
        verify(source).getText();
        verify(injector).apply(SOURCE_TXT);
        verify(script).setWindow(ScriptInjector.TOP_WINDOW);
        verify(script).setRemoveTag(false);
        verify(script).inject();
    }
}