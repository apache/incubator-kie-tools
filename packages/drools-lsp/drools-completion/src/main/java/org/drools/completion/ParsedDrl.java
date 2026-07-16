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

import java.util.List;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.drools.drl.parser.antlr4.DRL10Parser;
import org.drools.drl.parser.antlr4.DRLParserHelper;
import org.eclipse.lsp4j.Position;

/**
 * A single parse of one DRL document, shared across the editor features that
 * operate on it within a request — go-to-definition, find-references, rename,
 * hover — so the current document is parsed once rather than re-parsed by each
 * helper.
 *
 * <p>Holds the parser (its token stream filled by {@code compilationUnit()}),
 * the compilation unit, and the {@code declare}d types (extracted lazily, once).
 * Parse errors are silenced (see {@link DRLParsers#silent}), so a malformed
 * document yields a best-effort, possibly partial compilation unit and an empty
 * or partial declared-type list rather than throwing.
 *
 * <p>Not thread-safe: build one per request and use it on a single thread. The
 * parse tree and token stream are read-only once built, so sharing the instance
 * among the helpers within that request is safe.
 */
final class ParsedDrl {

    final String text;
    private final DRL10Parser parser;
    final DRL10Parser.CompilationUnitContext compilationUnit;
    private List<DeclaredType> declaredTypes;

    private ParsedDrl(String text, DRL10Parser parser,
                      DRL10Parser.CompilationUnitContext compilationUnit) {
        this.text = text;
        this.parser = parser;
        this.compilationUnit = compilationUnit;
    }

    /** Parses {@code text} once with errors silenced; never returns {@code null}. */
    static ParsedDrl of(String text) {
        DRL10Parser parser = DRLParsers.silent(text);
        return new ParsedDrl(text, parser, parser.compilationUnit());
    }

    /** The {@code declare}d types in this document, extracted once and cached. */
    List<DeclaredType> declaredTypes() {
        if (declaredTypes == null) {
            try {
                declaredTypes = DRLDeclaredTypeParser.extractFromCompilationUnit(compilationUnit);
            } catch (Exception e) {
                declaredTypes = List.of();
            }
        }
        return declaredTypes;
    }

    /** This document's filled token stream (all channels), for whole-document scans. */
    CommonTokenStream tokens() {
        return (CommonTokenStream) parser.getTokenStream();
    }

    /**
     * Index into {@link #tokens()} of the token the caret sits in (or just
     * after), or {@code null} when {@code position} resolves to no token.
     */
    Integer tokenIndexAt(Position position) {
        return DRLParserHelper.computeTokenIndex(
                parser, position.getLine() + 1, position.getCharacter());
    }

    /**
     * The token under {@code position}, or {@code null} when the caret resolves
     * to no token. Reuses this document's already-built token stream — no re-lex.
     */
    Token tokenAt(Position position) {
        Integer index = tokenIndexAt(position);
        CommonTokenStream tokens = tokens();
        if (index == null || index < 0 || index >= tokens.size()) {
            return null;
        }
        return tokens.get(index);
    }
}
