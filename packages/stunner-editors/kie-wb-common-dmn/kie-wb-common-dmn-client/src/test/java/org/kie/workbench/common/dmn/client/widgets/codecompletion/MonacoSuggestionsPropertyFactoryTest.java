/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import java.util.List;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.feel.Candidate;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.feel.FEELLanguageService.Position;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.client.widgets.codecompletion.MonacoSuggestionsPropertyFactory.INSERT_TEXT_KEY;
import static org.kie.workbench.common.dmn.client.widgets.codecompletion.MonacoSuggestionsPropertyFactory.INSERT_TEXT_RULES_KEY;
import static org.kie.workbench.common.dmn.client.widgets.codecompletion.MonacoSuggestionsPropertyFactory.KIND_KEY;
import static org.kie.workbench.common.dmn.client.widgets.codecompletion.MonacoSuggestionsPropertyFactory.LABEL_KEY;
import static org.kie.workbench.common.dmn.client.widgets.codecompletion.MonacoSuggestionsPropertyFactory.SORT_TEXT_KEY;
import static org.kie.workbench.common.dmn.client.widgets.codecompletion.feel.CompletionItemKind.Function;
import static org.kie.workbench.common.dmn.client.widgets.codecompletion.feel.CompletionItemKind.Keyword;
import static org.kie.workbench.common.dmn.client.widgets.codecompletion.feel.CompletionItemKind.Variable;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class MonacoSuggestionsPropertyFactoryTest {

    @Mock
    private MonacoFEELSuggestions monacoFEELSuggestions;

    @Captor
    private ArgumentCaptor<Candidate> candidateArgumentCaptor;

    private MonacoSuggestionsPropertyFactory suggestionsPropertyFactory;

    @Before
    public void setup() {
        suggestionsPropertyFactory = spy(new MonacoSuggestionsPropertyFactory(monacoFEELSuggestions));
    }

    @Test
    public void testCreate() {

        final String expression = "1 +";
        final Position position = new Position(1, 3);
        final JSONValue jsonValue = mock(JSONValue.class);
        final JSONArray expectedSuggestions = mock(JSONArray.class);

        when(monacoFEELSuggestions.getCandidates(expression, position)).thenReturn(asList(new Candidate("Decision-1", Variable),
                                                                                          new Candidate("else", Keyword),
                                                                                          new Candidate("custom(value)", "custom($1)", Function),
                                                                                          new Candidate("code(value)", "code($1)", Function)));

        doReturn(expectedSuggestions).when(suggestionsPropertyFactory).makeJSONArray();
        doReturn(jsonValue).when(suggestionsPropertyFactory).getSuggestion(candidateArgumentCaptor.capture(), anyInt());

        final JSONArray actualSuggestions = suggestionsPropertyFactory.create(expression, position);

        final List<Candidate> captorAllValues = candidateArgumentCaptor.getAllValues();

        // Suggestions provided by MonacoFEELSuggestions appear first
        assertEquals("Decision-1", captorAllValues.get(0).getLabel());
        assertEquals("else", captorAllValues.get(1).getLabel());
        assertEquals("custom(value)", captorAllValues.get(2).getLabel());
        assertEquals("code(value)", captorAllValues.get(3).getLabel());

        // Default function suggestions are appended
        assertEquals("abs(duration)", captorAllValues.get(4).getLabel());
        assertEquals("abs(number)", captorAllValues.get(5).getLabel());
        assertEquals("after(range, value)", captorAllValues.get(6).getLabel());
        assertEquals("after(range1, range2)", captorAllValues.get(7).getLabel());
        assertEquals("after(value, range)", captorAllValues.get(8).getLabel());
        assertEquals("after(value1, value2)", captorAllValues.get(9).getLabel());
        assertEquals("all(b)", captorAllValues.get(10).getLabel());
        assertEquals("all(list)", captorAllValues.get(11).getLabel());
        assertEquals("any(b)", captorAllValues.get(12).getLabel());
        assertEquals("any(list)", captorAllValues.get(13).getLabel());
        assertEquals("append(list, item)", captorAllValues.get(14).getLabel());
        assertEquals("before(range, value)", captorAllValues.get(15).getLabel());
        assertEquals("before(range1, range2)", captorAllValues.get(16).getLabel());
        assertEquals("before(value, range)", captorAllValues.get(17).getLabel());
        assertEquals("before(value1, value2)", captorAllValues.get(18).getLabel());
        assertEquals("ceiling(n)", captorAllValues.get(19).getLabel());
        assertEquals("coincides(range1, range2)", captorAllValues.get(20).getLabel());
        assertEquals("coincides(value1, value2)", captorAllValues.get(21).getLabel());
        assertEquals("concatenate(list)", captorAllValues.get(22).getLabel());
        assertEquals("contains(string, match)", captorAllValues.get(23).getLabel());
        assertEquals("count(c)", captorAllValues.get(24).getLabel());
        assertEquals("count(list)", captorAllValues.get(25).getLabel());
        assertEquals("date and time(date, time)", captorAllValues.get(26).getLabel());
        assertEquals("date and time(from)", captorAllValues.get(27).getLabel());
        assertEquals("date and time(year, month, day, hour, minute, second)", captorAllValues.get(28).getLabel());
        assertEquals("date and time(year, month, day, hour, minute, second, hour offset)", captorAllValues.get(29).getLabel());
        assertEquals("date and time(year, month, day, hour, minute, second, timezone)", captorAllValues.get(30).getLabel());
        assertEquals("date(from)", captorAllValues.get(31).getLabel());
        assertEquals("date(year, month, day)", captorAllValues.get(32).getLabel());
        assertEquals("day of week(date)", captorAllValues.get(33).getLabel());
        assertEquals("day of year(date)", captorAllValues.get(34).getLabel());
        assertEquals("decimal(n, scale)", captorAllValues.get(35).getLabel());
        assertEquals("decision table(ctx, outputs, input expression list, input values list, output values, rule list, hit policy, default output value)", captorAllValues.get(36).getLabel());
        assertEquals("distinct values(list)", captorAllValues.get(37).getLabel());
        assertEquals("duration(from)", captorAllValues.get(38).getLabel());
        assertEquals("during(range1, range2)", captorAllValues.get(39).getLabel());
        assertEquals("during(value, range)", captorAllValues.get(40).getLabel());
        assertEquals("ends with(string, match)", captorAllValues.get(41).getLabel());
        assertEquals("even(number)", captorAllValues.get(42).getLabel());
        assertEquals("exp(number)", captorAllValues.get(43).getLabel());
        assertEquals("finished by(range, value)", captorAllValues.get(44).getLabel());
        assertEquals("finished by(range1, range2)", captorAllValues.get(45).getLabel());
        assertEquals("finishes(range1, range2)", captorAllValues.get(46).getLabel());
        assertEquals("finishes(value, range)", captorAllValues.get(47).getLabel());
        assertEquals("flatten(list)", captorAllValues.get(48).getLabel());
        assertEquals("floor(n)", captorAllValues.get(49).getLabel());
        assertEquals("get entries(m)", captorAllValues.get(50).getLabel());
        assertEquals("get value(m, key)", captorAllValues.get(51).getLabel());
        assertEquals("includes(range, value)", captorAllValues.get(52).getLabel());
        assertEquals("includes(range1, range2)", captorAllValues.get(53).getLabel());
        assertEquals("index of(list, match)", captorAllValues.get(54).getLabel());
        assertEquals("insert before(list, position, newItem)", captorAllValues.get(55).getLabel());
        assertEquals("invoke(ctx, namespace, model name, decision name, parameters)", captorAllValues.get(56).getLabel());
        assertEquals("list contains(list, element)", captorAllValues.get(57).getLabel());
        assertEquals("log(number)", captorAllValues.get(58).getLabel());
        assertEquals("lower case(string)", captorAllValues.get(59).getLabel());
        assertEquals("matches(input, pattern)", captorAllValues.get(60).getLabel());
        assertEquals("matches(input, pattern, flags)", captorAllValues.get(61).getLabel());
        assertEquals("max(c)", captorAllValues.get(62).getLabel());
        assertEquals("max(list)", captorAllValues.get(63).getLabel());
        assertEquals("mean(list)", captorAllValues.get(64).getLabel());
        assertEquals("mean(n)", captorAllValues.get(65).getLabel());
        assertEquals("median(list)", captorAllValues.get(66).getLabel());
        assertEquals("median(n)", captorAllValues.get(67).getLabel());
        assertEquals("meets(range1, range2)", captorAllValues.get(68).getLabel());
        assertEquals("met by(range1, range2)", captorAllValues.get(69).getLabel());
        assertEquals("min(c)", captorAllValues.get(70).getLabel());
        assertEquals("min(list)", captorAllValues.get(71).getLabel());
        assertEquals("mode(list)", captorAllValues.get(72).getLabel());
        assertEquals("mode(n)", captorAllValues.get(73).getLabel());
        assertEquals("modulo(dividend, divisor)", captorAllValues.get(74).getLabel());
        assertEquals("month of year(date)", captorAllValues.get(75).getLabel());
        assertEquals("nn all(b)", captorAllValues.get(76).getLabel());
        assertEquals("nn all(list)", captorAllValues.get(77).getLabel());
        assertEquals("nn any(b)", captorAllValues.get(78).getLabel());
        assertEquals("nn any(list)", captorAllValues.get(79).getLabel());
        assertEquals("nn count(c)", captorAllValues.get(80).getLabel());
        assertEquals("nn count(list)", captorAllValues.get(81).getLabel());
        assertEquals("nn max(c)", captorAllValues.get(82).getLabel());
        assertEquals("nn max(list)", captorAllValues.get(83).getLabel());
        assertEquals("nn mean(list)", captorAllValues.get(84).getLabel());
        assertEquals("nn mean(n)", captorAllValues.get(85).getLabel());
        assertEquals("nn median(list)", captorAllValues.get(86).getLabel());
        assertEquals("nn median(n)", captorAllValues.get(87).getLabel());
        assertEquals("nn min(c)", captorAllValues.get(88).getLabel());
        assertEquals("nn min(list)", captorAllValues.get(89).getLabel());
        assertEquals("nn mode(list)", captorAllValues.get(90).getLabel());
        assertEquals("nn mode(n)", captorAllValues.get(91).getLabel());
        assertEquals("nn stddev(list)", captorAllValues.get(92).getLabel());
        assertEquals("nn stddev(n)", captorAllValues.get(93).getLabel());
        assertEquals("nn sum(list)", captorAllValues.get(94).getLabel());
        assertEquals("nn sum(n)", captorAllValues.get(95).getLabel());
        assertEquals("not(negand)", captorAllValues.get(96).getLabel());
        assertEquals("now()", captorAllValues.get(97).getLabel());
        assertEquals("number(from, grouping separator, decimal separator)", captorAllValues.get(98).getLabel());
        assertEquals("odd(number)", captorAllValues.get(99).getLabel());
        assertEquals("overlapped after by(range1, range2)", captorAllValues.get(100).getLabel());
        assertEquals("overlapped before by(range1, range2)", captorAllValues.get(101).getLabel());
        assertEquals("overlapped by(range1, range2)", captorAllValues.get(102).getLabel());
        assertEquals("overlaps after(range1, range2)", captorAllValues.get(103).getLabel());
        assertEquals("overlaps before(range1, range2)", captorAllValues.get(104).getLabel());
        assertEquals("overlaps(range1, range2)", captorAllValues.get(105).getLabel());
        assertEquals("product(list)", captorAllValues.get(106).getLabel());
        assertEquals("product(n)", captorAllValues.get(107).getLabel());
        assertEquals("remove(list, position)", captorAllValues.get(108).getLabel());
        assertEquals("replace(input, pattern, replacement)", captorAllValues.get(109).getLabel());
        assertEquals("replace(input, pattern, replacement, flags)", captorAllValues.get(110).getLabel());
        assertEquals("reverse(list)", captorAllValues.get(111).getLabel());
        assertEquals("sort()", captorAllValues.get(112).getLabel());
        assertEquals("sort(ctx, list, precedes)", captorAllValues.get(113).getLabel());
        assertEquals("sort(list)", captorAllValues.get(114).getLabel());
        assertEquals("split(string, delimiter)", captorAllValues.get(115).getLabel());
        assertEquals("split(string, delimiter, flags)", captorAllValues.get(116).getLabel());
        assertEquals("sqrt(number)", captorAllValues.get(117).getLabel());
        assertEquals("started by(range, value)", captorAllValues.get(118).getLabel());
        assertEquals("started by(range1, range2)", captorAllValues.get(119).getLabel());
        assertEquals("starts with(string, match)", captorAllValues.get(120).getLabel());
        assertEquals("starts(range1, range2)", captorAllValues.get(121).getLabel());
        assertEquals("starts(value, range)", captorAllValues.get(122).getLabel());
        assertEquals("stddev(list)", captorAllValues.get(123).getLabel());
        assertEquals("stddev(n)", captorAllValues.get(124).getLabel());
        assertEquals("string length(string)", captorAllValues.get(125).getLabel());
        assertEquals("string(from)", captorAllValues.get(126).getLabel());
        assertEquals("string(mask, p)", captorAllValues.get(127).getLabel());
        assertEquals("sublist(list, start position)", captorAllValues.get(128).getLabel());
        assertEquals("sublist(list, start position, length)", captorAllValues.get(129).getLabel());
        assertEquals("substring after(string, match)", captorAllValues.get(130).getLabel());
        assertEquals("substring before(string, match)", captorAllValues.get(131).getLabel());
        assertEquals("substring(string, start position)", captorAllValues.get(132).getLabel());
        assertEquals("substring(string, start position, length)", captorAllValues.get(133).getLabel());
        assertEquals("sum(list)", captorAllValues.get(134).getLabel());
        assertEquals("sum(n)", captorAllValues.get(135).getLabel());
        assertEquals("time(from)", captorAllValues.get(136).getLabel());
        assertEquals("time(hour, minute, second)", captorAllValues.get(137).getLabel());
        assertEquals("time(hour, minute, second, offset)", captorAllValues.get(138).getLabel());
        assertEquals("today()", captorAllValues.get(139).getLabel());
        assertEquals("union(list)", captorAllValues.get(140).getLabel());
        assertEquals("upper case(string)", captorAllValues.get(141).getLabel());
        assertEquals("week of year(date)", captorAllValues.get(142).getLabel());
        assertEquals("years and months duration(from, to)", captorAllValues.get(143).getLabel());

        // Default keywords suggestions are appended
        assertEquals("for", captorAllValues.get(144).getLabel());
        assertEquals("return", captorAllValues.get(145).getLabel());
        assertEquals("if", captorAllValues.get(146).getLabel());
        assertEquals("then", captorAllValues.get(147).getLabel());
        assertEquals("some", captorAllValues.get(148).getLabel());
        assertEquals("every", captorAllValues.get(149).getLabel());
        assertEquals("satisfies", captorAllValues.get(150).getLabel());
        assertEquals("instance", captorAllValues.get(151).getLabel());
        assertEquals("of", captorAllValues.get(152).getLabel());
        assertEquals("in", captorAllValues.get(153).getLabel());
        assertEquals("function", captorAllValues.get(154).getLabel());
        assertEquals("external", captorAllValues.get(155).getLabel());
        assertEquals("or", captorAllValues.get(156).getLabel());
        assertEquals("and", captorAllValues.get(157).getLabel());
        assertEquals("between", captorAllValues.get(158).getLabel());
        assertEquals("not", captorAllValues.get(159).getLabel());
        assertEquals("null", captorAllValues.get(160).getLabel());
        assertEquals("true", captorAllValues.get(161).getLabel());
        assertEquals("false", captorAllValues.get(162).getLabel());

        assertEquals(expectedSuggestions, actualSuggestions);
    }

    @Test
    public void testSuggestion() {

        final String candidateLabel = "sum(value)";
        final String insertText = "sum($1)";
        final Candidate candidate = new Candidate(candidateLabel, insertText, Function);
        final JSONObject expectedSuggestion = mock(JSONObject.class);
        final JSONValue jsonKindKeyValue = mock(JSONValue.class);
        final JSONValue jsonInsertTextRulesKeyValue = mock(JSONValue.class);
        final JSONString jsonLabelValue = mock(JSONString.class);
        final JSONString jsonInsertTextValue = mock(JSONString.class);
        final JSONString jsonSortKeyValue = mock(JSONString.class);
        final int completionItemInsertTextRuleInsertAsSnippet = 4;

        doReturn(expectedSuggestion).when(suggestionsPropertyFactory).makeJSONObject();
        doReturn(jsonKindKeyValue).when(suggestionsPropertyFactory).makeJSONNumber(Function.getValue());
        doReturn(jsonInsertTextRulesKeyValue).when(suggestionsPropertyFactory).makeJSONNumber(completionItemInsertTextRuleInsertAsSnippet);
        doReturn(jsonLabelValue).when(suggestionsPropertyFactory).makeJSONString(candidateLabel);
        doReturn(jsonInsertTextValue).when(suggestionsPropertyFactory).makeJSONString(insertText);
        doReturn(jsonSortKeyValue).when(suggestionsPropertyFactory).makeJSONString("0000");

        final JSONValue actualSuggestion = suggestionsPropertyFactory.getSuggestion(candidate, 0);

        verify(expectedSuggestion).put(KIND_KEY, jsonKindKeyValue);
        verify(expectedSuggestion).put(INSERT_TEXT_RULES_KEY, jsonInsertTextRulesKeyValue);
        verify(expectedSuggestion).put(LABEL_KEY, jsonLabelValue);
        verify(expectedSuggestion).put(INSERT_TEXT_KEY, jsonInsertTextValue);
        verify(expectedSuggestion).put(SORT_TEXT_KEY, jsonSortKeyValue);
        assertEquals(expectedSuggestion, actualSuggestion);
    }
}
