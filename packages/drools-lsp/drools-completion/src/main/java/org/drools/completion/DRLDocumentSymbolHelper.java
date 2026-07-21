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

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.drools.drl.parser.antlr4.DRL10Parser;
import org.eclipse.lsp4j.DocumentSymbol;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.SymbolKind;

/**
 * Produces the document outline ({@code textDocument/documentSymbol}) for a DRL
 * file: a flat list of top-level constructs, with {@code declare} types nesting
 * their fields (and {@code declare enum} types their constants).
 *
 * <ul>
 *   <li>{@code rule "X"} → {@link SymbolKind#Method}</li>
 *   <li>{@code query "X"} → {@link SymbolKind#Method}</li>
 *   <li>{@code function T f(...)} → {@link SymbolKind#Function}</li>
 *   <li>{@code global T name} → {@link SymbolKind#Variable}</li>
 *   <li>{@code declare X} → {@link SymbolKind#Class} (fields as {@link SymbolKind#Field})</li>
 *   <li>{@code declare enum X} → {@link SymbolKind#Enum} (constants as {@link SymbolKind#EnumMember})</li>
 * </ul>
 *
 * <p>Ranges come from the ANTLR parse tree, so they're exact regardless of
 * formatting. Parser errors are swallowed — a partial file still outlines its
 * well-formed constructs.
 */
public final class DRLDocumentSymbolHelper {

    private static final Logger logger = Logger.getLogger(DRLDocumentSymbolHelper.class.getName());

    private DRLDocumentSymbolHelper() {
    }

    /** Returns the outline symbols for {@code text}, or an empty list. */
    public static List<DocumentSymbol> symbols(String text) {
        List<DocumentSymbol> out = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return out;
        }
        try {
            DRL10Parser parser = DRLParsers.silent(text);
            DRL10Parser.CompilationUnitContext cu = parser.compilationUnit();
            if (cu == null) {
                return out;
            }
            for (DRL10Parser.DrlStatementdefContext stmt : cu.drlStatementdef()) {
                try {
                    DocumentSymbol sym = toSymbol(stmt);
                    if (sym != null) {
                        out.add(sym);
                    }
                } catch (Exception e) {
                    logger.fine(() -> "Skipping malformed statement: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            logger.fine(() -> "Failed to parse DRL for document symbols: " + e.getMessage());
        }
        return out;
    }

    private static DocumentSymbol toSymbol(DRL10Parser.DrlStatementdefContext stmt) {
        if (stmt.ruledef() != null) {
            DRL10Parser.RuledefContext r = stmt.ruledef();
            return symbol(displayName(r.name), SymbolKind.Method, r, r.name);
        }
        if (stmt.querydef() != null) {
            DRL10Parser.QuerydefContext q = stmt.querydef();
            return symbol(displayName(q.name), SymbolKind.Method, q, q.name);
        }
        if (stmt.functiondef() != null) {
            DRL10Parser.FunctiondefContext f = stmt.functiondef();
            DocumentSymbol s = symbol(text(f.drlIdentifier()), SymbolKind.Function, f, f.drlIdentifier());
            if (s != null && f.typeTypeOrVoid() != null) {
                s.setDetail(f.typeTypeOrVoid().getText());
            }
            return s;
        }
        if (stmt.globaldef() != null) {
            DRL10Parser.GlobaldefContext g = stmt.globaldef();
            DocumentSymbol s = symbol(text(g.drlIdentifier()), SymbolKind.Variable, g, g.drlIdentifier());
            if (s != null && g.type() != null) {
                s.setDetail(g.type().getText());
            }
            return s;
        }
        if (stmt.declaredef() != null) {
            return declareSymbol(stmt.declaredef());
        }
        return null;
    }

    private static DocumentSymbol declareSymbol(DRL10Parser.DeclaredefContext decl) {
        if (decl.typeDeclaration() != null) {
            DRL10Parser.TypeDeclarationContext t = decl.typeDeclaration();
            // The whole "declare ... end" block is the symbol range; the type name
            // is the selection range.
            DocumentSymbol s = symbol(text(t.name), SymbolKind.Class, decl, t.name);
            if (s == null) {
                return null;
            }
            List<DocumentSymbol> children = new ArrayList<>();
            for (DRL10Parser.FieldContext field : t.field()) {
                addIfPresent(children, fieldSymbol(field));
            }
            s.setChildren(children);
            return s;
        }
        if (decl.enumDeclaration() != null) {
            DRL10Parser.EnumDeclarationContext en = decl.enumDeclaration();
            DocumentSymbol s = symbol(text(en.name), SymbolKind.Enum, decl, en.name);
            if (s == null) {
                return null;
            }
            List<DocumentSymbol> children = new ArrayList<>();
            if (en.enumeratives() != null) {
                for (DRL10Parser.EnumerativeContext e : en.enumeratives().enumerative()) {
                    if (e.drlIdentifier() != null) {
                        addIfPresent(children,
                                symbol(text(e.drlIdentifier()), SymbolKind.EnumMember, e, e.drlIdentifier()));
                    }
                }
            }
            for (DRL10Parser.FieldContext field : en.field()) {
                addIfPresent(children, fieldSymbol(field));
            }
            s.setChildren(children);
            return s;
        }
        // entryPoint / window declarations are not class types — not outlined.
        return null;
    }

    private static DocumentSymbol fieldSymbol(DRL10Parser.FieldContext field) {
        if (field.label() == null) {
            return null;
        }
        // label().getText() returns "name:" — strip the trailing colon.
        String raw = field.label().getText();
        String name = raw.endsWith(":") ? raw.substring(0, raw.length() - 1).trim() : raw.trim();
        DocumentSymbol s = symbol(name, SymbolKind.Field, field, field.label());
        if (s != null && field.type() != null) {
            s.setDetail(field.type().getText());
        }
        return s;
    }

    /**
     * Builds a symbol from a name, a kind, the context spanning the whole
     * construct ({@code range}), and the context of the name token
     * ({@code selectionRange}). Returns {@code null} when the name is empty or
     * positions can't be derived.
     */
    private static DocumentSymbol symbol(String name, SymbolKind kind,
                                         ParserRuleContext rangeCtx, ParserRuleContext selectionCtx) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        Range range = rangeOf(rangeCtx);
        Range selection = rangeOf(selectionCtx);
        if (range == null) {
            return null;
        }
        // LSP requires selectionRange ⊆ range; fall back to the full range otherwise.
        if (selection == null || !contains(range, selection)) {
            selection = range;
        }
        return new DocumentSymbol(name, kind, range, selection);
    }

    private static void addIfPresent(List<DocumentSymbol> list, DocumentSymbol symbol) {
        if (symbol != null) {
            list.add(symbol);
        }
    }

    private static String text(ParserRuleContext ctx) {
        return ctx == null ? null : ctx.getText();
    }

    /** Strips surrounding double quotes from a {@code stringId} name for display. */
    private static String displayName(DRL10Parser.StringIdContext name) {
        if (name == null) {
            return null;
        }
        String t = name.getText();
        if (t != null && t.length() >= 2 && t.startsWith("\"") && t.endsWith("\"")) {
            return t.substring(1, t.length() - 1);
        }
        return t;
    }

    private static Range rangeOf(ParserRuleContext ctx) {
        if (ctx == null) {
            return null;
        }
        Token start = ctx.getStart();
        if (start == null) {
            return null;
        }
        Token stop = ctx.getStop() != null ? ctx.getStop() : start;
        int startLine = start.getLine() - 1;
        int startCol = start.getCharPositionInLine();
        int endLine = stop.getLine() - 1;
        int endCol = stop.getCharPositionInLine() + (stop.getText() == null ? 0 : stop.getText().length());
        return new Range(new Position(startLine, startCol), new Position(endLine, endCol));
    }

    private static boolean contains(Range outer, Range inner) {
        return !before(inner.getStart(), outer.getStart()) && !before(outer.getEnd(), inner.getEnd());
    }

    /** True when {@code a} is strictly before {@code b}. */
    private static boolean before(Position a, Position b) {
        return a.getLine() < b.getLine()
                || (a.getLine() == b.getLine() && a.getCharacter() < b.getCharacter());
    }
}
