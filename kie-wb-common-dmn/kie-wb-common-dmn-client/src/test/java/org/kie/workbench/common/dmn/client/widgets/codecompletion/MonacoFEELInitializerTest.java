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

package org.kie.workbench.common.dmn.client.widgets.codecompletion;

import java.util.function.Consumer;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.core.client.JavaScriptObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.client.views.pfly.monaco.MonacoEditorInitializer;
import org.uberfire.client.views.pfly.monaco.jsinterop.Monaco;
import org.uberfire.client.views.pfly.monaco.jsinterop.MonacoEditor;
import org.uberfire.client.views.pfly.monaco.jsinterop.MonacoLanguages;

import static org.kie.workbench.common.dmn.client.widgets.codecompletion.MonacoFEELInitializer.MonacoFEELInitializationStatus.INITIALIZED;
import static org.kie.workbench.common.dmn.client.widgets.codecompletion.MonacoFEELInitializer.MonacoFEELInitializationStatus.INITIALIZING;
import static org.kie.workbench.common.dmn.client.widgets.codecompletion.MonacoFEELInitializer.MonacoFEELInitializationStatus.NOT_INITIALIZED;
import static org.kie.workbench.common.dmn.client.widgets.codecompletion.MonacoPropertiesFactory.FEEL_LANGUAGE_ID;
import static org.kie.workbench.common.dmn.client.widgets.codecompletion.MonacoPropertiesFactory.FEEL_THEME_ID;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class MonacoFEELInitializerTest {

    private MonacoFEELInitializer initializer;

    @Before
    public void setup() {
        initializer = spy(new MonacoFEELInitializer());
    }

    @Test
    public void testInitializeFEELEditorWhenEditorIsNotInitialized() {

        final MonacoEditorInitializer monacoEditorInitializer = mock(MonacoEditorInitializer.class);
        final Consumer<Monaco> onMonacoLoaded = m -> {/* Nothing. */};

        doReturn(onMonacoLoaded).when(initializer).onMonacoLoaded();
        doReturn(monacoEditorInitializer).when(initializer).makeMonacoEditorInitializer();
        doReturn(NOT_INITIALIZED).when(initializer).getInitializationStatus();

        initializer.initializeFEELEditor();

        verify(monacoEditorInitializer).require(onMonacoLoaded);
    }

    @Test
    public void testInitializeFEELEditorWhenEditorIsInitializing() {

        final MonacoEditorInitializer monacoEditorInitializer = mock(MonacoEditorInitializer.class);

        doReturn(monacoEditorInitializer).when(initializer).makeMonacoEditorInitializer();
        doReturn(INITIALIZING).when(initializer).getInitializationStatus();

        initializer.initializeFEELEditor();

        verify(monacoEditorInitializer, never()).require(any());
    }

    @Test
    public void testInitializeFEELEditorWhenEditorIsInitialized() {

        final MonacoEditorInitializer monacoEditorInitializer = mock(MonacoEditorInitializer.class);

        doReturn(monacoEditorInitializer).when(initializer).makeMonacoEditorInitializer();
        doReturn(INITIALIZED).when(initializer).getInitializationStatus();

        initializer.initializeFEELEditor();

        verify(monacoEditorInitializer, never()).require(any());
    }

    @Test
    public void testOnMonacoLoaded() {

        final MonacoPropertiesFactory properties = mock(MonacoPropertiesFactory.class);
        final MonacoLanguages languages = mock(MonacoLanguages.class);
        final JavaScriptObject language = mock(JavaScriptObject.class);
        final JavaScriptObject languageDefinition = mock(JavaScriptObject.class);
        final JavaScriptObject completionItemProvider = mock(JavaScriptObject.class);
        final JavaScriptObject themeData = mock(JavaScriptObject.class);
        final JavaScriptObject constructionOptions = mock(JavaScriptObject.class);
        final MonacoEditor editor = mock(MonacoEditor.class);
        final Monaco monaco = mock(Monaco.class);

        monaco.languages = languages;
        monaco.editor = editor;

        when(properties.getLanguage()).thenReturn(language);
        when(properties.getLanguageDefinition()).thenReturn(languageDefinition);
        when(properties.getCompletionItemProvider()).thenReturn(completionItemProvider);
        when(properties.getThemeData()).thenReturn(themeData);
        when(properties.getConstructionOptions()).thenReturn(constructionOptions);
        when(properties.getConstructionOptions()).thenReturn(constructionOptions);
        doReturn(properties).when(initializer).makeMonacoPropertiesFactory();

        initializer.onMonacoLoaded().accept(monaco);

        verify(languages).register(language);
        verify(languages).setMonarchTokensProvider(FEEL_LANGUAGE_ID, languageDefinition);
        verify(languages).registerCompletionItemProvider(FEEL_LANGUAGE_ID, completionItemProvider);
        verify(languages).register(language);
        verify(editor).defineTheme(FEEL_THEME_ID, themeData);
        verify(initializer).setFEELAsInitialized();
    }
}
