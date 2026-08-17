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

/**
 * Determines the DRL package a file belongs to, which is what
 * {@code kmodule.xml} {@code packages} patterns are matched against.
 *
 * <p>Mirrors {@code KieBuilderImpl#packageNameForFile}: the {@code package}
 * declaration inside the file wins, and its location on disk is the fallback.
 * The declaration is found by a comment-aware scan rather than a full parse —
 * this runs once per DRL in the workspace, and the answer is a single
 * dotted name.
 */
final class DrlPackageReader {

    /**
     * Directory segments that mark the start of a package hierarchy. Matched in
     * order; the last occurrence in the path wins, so a nested module resolves
     * against its own root.
     */
    private static final String[] PACKAGE_ROOTS = {
            "/src/main/resources/",
            "/src/test/resources/",
            "/target/classes/",
            "/target/test-classes/",
            "/BOOT-INF/classes/",
    };

    private DrlPackageReader() {
    }

    /**
     * Returns the package named by the file's {@code package} declaration, or
     * {@code null} when it has none. Declarations inside comments and string
     * literals are ignored.
     */
    static String declaredPackage(String drlText) {
        if (drlText == null || drlText.isEmpty()) {
            return null;
        }
        int at = indexOfPackageKeyword(drlText);
        if (at < 0) {
            return null;
        }
        int from = at + "package".length();
        int semicolon = drlText.indexOf(';', from);
        int newline = drlText.indexOf('\n', from);
        int end;
        if (semicolon > 0) {
            end = (newline > 0) ? Math.min(semicolon, newline) : semicolon;
        } else {
            end = newline;
        }
        if (end < 0) {
            end = drlText.length();
        }
        String name = drlText.substring(from, end).trim();
        return name.isEmpty() ? null : name;
    }

    /**
     * Locates the {@code package} keyword outside comments and string literals,
     * requiring it to stand as a whole word. Returns {@code -1} when absent.
     */
    private static int indexOfPackageKeyword(String text) {
        boolean inLineComment = false;
        boolean inBlockComment = false;
        char stringDelimiter = 0;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (inLineComment) {
                if (c == '\n') {
                    inLineComment = false;
                }
                continue;
            }
            if (inBlockComment) {
                if (c == '*' && i + 1 < text.length() && text.charAt(i + 1) == '/') {
                    inBlockComment = false;
                    i++;
                }
                continue;
            }
            if (stringDelimiter != 0) {
                if (c == '\\') {
                    i++;
                } else if (c == stringDelimiter) {
                    stringDelimiter = 0;
                }
                continue;
            }
            if (c == '/' && i + 1 < text.length()) {
                char next = text.charAt(i + 1);
                if (next == '/') {
                    inLineComment = true;
                    i++;
                    continue;
                }
                if (next == '*') {
                    inBlockComment = true;
                    i++;
                    continue;
                }
            }
            if (c == '"' || c == '\'') {
                stringDelimiter = c;
                continue;
            }
            if (c == 'p' && text.startsWith("package", i)
                    && isWordBoundaryBefore(text, i)
                    && isWordBoundaryAfter(text, i + "package".length())) {
                return i;
            }
        }
        return -1;
    }

    private static boolean isWordBoundaryBefore(String text, int index) {
        return index == 0 || !Character.isJavaIdentifierPart(text.charAt(index - 1));
    }

    private static boolean isWordBoundaryAfter(String text, int index) {
        return index >= text.length() || !Character.isJavaIdentifierPart(text.charAt(index));
    }

    /**
     * Returns the package implied by a file's location: the directories between
     * the nearest enclosing resources (or build-output) root and the file,
     * joined with dots. Falls back to the path relative to {@code workspaceRoot}
     * when the file sits under no recognized root. Never {@code null}; a file at
     * a root itself yields the default (empty) package.
     */
    static String packageFromPath(Path drlFile, Path workspaceRoot) {
        String normalized = drlFile.toAbsolutePath().normalize().toString().replace('\\', '/');

        String belowRoot = null;
        for (String root : PACKAGE_ROOTS) {
            int at = normalized.lastIndexOf(root);
            if (at >= 0) {
                belowRoot = normalized.substring(at + root.length());
                break;
            }
        }
        if (belowRoot == null) {
            belowRoot = relativizeToWorkspace(normalized, workspaceRoot);
        }

        int lastSlash = belowRoot.lastIndexOf('/');
        String folders = (lastSlash < 0) ? "" : belowRoot.substring(0, lastSlash);
        return folders.replace('/', '.');
    }

    private static String relativizeToWorkspace(String normalizedFile, Path workspaceRoot) {
        if (workspaceRoot == null) {
            return normalizedFile;
        }
        String root = workspaceRoot.toAbsolutePath().normalize().toString().replace('\\', '/');
        if (!root.endsWith("/")) {
            root = root + "/";
        }
        return normalizedFile.startsWith(root) ? normalizedFile.substring(root.length()) : normalizedFile;
    }
}
