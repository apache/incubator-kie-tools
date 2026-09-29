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
import org.antlr.v4.runtime.Token;
import org.drools.drl.parser.antlr4.DRL10Parser;

/** Formats declare blocks: types, traits, enums, entry points and windows. */
final class DeclareFormatter {
  private final Emitter e;
  private final LhsFormatter lhs;

  DeclareFormatter(Emitter e, LhsFormatter lhs) {
    this.e = e;
    this.lhs = lhs;
  }

  void visitDeclare(DRL10Parser.DeclaredefContext ctx) {
    e.emitHiddenTokensBefore(ctx);
    if (ctx.typeDeclaration() != null) {
      visitTypeDeclaration(ctx.typeDeclaration());
    } else if (ctx.enumDeclaration() != null) {
      visitEnumDeclaration(ctx.enumDeclaration());
    } else if (ctx.entryPointDeclaration() != null) {
      visitEntryPointDeclaration(ctx.entryPointDeclaration());
    } else if (ctx.windowDeclaration() != null) {
      visitWindowDeclaration(ctx.windowDeclaration());
    } else {
      // bare "declare" – shouldn't happen, but be safe
      e.emit(Emitter.singleLine(e.sourceSlice(ctx)));
      e.newline();
    }
  }

  private void visitTypeDeclaration(DRL10Parser.TypeDeclarationContext ctx) {
    StringBuilder header = new StringBuilder("declare ");
    if (ctx.DRL_TRAIT() != null) {
      header.append("trait ");
    }
    if (ctx.DRL_TYPE() != null) {
      header.append("type ");
    }
    header.append(ctx.name.getText());
    if (ctx.EXTENDS() != null && !ctx.superTypes.isEmpty()) {
      header.append(" extends ");
      for (int i = 0; i < ctx.superTypes.size(); i++) {
        if (i > 0) header.append(", ");
        header.append(ctx.superTypes.get(i).getText());
      }
    }
    e.emitHeader(header.toString(), ctx.drlAnnotation());
    int headerEnd = ctx.drlAnnotation().isEmpty()
        ? (ctx.superTypes.isEmpty()
            ? ctx.name.getStop().getTokenIndex()
            : ctx.superTypes.get(ctx.superTypes.size() - 1).getStop().getTokenIndex())
        : ctx.drlAnnotation(ctx.drlAnnotation().size() - 1).getStop().getTokenIndex();
    emitFieldBlock(ctx.field(), false, headerEnd, ctx.getStop().getTokenIndex(), -1);
    e.depth--;
    e.emit("end");
    e.newline();
  }

  private void visitEnumDeclaration(DRL10Parser.EnumDeclarationContext ctx) {
    e.emitHeader("declare enum " + ctx.name.getText(), ctx.drlAnnotation());
    int headerEnd = ctx.drlAnnotation().isEmpty()
        ? ctx.name.getStop().getTokenIndex()
        : ctx.drlAnnotation(ctx.drlAnnotation().size() - 1).getStop().getTokenIndex();

    // The constant list is a table: the last one carries the mandatory ";" that
    // closes it (enumeratives SEMI in the grammar), the rest a ",".
    List<DRL10Parser.EnumerativeContext> constants = ctx.enumeratives().enumerative();
    BlockBuilder block = new BlockBuilder(headerEnd);
    for (int i = 0; i < constants.size(); i++) {
      DRL10Parser.EnumerativeContext constant = constants.get(i);
      block.addRow(constant, constantCells(constant, i == constants.size() - 1 ? ";" : ","));
    }
    int afterConstants = ctx.enumeratives().getStop().getTokenIndex();
    block.consumeCommentsUpTo(fieldsOrEndTokenIndex(ctx));
    block.flush();
    e.lastEmittedTokenIndex = Math.max(e.lastEmittedTokenIndex, afterConstants);

    emitFieldBlock(ctx.field(), true, afterConstants, ctx.getStop().getTokenIndex(),
        block.lastLine());
    e.depth--;
    e.emit("end");
    e.newline();
  }

  /** Where the constant list's trailing comments stop: the first field, else {@code end}. */
  private int fieldsOrEndTokenIndex(DRL10Parser.EnumDeclarationContext ctx) {
    return ctx.field().isEmpty()
        ? ctx.getStop().getTokenIndex()
        : ctx.field(0).getStart().getTokenIndex();
  }

  /**
   * The cells of one enum constant: the name with its opening paren, then one
   * cell per argument, then the closing paren with the row's separator. A
   * constant declared without arguments is a single cell — it has no columns to
   * line up with, so it is left alone rather than padded out to the arg'd rows.
   */
  private List<String> constantCells(DRL10Parser.EnumerativeContext ctx, String separator) {
    List<DRL10Parser.ExpressionContext> args = ctx.expression();
    if (args.isEmpty()) {
      return List.of(ctx.drlIdentifier().getText() + separator);
    }
    List<String> cells = new ArrayList<>(args.size() + 2);
    cells.add(ctx.drlIdentifier().getText() + "(");
    for (int i = 0; i < args.size(); i++) {
      cells.add(e.styledText(args.get(i)) + (i < args.size() - 1 ? "," : ""));
    }
    cells.add(")" + separator);
    return cells;
  }

  /**
   * Emits a run of field declarations as an aligned two-column block: the label
   * with its colon, then the type and whatever follows it.
   */
  private void emitFieldBlock(List<DRL10Parser.FieldContext> fields, boolean semiColon,
                              int scanFrom, int boundary, int seedLine) {
    if (fields.isEmpty()) {
      return;
    }
    BlockBuilder block = new BlockBuilder(scanFrom, seedLine);
    for (DRL10Parser.FieldContext field : fields) {
      block.addRow(field, fieldCells(field, semiColon));
    }
    block.consumeCommentsUpTo(boundary);
    block.flush();
  }

  private List<String> fieldCells(DRL10Parser.FieldContext ctx, boolean semiColon) {
    StringBuilder rest = new StringBuilder(ctx.type().getText());
    if (ctx.ASSIGN() != null && ctx.initExpr != null) {
      rest.append(" = ").append(ctx.initExpr.getText());
    }
    for (DRL10Parser.DrlAnnotationContext ann : ctx.drlAnnotation()) {
      rest.append(" ").append(e.styledText(ann));
    }
    if (semiColon) {
      rest.append(";");
    }
    return List.of(ctx.label().drlIdentifier().getText() + fieldColon(), rest.toString());
  }

  private String fieldColon() {
    return e.options.bindingColonSpace() ? " :" : ":";
  }

  /**
   * One line of a declare block: an aligned row of cells, or a verbatim line.
   *
   * <p>A row's trailing comment is held apart from its cells rather than being one
   * of them. As a cell it would set the width of whichever column it landed in,
   * and a long comment on a short row would then push every other row's data out
   * to meet it. Held apart, comments are measured only against each other.
   */
  private static final class BlockLine {
    private final List<String> cells;     // null for a verbatim line
    private final String verbatim;        // null for a row
    private String comment;               // trailing comment, or null

    private BlockLine(List<String> cells, String verbatim) {
      this.cells = cells;
      this.verbatim = verbatim;
    }

    static BlockLine row(List<String> cells) {
      return new BlockLine(new ArrayList<>(cells), null);
    }

    static BlockLine verbatim(String text) {
      return new BlockLine(null, text);
    }

    boolean isRow() {
      return cells != null;
    }
  }

  /**
   * Accumulates the rows of a {@code declare} block and emits them in aligned
   * columns.
   *
   * <p>A block runs between blank source lines: a gap of two or more lines ends
   * the current block, so each group measures its own columns and a short group
   * is not stretched to fit a long one elsewhere in the same declaration.
   *
   * <p>Comments are placed but never measured. One sharing a row's last line
   * becomes that row's final cell, so it stays with what it annotates and lines up
   * with its neighbours; one on its own line is emitted verbatim at the column the
   * author put it in, and — this is the point — does NOT end the block, so writing
   * a note into a constant list cannot silently re-align the constants around it.
   */
  private final class BlockBuilder {
    private final List<BlockLine> lines = new ArrayList<>();
    private int scanFrom;
    private int prevLine = -1;

    /**
     * Starts no earlier than the last token already emitted. A declaration holds
     * two blocks — its constants and its fields — reading one token stream, and
     * the second must not re-scan the gap the first consumed, or the comment in
     * that gap is placed twice.
     */
    BlockBuilder(int scanFrom) {
      this(scanFrom, -1);
    }

    /**
     * {@code seedLine} is the source line the previous block ended on, so a blank
     * line between two consecutive blocks — a constant list and the fields under
     * it — is recognised as the author's and kept.
     */
    BlockBuilder(int scanFrom, int seedLine) {
      this.scanFrom = Math.max(scanFrom, e.lastEmittedTokenIndex);
      this.prevLine = seedLine;
    }

    /** The source line this block ended on, for seeding the block that follows. */
    int lastLine() {
      return prevLine;
    }

    /** Adds {@code item}'s row, first placing any comments that precede it. */
    void addRow(ParserRuleContext item, List<String> cells) {
      consumeCommentsUpTo(item.getStart().getTokenIndex());
      if (startsNewBlock(item.getStart().getLine())) {
        breakBlock();
      }
      lines.add(BlockLine.row(cells));
      prevLine = item.getStop().getLine();
      scanFrom = item.getStop().getTokenIndex();
      consumed(scanFrom);
    }

    /** Places the comments between the cursor and {@code boundary} (exclusive). */
    void consumeCommentsUpTo(int boundary) {
      for (int i = scanFrom + 1; i < boundary && i < e.tokens.size(); i++) {
        Token token = e.tokens.get(i);
        if (!Emitter.isComment(token)) {
          continue;
        }
        String text = Emitter.stripTrailingWhitespace(token.getText());
        BlockLine last = lines.isEmpty() ? null : lines.get(lines.size() - 1);
        if (token.getLine() == prevLine && last != null && last.isRow()
            && last.comment == null) {
          last.comment = text;
        } else {
          if (startsNewBlock(token.getLine())) {
            breakBlock();
          }
          lines.add(BlockLine.verbatim(" ".repeat(token.getCharPositionInLine()) + text));
        }
        prevLine = token.getLine() + Emitter.countNewlines(text);
        scanFrom = i;
        consumed(i);
      }
    }

    /**
     * Marks a token as emitted by this block. Without it the generic hidden-token
     * machinery would emit the block's comments a second time, after {@code end}.
     */
    private void consumed(int tokenIndex) {
      e.lastEmittedTokenIndex = Math.max(e.lastEmittedTokenIndex, tokenIndex);
    }

    /** Emits what has accumulated, aligned, and starts over. */
    void flush() {
      List<List<String>> rows = new ArrayList<>();
      for (BlockLine line : lines) {
        if (line.isRow()) {
          rows.add(line.cells);
        }
      }
      List<String> aligned = e.options.alignDeclarations()
          ? ColumnAligner.align(rows)
          : rows.stream().map(r -> String.join(" ", r)).toList();

      // The comment column opens one past the longest row that carries a comment,
      // so the notes line up with each other without being dragged out by a long
      // uncommented row elsewhere in the block.
      int commentColumn = 0;
      int row = 0;
      for (BlockLine line : lines) {
        if (line.isRow() && line.comment != null) {
          commentColumn = Math.max(commentColumn, aligned.get(row).length() + 1);
        }
        if (line.isRow()) {
          row++;
        }
      }

      row = 0;
      for (BlockLine line : lines) {
        if (!line.isRow()) {
          e.emit(line.verbatim);
          e.newline();
          continue;
        }
        StringBuilder text = new StringBuilder(aligned.get(row++));
        if (line.comment != null) {
          while (text.length() < commentColumn) {
            text.append(' ');
          }
          text.append(line.comment);
        }
        e.emit(e.indent() + text);
        e.newline();
      }
      lines.clear();
    }

    private boolean startsNewBlock(int line) {
      return prevLine >= 0 && line - prevLine >= 2;
    }

    private void breakBlock() {
      flush();
      e.blankLine();
    }
  }

  private void visitEntryPointDeclaration(DRL10Parser.EntryPointDeclarationContext ctx) {
    e.emitHeader("declare entry-point " + ctx.name.getText(), ctx.drlAnnotation());
    e.depth--;
    e.emit("end");
    e.newline();
  }

  private void visitWindowDeclaration(DRL10Parser.WindowDeclarationContext ctx) {
    e.emitHeader("declare window " + ctx.name.getText(), ctx.drlAnnotation());
    e.emitHiddenTokensBefore(ctx.lhsPatternBind());
    lhs.visitPatternBind(ctx.lhsPatternBind());
    e.depth--;
    e.emit("end");
    e.newline();
  }
}
