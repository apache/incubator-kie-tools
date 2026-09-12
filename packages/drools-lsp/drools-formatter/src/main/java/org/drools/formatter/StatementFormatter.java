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

package org.drools.formatter;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import org.drools.drl.parser.antlr4.DRL10Parser;

/**
 * Walks the compilation unit and formats top-level statements; conditions,
 * consequences and declarations are delegated to their own formatters.
 */
final class StatementFormatter {
  final Emitter e;

  private final LhsFormatter lhs;
  private final DeclareFormatter declares;
  private final RhsFormatter rhs;

  // The tree the emission was built from, kept so the structural gate can
  // interrogate that derivation rather than a second, possibly different one.
  DRL10Parser.CompilationUnitContext compilationUnit;

  StatementFormatter(Emitter e) {
    this.e = e;
    this.lhs = new LhsFormatter(e);
    this.declares = new DeclareFormatter(e, lhs);
    this.rhs = new RhsFormatter(e);
  }

  // ── tree walking ──────────────────────────────────────────────────────

  void formatCompilationUnit() {
    // Fill the token stream so hidden tokens are accessible
    e.tokens.fill();
    e.computeSkipRegions();

    DRL10Parser.CompilationUnitContext cu = e.parser.compilationUnit();
    compilationUnit = cu;

    if (cu.packagedef() != null) {
      e.visitTopLevel(cu.packagedef(), () -> visitPackage(cu.packagedef()));
    }
    if (cu.unitdef() != null) {
      e.visitTopLevel(cu.unitdef(), () -> visitUnit(cu.unitdef()));
    }

    for (DRL10Parser.DrlStatementdefContext stmt : cu.drlStatementdef()) {
      e.visitTopLevel(stmt, () -> visitStatement(stmt));
    }

    // Emit any trailing comments/hidden tokens after last statement
    e.emitTrailingHiddenTokens();
  }

  private void visitPackage(DRL10Parser.PackagedefContext ctx) {
    e.emitHiddenTokensBefore(ctx);
    e.emit("package " + ctx.name.getText() + ";");
    e.newline();
  }

  private void visitUnit(DRL10Parser.UnitdefContext ctx) {
    e.emitHiddenTokensBefore(ctx);
    e.emit("unit " + ctx.name.getText() + ";");
    e.newline();
  }

  private void visitStatement(DRL10Parser.DrlStatementdefContext ctx) {
    if (ctx.importdef() != null) {
      visitImport(ctx.importdef(), ctx.SEMI() != null);
    } else if (ctx.globaldef() != null) {
      visitGlobal(ctx.globaldef(), ctx.SEMI() != null);
    } else if (ctx.declaredef() != null) {
      declares.visitDeclare(ctx.declaredef());
    } else if (ctx.ruledef() != null) {
      visitRule(ctx.ruledef());
    } else if (ctx.querydef() != null) {
      visitQuery(ctx.querydef());
    } else if (ctx.functiondef() != null) {
      visitFunction(ctx.functiondef());
    } else if (ctx.attributes() != null) {
      visitAttributes(ctx.attributes());
    }
  }

  // ── imports / globals ─────────────────────────────────────────────────

  private void visitImport(DRL10Parser.ImportdefContext ctx, boolean sourceSemi) {
    e.emitHiddenTokensBefore(ctx);
    e.emit(e.styledText(ctx));
    if (e.options.normalizeTerminators() || sourceSemi) {
      e.emit(";");
    }
    e.newline();
  }

  private void visitGlobal(DRL10Parser.GlobaldefContext ctx, boolean sourceSemi) {
    e.emitHiddenTokensBefore(ctx);
    e.emit("global " + ctx.type().getText() + " " + ctx.drlIdentifier().getText());
    if (!e.options.normalizeTerminators() && sourceSemi) {
      e.emit(";");
    }
    e.newline();
  }

  // ── rule ──────────────────────────────────────────────────────────────

  private void visitRule(DRL10Parser.RuledefContext ctx) {
    e.emitHiddenTokensBefore(ctx);

    // If the parser failed to produce a meaningful rule body (no attributes,
    // no LHS, no RHS), fall back to emitting the original text of the entire
    // rule, preserving the source formatting. This handles DRL files that use
    // syntax the parser doesn't fully support (e.g. agenda-group).
    boolean hasAnnotations = !ctx.drlAnnotation().isEmpty();
    boolean hasBody = ctx.attributes() != null || ctx.lhs() != null || ctx.rhs() != null;
    if (!hasBody && !hasAnnotations) {
      emitOriginalRule(ctx);
      return;
    }

    // rule header
    StringBuilder header = new StringBuilder("rule ");
    header.append(ruleNameText(ctx.name));
    String headerLine;
    if (ctx.EXTENDS() != null && ctx.parentName != null) {
      String extendsClause = " extends " + ruleNameText(ctx.parentName);
      if (header.length() + extendsClause.length() > e.options.lineLength()) {
        e.emit(header.toString());
        e.newline();
        headerLine = "extends " + ruleNameText(ctx.parentName);
      } else {
        header.append(extendsClause);
        headerLine = header.toString();
      }
    } else {
      headerLine = header.toString();
    }

    List<ParserRuleContext> items = new ArrayList<>(ctx.drlAnnotation());
    if (ctx.attributes() != null) {
      items.addAll(ctx.attributes().attribute());
    }
    e.emitHeader(headerLine, items);

    // when
    if (ctx.lhs() != null) {
      lhs.visitLhs(ctx.lhs());
    }

    // then
    if (ctx.rhs() != null) {
      rhs.visitRhs(ctx.rhs());
    }

    e.depth--;
    e.emit("end");
    e.newline();
  }

  /**
   * Fallback: emit the original source text of a rule when the parser
   * could not produce a complete CST (e.g. due to unrecognized attributes).
   * Reconstructs the text from ALL tokens (including hidden channel) to
   * preserve original formatting.
   */
  private void emitOriginalRule(DRL10Parser.RuledefContext ctx) {
    e.emitVerbatim(ctx.getStart().getTokenIndex(), ctx.getStop().getTokenIndex());
  }

  private String ruleNameText(DRL10Parser.StringIdContext nameCtx) {
    if (nameCtx == null) return "";
    return Emitter.singleLine(e.sourceSlice(nameCtx));
  }

  /**
   * A parenthesised, padded inline group — {@code ( a, b )} — or {@code ()} when
   * there is nothing to pad around. Signatures and parameter lists are built as
   * strings rather than emitted token by token, so they need the house paren
   * rule applied here rather than by {@code needsSpaceBetween}.
   */
  private String parenthesized(String inner) {
    String trimmed = inner.trim();
    return trimmed.isEmpty() ? "()" : e.options.parenPadding() ? "( " + trimmed + " )" : "(" + trimmed + ")";
  }

  // ── query ─────────────────────────────────────────────────────────────

  private void visitQuery(DRL10Parser.QuerydefContext ctx) {
    e.emitHiddenTokensBefore(ctx);

    StringBuilder header = new StringBuilder("query ");
    header.append(ruleNameText(ctx.name));
    if (ctx.parameters() != null) {
      header.append(parenthesized(e.getInnerText(ctx.parameters())));
    }
    e.emitHeader(header.toString(), ctx.drlAnnotation());

    // query LHS (no "when" keyword)
    if (ctx.queryLhs() != null) {
      for (DRL10Parser.LhsExpressionContext expr : ctx.queryLhs().lhsExpression()) {
        lhs.visitLhsExpression(expr);
      }
    }

    e.depth--;
    e.emit("end");
    e.newline();
  }

  // ── function ──────────────────────────────────────────────────────────

  private void visitFunction(DRL10Parser.FunctiondefContext ctx) {
    e.emitHiddenTokensBefore(ctx);
    StringBuilder sig = new StringBuilder("function ");
    if (ctx.typeTypeOrVoid() != null) {
      sig.append(ctx.typeTypeOrVoid().getText()).append(" ");
    }
    sig.append(ctx.drlIdentifier().getText());
    sig.append(parenthesized(e.getInnerText(ctx.formalParameters())));
    e.emit(sig.toString());
    e.newline();
    // Emit the body block verbatim, preserving original source formatting.
    e.emitVerbatim(ctx.drlBlock().getStart().getTokenIndex(),
        ctx.drlBlock().getStop().getTokenIndex());
  }

  // ── attributes ────────────────────────────────────────────────────────

  private void visitAttributes(DRL10Parser.AttributesContext ctx) {
    for (DRL10Parser.AttributeContext attr : ctx.attribute()) {
      e.emitHiddenTokensBefore(attr);
      e.emit(e.styledText(attr));
      e.newline();
    }
  }
}
