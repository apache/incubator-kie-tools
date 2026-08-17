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
import java.util.Objects;

/**
 * One declared group of DRL files, as parsed from either
 * {@code META-INF/kmodule.xml} or a {@code drl-lsp-kbases.json}. Mirrors the
 * subset of {@code <kbase>} that determines which resources compile together.
 *
 * @param name     the group's name; unique within a workspace after merging
 * @param packages package patterns, in declaration order — order is significant
 *                 (see {@link KieBasePackages#matches}). Empty means "every
 *                 package", matching the compiler's treatment of a {@code kbase}
 *                 with no {@code packages} attribute
 * @param includes names of other groups whose packages this group also claims
 * @param files    absolute paths listed explicitly by the declaration, already
 *                 resolved. Always empty for kmodule-derived groups, which
 *                 select their files by package pattern instead
 * @param selectsByPackage whether this group claims workspace files by matching
 *                 {@link #packages}. True for every kmodule-derived group —
 *                 including one with no {@code packages} attribute, which claims
 *                 the whole module. False for a group that lists its files
 *                 explicitly and nothing else, where an empty {@code packages}
 *                 must claim nothing rather than everything
 * @param origin   where the declaration was read from, for the editor to explain
 */
record KieBaseDecl(String name, List<String> packages, List<String> includes, List<Path> files,
                   boolean selectsByPackage, Origin origin) {

    KieBaseDecl {
        Objects.requireNonNull(name, "name");
        packages = List.copyOf(packages);
        includes = List.copyOf(includes);
        files = List.copyOf(files);
        origin = (origin == null) ? Origin.UNKNOWN : origin;
    }

    /** A group that selects workspace files by package pattern, as kmodule does. */
    KieBaseDecl(String name, List<String> packages, List<String> includes, List<Path> files) {
        this(name, packages, includes, files, true, Origin.UNKNOWN);
    }

    /**
     * Where a declaration came from.
     *
     * <p>{@code kind} is the display noun the editor should use, and is only set
     * when the group is something more specific than a group: a {@code <kbase>}
     * read out of a {@code kmodule.xml} really is a KIE base, so it says so. A
     * group declared in the editor's own config file, or adopted from a
     * project's in-house manifest, is left generic — the extension is used well
     * beyond the Drools core audience, and "group" is the word that needs no
     * explanation.
     */
    record Origin(String kind, Path declaredIn) {

        static final Origin UNKNOWN = new Origin(null, null);

        /** A {@code <kbase>} element in a kmodule descriptor. */
        static Origin kieBase(Path declaredIn) {
            return new Origin("KIE base", declaredIn);
        }

        /** Any other declaration: the editor's config file, or an adopted manifest. */
        static Origin group(Path declaredIn) {
            return new Origin(null, declaredIn);
        }
    }

    /**
     * Merges {@code other} into this declaration: package patterns, includes
     * and files are concatenated, first declaration's entries first, without
     * duplicates. Used when the same group name is declared in more than one
     * config file.
     */
    KieBaseDecl mergedWith(KieBaseDecl other) {
        return new KieBaseDecl(name,
                concatDistinct(packages, other.packages),
                concatDistinct(includes, other.includes),
                concatDistinct(files, other.files),
                selectsByPackage || other.selectsByPackage,
                // The first declaration is the one the editor points at; naming
                // one file is more use than naming none.
                origin);
    }

    private static <T> List<T> concatDistinct(List<T> first, List<T> second) {
        if (second.isEmpty()) {
            return first;
        }
        if (first.isEmpty()) {
            return second;
        }
        List<T> merged = new ArrayList<>(first);
        for (T item : second) {
            if (!merged.contains(item)) {
                merged.add(item);
            }
        }
        return Collections.unmodifiableList(merged);
    }
}
