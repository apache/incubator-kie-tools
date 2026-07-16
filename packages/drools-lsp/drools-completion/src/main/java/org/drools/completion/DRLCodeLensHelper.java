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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.lsp4j.CodeLens;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

/**
 * Code lenses ({@code textDocument/codeLens}) for DRL: a "{@code N references}"
 * lens above every {@code declare}d type (and declared enum) in the document,
 * counting its uses across the workspace via {@link DRLReferencesHelper}.
 *
 * <p>The lens carries the {@code drools.peekReferences} command — a thin
 * client-side command (registered by the extension) that converts the LSP
 * arguments into VS Code types and opens the references peek. The arguments
 * are {@code [documentUri, declarationPosition, referenceLocations]}; the
 * counts are computed eagerly here, so no {@code codeLens/resolve} round-trip
 * is needed.
 *
 * <p>Counts exclude the declaration site itself (the LSP {@code includeDeclaration
 * = false} convention), so "{@code 0 references}" flags an unused type.
 */
public final class DRLCodeLensHelper {

    /** Client-side command the lens invokes; registered by the VS Code extension. */
    public static final String PEEK_REFERENCES_COMMAND = "drools.peekReferences";

    private DRLCodeLensHelper() {
    }

    /**
     * Returns one reference-count lens per declared type in {@code text}, or an
     * empty list. Each declared type's uses are resolved across the current
     * document and its sibling {@code .drl} files.
     */
    public static List<CodeLens> codeLenses(String uri, String text, Map<Path, String> openFiles,
                                            ClassIndex classIndex, Set<Path> buildOutputDirs) {
        List<CodeLens> out = new ArrayList<>();
        if (text == null) {
            return out;
        }
        for (DeclaredType declared : DRLDeclaredTypeParser.parseDeclaredTypes(text)) {
            if (declared.name == null || declared.name.isEmpty()) {
                continue;
            }
            Position namePos = new Position(declared.nameLine, declared.nameCol);
            List<Location> refs = DRLReferencesHelper.references(uri, text, namePos, openFiles,
                    classIndex, buildOutputDirs, false);

            Range range = new Range(namePos,
                    new Position(declared.nameLine, declared.nameCol + declared.name.length()));
            String title = refs.size() == 1 ? "1 reference" : refs.size() + " references";

            List<Object> args = new ArrayList<>();
            args.add(uri);
            args.add(namePos);
            args.add(refs);
            out.add(new CodeLens(range, new Command(title, PEEK_REFERENCES_COMMAND, args), null));
        }
        return out;
    }
}
