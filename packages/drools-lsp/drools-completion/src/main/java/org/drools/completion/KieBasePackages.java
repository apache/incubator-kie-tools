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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Decides whether a DRL package belongs to a {@link KieBaseDecl}, following the
 * {@code packages} attribute semantics of {@code kmodule.xml}.
 *
 * <p><strong>This is a port, not an invention.</strong> The normative
 * implementation is
 * {@code org.drools.compiler.kie.builder.impl.KieBuilderImpl#isPackageInKieBase}
 * (and {@code #isPackageInKieBaseOrIncludedKieBases} for {@code includes}). It is
 * reproduced here rather than called because {@code drools-completion} carries no
 * dependency on {@code drools-compiler}, and pulling the rule compiler into the
 * language-server process to answer a string-matching question is not a trade
 * worth making. Any change to the compiler's rules should be mirrored here.
 *
 * <p>Supported pattern forms, evaluated in declaration order:
 * <ul>
 *   <li>{@code *} — every package.</li>
 *   <li>{@code com.example.rules} — that package exactly, or any package ending
 *       in {@code .com.example.rules}. An unqualified {@code rules} therefore
 *       matches {@code com.example.rules} as a suffix.</li>
 *   <li>{@code com.example.*} — {@code com.example} and everything beneath it.
 *       Matched against the package name with a build-tree prefix
 *       ({@code src.main.resources.}, {@code BOOT-INF.classes.}) stripped, and
 *       again with the group's own name stripped, so patterns may be written
 *       relative to the group.</li>
 *   <li>A leading {@code !} negates any of the above.</li>
 * </ul>
 *
 * <p><strong>The first pattern that matches decides, sign included.</strong> A
 * negation only excludes if no earlier pattern already matched, so
 * {@code ["com.example.*", "!com.example.internal.*"]} does <em>not</em> exclude
 * {@code com.example.internal} — the exclusion has to be listed first. This is
 * the compiler's behavior and the most common way a {@code packages} attribute
 * surprises its author.
 */
final class KieBasePackages {

    /**
     * Prefixes stripped from a package name before wildcard matching, so that a
     * pattern works whether the package was derived from a source tree or from
     * an already-packaged artifact. Mirrors {@code SUPPORTED_RESOURCES_ROOTS}.
     */
    private static final String[] RESOURCE_ROOT_PREFIXES = {"src.main.resources.", "BOOT-INF.classes."};

    private KieBasePackages() {
    }

    /**
     * Returns whether {@code packageName} is claimed by {@code kbase}'s own
     * {@code packages} patterns, ignoring {@code includes}. A declaration with
     * no patterns claims every package, as an empty {@code packages} attribute
     * does in {@code kmodule.xml}.
     */
    static boolean matches(KieBaseDecl kbase, String packageName) {
        if (kbase.packages().isEmpty()) {
            return true;
        }
        String pkgName = (packageName == null) ? "" : packageName;

        for (String candidate : kbase.packages()) {
            boolean negated = candidate.startsWith("!");
            String pattern = negated ? candidate.substring(1) : candidate;

            if (pattern.equals("*") || pkgName.equals(pattern) || pkgName.endsWith("." + pattern)) {
                return !negated;
            }
            if (pattern.endsWith(".*")) {
                String stem = pattern.substring(0, pattern.length() - 2);
                String relative = stripResourceRoot(pkgName);
                if (isAtOrUnder(relative, stem)) {
                    return !negated;
                }
                // A pattern may also be written relative to the group's own name.
                String selfPrefix = kbase.name() + ".";
                if (relative.startsWith(selfPrefix)
                        && isAtOrUnder(relative.substring(selfPrefix.length()), stem)) {
                    return !negated;
                }
            }
        }
        return false;
    }

    /**
     * As {@link #matches}, but also consults the groups named by
     * {@code kbase.includes()}, transitively. {@code allByName} supplies the
     * workspace's other declarations; unknown include names are ignored, and
     * include cycles terminate.
     */
    static boolean matchesWithIncludes(KieBaseDecl kbase, Map<String, KieBaseDecl> allByName, String packageName) {
        return matchesWithIncludes(kbase, allByName, packageName, new HashSet<>());
    }

    private static boolean matchesWithIncludes(KieBaseDecl kbase, Map<String, KieBaseDecl> allByName,
                                               String packageName, Set<String> visited) {
        if (!visited.add(kbase.name())) {
            return false;
        }
        if (matches(kbase, packageName)) {
            return true;
        }
        for (String includedName : kbase.includes()) {
            KieBaseDecl included = allByName.get(includedName);
            if (included != null && matchesWithIncludes(included, allByName, packageName, visited)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isAtOrUnder(String pkgName, String stem) {
        return pkgName.equals(stem) || pkgName.startsWith(stem + ".");
    }

    private static String stripResourceRoot(String pkgName) {
        for (String prefix : RESOURCE_ROOT_PREFIXES) {
            if (pkgName.startsWith(prefix)) {
                return pkgName.substring(prefix.length());
            }
        }
        return pkgName;
    }
}
