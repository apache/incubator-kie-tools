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
import java.util.List;
import java.util.logging.Logger;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.drools.drl.parser.antlr4.DRL10Lexer;
import org.drools.drl.parser.antlr4.DRL10Parser;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

/**
 * Finds the ranges where a symbol is used within a single DRL document — the
 * shared core of find-references and rename. Two complementary passes:
 *
 * <ul>
 *   <li><b>Structured</b> ({@link #typeOccurrences}) walks the ANTLR parse tree
 *       and collects the matching type-name token from each grammar position
 *       that bears a type: pattern object types, {@code declare}/{@code enum}
 *       names (the declaration site) and their {@code extends} parents, field
 *       types, global types, function return/parameter types, and import names.
 *       Because it reads named grammar contexts, it never matches strings,
 *       comments, field labels, or binding variables.</li>
 *   <li><b>Consequence</b> the RHS {@code then ... end} block is lexed into
 *       opaque chunk tokens, so the parse tree can't see type uses there. The
 *       consequence pass scans {@code RHS_CHUNK} tokens for whole-word matches
 *       and skips {@code RHS_STRING_LITERAL} / RHS comment tokens.</li>
 * </ul>
 *
 * <p>{@link #bindingOccurrences} is the bound-variable counterpart: it scopes
 * to the rule enclosing the caret and finds every use of a {@code $binding}
 * there (LHS tokens and RHS chunks), so rename stays within one rule.
 */
final class DRLReferenceScanner {

    private static final Logger logger = Logger.getLogger(DRLReferenceScanner.class.getName());

    private DRLReferenceScanner() {
    }

    /** A type-name use: its range, and whether it is the declaration site itself. */
    static final class Occurrence {
        final Range range;
        final boolean declaration;

        Occurrence(Range range, boolean declaration) {
            this.range = range;
            this.declaration = declaration;
        }
    }

    /**
     * All uses of the simple type name {@code simpleName} in {@code text}:
     * structured type positions (with the declaration site flagged) plus
     * whole-word matches inside RHS consequence chunks. Best-effort — any parse
     * failure yields an empty list.
     */
    static List<Occurrence> typeOccurrences(String text, String simpleName) {
        if (text == null || simpleName == null || simpleName.isEmpty()) {
            return new ArrayList<>();
        }
        return typeOccurrences(ParsedDrl.of(text), simpleName);
    }

    /** As {@link #typeOccurrences(String, String)}, reusing an existing parse of the document. */
    static List<Occurrence> typeOccurrences(ParsedDrl parsed, String simpleName) {
        List<Occurrence> out = new ArrayList<>();
        if (parsed == null || simpleName == null || simpleName.isEmpty()) {
            return out;
        }
        try {
            collectStructured(parsed.compilationUnit, simpleName, out);
            collectConsequence(parsed.tokens(), simpleName, out);
        } catch (Exception e) {
            logger.fine(() -> "typeOccurrences failed for " + simpleName + ": " + e.getMessage());
        }
        return out;
    }

    /**
     * Every use of {@code bindingName} (the {@code $}-prefixed variable) within
     * the rule (or query) enclosing {@code caret} — both LHS tokens and RHS
     * consequence chunks. Returns an empty list when the caret is not inside a
     * rule. Scoping to the enclosing rule is what keeps a binding rename from
     * touching an identically-named binding in another rule.
     */
    static List<Range> bindingOccurrences(String text, Position caret, String bindingName) {
        if (text == null || caret == null || bindingName == null || bindingName.length() < 2) {
            return new ArrayList<>();
        }
        return bindingOccurrences(ParsedDrl.of(text), caret, bindingName);
    }

    /** As {@link #bindingOccurrences(String, Position, String)}, reusing an existing parse. */
    static List<Range> bindingOccurrences(ParsedDrl parsed, Position caret, String bindingName) {
        List<Range> out = new ArrayList<>();
        if (parsed == null || caret == null || bindingName == null || bindingName.length() < 2) {
            return out;
        }
        try {
            Integer tokenIndex = parsed.tokenIndexAt(caret);
            if (tokenIndex == null) {
                return out;
            }
            ParserRuleContext rule = enclosingRule(parsed.compilationUnit, tokenIndex);
            if (rule == null || rule.getStart() == null || rule.getStop() == null) {
                return out;
            }
            List<Token> all = parsed.tokens().getTokens();
            int from = rule.getStart().getTokenIndex();
            int to = rule.getStop().getTokenIndex();
            for (int i = from; i <= to && i < all.size(); i++) {
                Token t = all.get(i);
                if (t.getType() == DRL10Lexer.RHS_CHUNK) {
                    // In the consequence, "$p" sits inside a chunk token.
                    scanChunk(t, bindingName, out);
                } else if (bindingName.equals(t.getText())) {
                    // In the LHS, "$p" is a single identifier token (Java letters
                    // include '$'); string/comment tokens carry their delimiters
                    // so they never collide with "$name".
                    out.add(rangeAt(t.getLine(), t.getCharPositionInLine(), bindingName.length()));
                }
            }
        } catch (Exception e) {
            logger.fine(() -> "bindingOccurrences failed for " + bindingName + ": " + e.getMessage());
        }
        return out;
    }

    // --- structured pass -------------------------------------------------

    private static void collectStructured(ParseTree node, String simpleName, List<Occurrence> out) {
        if (node == null) {
            return;
        }
        if (node instanceof DRL10Parser.LhsPatternContext p) {
            if (p.objectType != null) {
                collectMatches(p.objectType, simpleName, false, out);
            }
        } else if (node instanceof DRL10Parser.TypeDeclarationContext td) {
            if (td.name != null) {
                collectMatches(td.name, simpleName, true, out);
            }
            for (DRL10Parser.DrlQualifiedNameContext st : td.superTypes) {
                collectMatches(st, simpleName, false, out);
            }
        } else if (node instanceof DRL10Parser.EnumDeclarationContext ed) {
            if (ed.name != null) {
                collectMatches(ed.name, simpleName, true, out);
            }
        } else if (node instanceof DRL10Parser.FieldContext f) {
            if (f.type() != null) {
                collectMatches(f.type(), simpleName, false, out);
            }
        } else if (node instanceof DRL10Parser.GlobaldefContext g) {
            if (g.type() != null) {
                collectMatches(g.type(), simpleName, false, out);
            }
        } else if (node instanceof DRL10Parser.FunctiondefContext fn) {
            if (fn.typeTypeOrVoid() != null) {
                collectMatches(fn.typeTypeOrVoid(), simpleName, false, out);
            }
            if (fn.formalParameters() != null) {
                collectMatches(fn.formalParameters(), simpleName, false, out);
            }
        } else if (node instanceof DRL10Parser.ImportdefContext imp) {
            collectMatches(imp, simpleName, false, out);
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            collectStructured(node.getChild(i), simpleName, out);
        }
    }

    /** Adds an occurrence for every terminal under {@code node} whose text is {@code simpleName}. */
    private static void collectMatches(ParseTree node, String simpleName, boolean declaration,
                                       List<Occurrence> out) {
        if (node instanceof TerminalNode tn) {
            Token t = tn.getSymbol();
            if (simpleName.equals(t.getText())) {
                out.add(new Occurrence(
                        rangeAt(t.getLine(), t.getCharPositionInLine(), simpleName.length()), declaration));
            }
            return;
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            collectMatches(node.getChild(i), simpleName, declaration, out);
        }
    }

    // --- consequence (RHS) pass -----------------------------------------

    private static void collectConsequence(CommonTokenStream tokens, String simpleName, List<Occurrence> out) {
        for (Token t : tokens.getTokens()) {
            if (t.getType() == DRL10Lexer.RHS_CHUNK) {
                List<Range> ranges = new ArrayList<>();
                scanChunk(t, simpleName, ranges);
                for (Range r : ranges) {
                    out.add(new Occurrence(r, false));
                }
            }
        }
    }

    /**
     * Finds whole-word occurrences of {@code word} inside a single-line RHS
     * chunk token, appending a range for each. {@code RHS_CHUNK} never spans a
     * newline, so all matches share the token's line.
     */
    private static void scanChunk(Token chunk, String word, List<Range> out) {
        String text = chunk.getText();
        int line = chunk.getLine();
        int baseCol = chunk.getCharPositionInLine();
        int from = 0;
        int hit;
        while ((hit = text.indexOf(word, from)) >= 0) {
            int end = hit + word.length();
            boolean leftOk = hit == 0 || !isIdentifierChar(text.charAt(hit - 1));
            boolean rightOk = end >= text.length() || !isIdentifierChar(text.charAt(end));
            if (leftOk && rightOk) {
                out.add(rangeAt(line, baseCol + hit, word.length()));
            }
            from = hit + 1;
        }
    }

    // --- enclosing-rule lookup ------------------------------------------

    /** Deepest {@code rule}/{@code query} context whose token span contains {@code tokenIndex}. */
    private static ParserRuleContext enclosingRule(ParseTree node, int tokenIndex) {
        if (!(node instanceof ParserRuleContext ctx) || !spans(ctx, tokenIndex)) {
            return null;
        }
        ParserRuleContext best =
                (ctx instanceof DRL10Parser.RuledefContext || ctx instanceof DRL10Parser.QuerydefContext)
                        ? ctx : null;
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParserRuleContext deeper = enclosingRule(ctx.getChild(i), tokenIndex);
            if (deeper != null) {
                best = deeper;
            }
        }
        return best;
    }

    private static boolean spans(ParserRuleContext ctx, int tokenIndex) {
        return ctx.getStart() != null && ctx.getStop() != null
                && ctx.getStart().getTokenIndex() <= tokenIndex
                && tokenIndex <= ctx.getStop().getTokenIndex();
    }

    // --- helpers ---------------------------------------------------------

    /** Builds a single-line range from a 1-based ANTLR line and 0-based column. */
    private static Range rangeAt(int antlrLine, int col, int length) {
        int line = antlrLine - 1;
        return new Range(new Position(line, col), new Position(line, col + length));
    }

    private static boolean isIdentifierChar(char c) {
        return Character.isLetterOrDigit(c) || c == '_' || c == '$';
    }
}
