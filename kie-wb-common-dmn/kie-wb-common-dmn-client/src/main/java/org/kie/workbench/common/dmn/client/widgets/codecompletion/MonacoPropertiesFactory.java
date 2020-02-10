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
import elemental2.core.RegExp;
import jsinterop.base.Js;
import org.uberfire.client.views.pfly.monaco.jsinterop.MonacoLanguages.ProvideCompletionItemsFunction;

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
    public JavaScriptObject getCompletionItemProvider(final MonacoFEELVariableSuggestions variableSuggestions) {
        return makeJavaScriptObject("provideCompletionItems", makeJSONObject(getProvideCompletionItemsFunction(variableSuggestions)));
    }

    /*
     * This method returns a JavaScript object with properties specified here:
     * https://microsoft.github.io/monaco-editor/api/interfaces/monaco.languages.completionlist.html
     */
    ProvideCompletionItemsFunction getProvideCompletionItemsFunction(final MonacoFEELVariableSuggestions variableSuggestions) {
        return () -> {
            final JSONObject suggestions = makeJSONObject();
            suggestions.put("suggestions", getSuggestions(variableSuggestions));
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
     * This methods returns a collection of IShortMonarchLanguageRule1[RegExp, IMonarchLanguageAction]
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
        push(root, row("(?:(\\bif\\b)|(\\bthen\\b)|(\\belse\\b))", "feel-keyword"));
        push(root, row("(?:(\\bfor\\b)|(\\bin\\b)|(\\breturn\\b))", "feel-keyword"));
        return root;
    }

    JSONArray getSuggestions(final MonacoFEELVariableSuggestions variableSuggestions) {

        final JSONArray suggestionTypes = makeJSONArray();

        populateVariableSuggestions(variableSuggestions, suggestionTypes);
        populateFunctionSuggestions(suggestionTypes);

        return suggestionTypes;
    }

    private void populateVariableSuggestions(final MonacoFEELVariableSuggestions variableSuggestions,
                                             final JSONArray suggestionArray) {
        variableSuggestions
                .getSuggestions()
                .forEach(variable -> push(suggestionArray, getVariableSuggestion(variable)));
    }

    private void populateFunctionSuggestions(final JSONArray suggestionTypes) {
        push(suggestionTypes, getFunctionSuggestion("abs(duration)", "abs($1)"));
        push(suggestionTypes, getFunctionSuggestion("abs(number)", "abs($1)"));
        push(suggestionTypes, getFunctionSuggestion("after(range, value)", "after($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("after(range1, range2)", "after($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("after(value, range)", "after($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("after(value1, value2)", "after($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("all(b)", "all($1)"));
        push(suggestionTypes, getFunctionSuggestion("all(list)", "all($1)"));
        push(suggestionTypes, getFunctionSuggestion("any(b)", "any($1)"));
        push(suggestionTypes, getFunctionSuggestion("any(list)", "any($1)"));
        push(suggestionTypes, getFunctionSuggestion("append(list, item)", "append($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("before(range, value)", "before($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("before(range1, range2)", "before($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("before(value, range)", "before($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("before(value1, value2)", "before($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("ceiling(n)", "ceiling($1)"));
        push(suggestionTypes, getFunctionSuggestion("code(value)", "code($1)"));
        push(suggestionTypes, getFunctionSuggestion("coincides(range1, range2)", "coincides($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("coincides(value1, value2)", "coincides($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("concatenate(list)", "concatenate($1)"));
        push(suggestionTypes, getFunctionSuggestion("contains(string, match)", "contains($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("count(c)", "count($1)"));
        push(suggestionTypes, getFunctionSuggestion("count(list)", "count($1)"));
        push(suggestionTypes, getFunctionSuggestion("date and time(date, time)", "date and time($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("date and time(from)", "date and time($1)"));
        push(suggestionTypes, getFunctionSuggestion("date and time(year, month, day, hour, minute, second)", "date and time($1, $2, $3, $4, $5, $6)"));
        push(suggestionTypes, getFunctionSuggestion("date and time(year, month, day, hour, minute, second, hour offset)", "date and time($1, $2, $3, $4, $5, $6, $7)"));
        push(suggestionTypes, getFunctionSuggestion("date and time(year, month, day, hour, minute, second, timezone)", "date and time($1, $2, $3, $4, $5, $6, $7)"));
        push(suggestionTypes, getFunctionSuggestion("date(from)", "date($1)"));
        push(suggestionTypes, getFunctionSuggestion("date(year, month, day)", "date($1, $2, $3)"));
        push(suggestionTypes, getFunctionSuggestion("day of week(date)", "day of week($1)"));
        push(suggestionTypes, getFunctionSuggestion("day of year(date)", "day of year($1)"));
        push(suggestionTypes, getFunctionSuggestion("decimal(n, scale)", "decimal($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("decision table(ctx, outputs, input expression list, input values list, output values, rule list, hit policy, default output value)", "decision table($1, $2, $3, $4, $5, $6, $7, $8)"));
        push(suggestionTypes, getFunctionSuggestion("distinct values(list)", "distinct values($1)"));
        push(suggestionTypes, getFunctionSuggestion("duration(from)", "duration($1)"));
        push(suggestionTypes, getFunctionSuggestion("during(range1, range2)", "during($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("during(value, range)", "during($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("ends with(string, match)", "ends with($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("even(number)", "even($1)"));
        push(suggestionTypes, getFunctionSuggestion("exp(number)", "exp($1)"));
        push(suggestionTypes, getFunctionSuggestion("finished by(range, value)", "finished by($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("finished by(range1, range2)", "finished by($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("finishes(range1, range2)", "finishes($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("finishes(value, range)", "finishes($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("flatten(list)", "flatten($1)"));
        push(suggestionTypes, getFunctionSuggestion("floor(n)", "floor($1)"));
        push(suggestionTypes, getFunctionSuggestion("get entries(m)", "get entries($1)"));
        push(suggestionTypes, getFunctionSuggestion("get value(m, key)", "get value($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("includes(range, value)", "includes($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("includes(range1, range2)", "includes($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("index of(list, match)", "index of($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("insert before(list, position, newItem)", "insert before($1, $2, $3)"));
        push(suggestionTypes, getFunctionSuggestion("invoke(ctx, namespace, model name, decision name, parameters)", "invoke($1, $2, $3, $4, $5)"));
        push(suggestionTypes, getFunctionSuggestion("list contains(list, element)", "list contains($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("log(number)", "log($1)"));
        push(suggestionTypes, getFunctionSuggestion("lower case(string)", "lower case($1)"));
        push(suggestionTypes, getFunctionSuggestion("matches(input, pattern)", "matches($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("matches(input, pattern, flags)", "matches($1, $2, $3)"));
        push(suggestionTypes, getFunctionSuggestion("max(c)", "max($1)"));
        push(suggestionTypes, getFunctionSuggestion("max(list)", "max($1)"));
        push(suggestionTypes, getFunctionSuggestion("mean(list)", "mean($1)"));
        push(suggestionTypes, getFunctionSuggestion("mean(n)", "mean($1)"));
        push(suggestionTypes, getFunctionSuggestion("median(list)", "median($1)"));
        push(suggestionTypes, getFunctionSuggestion("median(n)", "median($1)"));
        push(suggestionTypes, getFunctionSuggestion("meets(range1, range2)", "meets($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("met by(range1, range2)", "met by($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("min(c)", "min($1)"));
        push(suggestionTypes, getFunctionSuggestion("min(list)", "min($1)"));
        push(suggestionTypes, getFunctionSuggestion("mode(list)", "mode($1)"));
        push(suggestionTypes, getFunctionSuggestion("mode(n)", "mode($1)"));
        push(suggestionTypes, getFunctionSuggestion("modulo(dividend, divisor)", "modulo($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("month of year(date)", "month of year($1)"));
        push(suggestionTypes, getFunctionSuggestion("nn all(b)", "nn all($1)"));
        push(suggestionTypes, getFunctionSuggestion("nn all(list)", "nn all($1)"));
        push(suggestionTypes, getFunctionSuggestion("nn any(b)", "nn any($1)"));
        push(suggestionTypes, getFunctionSuggestion("nn any(list)", "nn any($1)"));
        push(suggestionTypes, getFunctionSuggestion("nn count(c)", "nn count($1)"));
        push(suggestionTypes, getFunctionSuggestion("nn count(list)", "nn count($1)"));
        push(suggestionTypes, getFunctionSuggestion("nn max(c)", "nn max($1)"));
        push(suggestionTypes, getFunctionSuggestion("nn max(list)", "nn max($1)"));
        push(suggestionTypes, getFunctionSuggestion("nn mean(list)", "nn mean($1)"));
        push(suggestionTypes, getFunctionSuggestion("nn mean(n)", "nn mean($1)"));
        push(suggestionTypes, getFunctionSuggestion("nn median(list)", "nn median($1)"));
        push(suggestionTypes, getFunctionSuggestion("nn median(n)", "nn median($1)"));
        push(suggestionTypes, getFunctionSuggestion("nn min(c)", "nn min($1)"));
        push(suggestionTypes, getFunctionSuggestion("nn min(list)", "nn min($1)"));
        push(suggestionTypes, getFunctionSuggestion("nn mode(list)", "nn mode($1)"));
        push(suggestionTypes, getFunctionSuggestion("nn mode(n)", "nn mode($1)"));
        push(suggestionTypes, getFunctionSuggestion("nn stddev(list)", "nn stddev($1)"));
        push(suggestionTypes, getFunctionSuggestion("nn stddev(n)", "nn stddev($1)"));
        push(suggestionTypes, getFunctionSuggestion("nn sum(list)", "nn sum($1)"));
        push(suggestionTypes, getFunctionSuggestion("nn sum(n)", "nn sum($1)"));
        push(suggestionTypes, getFunctionSuggestion("not(negand)", "not($1)"));
        push(suggestionTypes, getFunctionSuggestion("now()", "now()"));
        push(suggestionTypes, getFunctionSuggestion("number(from, grouping separator, decimal separator)", "number($1, $2, $3)"));
        push(suggestionTypes, getFunctionSuggestion("odd(number)", "odd($1)"));
        push(suggestionTypes, getFunctionSuggestion("overlapped after by(range1, range2)", "overlapped after by($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("overlapped before by(range1, range2)", "overlapped before by($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("overlapped by(range1, range2)", "overlapped by($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("overlaps after(range1, range2)", "overlaps after($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("overlaps before(range1, range2)", "overlaps before($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("overlaps(range1, range2)", "overlaps($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("product(list)", "product($1)"));
        push(suggestionTypes, getFunctionSuggestion("product(n)", "product($1)"));
        push(suggestionTypes, getFunctionSuggestion("remove(list, position)", "remove($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("replace(input, pattern, replacement)", "replace($1, $2, $3)"));
        push(suggestionTypes, getFunctionSuggestion("replace(input, pattern, replacement, flags)", "replace($1, $2, $3, $4)"));
        push(suggestionTypes, getFunctionSuggestion("reverse(list)", "reverse($1)"));
        push(suggestionTypes, getFunctionSuggestion("sort()", "sort()"));
        push(suggestionTypes, getFunctionSuggestion("sort(ctx, list, precedes)", "sort($1, $2, $3)"));
        push(suggestionTypes, getFunctionSuggestion("sort(list)", "sort($1)"));
        push(suggestionTypes, getFunctionSuggestion("split(string, delimiter)", "split($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("split(string, delimiter, flags)", "split($1, $2, $3)"));
        push(suggestionTypes, getFunctionSuggestion("sqrt(number)", "sqrt($1)"));
        push(suggestionTypes, getFunctionSuggestion("started by(range, value)", "started by($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("started by(range1, range2)", "started by($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("starts with(string, match)", "starts with($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("starts(range1, range2)", "starts($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("starts(value, range)", "starts($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("stddev(list)", "stddev($1)"));
        push(suggestionTypes, getFunctionSuggestion("stddev(n)", "stddev($1)"));
        push(suggestionTypes, getFunctionSuggestion("string length(string)", "string length($1)"));
        push(suggestionTypes, getFunctionSuggestion("string(from)", "string($1)"));
        push(suggestionTypes, getFunctionSuggestion("string(mask, p)", "string($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("sublist(list, start position)", "sublist($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("sublist(list, start position, length)", "sublist($1, $2, $3)"));
        push(suggestionTypes, getFunctionSuggestion("substring after(string, match)", "substring after($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("substring before(string, match)", "substring before($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("substring(string, start position)", "substring($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("substring(string, start position, length)", "substring($1, $2, $3)"));
        push(suggestionTypes, getFunctionSuggestion("sum(list)", "sum($1)"));
        push(suggestionTypes, getFunctionSuggestion("sum(n)", "sum($1)"));
        push(suggestionTypes, getFunctionSuggestion("time(from)", "time($1)"));
        push(suggestionTypes, getFunctionSuggestion("time(hour, minute, second)", "time($1, $2, $3)"));
        push(suggestionTypes, getFunctionSuggestion("time(hour, minute, second, offset)", "time($1, $2, $3, $4)"));
        push(suggestionTypes, getFunctionSuggestion("today()", "today()"));
        push(suggestionTypes, getFunctionSuggestion("union(list)", "union($1)"));
        push(suggestionTypes, getFunctionSuggestion("upper case(string)", "upper case($1)"));
        push(suggestionTypes, getFunctionSuggestion("week of year(date)", "week of year($1)"));
        push(suggestionTypes, getFunctionSuggestion("years and months duration(from, to)", "years and months duration($1, $2)"));
    }

    JSONValue getFunctionSuggestion(final String label,
                                    final String insertText) {

        final JSONObject suggestion = makeJSONObject();
        final int completionItemKindFunction = 1;
        final int completionItemInsertTextRuleInsertAsSnippet = 4;

        suggestion.put("kind", makeJSONNumber(completionItemKindFunction));
        suggestion.put("insertTextRules", makeJSONNumber(completionItemInsertTextRuleInsertAsSnippet));
        suggestion.put("label", makeJSONString(label));
        suggestion.put("insertText", makeJSONString(insertText));

        return suggestion;
    }

    JSONValue getVariableSuggestion(final String variable) {

        final JSONObject suggestion = makeJSONObject();
        final int completionItemKindVariable = 4;
        final int completionItemInsertTextRuleInsertAsSnippet = 4;
        final JSONString variableSuggestion = makeJSONString(variable);

        suggestion.put("kind", makeJSONNumber(completionItemKindVariable));
        suggestion.put("insertTextRules", makeJSONNumber(completionItemInsertTextRuleInsertAsSnippet));
        suggestion.put("label", variableSuggestion);
        suggestion.put("insertText", variableSuggestion);

        return suggestion;
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

    RegExp makeRegExp(final String pattern) {
        return new RegExp(pattern);
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
