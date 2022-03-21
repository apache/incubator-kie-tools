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

package org.kie.workbench.common.dmn.client.widgets.codecompletion.feel;

import java.util.List;

import com.google.gwtmockito.GwtMockito;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.antlr.v4.runtime.tree.ParseTree;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.dmn.feel.gwt.functions.api.FunctionOverrideVariation;
import org.kie.dmn.feel.gwt.functions.api.Parameter;
import org.kie.dmn.feel.gwt.functions.client.FEELFunctionProvider;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.client.widgets.codecompletion.feel.FEELLanguageService.Position;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

@RunWith(GwtMockitoTestRunner.class)
public class FEELLanguageServiceTest {

    private FEELLanguageService service;

    private List<FunctionOverrideVariation> functionOverrideVariations;

    @Mock
    private FEELFunctionProvider feelFunctionProviderMock;

    @Before
    public void setup() {
        service = spy(new FEELLanguageService(new TypeStackUtils()));
        functionOverrideVariations = getFunctionOverrideVariations();

        GwtMockito.useProviderForType(FEELFunctionProvider.class, aClass -> feelFunctionProviderMock);

        doReturn(functionOverrideVariations).when(service).getFunctionOverrideVariations();
        doReturn(functionOverrideVariations).when(feelFunctionProviderMock).getDefinitions();
    }

    @Test
    public void testGetCandidatesForNumberScenario() {

        final String expression = "2 + |";
        final Position position = new Position(1, expression.indexOf("|"));
        final String expressionToParse = expression.replace("|", "");
        final List<Variable> variables = asList(new Variable("Decision-1", BuiltInType.STRING),
                                                new Variable("Decision-2", BuiltInType.NUMBER),
                                                new Variable("Decision-3", BuiltInType.BOOLEAN),
                                                new Variable("Decision-4", BuiltInType.NUMBER),
                                                new Variable("Decision-5", BuiltInType.DATE));

        final List<Candidate> actualCandidates = service.getCandidates(expressionToParse, variables, position);

        Assertions.assertThat(actualCandidates).containsExactly(
                new Candidate("Decision-2", "Decision-2", CompletionItemKind.Variable),
                new Candidate("Decision-4", "Decision-4", CompletionItemKind.Variable),
                new Candidate("abs(duration)", "abs($1)", CompletionItemKind.Function),
                new Candidate("abs(number)", "abs($1)", CompletionItemKind.Function),
                new Candidate("sum(list)", "sum($1)", CompletionItemKind.Function),
                new Candidate("not", "not", CompletionItemKind.Keyword),
                new Candidate("for", "for", CompletionItemKind.Keyword),
                new Candidate("if", "if", CompletionItemKind.Keyword),
                new Candidate("some", "some", CompletionItemKind.Keyword),
                new Candidate("every", "every", CompletionItemKind.Keyword),
                new Candidate("function", "function", CompletionItemKind.Keyword)
        );
    }

    @Test
    public void testGetCandidatesForStringScenario() {

        final String expression = "\"\" + |";
        final Position position = new Position(1, expression.indexOf("|"));
        final String expressionToParse = expression.replace("|", "");
        final List<Variable> variables = asList(new Variable("Decision-1", BuiltInType.STRING),
                                                new Variable("Decision-2", BuiltInType.NUMBER),
                                                new Variable("Decision-3", BuiltInType.BOOLEAN),
                                                new Variable("Decision-4", BuiltInType.NUMBER),
                                                new Variable("Decision-5", BuiltInType.DATE));

        final List<Candidate> actualCandidates = service.getCandidates(expressionToParse, variables, position);

        Assertions.assertThat(actualCandidates).containsExactly(
                new Candidate("Decision-1", "Decision-1", CompletionItemKind.Variable),
                new Candidate("string(number)", "string($1)", CompletionItemKind.Function),
                new Candidate("not", "not", CompletionItemKind.Keyword),
                new Candidate("for", "for", CompletionItemKind.Keyword),
                new Candidate("if", "if", CompletionItemKind.Keyword),
                new Candidate("some", "some", CompletionItemKind.Keyword),
                new Candidate("every", "every", CompletionItemKind.Keyword),
                new Candidate("function", "function", CompletionItemKind.Keyword)
        );
    }

    @Test
    public void testGetCandidatesForBooleanScenario() {

        final String expression = "true and + |";
        final Position position = new Position(1, expression.indexOf("|"));
        final String expressionToParse = expression.replace("|", "");
        final List<Variable> variables = asList(new Variable("Decision-1", BuiltInType.STRING),
                                                new Variable("Decision-2", BuiltInType.NUMBER),
                                                new Variable("Decision-3", BuiltInType.BOOLEAN),
                                                new Variable("Decision-4", BuiltInType.NUMBER),
                                                new Variable("Decision-5", BuiltInType.DATE));

        final List<Candidate> actualCandidates = service.getCandidates(expressionToParse, variables, position);

        Assertions.assertThat(actualCandidates).containsExactly(
                new Candidate("Decision-3", "Decision-3", CompletionItemKind.Variable),
                new Candidate("any(list)", "any($1)", CompletionItemKind.Function),
                new Candidate("not", "not", CompletionItemKind.Keyword),
                new Candidate("for", "for", CompletionItemKind.Keyword),
                new Candidate("if", "if", CompletionItemKind.Keyword),
                new Candidate("some", "some", CompletionItemKind.Keyword),
                new Candidate("every", "every", CompletionItemKind.Keyword),
                new Candidate("function", "function", CompletionItemKind.Keyword)
        );
    }

    @Test
    public void testGetCandidatesForDateScenario() {

        final String expression = "now() + |";
        final Position position = new Position(1, expression.indexOf("|"));
        final String expressionToParse = expression.replace("|", "");
        final List<Variable> variables = asList(new Variable("Decision-1", BuiltInType.STRING),
                                                new Variable("Decision-2", BuiltInType.NUMBER),
                                                new Variable("Decision-3", BuiltInType.BOOLEAN),
                                                new Variable("Decision-4", BuiltInType.NUMBER),
                                                new Variable("Decision-5", BuiltInType.DATE));

        final List<Candidate> actualCandidates = service.getCandidates(expressionToParse, variables, position);

        Assertions.assertThat(actualCandidates).containsExactly(
                new Candidate("Decision-5", "Decision-5", CompletionItemKind.Variable),
                new Candidate("date(string)", "date($1)", CompletionItemKind.Function),
                new Candidate("date(number, number, number)", "date($1, $2, $3)", CompletionItemKind.Function),
                new Candidate("now()", "now()", CompletionItemKind.Function),
                new Candidate("not", "not", CompletionItemKind.Keyword),
                new Candidate("for", "for", CompletionItemKind.Keyword),
                new Candidate("if", "if", CompletionItemKind.Keyword),
                new Candidate("some", "some", CompletionItemKind.Keyword),
                new Candidate("every", "every", CompletionItemKind.Keyword),
                new Candidate("function", "function", CompletionItemKind.Keyword)
        );
    }

    @Test
    @Ignore("DROOLS-6210")
    public void testGetCandidatesForVariableScenario() {

        final String expression = "Decision-2 + |";
        final Position position = new Position(1, expression.indexOf("|"));
        final String expressionToParse = expression.replace("|", "");
        final List<Variable> variables = asList(new Variable("Decision-1", BuiltInType.STRING),
                                                new Variable("Decision-2", BuiltInType.NUMBER),
                                                new Variable("Decision-3", BuiltInType.BOOLEAN),
                                                new Variable("Decision-4", BuiltInType.NUMBER),
                                                new Variable("Decision-5", BuiltInType.DATE));

        final List<Candidate> actualCandidates = service.getCandidates(expressionToParse, variables, position);

        Assertions.assertThat(actualCandidates).containsExactly(
                new Candidate("Decision-2", "Decision-2", CompletionItemKind.Variable),
                new Candidate("Decision-4", "Decision-4", CompletionItemKind.Variable),
                new Candidate("abs(duration)", "abs($1)", CompletionItemKind.Function),
                new Candidate("abs(number)", "abs($1)", CompletionItemKind.Function),
                new Candidate("sum(list)", "sum($1)", CompletionItemKind.Function),
                new Candidate("not", "not", CompletionItemKind.Keyword),
                new Candidate("for", "for", CompletionItemKind.Keyword),
                new Candidate("if", "if", CompletionItemKind.Keyword),
                new Candidate("some", "some", CompletionItemKind.Keyword),
                new Candidate("every", "every", CompletionItemKind.Keyword),
                new Candidate("function", "function", CompletionItemKind.Keyword)
        );
    }

    @Test
    public void testGetCandidatesWhenTypeDoesNotMatch() {

        final String expression = "[1..10] |";
        final Position position = new Position(1, expression.indexOf("|"));
        final String expressionToParse = expression.replace("|", "");
        final List<Variable> variables = asList(new Variable("Decision-1", BuiltInType.STRING),
                                                new Variable("Decision-2", BuiltInType.NUMBER),
                                                new Variable("Decision-3", BuiltInType.BOOLEAN),
                                                new Variable("Decision-4", BuiltInType.NUMBER),
                                                new Variable("Decision-5", BuiltInType.DATE));

        final List<Candidate> actualCandidates = service.getCandidates(expressionToParse, variables, position);

        Assertions.assertThat(actualCandidates).containsExactly(
                new Candidate("not", "not", CompletionItemKind.Keyword),
                new Candidate("for", "for", CompletionItemKind.Keyword),
                new Candidate("if", "if", CompletionItemKind.Keyword),
                new Candidate("some", "some", CompletionItemKind.Keyword),
                new Candidate("every", "every", CompletionItemKind.Keyword),
                new Candidate("function", "function", CompletionItemKind.Keyword)
        );
    }

    @Test
    public void testGetTypeSingleLine() {
        assertGetType("|(1 + (2 + (\"\" + (4 + 5))))", BuiltInType.UNKNOWN);
        assertGetType("(1| + (2 + (\"\" + (4 + 5))))", BuiltInType.NUMBER);
        assertGetType("(1 |+ (2 + (\"\" + (4 + 5))))", BuiltInType.NUMBER);
        assertGetType("(1 +| (2 + (\"\" + (4 + 5))))", BuiltInType.NUMBER);
        assertGetType("(1 + |(2 + (\"\" + (4 + 5))))", BuiltInType.NUMBER);
        assertGetType("(1 + (|2 + (\"\" + (4 + 5))))", BuiltInType.NUMBER);
        assertGetType("(1 + (2| + (\"\" + (4 + 5))))", BuiltInType.NUMBER);
        assertGetType("(1 + (2 |+ (\"\" + (4 + 5))))", BuiltInType.NUMBER);
        assertGetType("(1 + (2 +| (\"\" + (4 + 5))))", BuiltInType.NUMBER);
        assertGetType("(1 + (2 + |(\"\" + (4 + 5))))", BuiltInType.NUMBER);
        assertGetType("(1 + (2 + (|\"\" + (4 + 5))))", BuiltInType.STRING);
        assertGetType("(1 + (2 + (\"\"| + (4 + 5))))", BuiltInType.STRING);
        assertGetType("(1 + (2 + (\"\" |+ (4 + 5))))", BuiltInType.STRING);
        assertGetType("(1 + (2 + (\"\" +| (4 + 5))))", BuiltInType.STRING);
        assertGetType("(1 + (2 + (\"\" + |(4 + 5))))", BuiltInType.STRING);
        assertGetType("(1 + (2 + (\"\" + (|4 + 5))))", BuiltInType.NUMBER);
        assertGetType("(1 + (2 + (\"\" + (4| + 5))))", BuiltInType.NUMBER);
        assertGetType("(1 + (2 + (\"\" + (4 |+ 5))))", BuiltInType.NUMBER);
        assertGetType("(1 + (2 + (\"\" + (4 +| 5))))", BuiltInType.NUMBER);
        assertGetType("(1 + (2 + (\"\" + (4 + |5))))", BuiltInType.NUMBER);
        assertGetType("(1 + (2 + (\"\" + (4 + 5|))))", BuiltInType.NUMBER);
        assertGetType("(1 + (2 + (\"\" + (4 + 5)|)))", BuiltInType.NUMBER);
        assertGetType("(1 + (2 + (\"\" + (4 + 5))|))", BuiltInType.NUMBER);
        assertGetType("(1 + (2 + (\"\" + (4 + 5)))|)", BuiltInType.NUMBER);
        assertGetType("(1 + (2 + (\"\" + (4 + 5))))|", BuiltInType.NUMBER);
    }

    @Test
    public void testGetTypeMultiLine() {
        assertGetType("" +
                              "(                    \n" +
                              "  1| + (             \n" +
                              "    2 + (            \n" +
                              "      \"\" + (4 + 5) \n" +
                              "    )                \n" +
                              "  )                  \n" +
                              ")                    \n", BuiltInType.NUMBER);
        assertGetType("" +
                              "(                    \n" +
                              "  1 + (              \n" +
                              "    2| + (           \n" +
                              "      \"\" + (4 + 5) \n" +
                              "    )                \n" +
                              "  )                  \n" +
                              ")                    \n", BuiltInType.NUMBER);
        assertGetType("" +
                              "(                    \n" +
                              "  1 + (              \n" +
                              "    2 + (            \n" +
                              "      \"\"| + (4 + 5)\n" +
                              "    )                \n" +
                              "  )                  \n" +
                              ")                    \n", BuiltInType.STRING);
        assertGetType("" +
                              "(                    \n" +
                              "  1 + (              \n" +
                              "    2 + (            \n" +
                              "      \"\" + (4| + 5)\n" +
                              "    )                \n" +
                              "  )                  \n" +
                              ")                    \n", BuiltInType.NUMBER);

        assertGetType("" +
                              "date(                \n" +
                              "  2021,              \n" +
                              "     1,              \n" +
                              "     1               \n" +
                              ")                   |\n", BuiltInType.DATE);

        /*
        DROOLS-6214
        assertGetType("" +
                              "date(                \n" +
                              "  2021 |,            \n" +
                              "     1,              \n" +
                              "     1               \n" +
                              ")                    \n", BuiltInType.NUMBER);
         */
    }

    @Test
    public void testLiteralTypes() {
        assertGetType("\"\"           |", BuiltInType.STRING);
        assertGetType("[1, 2, 3]      |", BuiltInType.LIST);
        assertGetType("2              |", BuiltInType.NUMBER);
        assertGetType("2 -            |", BuiltInType.NUMBER);
        assertGetType("2 *            |", BuiltInType.NUMBER);
        assertGetType("2 /            |", BuiltInType.NUMBER);
        assertGetType("false          |", BuiltInType.BOOLEAN);
        assertGetType("false and      |", BuiltInType.BOOLEAN);
        assertGetType("[1..10]        |", BuiltInType.RANGE);
        assertGetType("function() { } |", BuiltInType.FUNCTION);
        assertGetType("\"\" + 2       |", BuiltInType.UNKNOWN);
    }

    @Test
    public void testAdvancedLiteralTypes() {

        // DROOLS-6207
        // assertGetType("not false      |", BuiltInType.BOOLEAN);

        // = DROOLS-6211 =
        // assertGetType("sum([1,2,3]) + sum([3,2,1])                     |", BuiltInType.NUMBER);

        // DROOLS-6209
        // assertGetType("date( \"2012-12-25\" ) - date( \"2012-12-24\" ) |", BuiltInType.DURATION);
        assertGetType("date(date and time( \"2012-12-25T11:00:00Z\" )) |", BuiltInType.DATE);
        assertGetType("sum([1,2,3])                                    |", BuiltInType.NUMBER);
    }

    private void assertGetType(final String expression,
                               final BuiltInType expected) {

        final String cursor = "|";
        final int line = expression.substring(0, expression.indexOf(cursor)).split("\n").length;
        final int column = expression.split("\n")[line - 1].indexOf(cursor);
        final Position position = new Position(line, column);
        final String expressionToParse = expression.replace(cursor, "");
        final BaseNode astNode = getASTNode(expressionToParse);

        final Type actual = service.getType(astNode, position);

        assertEquals(expected, actual);
    }

    private BaseNode getASTNode(final String expressionToParse) {
        final FEEL_1_1Parser parser = service.getParser(expressionToParse);
        final ParseTree parseTree = parser.expression();
        return service.getASTNode(parseTree);
    }

    private List<FunctionOverrideVariation> getFunctionOverrideVariations() {
        return asList(
                new FunctionOverrideVariation(BuiltInType.NUMBER, "abs", new Parameter("duration", BuiltInType.DURATION)),
                new FunctionOverrideVariation(BuiltInType.NUMBER, "abs", new Parameter("number", BuiltInType.NUMBER)),
                new FunctionOverrideVariation(BuiltInType.BOOLEAN, "any", new Parameter("list", BuiltInType.LIST)),
                new FunctionOverrideVariation(BuiltInType.DATE, "date", new Parameter("from", BuiltInType.STRING)),
                new FunctionOverrideVariation(BuiltInType.DATE, "date",
                                              new Parameter("year", BuiltInType.NUMBER),
                                              new Parameter("month", BuiltInType.NUMBER),
                                              new Parameter("day", BuiltInType.NUMBER)),
                new FunctionOverrideVariation(BuiltInType.DATE, "now"),
                new FunctionOverrideVariation(BuiltInType.NUMBER, "sum", new Parameter("list", BuiltInType.LIST)),
                new FunctionOverrideVariation(BuiltInType.STRING, "string", new Parameter("from", BuiltInType.NUMBER))
        );
    }
}
