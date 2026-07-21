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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.lsp4j.DocumentSymbol;
import org.eclipse.lsp4j.FoldingRange;
import org.eclipse.lsp4j.FoldingRangeKind;

/**
 * Produces folding ranges ({@code textDocument/foldingRange}) for a DRL file:
 *
 * <ul>
 *   <li>each multi-line top-level construct (rule / declare / function / query)
 *       folds as a {@link FoldingRangeKind#Region}, reusing the spans computed
 *       by {@link DRLDocumentSymbolHelper};</li>
 *   <li>each multi-line {@code /* ... *}{@code /} block comment folds as a
 *       {@link FoldingRangeKind#Comment}.</li>
 * </ul>
 *
 * <p>Single-line constructs (e.g. a one-line {@code global}) produce no fold.
 * Whole-construct folds only — a rule's {@code when}/{@code then} are not split
 * into separate sub-folds.
 */
public final class DRLFoldingRangeHelper {

    /** A {@code /* ... *}{@code /} block comment (DOTALL, lazy body). */
    private static final Pattern BLOCK_COMMENT = Pattern.compile("(?s)/\\*.*?\\*/");

    private DRLFoldingRangeHelper() {
    }

    /** Returns the folding ranges for {@code text}, or an empty list. */
    public static List<FoldingRange> foldingRanges(String text) {
        List<FoldingRange> out = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return out;
        }

        // 1. Top-level constructs — reuse the document-symbol spans.
        for (DocumentSymbol symbol : DRLDocumentSymbolHelper.symbols(text)) {
            int start = symbol.getRange().getStart().getLine();
            int end = symbol.getRange().getEnd().getLine();
            if (end > start) {
                out.add(folding(start, end, FoldingRangeKind.Region));
            }
        }

        // 2. Multi-line block comments.
        Matcher m = BLOCK_COMMENT.matcher(text);
        while (m.find()) {
            int startLine = lineAt(text, m.start());
            int endLine = lineAt(text, Math.max(m.start(), m.end() - 1));
            if (endLine > startLine) {
                out.add(folding(startLine, endLine, FoldingRangeKind.Comment));
            }
        }

        return out;
    }

    private static FoldingRange folding(int startLine, int endLine, String kind) {
        FoldingRange range = new FoldingRange(startLine, endLine);
        range.setKind(kind);
        return range;
    }

    /** Zero-based line index of {@code offset} within {@code text}. */
    private static int lineAt(String text, int offset) {
        int safe = Math.max(0, Math.min(offset, text.length()));
        int line = 0;
        for (int i = 0; i < safe; i++) {
            if (text.charAt(i) == '\n') {
                line++;
            }
        }
        return line;
    }
}
