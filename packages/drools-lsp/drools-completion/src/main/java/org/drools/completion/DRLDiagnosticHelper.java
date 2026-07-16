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
import java.util.Collections;
import java.util.List;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.drools.drl.parser.antlr4.DRL10Parser;
import org.drools.drl.parser.antlr4.DRLParserHelper;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

/**
 * Validates DRL text with the ANTLR parser and converts lexer and parser
 * errors into LSP {@link Diagnostic}s, so editors can surface syntax
 * problems as the user types.
 */
public final class DRLDiagnosticHelper {

    private DRLDiagnosticHelper() {
    }

    /** A single parse: the compilation unit and the syntax errors found building it. */
    public static final class Parsed {
        /** The parse tree, or {@code null} for blank input. */
        public final DRL10Parser.CompilationUnitContext compilationUnit;
        /** One diagnostic per syntax error, in document order. */
        public final List<Diagnostic> diagnostics;

        Parsed(DRL10Parser.CompilationUnitContext compilationUnit, List<Diagnostic> diagnostics) {
            this.compilationUnit = compilationUnit;
            this.diagnostics = diagnostics;
        }
    }

    /**
     * Parses {@code text} once, returning both the compilation unit and the
     * syntax diagnostics. Callers that also need the tree (e.g. the unknown-type
     * lint) can reuse it instead of re-parsing.
     */
    public static Parsed parse(String text) {
        if (text == null || text.isEmpty()) {
            return new Parsed(null, Collections.emptyList());
        }
        DRL10Parser parser = DRLParserHelper.createDrlParser(text);
        List<Diagnostic> diagnostics = new ArrayList<>();
        CollectingErrorListener listener = new CollectingErrorListener(diagnostics);

        // Safe today because DRLParserHelper builds an unfilled
        // CommonTokenStream directly over the lexer; lexer errors emitted
        // during lazy tokenization therefore reach the listener attached
        // below. If the helper ever pre-fills the stream, lexer errors would
        // be lost — attach the listener before tokenization in that case.
        Lexer lexer = (Lexer) parser.getTokenStream().getTokenSource();
        lexer.removeErrorListeners();
        lexer.addErrorListener(listener);

        parser.removeErrorListeners();
        parser.addErrorListener(listener);

        return new Parsed(parser.compilationUnit(), diagnostics);
    }

    /**
     * Parses {@code text} and returns one {@link Diagnostic} per syntax
     * error, in document order. Returns an empty list for {@code null},
     * empty, or syntactically clean input.
     */
    public static List<Diagnostic> validate(String text) {
        return parse(text).diagnostics;
    }

    private static class CollectingErrorListener extends BaseErrorListener {

        private final List<Diagnostic> diagnostics;

        CollectingErrorListener(List<Diagnostic> diagnostics) {
            this.diagnostics = diagnostics;
        }

        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                                int line, int charPositionInLine, String msg,
                                RecognitionException e) {
            int startCol = charPositionInLine;
            int endCol = charPositionInLine + 1;
            if (offendingSymbol instanceof Token) {
                Token token = (Token) offendingSymbol;
                if (token.getType() == Token.EOF) {
                    // EOF has no extent and its position is one past the last
                    // char; getText() is the 5-char placeholder "<EOF>".
                    // Anchor the range on the last real character (zero-width
                    // at column 0) so it never points past the text.
                    startCol = Math.max(0, charPositionInLine - 1);
                    endCol = charPositionInLine;
                } else {
                    // Widen the range to the offending token, but only when
                    // its text is usable: getText() may be null for detached
                    // tokens, and a token spanning lines would yield an end
                    // column computed on the wrong line — those fall back to
                    // the 1-char caret.
                    String tokenText = token.getText();
                    if (tokenText != null && !tokenText.isEmpty()
                            && tokenText.indexOf('\n') < 0 && tokenText.indexOf('\r') < 0) {
                        endCol = charPositionInLine + tokenText.length();
                    }
                }
            }
            Diagnostic d = new Diagnostic();
            d.setRange(new Range(new Position(line - 1, startCol),
                                 new Position(line - 1, endCol)));
            d.setSeverity(DiagnosticSeverity.Error);
            d.setSource("drools-parser");
            d.setMessage(msg);
            diagnostics.add(d);
        }
    }
}
