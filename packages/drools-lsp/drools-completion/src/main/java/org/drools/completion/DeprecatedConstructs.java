/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.drools.completion;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.drools.drl.parser.antlr4.DRL10Lexer;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

/**
 * Recognizes DRL constructs the engine's legacy parser still compiles but has
 * deprecated for removal — the families {@code org.drools.drl.parser.lang.ParserHelper}
 * warns about: half constraints, patterns joined with {@code &&}/{@code ||},
 * annotations inside LHS patterns, {@code agenda-group}, and custom operators
 * without the {@code ##} prefix DRL10 requires (apache/incubator-kie#6220). A
 * custom operator is recognized only when the project declares it — see
 * {@link CustomOperators}.
 *
 * <p>The engine's own warnings fire once per family per parse and carry no
 * position, so they cannot drive a migration. Nor can this grammar's parse
 * errors: it rejects some of these constructs ({@code age > 18 && < 65}),
 * reads others as something else without an error
 * ({@code ref.order > $ref.order && < $x}), and still accepts a third kind
 * (pattern annotations). Detection therefore scans the token stream, which the
 * lexer produces even where the parser fails, and relies on paren depth and
 * section for precision. A recognized construct replaces the parse errors on
 * its line, which describe the same thing less usefully; otherwise the
 * deprecation is added.
 *
 * <p>Severity comes from {@value #PROP_DEPRECATED} (default {@code warning});
 * {@code off} disables recognition, leaving the raw parse errors in place. At
 * most one construct is reported per line; a second surfaces once the first is
 * migrated, which keeps a sweep's output one finding per line rather than one
 * per recovery attempt.
 */
final class DeprecatedConstructs {

    /** System property: severity for deprecated-construct diagnostics. */
    static final String PROP_DEPRECATED = "drools.lsp.lint.deprecated";

    private static final String SOURCE = "drools-deprecated";

    private static final String REMOVAL = " will be removed in a future version (LanguageLevel.DRL10)";

    /**
     * A recognized construct: the message to show and, when the migration is
     * mechanical, the text that should replace the diagnostic's range. A
     * {@code null} replacement means the construct needs a human decision
     * (moving an annotation) or a rulebase-wide pass ({@code agenda-group}),
     * so no quick fix is offered.
     */
    private static final class Finding {
        final String message;
        final String replacement;

        Finding(String message, String replacement) {
            this.message = message;
            this.replacement = replacement;
        }
    }

    /** Tokens that bound the left-hand operand when scanning back from an operator. */
    private static final Set<Integer> OPERAND_BOUNDARY = Set.of(
            DRL10Lexer.LPAREN, DRL10Lexer.COMMA, DRL10Lexer.AND, DRL10Lexer.OR, DRL10Lexer.SEMI);

    /** Relational operators that, with no left operand, form a half constraint. */
    private static final Set<Integer> RELATIONAL = Set.of(
            DRL10Lexer.LT, DRL10Lexer.GT, DRL10Lexer.LE, DRL10Lexer.GE,
            DRL10Lexer.EQUAL, DRL10Lexer.NOTEQUAL);

    /** Tokens that can end the left-hand side of a custom-operator comparison. */
    private static final Set<Integer> OPERAND_END = Set.of(
            DRL10Lexer.IDENTIFIER, DRL10Lexer.RPAREN);

    private DeprecatedConstructs() {
    }

    /**
     * Returns {@code syntaxDiagnostics} with every error that marks a deprecated
     * construct replaced by a diagnostic naming it, and the follow-on errors on
     * those lines removed. Returns the input unchanged when recognition is off
     * or nothing matches.
     */
    static List<Diagnostic> reclassify(String text, List<Diagnostic> syntaxDiagnostics) {
        List<Diagnostic> syntax = syntaxDiagnostics == null ? List.of() : syntaxDiagnostics;
        if (text == null || text.isEmpty()) {
            return syntax;
        }
        DiagnosticSeverity severity = DRLLintHelper.severityFor(PROP_DEPRECATED, "warning");
        if (severity == null) {
            return syntax;
        }
        List<Token> tokens = defaultChannelTokens(text);
        if (tokens.isEmpty()) {
            return syntax;
        }

        List<Diagnostic> found = new ArrayList<>();
        Set<Integer> claimedLines = new HashSet<>();
        boolean inWhen = false;
        int parenDepth = 0;
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            int type = token.getType();
            if (type == DRL10Lexer.DRL_WHEN) {
                inWhen = true;
                parenDepth = 0;
            } else if (type == DRL10Lexer.DRL_THEN || type == DRL10Lexer.DRL_END) {
                inWhen = false;
                parenDepth = 0;
            }

            Finding finding = describe(tokens, i, inWhen, parenDepth, text);
            if (finding != null) {
                claimedLines.add(token.getLine() - 1);
                Diagnostic d = new Diagnostic();
                d.setSeverity(severity);
                d.setSource(SOURCE);
                d.setMessage(finding.message);
                d.setRange(rangeOf(token));
                // The replacement rides in `data` for the quick fix, the same
                // way the unknown-type lint carries its suggestion.
                if (finding.replacement != null) {
                    d.setData(finding.replacement);
                }
                found.add(d);
            }

            if (type == DRL10Lexer.LPAREN) {
                parenDepth++;
            } else if (type == DRL10Lexer.RPAREN && parenDepth > 0) {
                parenDepth--;
            }
        }
        if (found.isEmpty()) {
            return syntax;
        }
        for (Diagnostic d : syntax) {
            if (d.getRange() == null || d.getRange().getStart() == null
                    || !claimedLines.contains(d.getRange().getStart().getLine())) {
                found.add(d);
            }
        }
        return found;
    }

    /**
     * Names the deprecated construct at {@code index}, or {@code null} when the
     * tokens there are an ordinary parse failure. Order matters: a {@code &&}
     * before a relational operator is a half constraint, while the same token
     * after a closing paren joins two patterns.
     */
    private static Finding describe(List<Token> tokens, int index, boolean inWhen,
                                    int parenDepth, String text) {
        Token token = tokens.get(index);
        int type = token.getType();
        Token next = index + 1 < tokens.size() ? tokens.get(index + 1) : null;
        Token previous = index > 0 ? tokens.get(index - 1) : null;
        boolean connective = type == DRL10Lexer.AND || type == DRL10Lexer.OR;
        boolean insideConstraint = inWhen && parenDepth >= 1;

        // Half constraint: `age > 18 && < 65`, inside a pattern's constraints.
        // The fix repeats the operand the preceding comparison used, so
        // `ref.order > $ref.order && < $x` becomes
        // `ref.order > $ref.order && ref.order < $x`.
        if (insideConstraint && connective && next != null && RELATIONAL.contains(next.getType())) {
            String operand = leftOperandOfPreviousComparison(tokens, index, text);
            return new Finding("Deprecated: half constraint '" + token.getText() + " " + next.getText()
                    + "'" + REMOVAL + " — repeat the left operand.",
                    operand == null ? null : token.getText() + " " + operand);
        }

        // Pattern connective: `Person( ... ) && Account( ... )`. Depth is the
        // discriminator — the pattern's own ')' has already closed, so a
        // pattern-level connective sits at depth 0, while the same tokens in
        // `Person( (age > 1) && (age < 5) )` are still inside the constraint
        // list and must not be flagged.
        if (inWhen && parenDepth == 0 && connective
                && previous != null && previous.getType() == DRL10Lexer.RPAREN) {
            String replacement = type == DRL10Lexer.AND ? "and" : "or";
            return new Finding("Deprecated: connecting patterns with '" + token.getText() + "'"
                    + REMOVAL + " — use '" + replacement + "' instead.", replacement);
        }

        // Annotation inside a pattern: `Person( @watch(age) age > 1 )`. Moving
        // or dropping it is a judgement call, so no fix is offered.
        if (insideConstraint && type == DRL10Lexer.AT) {
            return new Finding("Deprecated: annotations inside LHS patterns" + REMOVAL
                    + "; annotations elsewhere remain supported.", null);
        }

        // `agenda-group "x"` in a rule's header — lexed as the identifier
        // `agenda`, `-`, `group` because DRL10 has no such keyword. No fix: the
        // switch to ruleflow-group changes activation semantics and is
        // all-or-nothing across a rulebase, so it belongs in one deliberate
        // pass rather than per-occurrence clicks.
        if (!inWhen && type == DRL10Lexer.IDENTIFIER && "agenda".equals(token.getText())
                && next != null && next.getType() == DRL10Lexer.SUB
                && index + 2 < tokens.size()
                && "group".equals(tokens.get(index + 2).getText())) {
            return new Finding("Deprecated: 'agenda-group'" + REMOVAL
                    + " — migrate to 'ruleflow-group' across the rulebase.", null);
        }

        // Custom operator used without its DRL10 prefix: `name supersetOf "x"`.
        // Only an operator the project declares counts (see CustomOperators):
        // an undeclared identifier between two operands is just as likely a
        // typo, and stays the parse error it is.
        if (insideConstraint && type == DRL10Lexer.IDENTIFIER
                && previous != null && next != null
                && OPERAND_END.contains(previous.getType())
                && isOperandStart(next)
                && CustomOperators.isRegistered(token.getText())) {
            return new Finding("Deprecated: custom operator '" + token.getText()
                    + "' requires a '##' prefix in DRL10 — write '##" + token.getText() + "'.",
                    "##" + token.getText());
        }

        return null;
    }

    /**
     * The source text of the operand on the left of the comparison preceding
     * {@code connectiveIndex} — the operand a half constraint omitted. Scans
     * back for that comparison's operator, then for the operand in front of it,
     * stopping at a boundary ({@code (}, {@code ,}, {@code &&}, {@code ||},
     * {@code ;}) so it never reaches into a neighbouring constraint. Returns
     * {@code null} when the shape is not recognizable, leaving no quick fix
     * rather than a wrong one.
     */
    private static String leftOperandOfPreviousComparison(List<Token> tokens, int connectiveIndex,
                                                          String text) {
        int operator = -1;
        for (int i = connectiveIndex - 1; i >= 0; i--) {
            int type = tokens.get(i).getType();
            if (RELATIONAL.contains(type)) {
                operator = i;
                break;
            }
            if (OPERAND_BOUNDARY.contains(type)) {
                return null;
            }
        }
        if (operator <= 0) {
            return null;
        }
        int end = operator - 1;
        int start = end;
        for (int i = end; i >= 0; i--) {
            int type = tokens.get(i).getType();
            if (OPERAND_BOUNDARY.contains(type) || RELATIONAL.contains(type)) {
                break;
            }
            start = i;
        }
        Token first = tokens.get(start);
        Token last = tokens.get(end);
        int from = first.getStartIndex();
        int to = last.getStopIndex() + 1;
        if (from < 0 || to > text.length() || from >= to) {
            return null;
        }
        // Sliced from the source so dotted paths and bindings keep their exact
        // spelling rather than being rebuilt from token texts.
        return text.substring(from, to).trim();
    }

    /** True when a token can begin the right-hand side of a comparison. */
    private static boolean isOperandStart(Token token) {
        int type = token.getType();
        return type == DRL10Lexer.IDENTIFIER
                || type == DRL10Lexer.DRL_STRING_LITERAL
                || type == DRL10Lexer.STRING_LITERAL
                || type == DRL10Lexer.DECIMAL_LITERAL
                || type == DRL10Lexer.BOOL_LITERAL;
    }

    /** Tokens on the default channel; comments and whitespace are irrelevant here. */
    private static List<Token> defaultChannelTokens(String text) {
        List<Token> out = new ArrayList<>();
        try {
            DRL10Lexer lexer = new DRL10Lexer(CharStreams.fromString(text));
            lexer.removeErrorListeners();
            CommonTokenStream stream = new CommonTokenStream(lexer);
            stream.fill();
            for (Token token : stream.getTokens()) {
                if (token.getChannel() == Token.DEFAULT_CHANNEL && token.getType() != Token.EOF) {
                    out.add(token);
                }
            }
        } catch (RuntimeException e) {
            return List.of();
        }
        return out;
    }

    private static Range rangeOf(Token token) {
        int line = token.getLine() - 1;
        int start = token.getCharPositionInLine();
        int length = token.getText() == null ? 1 : token.getText().length();
        return new Range(new Position(line, start), new Position(line, start + Math.max(1, length)));
    }
}
