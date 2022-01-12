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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.vmware.antlr4c3.CodeCompletionCore;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.kie.dmn.feel.gwt.functions.api.FunctionDefinitionStrings;
import org.kie.dmn.feel.gwt.functions.api.FunctionOverrideVariation;
import org.kie.dmn.feel.gwt.functions.client.FEELFunctionProvider;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.parser.feel11.ASTBuilderVisitor;
import org.kie.dmn.feel.parser.feel11.FEELParser;
import org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser;

import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.ADD;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.ANY_OTHER_CHAR;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.AT;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.BANG;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.BooleanLiteral;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.COLON;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.COMMA;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.COMMENT;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.DIV;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.DOT;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.ELIPSIS;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.EQUAL;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.FloatingPointLiteral;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.GE;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.GT;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.Identifier;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.IntegerLiteral;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.LBRACE;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.LBRACK;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.LE;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.LINE_COMMENT;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.LPAREN;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.LT;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.MUL;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.NOTEQUAL;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.NULL;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.POW;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.QUOTE;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.RARROW;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.RBRACE;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.RBRACK;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.RPAREN;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.SUB;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.StringLiteral;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.WS;
import static org.kie.workbench.common.dmn.client.widgets.codecompletion.feel.DisplayNameUtils.getDisplayName;

@ApplicationScoped
public class FEELLanguageService {

    private static final Set<Integer> IGNORED_TOKENS = Stream
            .of(ADD, ANY_OTHER_CHAR, AT, BANG, BooleanLiteral, COLON, COMMA, COMMENT, DIV, DOT,
                ELIPSIS, EQUAL, FloatingPointLiteral, GE, GT, Identifier, IntegerLiteral, LBRACE,
                LBRACK, LE, LINE_COMMENT, LPAREN, LT, MUL, NOTEQUAL, NULL, POW, QUOTE, RARROW, RBRACE,
                RBRACK, RPAREN, StringLiteral, SUB, WS).collect(Collectors.toCollection(HashSet::new));

    private final List<FunctionOverrideVariation> functions = new ArrayList<>();

    private final TypeStackUtils typeStackUtils;

    @Inject
    public FEELLanguageService(final TypeStackUtils typeStackUtils) {
        this.typeStackUtils = typeStackUtils;
    }

    public List<Candidate> getCandidates(final String text,
                                         final List<Variable> variables,
                                         final Position position) {

        final FEEL_1_1Parser parser = getParser(text);
        final ParseTree parseTree = parser.expression();
        final BaseNode astNode = getASTNode(parseTree);
        final Type type = getType(astNode, position);

        final List<Candidate> candidateVariables = getCandidateVariables(type, variables);
        final List<Candidate> candidateKeyword = getFeelKeywords(parseTree, parser, position);
        final List<Candidate> candidateFunctions = getCandidateFunctions(type);

        final List<Candidate> keywords = new ArrayList<>();

        keywords.addAll(candidateVariables);
        keywords.addAll(candidateFunctions);
        keywords.addAll(candidateKeyword);

        return keywords;
    }

    private List<Candidate> getCandidateVariables(final Type type,
                                                  final List<Variable> variables) {
        return variables
                .stream()
                .filter(v -> v.getType().conformsTo(type))
                .map(v -> new Candidate(v.getName(), CompletionItemKind.Variable))
                .collect(Collectors.toList());
    }

    private List<Candidate> getFeelKeywords(final ParseTree parseTree,
                                            final FEEL_1_1Parser parser,
                                            final Position position) {

        final CodeCompletionCore core = new CodeCompletionCore(parser, null, IGNORED_TOKENS);
        final int caretIndex = computeTokenIndex(parseTree, position);
        final CodeCompletionCore.CandidatesCollection candidates = core.collectCandidates(caretIndex, parser.getContext());

        return getCandidateNames(parser, candidates)
                .stream()
                .map(name -> new Candidate(name, CompletionItemKind.Keyword))
                .collect(Collectors.toList());
    }

    FEEL_1_1Parser getParser(final String text) {
        return FEELParser.parse(null,
                                text,
                                Collections.emptyMap(),
                                Collections.emptyMap(),
                                Collections.emptyList(),
                                Collections.emptyList(),
                                null);
    }

    Type getType(final BaseNode astNode,
                 final Position position) {
        final List<Type> typeStack = typeStackUtils.getTypeStack(astNode, position);
        return typeStack.size() == 0 ? BuiltInType.UNKNOWN : typeStack.get(typeStack.size() - 1);
    }

    private List<Candidate> getCandidateFunctions(final Type type) {

        final List<Candidate> functions = new ArrayList<>();

        for (final FunctionOverrideVariation function : getFunctions()) {
            if (type.isAssignableValue(BuiltInType.UNKNOWN) || Objects.equals(function.getReturnType(), type)) {

                final FunctionDefinitionStrings definitionStrings = function.toHumanReadableStrings();
                final String humanReadable = definitionStrings.getHumanReadable();
                final String template = definitionStrings.getTemplate();

                functions.add(new Candidate(humanReadable, template, CompletionItemKind.Function));
            }
        }

        return functions;
    }

    private List<FunctionOverrideVariation> getFunctions() {
        if (functions.isEmpty()) {
            functions.addAll(getFunctionOverrideVariations());
        }
        return functions;
    }

    public List<FunctionOverrideVariation> getFunctionOverrideVariations() {
        final FEELFunctionProvider functionProvider = GWT.create(FEELFunctionProvider.class);
        return functionProvider.getDefinitions();
    }

    BaseNode getASTNode(final ParseTree parseTree) {
        return new ASTBuilderVisitor(Collections.emptyMap(), null).visit(parseTree);
    }

    private List<String> getCandidateNames(final FEEL_1_1Parser parser,
                                           final CodeCompletionCore.CandidatesCollection candidates) {

        final List<String> candidateNames = new ArrayList<>();

        candidateNames.addAll(getDisplayName(parser, candidates.tokens));
        candidateNames.addAll(getDisplayName(parser, candidates.rules));
        candidateNames.addAll(getDisplayName(parser, candidates.rulePositions));

        return candidateNames;
    }

    private Integer computeTokenIndex(final ParseTree parseTree,
                                      final Position position) {
        if (parseTree instanceof TerminalNode) {
            return computeTokenIndexOfTerminalNode((TerminalNode) parseTree, position);
        } else {
            return computeTokenIndexOfChildNode(parseTree, position);
        }
    }

    private Integer computeTokenIndexOfTerminalNode(final TerminalNode parseTree,
                                                    final Position position) {

        final Token symbol = parseTree.getSymbol();
        final int start = symbol.getCharPositionInLine();
        final int stop = symbol.getCharPositionInLine() + parseTree.getText().length();

        if (symbol.getLine() == position.line && position.column >= start && position.column <= stop) {
            return symbol.getTokenIndex();
        } else {
            return 0;
        }
    }

    private Integer computeTokenIndexOfChildNode(final ParseTree parseTree,
                                                 final Position position) {
        for (int i = 0; i < parseTree.getChildCount(); i++) {
            int index = computeTokenIndex(parseTree.getChild(i), position);
            if (index != 0) {
                return index;
            }
        }
        return 0;
    }

    public static class Position {

        int line;
        int column;

        public Position(final int line,
                        final int column) {
            this.line = line;
            this.column = column;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final Position position = (Position) o;
            return line == position.line && column == position.column;
        }

        @Override
        public int hashCode() {
            return Objects.hash(line, column);
        }

        @Override
        public String toString() {
            return "Position{" +
                    "line=" + line +
                    ", column=" + column +
                    '}';
        }
    }
}
