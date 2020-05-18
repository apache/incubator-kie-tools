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

import java.util.ArrayList;
import java.util.List;

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
import org.mockito.Mock;
import org.uberfire.client.views.pfly.monaco.jsinterop.MonacoLanguages.ProvideCompletionItemsFunction;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.client.widgets.codecompletion.MonacoPropertiesFactory.FEEL_LANGUAGE_ID;
import static org.kie.workbench.common.dmn.client.widgets.codecompletion.MonacoPropertiesFactory.FEEL_THEME_ID;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class MonacoPropertiesFactoryTest {

    private MonacoPropertiesFactory factory;

    @Mock
    private MonacoFEELVariableSuggestions variableSuggestions;

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
    public void testGetSuggestions() {

        final JSONArray expectedSuggestions = mock(JSONArray.class);
        final List<JSONValue> suggestions = new ArrayList<>();
        final List<String> variableSuggestions = buildVariableSuggestions();

        when(this.variableSuggestions.getSuggestions()).thenReturn(variableSuggestions);

        buildKeywordSuggestions().forEach(suggestion -> {
            final JSONValue keyword = mock(JSONValue.class);
            suggestions.add(keyword);
            doReturn(keyword).when(factory).getKeywordSuggestion(suggestion);
        });

        buildFunctionSuggestions().forEach(suggestion -> {
            final JSONValue function = mock(JSONValue.class);
            suggestions.add(function);
            doReturn(function).when(factory).getFunctionSuggestion(suggestion.get(0), suggestion.get(1));
        });

        variableSuggestions.forEach(suggestion -> {
            final JSONValue variable = mock(JSONValue.class);
            suggestions.add(variable);
            doReturn(variable).when(factory).getVariableSuggestion(suggestion);
        });

        doReturn(expectedSuggestions).when(factory).makeJSONArray();

        final JSONArray actualSuggestions = factory.getSuggestions(this.variableSuggestions);

        suggestions.forEach(suggestion -> {
            verify(factory).push(expectedSuggestions, suggestion);
        });
        assertEquals(expectedSuggestions, actualSuggestions);
    }

    private List<String> buildVariableSuggestions() {
        return asList(
                "Decision-1",
                "Decision-2",
                "Decision-3",
                "Input-Data-1",
                "Input-Data-2",
                "Input-Data-3",
                "Data-Type-1",
                "Data-Type-2",
                "Data-Type-3"
        );
    }

    private List<List<String>> buildFunctionSuggestions() {
        return asList(
                asList("abs(duration)", "abs($1)"),
                asList("abs(number)", "abs($1)"),
                asList("after(range, value)", "after($1, $2)"),
                asList("after(range1, range2)", "after($1, $2)"),
                asList("after(value, range)", "after($1, $2)"),
                asList("after(value1, value2)", "after($1, $2)"),
                asList("all(b)", "all($1)"),
                asList("all(list)", "all($1)"),
                asList("any(b)", "any($1)"),
                asList("any(list)", "any($1)"),
                asList("append(list, item)", "append($1, $2)"),
                asList("before(range, value)", "before($1, $2)"),
                asList("before(range1, range2)", "before($1, $2)"),
                asList("before(value, range)", "before($1, $2)"),
                asList("before(value1, value2)", "before($1, $2)"),
                asList("ceiling(n)", "ceiling($1)"),
                asList("code(value)", "code($1)"),
                asList("coincides(range1, range2)", "coincides($1, $2)"),
                asList("coincides(value1, value2)", "coincides($1, $2)"),
                asList("concatenate(list)", "concatenate($1)"),
                asList("contains(string, match)", "contains($1, $2)"),
                asList("count(c)", "count($1)"),
                asList("count(list)", "count($1)"),
                asList("date and time(date, time)", "date and time($1, $2)"),
                asList("date and time(from)", "date and time($1)"),
                asList("date and time(year, month, day, hour, minute, second)", "date and time($1, $2, $3, $4, $5, $6)"),
                asList("date and time(year, month, day, hour, minute, second, hour offset)", "date and time($1, $2, $3, $4, $5, $6, $7)"),
                asList("date and time(year, month, day, hour, minute, second, timezone)", "date and time($1, $2, $3, $4, $5, $6, $7)"),
                asList("date(from)", "date($1)"),
                asList("date(year, month, day)", "date($1, $2, $3)"),
                asList("day of week(date)", "day of week($1)"),
                asList("day of year(date)", "day of year($1)"),
                asList("decimal(n, scale)", "decimal($1, $2)"),
                asList("decision table(ctx, outputs, input expression list, input values list, output values, rule list, hit policy, default output value)", "decision table($1, $2, $3, $4, $5, $6, $7, $8)"),
                asList("distinct values(list)", "distinct values($1)"),
                asList("duration(from)", "duration($1)"),
                asList("during(range1, range2)", "during($1, $2)"),
                asList("during(value, range)", "during($1, $2)"),
                asList("ends with(string, match)", "ends with($1, $2)"),
                asList("even(number)", "even($1)"),
                asList("exp(number)", "exp($1)"),
                asList("finished by(range, value)", "finished by($1, $2)"),
                asList("finished by(range1, range2)", "finished by($1, $2)"),
                asList("finishes(range1, range2)", "finishes($1, $2)"),
                asList("finishes(value, range)", "finishes($1, $2)"),
                asList("flatten(list)", "flatten($1)"),
                asList("floor(n)", "floor($1)"),
                asList("get entries(m)", "get entries($1)"),
                asList("get value(m, key)", "get value($1, $2)"),
                asList("includes(range, value)", "includes($1, $2)"),
                asList("includes(range1, range2)", "includes($1, $2)"),
                asList("index of(list, match)", "index of($1, $2)"),
                asList("insert before(list, position, newItem)", "insert before($1, $2, $3)"),
                asList("invoke(ctx, namespace, model name, decision name, parameters)", "invoke($1, $2, $3, $4, $5)"),
                asList("list contains(list, element)", "list contains($1, $2)"),
                asList("log(number)", "log($1)"),
                asList("lower case(string)", "lower case($1)"),
                asList("matches(input, pattern)", "matches($1, $2)"),
                asList("matches(input, pattern, flags)", "matches($1, $2, $3)"),
                asList("max(c)", "max($1)"),
                asList("max(list)", "max($1)"),
                asList("mean(list)", "mean($1)"),
                asList("mean(n)", "mean($1)"),
                asList("median(list)", "median($1)"),
                asList("median(n)", "median($1)"),
                asList("meets(range1, range2)", "meets($1, $2)"),
                asList("met by(range1, range2)", "met by($1, $2)"),
                asList("min(c)", "min($1)"),
                asList("min(list)", "min($1)"),
                asList("mode(list)", "mode($1)"),
                asList("mode(n)", "mode($1)"),
                asList("modulo(dividend, divisor)", "modulo($1, $2)"),
                asList("month of year(date)", "month of year($1)"),
                asList("nn all(b)", "nn all($1)"),
                asList("nn all(list)", "nn all($1)"),
                asList("nn any(b)", "nn any($1)"),
                asList("nn any(list)", "nn any($1)"),
                asList("nn count(c)", "nn count($1)"),
                asList("nn count(list)", "nn count($1)"),
                asList("nn max(c)", "nn max($1)"),
                asList("nn max(list)", "nn max($1)"),
                asList("nn mean(list)", "nn mean($1)"),
                asList("nn mean(n)", "nn mean($1)"),
                asList("nn median(list)", "nn median($1)"),
                asList("nn median(n)", "nn median($1)"),
                asList("nn min(c)", "nn min($1)"),
                asList("nn min(list)", "nn min($1)"),
                asList("nn mode(list)", "nn mode($1)"),
                asList("nn mode(n)", "nn mode($1)"),
                asList("nn stddev(list)", "nn stddev($1)"),
                asList("nn stddev(n)", "nn stddev($1)"),
                asList("nn sum(list)", "nn sum($1)"),
                asList("nn sum(n)", "nn sum($1)"),
                asList("not(negand)", "not($1)"),
                asList("now()", "now()"),
                asList("number(from, grouping separator, decimal separator)", "number($1, $2, $3)"),
                asList("odd(number)", "odd($1)"),
                asList("overlapped after by(range1, range2)", "overlapped after by($1, $2)"),
                asList("overlapped before by(range1, range2)", "overlapped before by($1, $2)"),
                asList("overlapped by(range1, range2)", "overlapped by($1, $2)"),
                asList("overlaps after(range1, range2)", "overlaps after($1, $2)"),
                asList("overlaps before(range1, range2)", "overlaps before($1, $2)"),
                asList("overlaps(range1, range2)", "overlaps($1, $2)"),
                asList("product(list)", "product($1)"),
                asList("product(n)", "product($1)"),
                asList("remove(list, position)", "remove($1, $2)"),
                asList("replace(input, pattern, replacement)", "replace($1, $2, $3)"),
                asList("replace(input, pattern, replacement, flags)", "replace($1, $2, $3, $4)"),
                asList("reverse(list)", "reverse($1)"),
                asList("sort()", "sort()"),
                asList("sort(ctx, list, precedes)", "sort($1, $2, $3)"),
                asList("sort(list)", "sort($1)"),
                asList("split(string, delimiter)", "split($1, $2)"),
                asList("split(string, delimiter, flags)", "split($1, $2, $3)"),
                asList("sqrt(number)", "sqrt($1)"),
                asList("started by(range, value)", "started by($1, $2)"),
                asList("started by(range1, range2)", "started by($1, $2)"),
                asList("starts with(string, match)", "starts with($1, $2)"),
                asList("starts(range1, range2)", "starts($1, $2)"),
                asList("starts(value, range)", "starts($1, $2)"),
                asList("stddev(list)", "stddev($1)"),
                asList("stddev(n)", "stddev($1)"),
                asList("string length(string)", "string length($1)"),
                asList("string(from)", "string($1)"),
                asList("string(mask, p)", "string($1, $2)"),
                asList("sublist(list, start position)", "sublist($1, $2)"),
                asList("sublist(list, start position, length)", "sublist($1, $2, $3)"),
                asList("substring after(string, match)", "substring after($1, $2)"),
                asList("substring before(string, match)", "substring before($1, $2)"),
                asList("substring(string, start position)", "substring($1, $2)"),
                asList("substring(string, start position, length)", "substring($1, $2, $3)"),
                asList("sum(list)", "sum($1)"),
                asList("sum(n)", "sum($1)"),
                asList("time(from)", "time($1)"),
                asList("time(hour, minute, second)", "time($1, $2, $3)"),
                asList("time(hour, minute, second, offset)", "time($1, $2, $3, $4)"),
                asList("today()", "today()"),
                asList("union(list)", "union($1)"),
                asList("upper case(string)", "upper case($1)"),
                asList("week of year(date)", "week of year($1)"),
                asList("years and months duration(from, to)", "years and months duration($1, $2)"));
    }

    private List<String> buildKeywordSuggestions() {
        return asList(
                "for", "return", "if", "then", "else", "some", "every", "satisfies", "instance", "of",
                "in", "function", "external", "or", "and", "between", "not", "null", "true", "false"
        );
    }

    @Test
    public void testGetKeywordSuggestion() {

        final String keyword = "keyword";
        final JSONValue kind = mock(JSONValue.class);
        final JSONValue insertTextRules = mock(JSONValue.class);
        final JSONObject expectedSuggestion = mock(JSONObject.class);
        final JSONString keywordStringValue = mock(JSONString.class);

        when(factory.makeJSONNumber(17)).thenReturn(kind);
        when(factory.makeJSONNumber(4)).thenReturn(insertTextRules);
        doReturn(expectedSuggestion).when(factory).makeJSONObject();
        doReturn(keywordStringValue).when(factory).makeJSONString(keyword);

        final JSONValue actualSuggestion = factory.getKeywordSuggestion(keyword);

        verify(expectedSuggestion).put("kind", kind);
        verify(expectedSuggestion).put("insertTextRules", insertTextRules);
        verify(expectedSuggestion).put("label", keywordStringValue);
        verify(expectedSuggestion).put("insertText", keywordStringValue);
        assertEquals(expectedSuggestion, actualSuggestion);
    }

    @Test
    public void testGetFunctionSuggestion() {

        final String label = "label";
        final String insertText = "insertText";
        final JSONValue kind = mock(JSONValue.class);
        final JSONValue insertTextRules = mock(JSONValue.class);
        final JSONObject expectedSuggestion = mock(JSONObject.class);
        final JSONString labelString = mock(JSONString.class);
        final JSONString insertTextString = mock(JSONString.class);

        doReturn(expectedSuggestion).when(factory).makeJSONObject();
        doReturn(kind).when(factory).makeJSONNumber(1);
        doReturn(insertTextRules).when(factory).makeJSONNumber(4);
        doReturn(labelString).when(factory).makeJSONString(label);
        doReturn(insertTextString).when(factory).makeJSONString(insertText);

        final JSONValue actualSuggestion = factory.getFunctionSuggestion(label, insertText);

        verify(expectedSuggestion).put("kind", kind);
        verify(expectedSuggestion).put("insertTextRules", insertTextRules);
        verify(expectedSuggestion).put("label", labelString);
        verify(expectedSuggestion).put("insertText", insertTextString);
        assertEquals(expectedSuggestion, actualSuggestion);
    }

    @Test
    public void testGetVariableSuggestion() {

        final String variable = "variable";
        final JSONValue kind = mock(JSONValue.class);
        final JSONValue insertTextRules = mock(JSONValue.class);
        final JSONObject expectedSuggestion = mock(JSONObject.class);
        final JSONString variableStringValue = mock(JSONString.class);

        when(factory.makeJSONNumber(4)).thenReturn(kind, insertTextRules);
        doReturn(expectedSuggestion).when(factory).makeJSONObject();
        doReturn(variableStringValue).when(factory).makeJSONString(variable);

        final JSONValue actualSuggestion = factory.getVariableSuggestion(variable);

        verify(expectedSuggestion).put("kind", kind);
        verify(expectedSuggestion).put("insertTextRules", insertTextRules);
        verify(expectedSuggestion).put("label", variableStringValue);
        verify(expectedSuggestion).put("insertText", variableStringValue);
        assertEquals(expectedSuggestion, actualSuggestion);
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

        doReturn(provideCompletionItemsFunction).when(factory).getProvideCompletionItemsFunction(variableSuggestions);
        doReturn(functionObject).when(factory).makeJSONObject(provideCompletionItemsFunction);
        doReturn(expectedCompletionItemProvider).when(factory).makeJavaScriptObject("provideCompletionItems", functionObject);

        final JavaScriptObject actualCompletionItemProvider = factory.getCompletionItemProvider(variableSuggestions);

        assertEquals(expectedCompletionItemProvider, actualCompletionItemProvider);
    }

    @Test
    public void testGetProvideCompletionItemsFunction() {

        final JavaScriptObject expectedSuggestions = mock(JavaScriptObject.class);
        final JSONObject expectedJSONObjectSuggestions = mock(JSONObject.class);
        final JSONArray suggestions = mock(JSONArray.class);

        doReturn(expectedJSONObjectSuggestions).when(factory).makeJSONObject();
        doReturn(suggestions).when(factory).getSuggestions(variableSuggestions);
        when(expectedJSONObjectSuggestions.getJavaScriptObject()).thenReturn(expectedSuggestions);

        final JavaScriptObject actualSuggestions = factory.getProvideCompletionItemsFunction(variableSuggestions).call();

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
