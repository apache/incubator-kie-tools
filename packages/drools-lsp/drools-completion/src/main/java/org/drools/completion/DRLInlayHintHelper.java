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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.lsp4j.InlayHint;
import org.eclipse.lsp4j.InlayHintKind;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

/**
 * Produces inlay hints for DRL files. Binding types are resolved through the
 * shared {@link LhsBindingResolver#collect}, so inlay sees exactly the same
 * bindings as completion and hover:
 *
 * <ul>
 *   <li><b>Outer pattern bindings</b> — {@code $p : Person(...)}. The type is
 *       explicit at the declaration, so no hint is emitted.</li>
 *   <li><b>Implicit bindings</b> — field ({@code $n : fieldName}), nested-path
 *       ({@code $name : Person.name}), JDK-accessor ({@code $n : intValue}), and
 *       accumulate-result ({@code $c : count()}) bindings. A {@code : Type} hint
 *       is emitted at the declaration site and at every usage.</li>
 * </ul>
 *
 * <p>Hints are emitted at LHS constraint usages (a {@code $name} that isn't the
 * binding declaration) and at RHS usages; implicit bindings additionally get a
 * hint at their declaration site, where the type isn't otherwise visible.
 *
 * <p>Out of scope: DRL-declared types beyond the shared workspace type index,
 * and RHS parameter-name hints.
 */
public final class DRLInlayHintHelper {

    private static final Logger logger = Logger.getLogger(DRLInlayHintHelper.class.getName());

    /** Matches one full {@code rule "..." when ... then ... end} block. */
    private static final Pattern RULE_BLOCK = Pattern.compile(
            "(?s)\\brule\\b.*?\\bwhen\\b(.*?)\\bthen\\b(.*?)\\bend\\b");

    /** Matches any {@code $name} reference (used on the RHS). */
    private static final Pattern RHS_VAR = Pattern.compile("\\$(\\w+)\\b");

    /**
     * Matches a {@code $name} usage on the LHS — i.e. a {@code $name} that is
     * NOT immediately followed by {@code :} (which would mark it as a binding
     * declaration like {@code $ref: ref}). Lets us hint at cross-pattern
     * constraint usages like {@code ref.order == $ref.order} without
     * double-hinting the original binding site.
     */
    private static final Pattern LHS_VAR_USAGE = Pattern.compile("\\$(\\w+)\\b(?!\\s*:)");

    private DRLInlayHintHelper() {
    }

    public static List<InlayHint> getHints(String text, Range range, Path filePath,
                                           Map<Path, String> openFiles) {
        try {
            return getHintsImpl(text, range, filePath, openFiles);
        } catch (Throwable t) {
            logger.warning("[inlayHint] threw " + t.getClass().getSimpleName()
                    + ": " + t.getMessage());
            return Collections.emptyList();
        }
    }

    /** Convenience overload for tests and callers without a workspace context. */
    public static List<InlayHint> getHints(String text, Range range) {
        return getHints(text, range, null, Collections.emptyMap());
    }

    private static List<InlayHint> getHintsImpl(String text, Range range, Path filePath,
                                                Map<Path, String> openFiles) {
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, DeclaredType> typesByName = DRLWorkspaceTypeIndex.build(text, filePath, openFiles);
        Map<String, String> accum = AccumulateFunctionTypes.get();
        List<InlayHint> hints = new ArrayList<>();

        Matcher ruleMatch = RULE_BLOCK.matcher(text);
        while (ruleMatch.find()) {
            String whenSection = ruleMatch.group(1);
            String thenSection = ruleMatch.group(2);
            int whenStart = ruleMatch.start(1);
            int thenStart = ruleMatch.start(2);

            LhsBindingResolver.Bindings b =
                    LhsBindingResolver.collect(whenSection, typesByName, accum);
            Map<String, String> bindings = b.types;

            for (LhsBindingResolver.Decl d : b.declarations) {
                emitHint(hints, text, whenStart + d.hintOffset, d.type, range);
            }

            if (bindings.isEmpty()) {
                continue;
            }

            // LHS usages: $name references inside constraints that aren't the
            // binding declaration itself.
            Matcher lhsUse = LHS_VAR_USAGE.matcher(whenSection);
            while (lhsUse.find()) {
                String type = bindings.get(lhsUse.group(1));
                if (type == null) {
                    continue;
                }
                emitHint(hints, text, whenStart + lhsUse.end(), type, range);
            }

            // RHS usages.
            Matcher v = RHS_VAR.matcher(thenSection);
            while (v.find()) {
                String type = bindings.get(v.group(1));
                if (type == null) {
                    continue;
                }
                emitHint(hints, text, thenStart + v.end(), type, range);
            }
        }

        return hints;
    }

    /**
     * Appends an inlay hint of {@code ": typeName"} at the given absolute
     * character offset, unless the offset falls outside the requested
     * {@code range}.
     */
    private static void emitHint(List<InlayHint> hints, String text, int absOffset,
                                 String typeName, Range range) {
        Position pos = offsetToPosition(text, absOffset);
        if (range != null && !isPositionInRange(pos, range)) {
            return;
        }
        InlayHint hint = new InlayHint(pos, Either.forLeft(": " + typeName));
        hint.setKind(InlayHintKind.Type);
        hint.setPaddingLeft(Boolean.FALSE);
        hint.setPaddingRight(Boolean.TRUE);
        hints.add(hint);
    }

    /**
     * Converts a zero-based character offset within {@code text} to an LSP
     * {@link Position} (zero-based line + UTF-16 character offset on that line).
     * DRL files are ASCII in practice, so character offset matches code units.
     */
    static Position offsetToPosition(String text, int offset) {
        int safe = Math.max(0, Math.min(offset, text.length()));
        int line = 0;
        int lastNewline = -1;
        for (int i = 0; i < safe; i++) {
            if (text.charAt(i) == '\n') {
                line++;
                lastNewline = i;
            }
        }
        return new Position(line, safe - lastNewline - 1);
    }

    /**
     * Tests whether {@code pos} lies within {@code range}. The end is exclusive,
     * matching LSP range semantics — a position exactly at {@code range.end} is
     * outside the range, so no hint is emitted there.
     */
    private static boolean isPositionInRange(Position pos, Range range) {
        Position start = range.getStart();
        Position end = range.getEnd();
        if (pos.getLine() < start.getLine()) {
            return false;
        }
        if (pos.getLine() > end.getLine()) {
            return false;
        }
        if (pos.getLine() == start.getLine() && pos.getCharacter() < start.getCharacter()) {
            return false;
        }
        if (pos.getLine() == end.getLine() && pos.getCharacter() >= end.getCharacter()) {
            return false;
        }
        return true;
    }
}
