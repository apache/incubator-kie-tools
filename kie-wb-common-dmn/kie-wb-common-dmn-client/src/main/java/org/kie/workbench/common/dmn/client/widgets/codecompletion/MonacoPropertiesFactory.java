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
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import elemental2.core.JsRegExp;
import jsinterop.base.Js;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.feel.FEELLanguageService.Position;
import org.uberfire.client.views.pfly.monaco.jsinterop.MonacoLanguages.ProvideCompletionItemsFunction;

import static org.kie.workbench.common.dmn.client.widgets.codecompletion.MonacoFEELInitializer.FEEL_RESERVED_KEYWORDS;

public class MonacoPropertiesFactory {

    public static final String FEEL_LANGUAGE_ID = "feel-language";
    public static final String FEEL_THEME_ID = "feel-theme";

    /*
     * This method returns a JavaScript object with properties specified here:
     * https://microsoft.github.io/monaco-editor/api/interfaces/monaco.editor.ieditorconstructionoptions.html
     */
    public JavaScriptObject getConstructionOptions() {

        final JSONObject options = makeJSONObject();
        final JSONObject scrollbar = makeJSONObject();
        final JSONObject miniMap = makeJSONObject();

        options.put("language", makeJSONString(FEEL_LANGUAGE_ID));
        options.put("theme", makeJSONString(FEEL_THEME_ID));

        options.put("renderLineHighlight", makeJSONString("none"));
        options.put("lineNumbers", makeJSONString("off"));

        options.put("fontSize", makeJSONNumber(12));
        options.put("lineNumbersMinChars", makeJSONNumber(1));
        options.put("lineDecorationsWidth", makeJSONNumber(1));

        options.put("overviewRulerBorder", makeJSONBoolean(false));
        options.put("scrollBeyondLastLine", makeJSONBoolean(false));
        options.put("snippetSuggestions", makeJSONBoolean(false));
        options.put("useTabStops", makeJSONBoolean(false));
        options.put("contextmenu", makeJSONBoolean(false));
        options.put("folding", makeJSONBoolean(false));
        miniMap.put("enabled", makeJSONBoolean(false));
        scrollbar.put("useShadows", makeJSONBoolean(false));

        options.put("automaticLayout", makeJSONBoolean(true));
        options.put("renderWhitespace", makeJSONBoolean(true));
        options.put("hideCursorInOverviewRuler", makeJSONBoolean(true));

        options.put("scrollbar", scrollbar);
        options.put("minimap", miniMap);

        return options.getJavaScriptObject();
    }

    /*
     * This method returns a JavaScript object with properties specified here:
     * https://microsoft.github.io/monaco-editor/api/interfaces/monaco.editor.istandalonethemedata.html
     */
    public JavaScriptObject getThemeData() {

        final JSONObject themeDefinition = makeJSONObject();
        final JSONObject colors = makeJSONObject();
        final JSONString colorHEXCode = makeJSONString("#000000");
        final JSONString base = makeJSONString("vs");
        final JSONBoolean inherit = makeJSONBoolean(false);
        final JSONArray rules = getRules();

        colors.put("editorLineNumber.foreground", colorHEXCode);
        themeDefinition.put("base", base);
        themeDefinition.put("inherit", inherit);
        themeDefinition.put("rules", rules);
        themeDefinition.put("colors", colors);

        return themeDefinition.getJavaScriptObject();
    }

    public JSONArray getRules() {

        final JSONObject rule1 = makeJSONObject();
        final JSONObject rule2 = makeJSONObject();
        final JSONObject rule3 = makeJSONObject();
        final JSONObject rule4 = makeJSONObject();
        final JSONObject rule5 = makeJSONObject();
        final JSONArray rules = makeJSONArray();

        rule1.put("token", makeJSONString("feel-keyword"));
        rule1.put("foreground", makeJSONString("26268C"));
        rule1.put("fontStyle", makeJSONString("bold"));

        rule2.put("token", makeJSONString("feel-numeric"));
        rule2.put("foreground", makeJSONString("3232E7"));

        rule3.put("token", makeJSONString("feel-boolean"));
        rule3.put("foreground", makeJSONString("26268D"));
        rule3.put("fontStyle", makeJSONString("bold"));

        rule4.put("token", makeJSONString("feel-string"));
        rule4.put("foreground", makeJSONString("2A9343"));
        rule4.put("fontStyle", makeJSONString("bold"));

        rule5.put("token", makeJSONString("feel-function"));
        rule5.put("foreground", makeJSONString("3232E8"));

        push(rules, rule1);
        push(rules, rule2);
        push(rules, rule3);
        push(rules, rule4);
        push(rules, rule5);

        return rules;
    }

    /*
     * This method returns a JavaScript object with properties specified here:
     * https://microsoft.github.io/monaco-editor/api/interfaces/monaco.languages.completionitemprovider.html
     */
    public JavaScriptObject getCompletionItemProvider(final MonacoSuggestionsPropertyFactory suggestionsPropertyFactory) {
        return makeJavaScriptObject("provideCompletionItems", makeJSONObject(getProvideCompletionItemsFunction(suggestionsPropertyFactory)));
    }

    /*
     * This method returns a JavaScript object with properties specified here:
     * https://microsoft.github.io/monaco-editor/api/interfaces/monaco.languages.completionlist.html
     */
    ProvideCompletionItemsFunction getProvideCompletionItemsFunction(final MonacoSuggestionsPropertyFactory suggestionsPropertyFactory) {
        return (model, position) -> {
            final JSONObject suggestions = makeJSONObject();
            final String expression = model.getValue();
            final Position lspPosition = new Position(position.getLineNumber(), position.getColumn() - 1);
            suggestions.put("suggestions", suggestionsPropertyFactory.create(expression, lspPosition));
            return suggestions.getJavaScriptObject();
        };
    }

    /*
     * This method returns a JavaScript object with properties specified here:
     * https://microsoft.github.io/monaco-editor/api/interfaces/monaco.languages.imonarchlanguage.html
     */
    public JavaScriptObject getLanguageDefinition() {
        return makeJavaScriptObject("tokenizer", getTokenizer());
    }

    public JSONValue getTokenizer() {
        final JSONObject tokenizer = makeJSONObject();
        tokenizer.put("root", getRoot());
        return tokenizer;
    }

    /*
     * This methods returns a collection of IShortMonarchLanguageRule1[JsRegExpt, IMonarchLanguageAction]
     * https://microsoft.github.io/monaco-editor/api/modules/monaco.languages.html#ishortmonarchlanguagerule1
     * Each item from the 'root' array represents a rule:
     * - 1st rule     - occurrences of booleans' are marked as "feel-boolean"
     * - 2nd rule     - occurrences of numbers are marked as "feel-numeric"
     * - 3rd rule     - occurrences of strings are marked as "feel-string"
     * - 4th rule     - occurrences of FEEL functions calls are marked as "feel-function"
     * - 5th/6th rule - occurrences of FEEL keywords(if, then, else, for, in, return) are marked as "feel-keyword"
     */
    public JSONArray getRoot() {
        final JSONArray root = makeJSONArray();
        push(root, row("(?:(\\btrue\\b)|(\\bfalse\\b))", "feel-boolean"));
        push(root, row("[0-9]+", "feel-numeric"));
        push(root, row("(?:\\\"(?:.*?)\\\")", "feel-string"));
        push(root, row("(?:(?:[a-z ]+\\()|(?:\\()|(?:\\)))", "feel-function"));
        push(root, row("(?:(\\b" + String.join("\\b)|(\\b", FEEL_RESERVED_KEYWORDS) + "\\b))", "feel-keyword"));
        return root;
    }

    public JSONArray row(final String pattern,
                         final String name) {
        final JSONArray row = makeJSONArray();
        row.set(0, makeJSONObject(makeRegExp(pattern)));
        row.set(1, makeJSONString(name));
        return row;
    }

    /*
     * This method returns a JavaScript object with properties specified here:
     * https://microsoft.github.io/monaco-editor/api/interfaces/monaco.languages.ilanguageextensionpoint.html
     */
    public JavaScriptObject getLanguage() {
        return makeJavaScriptObject("id", makeJSONString(FEEL_LANGUAGE_ID));
    }

    JavaScriptObject makeJavaScriptObject(final String property,
                                          final JSONValue value) {
        final JSONObject jsonObject = makeJSONObject();
        jsonObject.put(property, value);
        return jsonObject.getJavaScriptObject();
    }

    JsRegExp makeRegExp(final String pattern) {
        return new JsRegExp(pattern);
    }

    JSONArray makeJSONArray() {
        return new JSONArray();
    }

    JSONBoolean makeJSONBoolean(final boolean value) {
        return JSONBoolean.getInstance(value);
    }

    JSONString makeJSONString(final String value) {
        return new JSONString(value);
    }

    JSONValue makeJSONNumber(final int value) {
        return new JSONNumber(value);
    }

    JSONObject makeJSONObject(final Object obj) {
        return new JSONObject(Js.uncheckedCast(obj));
    }

    JSONObject makeJSONObject() {
        return new JSONObject();
    }

    void push(final JSONArray jsonArray,
              final JSONValue jsonValue) {
        jsonArray.set(jsonArray.size(), jsonValue);
    }
}
