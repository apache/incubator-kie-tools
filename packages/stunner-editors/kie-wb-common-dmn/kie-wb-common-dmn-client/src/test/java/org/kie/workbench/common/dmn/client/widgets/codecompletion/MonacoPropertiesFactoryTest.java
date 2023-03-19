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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.core.JsRegExp;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.feel.FEELLanguageService;
import org.mockito.Mock;
import org.uberfire.client.views.pfly.monaco.jsinterop.ITextModel;
import org.uberfire.client.views.pfly.monaco.jsinterop.MonacoLanguages.ProvideCompletionItemsFunction;
import org.uberfire.client.views.pfly.monaco.jsinterop.Position;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.client.widgets.codecompletion.MonacoPropertiesFactory.FEEL_LANGUAGE_ID;
import static org.kie.workbench.common.dmn.client.widgets.codecompletion.MonacoPropertiesFactory.FEEL_THEME_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class MonacoPropertiesFactoryTest {

    private MonacoPropertiesFactory factory;

    @Mock
    private MonacoSuggestionsPropertyFactory suggestionsPropertyFactory;

    @Before
    public void setup() {
        factory = spy(new MonacoPropertiesFactory());
    }

    @Test
    public void testGetConstructionOptions() {

        final JSONObject options = mock(JSONObject.class);
        final JSONObject scrollbar = mock(JSONObject.class);
        final JSONObject miniMap = mock(JSONObject.class);
        final JSONString language = mock(JSONString.class);
        final JSONString theme = mock(JSONString.class);
        final JSONString renderLineHighlight = mock(JSONString.class);
        final JSONString lineNumbers = mock(JSONString.class);
        final JSONBoolean overviewRulerBorder = mock(JSONBoolean.class);
        final JSONBoolean scrollBeyondLastLine = mock(JSONBoolean.class);
        final JSONBoolean snippetSuggestions = mock(JSONBoolean.class);
        final JSONBoolean useTabStops = mock(JSONBoolean.class);
        final JSONBoolean contextmenu = mock(JSONBoolean.class);
        final JSONBoolean folding = mock(JSONBoolean.class);
        final JSONBoolean enabled = mock(JSONBoolean.class);
        final JSONBoolean useShadows = mock(JSONBoolean.class);
        final JSONValue fontSize = mock(JSONValue.class);
        final JSONValue lineNumbersMinChars = mock(JSONValue.class);
        final JSONValue lineDecorationsWidth = mock(JSONValue.class);
        final JSONBoolean automaticLayout = mock(JSONBoolean.class);
        final JSONBoolean renderWhitespace = mock(JSONBoolean.class);
        final JSONBoolean hideCursorInOverviewRuler = mock(JSONBoolean.class);
        final JavaScriptObject expectedOptions = mock(JavaScriptObject.class);

        doReturn(language).when(factory).makeJSONString(FEEL_LANGUAGE_ID);
        doReturn(theme).when(factory).makeJSONString(FEEL_THEME_ID);
        doReturn(renderLineHighlight).when(factory).makeJSONString("none");
        doReturn(lineNumbers).when(factory).makeJSONString("off");
        doReturn(fontSize).when(factory).makeJSONNumber(12);

        when(options.getJavaScriptObject()).thenReturn(expectedOptions);

        // mocking spy with when(...).thenReturn(...) because the many calls are being mocked.
        when(factory.makeJSONObject()).thenReturn(options, scrollbar, miniMap);
        when(factory.makeJSONNumber(1)).thenReturn(lineNumbersMinChars, lineDecorationsWidth);
        when(factory.makeJSONBoolean(false)).thenReturn(overviewRulerBorder, scrollBeyondLastLine, snippetSuggestions, useTabStops, contextmenu, folding, enabled, useShadows);
        when(factory.makeJSONBoolean(true)).thenReturn(automaticLayout, renderWhitespace, hideCursorInOverviewRuler);

        final JavaScriptObject actualOptions = factory.getConstructionOptions();

        verify(options).put("language", language);
        verify(options).put("theme", theme);
        verify(options).put("renderLineHighlight", renderLineHighlight);
        verify(options).put("fontSize", fontSize);
        verify(options).put("lineNumbersMinChars", lineNumbersMinChars);
        verify(options).put("lineDecorationsWidth", lineDecorationsWidth);
        verify(options).put("overviewRulerBorder", overviewRulerBorder);
        verify(options).put("scrollBeyondLastLine", scrollBeyondLastLine);
        verify(options).put("snippetSuggestions", snippetSuggestions);
        verify(options).put("useTabStops", useTabStops);
        verify(options).put("contextmenu", contextmenu);
        verify(options).put("folding", folding);
        verify(miniMap).put("enabled", enabled);
        verify(scrollbar).put("useShadows", useShadows);
        verify(options).put("automaticLayout", automaticLayout);
        verify(options).put("renderWhitespace", renderWhitespace);
        verify(options).put("hideCursorInOverviewRuler", hideCursorInOverviewRuler);

        assertEquals(expectedOptions, actualOptions);
    }

    @Test
    public void testGetThemeData() {

        final JSONObject themeDefinition = mock(JSONObject.class);
        final JSONObject colors = mock(JSONObject.class);
        final JSONString colorHEXCode = mock(JSONString.class);
        final JSONString base = mock(JSONString.class);
        final JSONBoolean inherit = mock(JSONBoolean.class);
        final JSONArray rules = mock(JSONArray.class);
        final JavaScriptObject expectedEditorThemeData = mock(JavaScriptObject.class);

        doReturn(colorHEXCode).when(factory).makeJSONString("#000000");
        doReturn(base).when(factory).makeJSONString("vs");
        doReturn(inherit).when(factory).makeJSONBoolean(false);
        doReturn(rules).when(factory).getRules();

        when(themeDefinition.getJavaScriptObject()).thenReturn(expectedEditorThemeData);

        // mocking spy with when(...).thenReturn(...) because the many calls are being mocked.
        when(factory.makeJSONObject()).thenReturn(themeDefinition, colors);

        final JavaScriptObject actualEditorThemeData = factory.getThemeData();

        verify(colors).put("editorLineNumber.foreground", colorHEXCode);
        verify(themeDefinition).put("base", base);
        verify(themeDefinition).put("inherit", inherit);
        verify(themeDefinition).put("rules", rules);
        verify(themeDefinition).put("colors", colors);

        assertEquals(expectedEditorThemeData, actualEditorThemeData);
    }

    @Test
    public void testGetRules() {

        final JSONObject rule1 = mock(JSONObject.class);
        final JSONObject rule2 = mock(JSONObject.class);
        final JSONObject rule3 = mock(JSONObject.class);
        final JSONObject rule4 = mock(JSONObject.class);
        final JSONObject rule5 = mock(JSONObject.class);
        final JSONString token1 = mock(JSONString.class);
        final JSONString foreground1 = mock(JSONString.class);
        final JSONString token2 = mock(JSONString.class);
        final JSONString foreground2 = mock(JSONString.class);
        final JSONString token3 = mock(JSONString.class);
        final JSONString foreground3 = mock(JSONString.class);
        final JSONString token4 = mock(JSONString.class);
        final JSONString foreground4 = mock(JSONString.class);
        final JSONString token5 = mock(JSONString.class);
        final JSONString foreground5 = mock(JSONString.class);
        final JSONArray expectedRules = mock(JSONArray.class);
        final JSONString bold = mock(JSONString.class);

        doReturn(token1).when(factory).makeJSONString("feel-keyword");
        doReturn(token2).when(factory).makeJSONString("feel-numeric");
        doReturn(token3).when(factory).makeJSONString("feel-boolean");
        doReturn(token4).when(factory).makeJSONString("feel-string");
        doReturn(token5).when(factory).makeJSONString("feel-function");

        doReturn(foreground1).when(factory).makeJSONString("26268C");
        doReturn(foreground2).when(factory).makeJSONString("3232E7");
        doReturn(foreground3).when(factory).makeJSONString("26268D");
        doReturn(foreground4).when(factory).makeJSONString("2A9343");
        doReturn(foreground5).when(factory).makeJSONString("3232E8");

        doReturn(expectedRules).when(factory).makeJSONArray();
        doReturn(bold).when(factory).makeJSONString("bold");

        // mocking spy with when(...).thenReturn(...) because the many calls are being mocked.
        when(factory.makeJSONObject()).thenReturn(rule1, rule2, rule3, rule4, rule5);

        final JSONArray actualRules = factory.getRules();

        verify(rule1).put("token", token1);
        verify(rule2).put("token", token2);
        verify(rule3).put("token", token3);
        verify(rule4).put("token", token4);
        verify(rule5).put("token", token5);

        verify(rule1).put("foreground", foreground1);
        verify(rule2).put("foreground", foreground2);
        verify(rule3).put("foreground", foreground3);
        verify(rule4).put("foreground", foreground4);
        verify(rule5).put("foreground", foreground5);

        verify(rule1).put("fontStyle", bold);
        verify(rule4).put("fontStyle", bold);
        verify(rule3).put("fontStyle", bold);

        verify(factory).push(expectedRules, rule1);
        verify(factory).push(expectedRules, rule2);
        verify(factory).push(expectedRules, rule3);
        verify(factory).push(expectedRules, rule4);
        verify(factory).push(expectedRules, rule5);

        assertEquals(expectedRules, actualRules);
    }

    @Test
    public void testRow() {

        final String pattern = "pattern";
        final String name = "name";
        final JsRegExp regExp = mock(JsRegExp.class);
        final JSONObject jsonRegExp = mock(JSONObject.class);
        final JSONString jsonName = mock(JSONString.class);
        final JSONArray expectedRow = mock(JSONArray.class);

        doReturn(regExp).when(factory).makeRegExp(pattern);
        doReturn(jsonRegExp).when(factory).makeJSONObject(regExp);
        doReturn(jsonName).when(factory).makeJSONString(name);
        doReturn(expectedRow).when(factory).makeJSONArray();

        final JSONValue actualRow = factory.row(pattern, name);

        verify(expectedRow).set(0, jsonRegExp);
        verify(expectedRow).set(1, jsonName);
        assertEquals(expectedRow, actualRow);
    }

    @Test
    public void testGetLanguage() {

        final JSONString languageId = mock(JSONString.class);
        final JavaScriptObject expectedLanguage = mock(JavaScriptObject.class);

        doReturn(languageId).when(factory).makeJSONString(FEEL_LANGUAGE_ID);
        doReturn(expectedLanguage).when(factory).makeJavaScriptObject("id", languageId);

        final JavaScriptObject actualLanguage = factory.getLanguage();

        assertEquals(expectedLanguage, actualLanguage);
    }

    @Test
    public void testGetCompletionItemProvider() {

        final ProvideCompletionItemsFunction provideCompletionItemsFunction = mock(ProvideCompletionItemsFunction.class);
        final JSONObject functionObject = mock(JSONObject.class);
        final JavaScriptObject expectedCompletionItemProvider = mock(JavaScriptObject.class);

        doReturn(provideCompletionItemsFunction).when(factory).getProvideCompletionItemsFunction(suggestionsPropertyFactory);
        doReturn(functionObject).when(factory).makeJSONObject(provideCompletionItemsFunction);
        doReturn(expectedCompletionItemProvider).when(factory).makeJavaScriptObject("provideCompletionItems", functionObject);

        final JavaScriptObject actualCompletionItemProvider = factory.getCompletionItemProvider(suggestionsPropertyFactory);

        assertEquals(expectedCompletionItemProvider, actualCompletionItemProvider);
    }

    @Test
    public void testGetProvideCompletionItemsFunction() {

        final JavaScriptObject expectedSuggestions = mock(JavaScriptObject.class);
        final JSONObject expectedJSONObjectSuggestions = mock(JSONObject.class);
        final JSONArray suggestions = mock(JSONArray.class);
        final ITextModel model = mock(ITextModel.class);
        final Position position = mock(Position.class);
        final String expression = "expression";
        final int line = 2;
        final int column = 3;
        final FEELLanguageService.Position lspPosition = new FEELLanguageService.Position(line, column - 1);

        doReturn(expectedJSONObjectSuggestions).when(factory).makeJSONObject();
        when(model.getValue()).thenReturn(expression);
        when(position.getLineNumber()).thenReturn(line);
        when(position.getColumn()).thenReturn(column);
        when(expectedJSONObjectSuggestions.getJavaScriptObject()).thenReturn(expectedSuggestions);
        when(suggestionsPropertyFactory.create(any(), any())).thenReturn(suggestions);

        final JavaScriptObject actualSuggestions = factory.getProvideCompletionItemsFunction(suggestionsPropertyFactory).call(model, position);

        verify(suggestionsPropertyFactory).create(eq(expression), eq(lspPosition));
        verify(expectedJSONObjectSuggestions).put("suggestions", suggestions);
        assertEquals(expectedSuggestions, actualSuggestions);
    }

    @Test
    public void testGetLanguageDefinition() {

        final JSONValue tokenizer = mock(JSONValue.class);
        final JavaScriptObject expectedLanguageDefinition = mock(JavaScriptObject.class);

        doReturn(expectedLanguageDefinition).when(factory).makeJavaScriptObject("tokenizer", tokenizer);
        doReturn(tokenizer).when(factory).getTokenizer();

        final JavaScriptObject actualLanguageDefinition = factory.getLanguageDefinition();

        assertEquals(expectedLanguageDefinition, actualLanguageDefinition);
    }

    @Test
    public void testGetTokenizer() {

        final JSONObject expectedTokenizer = mock(JSONObject.class);
        final JSONArray root = mock(JSONArray.class);

        doReturn(expectedTokenizer).when(factory).makeJSONObject();
        doReturn(root).when(factory).getRoot();

        final JSONValue actualTokenizer = factory.getTokenizer();

        verify(expectedTokenizer).put("root", root);
        assertEquals(expectedTokenizer, actualTokenizer);
    }

    @Test
    public void testGetRoot() {

        final JSONArray expectedRoot = mock(JSONArray.class);
        final JSONArray row1 = mock(JSONArray.class);
        final JSONArray row2 = mock(JSONArray.class);
        final JSONArray row3 = mock(JSONArray.class);
        final JSONArray row4 = mock(JSONArray.class);
        final JSONArray row5 = mock(JSONArray.class);

        doReturn(expectedRoot).when(factory).makeJSONArray();
        doReturn(row1).when(factory).row("(?:(\\btrue\\b)|(\\bfalse\\b))", "feel-boolean");
        doReturn(row2).when(factory).row("[0-9]+", "feel-numeric");
        doReturn(row3).when(factory).row("(?:\\\"(?:.*?)\\\")", "feel-string");
        doReturn(row4).when(factory).row("(?:(?:[a-z ]+\\()|(?:\\()|(?:\\)))", "feel-function");
        doReturn(row5).when(factory).row("(?:(\\bfor\\b)|(\\breturn\\b)|(\\bif\\b)|(\\bthen\\b)|(\\belse\\b)" +
                                                 "|(\\bsome\\b)|(\\bevery\\b)|(\\bsatisfies\\b)|(\\binstance\\b)" +
                                                 "|(\\bof\\b)|(\\bin\\b)|(\\bfunction\\b)|(\\bexternal\\b)|(\\bor\\b)" +
                                                 "|(\\band\\b)|(\\bbetween\\b)|(\\bnot\\b)|(\\bnull\\b)|(\\btrue\\b)" +
                                                 "|(\\bfalse\\b))", "feel-keyword");

        final JSONArray actualRoot = factory.getRoot();

        verify(factory).push(expectedRoot, row1);
        verify(factory).push(expectedRoot, row2);
        verify(factory).push(expectedRoot, row3);
        verify(factory).push(expectedRoot, row4);
        verify(factory).push(expectedRoot, row5);
        assertEquals(expectedRoot, actualRoot);
    }
}
