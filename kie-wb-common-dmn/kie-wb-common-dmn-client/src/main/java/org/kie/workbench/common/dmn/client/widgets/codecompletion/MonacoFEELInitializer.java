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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.common.collect.ImmutableList;
import org.uberfire.client.views.pfly.monaco.MonacoEditorInitializer;
import org.uberfire.client.views.pfly.monaco.jsinterop.Monaco;

import static org.kie.workbench.common.dmn.client.widgets.codecompletion.MonacoFEELInitializer.MonacoFEELInitializationStatus.INITIALIZED;
import static org.kie.workbench.common.dmn.client.widgets.codecompletion.MonacoFEELInitializer.MonacoFEELInitializationStatus.INITIALIZING;
import static org.kie.workbench.common.dmn.client.widgets.codecompletion.MonacoFEELInitializer.MonacoFEELInitializationStatus.NOT_INITIALIZED;
import static org.kie.workbench.common.dmn.client.widgets.codecompletion.MonacoPropertiesFactory.FEEL_LANGUAGE_ID;
import static org.kie.workbench.common.dmn.client.widgets.codecompletion.MonacoPropertiesFactory.FEEL_THEME_ID;

@ApplicationScoped
public class MonacoFEELInitializer {

    public static final ImmutableList<String> FEEL_RESERVED_KEYWORDS = ImmutableList.of(
            "for", "return", "if", "then", "else", "some", "every", "satisfies", "instance", "of",
            "in", "function", "external", "or", "and", "between", "not", "null", "true", "false"
    );
    private final MonacoFEELVariableSuggestions variableSuggestions;
    private MonacoFEELInitializationStatus initializationStatus = NOT_INITIALIZED;

    @Inject
    public MonacoFEELInitializer(final MonacoFEELVariableSuggestions variableSuggestions) {
        this.variableSuggestions = variableSuggestions;
    }

    public void initializeFEELEditor() {

        if (isFEELInitialized()) {
            return;
        }

        setFEELAsInitializing();
        makeMonacoEditorInitializer().require(onMonacoLoaded());
    }

    Consumer<Monaco> onMonacoLoaded() {
        final MonacoPropertiesFactory properties = makeMonacoPropertiesFactory();
        return monaco -> {
            monaco.languages.register(properties.getLanguage());
            monaco.languages.setMonarchTokensProvider(FEEL_LANGUAGE_ID, properties.getLanguageDefinition());
            monaco.languages.registerCompletionItemProvider(FEEL_LANGUAGE_ID, properties.getCompletionItemProvider(variableSuggestions));
            monaco.editor.defineTheme(FEEL_THEME_ID, properties.getThemeData());
            setFEELAsInitialized();
        };
    }

    MonacoEditorInitializer makeMonacoEditorInitializer() {
        return new MonacoEditorInitializer();
    }

    MonacoPropertiesFactory makeMonacoPropertiesFactory() {
        return new MonacoPropertiesFactory();
    }

    void setFEELAsInitialized() {
        initializationStatus = INITIALIZED;
    }

    void setFEELAsInitializing() {
        initializationStatus = INITIALIZING;
    }

    boolean isFEELInitialized() {
        return INITIALIZED == getInitializationStatus() || INITIALIZING == getInitializationStatus();
    }

    public MonacoFEELInitializationStatus getInitializationStatus() {
        return initializationStatus;
    }

    enum MonacoFEELInitializationStatus {
        NOT_INITIALIZED,
        INITIALIZING,
        INITIALIZED
    }
}
