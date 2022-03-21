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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.feel.Candidate;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.feel.FEELLanguageService.Position;

import static org.kie.workbench.common.dmn.client.widgets.codecompletion.MonacoFEELInitializer.FEEL_RESERVED_KEYWORDS;
import static org.kie.workbench.common.dmn.client.widgets.codecompletion.feel.CompletionItemKind.Function;
import static org.kie.workbench.common.dmn.client.widgets.codecompletion.feel.CompletionItemKind.Keyword;

@ApplicationScoped
public class MonacoSuggestionsPropertyFactory {

    static final String KIND_KEY = "kind";

    static final String INSERT_TEXT_RULES_KEY = "insertTextRules";

    static final String LABEL_KEY = "label";

    static final String INSERT_TEXT_KEY = "insertText";

    static final String SORT_TEXT_KEY = "sortText";

    private final List<Candidate> DEFAULT_KEYWORDS = getAllKeywords();

    private final List<Candidate> DEFAULT_FUNCTIONS = getAllFunctions();

    private final MonacoFEELSuggestions monacoFEELSuggestions;

    @Inject
    public MonacoSuggestionsPropertyFactory(final MonacoFEELSuggestions monacoFEELSuggestions) {
        this.monacoFEELSuggestions = monacoFEELSuggestions;
    }

    public JSONArray create(final String expression,
                            final Position position) {
        final JSONArray jsonArray = makeJSONArray();
        pushAll(jsonArray, getSuggestions(expression, position));
        return jsonArray;
    }

    private List<Candidate> getSuggestions(final String expression,
                                           final Position position) {

        final List<Candidate> suggestions = new ArrayList<>();

        final List<Candidate> feelCandidates = monacoFEELSuggestions.getCandidates(expression, position);
        final List<Candidate> functions = new ArrayList<>(DEFAULT_FUNCTIONS);
        final List<Candidate> keywords = new ArrayList<>(DEFAULT_KEYWORDS);

        for (final Candidate candidate : feelCandidates) {
            switch (candidate.getKind()) {
                case Function:
                    suggestions.add(removeIfPresent(functions, candidate));
                    break;
                case Keyword:
                    suggestions.add(removeIfPresent(keywords, candidate));
                    break;
                default:
                    suggestions.add(candidate);
            }
        }

        suggestions.addAll(functions);
        suggestions.addAll(keywords);

        return suggestions;
    }

    private Candidate removeIfPresent(final List<Candidate> candidates,
                                      final Candidate candidate) {

        final int index = candidates.indexOf(candidate);
        final Candidate element;

        if (index != -1) {
            element = candidates.get(index);
            candidates.remove(index);
        } else {
            element = candidate;
        }

        return element;
    }

    JSONValue getSuggestion(final Candidate candidate,
                            final int index) {

        final JSONObject suggestion = makeJSONObject();
        final int completionItemKindFunction = candidate.getKind().getValue();
        final int completionItemInsertTextRuleInsertAsSnippet = 4;
        final String sortTextValue = NumberFormat.getFormat("#0000").format(index);

        suggestion.put(KIND_KEY, makeJSONNumber(completionItemKindFunction));
        suggestion.put(INSERT_TEXT_RULES_KEY, makeJSONNumber(completionItemInsertTextRuleInsertAsSnippet));
        suggestion.put(LABEL_KEY, makeJSONString(candidate.getLabel()));
        suggestion.put(INSERT_TEXT_KEY, makeJSONString(candidate.getInsertText()));
        suggestion.put(SORT_TEXT_KEY, makeJSONString(sortTextValue));

        return suggestion;
    }

    private void pushAll(final JSONArray jsArray,
                         final List<Candidate> candidates) {
        for (int index = 0; index < candidates.size(); index++) {
            push(jsArray, getSuggestion(candidates.get(index), index));
        }
    }

    void push(final JSONArray jsonArray,
              final JSONValue jsonValue) {
        jsonArray.set(jsonArray.size(), jsonValue);
    }

    JSONArray makeJSONArray() {
        return new JSONArray();
    }

    JSONString makeJSONString(final String value) {
        return new JSONString(value);
    }

    JSONValue makeJSONNumber(final int value) {
        return new JSONNumber(value);
    }

    JSONObject makeJSONObject() {
        return new JSONObject();
    }

    private List<Candidate> getAllKeywords() {
        return FEEL_RESERVED_KEYWORDS.stream().map(this::makeKeywordCandidate).collect(Collectors.toList());
    }

    private List<Candidate> getAllFunctions() {
        return Arrays.asList(
                makeFunctionCandidate("abs(duration)", "abs($1)"),
                makeFunctionCandidate("abs(number)", "abs($1)"),
                makeFunctionCandidate("after(range, value)", "after($1, $2)"),
                makeFunctionCandidate("after(range1, range2)", "after($1, $2)"),
                makeFunctionCandidate("after(value, range)", "after($1, $2)"),
                makeFunctionCandidate("after(value1, value2)", "after($1, $2)"),
                makeFunctionCandidate("all(b)", "all($1)"),
                makeFunctionCandidate("all(list)", "all($1)"),
                makeFunctionCandidate("any(b)", "any($1)"),
                makeFunctionCandidate("any(list)", "any($1)"),
                makeFunctionCandidate("append(list, item)", "append($1, $2)"),
                makeFunctionCandidate("before(range, value)", "before($1, $2)"),
                makeFunctionCandidate("before(range1, range2)", "before($1, $2)"),
                makeFunctionCandidate("before(value, range)", "before($1, $2)"),
                makeFunctionCandidate("before(value1, value2)", "before($1, $2)"),
                makeFunctionCandidate("ceiling(n)", "ceiling($1)"),
                makeFunctionCandidate("code(value)", "code($1)"),
                makeFunctionCandidate("coincides(range1, range2)", "coincides($1, $2)"),
                makeFunctionCandidate("coincides(value1, value2)", "coincides($1, $2)"),
                makeFunctionCandidate("concatenate(list)", "concatenate($1)"),
                makeFunctionCandidate("contains(string, match)", "contains($1, $2)"),
                makeFunctionCandidate("count(c)", "count($1)"),
                makeFunctionCandidate("count(list)", "count($1)"),
                makeFunctionCandidate("date and time(date, time)", "date and time($1, $2)"),
                makeFunctionCandidate("date and time(from)", "date and time($1)"),
                makeFunctionCandidate("date and time(year, month, day, hour, minute, second)", "date and time($1, $2, $3, $4, $5, $6)"),
                makeFunctionCandidate("date and time(year, month, day, hour, minute, second, hour offset)", "date and time($1, $2, $3, $4, $5, $6, $7)"),
                makeFunctionCandidate("date and time(year, month, day, hour, minute, second, timezone)", "date and time($1, $2, $3, $4, $5, $6, $7)"),
                makeFunctionCandidate("date(from)", "date($1)"),
                makeFunctionCandidate("date(year, month, day)", "date($1, $2, $3)"),
                makeFunctionCandidate("day of week(date)", "day of week($1)"),
                makeFunctionCandidate("day of year(date)", "day of year($1)"),
                makeFunctionCandidate("decimal(n, scale)", "decimal($1, $2)"),
                makeFunctionCandidate("decision table(ctx, outputs, input expression list, input values list, output values, rule list, hit policy, default output value)", "decision table($1, $2, $3, $4, $5, $6, $7, $8)"),
                makeFunctionCandidate("distinct values(list)", "distinct values($1)"),
                makeFunctionCandidate("duration(from)", "duration($1)"),
                makeFunctionCandidate("during(range1, range2)", "during($1, $2)"),
                makeFunctionCandidate("during(value, range)", "during($1, $2)"),
                makeFunctionCandidate("ends with(string, match)", "ends with($1, $2)"),
                makeFunctionCandidate("even(number)", "even($1)"),
                makeFunctionCandidate("exp(number)", "exp($1)"),
                makeFunctionCandidate("finished by(range, value)", "finished by($1, $2)"),
                makeFunctionCandidate("finished by(range1, range2)", "finished by($1, $2)"),
                makeFunctionCandidate("finishes(range1, range2)", "finishes($1, $2)"),
                makeFunctionCandidate("finishes(value, range)", "finishes($1, $2)"),
                makeFunctionCandidate("flatten(list)", "flatten($1)"),
                makeFunctionCandidate("floor(n)", "floor($1)"),
                makeFunctionCandidate("get entries(m)", "get entries($1)"),
                makeFunctionCandidate("get value(m, key)", "get value($1, $2)"),
                makeFunctionCandidate("includes(range, value)", "includes($1, $2)"),
                makeFunctionCandidate("includes(range1, range2)", "includes($1, $2)"),
                makeFunctionCandidate("index of(list, match)", "index of($1, $2)"),
                makeFunctionCandidate("insert before(list, position, newItem)", "insert before($1, $2, $3)"),
                makeFunctionCandidate("invoke(ctx, namespace, model name, decision name, parameters)", "invoke($1, $2, $3, $4, $5)"),
                makeFunctionCandidate("list contains(list, element)", "list contains($1, $2)"),
                makeFunctionCandidate("log(number)", "log($1)"),
                makeFunctionCandidate("lower case(string)", "lower case($1)"),
                makeFunctionCandidate("matches(input, pattern)", "matches($1, $2)"),
                makeFunctionCandidate("matches(input, pattern, flags)", "matches($1, $2, $3)"),
                makeFunctionCandidate("max(c)", "max($1)"),
                makeFunctionCandidate("max(list)", "max($1)"),
                makeFunctionCandidate("mean(list)", "mean($1)"),
                makeFunctionCandidate("mean(n)", "mean($1)"),
                makeFunctionCandidate("median(list)", "median($1)"),
                makeFunctionCandidate("median(n)", "median($1)"),
                makeFunctionCandidate("meets(range1, range2)", "meets($1, $2)"),
                makeFunctionCandidate("met by(range1, range2)", "met by($1, $2)"),
                makeFunctionCandidate("min(c)", "min($1)"),
                makeFunctionCandidate("min(list)", "min($1)"),
                makeFunctionCandidate("mode(list)", "mode($1)"),
                makeFunctionCandidate("mode(n)", "mode($1)"),
                makeFunctionCandidate("modulo(dividend, divisor)", "modulo($1, $2)"),
                makeFunctionCandidate("month of year(date)", "month of year($1)"),
                makeFunctionCandidate("nn all(b)", "nn all($1)"),
                makeFunctionCandidate("nn all(list)", "nn all($1)"),
                makeFunctionCandidate("nn any(b)", "nn any($1)"),
                makeFunctionCandidate("nn any(list)", "nn any($1)"),
                makeFunctionCandidate("nn count(c)", "nn count($1)"),
                makeFunctionCandidate("nn count(list)", "nn count($1)"),
                makeFunctionCandidate("nn max(c)", "nn max($1)"),
                makeFunctionCandidate("nn max(list)", "nn max($1)"),
                makeFunctionCandidate("nn mean(list)", "nn mean($1)"),
                makeFunctionCandidate("nn mean(n)", "nn mean($1)"),
                makeFunctionCandidate("nn median(list)", "nn median($1)"),
                makeFunctionCandidate("nn median(n)", "nn median($1)"),
                makeFunctionCandidate("nn min(c)", "nn min($1)"),
                makeFunctionCandidate("nn min(list)", "nn min($1)"),
                makeFunctionCandidate("nn mode(list)", "nn mode($1)"),
                makeFunctionCandidate("nn mode(n)", "nn mode($1)"),
                makeFunctionCandidate("nn stddev(list)", "nn stddev($1)"),
                makeFunctionCandidate("nn stddev(n)", "nn stddev($1)"),
                makeFunctionCandidate("nn sum(list)", "nn sum($1)"),
                makeFunctionCandidate("nn sum(n)", "nn sum($1)"),
                makeFunctionCandidate("not(negand)", "not($1)"),
                makeFunctionCandidate("now()", "now()"),
                makeFunctionCandidate("number(from, grouping separator, decimal separator)", "number($1, $2, $3)"),
                makeFunctionCandidate("odd(number)", "odd($1)"),
                makeFunctionCandidate("overlapped after by(range1, range2)", "overlapped after by($1, $2)"),
                makeFunctionCandidate("overlapped before by(range1, range2)", "overlapped before by($1, $2)"),
                makeFunctionCandidate("overlapped by(range1, range2)", "overlapped by($1, $2)"),
                makeFunctionCandidate("overlaps after(range1, range2)", "overlaps after($1, $2)"),
                makeFunctionCandidate("overlaps before(range1, range2)", "overlaps before($1, $2)"),
                makeFunctionCandidate("overlaps(range1, range2)", "overlaps($1, $2)"),
                makeFunctionCandidate("product(list)", "product($1)"),
                makeFunctionCandidate("product(n)", "product($1)"),
                makeFunctionCandidate("remove(list, position)", "remove($1, $2)"),
                makeFunctionCandidate("replace(input, pattern, replacement)", "replace($1, $2, $3)"),
                makeFunctionCandidate("replace(input, pattern, replacement, flags)", "replace($1, $2, $3, $4)"),
                makeFunctionCandidate("reverse(list)", "reverse($1)"),
                makeFunctionCandidate("sort()", "sort()"),
                makeFunctionCandidate("sort(ctx, list, precedes)", "sort($1, $2, $3)"),
                makeFunctionCandidate("sort(list)", "sort($1)"),
                makeFunctionCandidate("split(string, delimiter)", "split($1, $2)"),
                makeFunctionCandidate("split(string, delimiter, flags)", "split($1, $2, $3)"),
                makeFunctionCandidate("sqrt(number)", "sqrt($1)"),
                makeFunctionCandidate("started by(range, value)", "started by($1, $2)"),
                makeFunctionCandidate("started by(range1, range2)", "started by($1, $2)"),
                makeFunctionCandidate("starts with(string, match)", "starts with($1, $2)"),
                makeFunctionCandidate("starts(range1, range2)", "starts($1, $2)"),
                makeFunctionCandidate("starts(value, range)", "starts($1, $2)"),
                makeFunctionCandidate("stddev(list)", "stddev($1)"),
                makeFunctionCandidate("stddev(n)", "stddev($1)"),
                makeFunctionCandidate("string length(string)", "string length($1)"),
                makeFunctionCandidate("string(from)", "string($1)"),
                makeFunctionCandidate("string(mask, p)", "string($1, $2)"),
                makeFunctionCandidate("sublist(list, start position)", "sublist($1, $2)"),
                makeFunctionCandidate("sublist(list, start position, length)", "sublist($1, $2, $3)"),
                makeFunctionCandidate("substring after(string, match)", "substring after($1, $2)"),
                makeFunctionCandidate("substring before(string, match)", "substring before($1, $2)"),
                makeFunctionCandidate("substring(string, start position)", "substring($1, $2)"),
                makeFunctionCandidate("substring(string, start position, length)", "substring($1, $2, $3)"),
                makeFunctionCandidate("sum(list)", "sum($1)"),
                makeFunctionCandidate("sum(n)", "sum($1)"),
                makeFunctionCandidate("time(from)", "time($1)"),
                makeFunctionCandidate("time(hour, minute, second)", "time($1, $2, $3)"),
                makeFunctionCandidate("time(hour, minute, second, offset)", "time($1, $2, $3, $4)"),
                makeFunctionCandidate("today()", "today()"),
                makeFunctionCandidate("union(list)", "union($1)"),
                makeFunctionCandidate("upper case(string)", "upper case($1)"),
                makeFunctionCandidate("week of year(date)", "week of year($1)"),
                makeFunctionCandidate("years and months duration(from, to)", "years and months duration($1, $2)")
        );
    }

    private Candidate makeKeywordCandidate(final String label) {
        return new Candidate(label, Keyword);
    }

    private Candidate makeFunctionCandidate(final String label,
                                            final String insertText) {
        return new Candidate(label, insertText, Function);
    }
}
