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
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The expectations here are the behavior of
 * {@code org.drools.compiler.kie.builder.impl.KieBuilderImpl#isPackageInKieBase},
 * which is the normative definition of kbase membership. Each test names the
 * branch of that method it pins.
 */
class KieBasePackagesTest {

    private static KieBaseDecl kbase(String name, String... packages) {
        return new KieBaseDecl(name, List.of(packages), List.of(), List.of());
    }

    // ── no packages attribute → the kbase claims every resource ──────────────

    @Test
    void emptyPackagesMatchesEveryPackage() {
        KieBaseDecl kb = kbase("everything");

        assertThat(KieBasePackages.matches(kb, "com.example.any")).isTrue();
        assertThat(KieBasePackages.matches(kb, "")).isTrue();
    }

    // ── literal forms ────────────────────────────────────────────────────────

    @Test
    void starMatchesEveryPackage() {
        assertThat(KieBasePackages.matches(kbase("kb", "*"), "com.example.foo")).isTrue();
    }

    @Test
    void exactPackageNameMatches() {
        KieBaseDecl kb = kbase("kb", "com.example.validation");

        assertThat(KieBasePackages.matches(kb, "com.example.validation")).isTrue();
        assertThat(KieBasePackages.matches(kb, "com.example.validation.sub")).isFalse();
    }

    @Test
    void unqualifiedCandidateMatchesAsASuffix() {
        // pkgName.endsWith("." + candidatePkg)
        KieBaseDecl kb = kbase("kb", "validation");

        assertThat(KieBasePackages.matches(kb, "com.example.validation")).isTrue();
        assertThat(KieBasePackages.matches(kb, "com.example.validations")).isFalse();
    }

    @Test
    void nonMatchingPackageIsRejected() {
        assertThat(KieBasePackages.matches(kbase("kb", "com.other.*"), "com.example.foo")).isFalse();
    }

    // ── ".*" wildcard ────────────────────────────────────────────────────────

    @Test
    void trailingWildcardMatchesTheStemAndItsDescendants() {
        KieBaseDecl kb = kbase("kb", "com.example.*");

        assertThat(KieBasePackages.matches(kb, "com.example")).isTrue();
        assertThat(KieBasePackages.matches(kb, "com.example.validation")).isTrue();
        assertThat(KieBasePackages.matches(kb, "com.example.validation.deep")).isTrue();
        assertThat(KieBasePackages.matches(kb, "com.examples.other")).isFalse();
        assertThat(KieBasePackages.matches(kb, "com")).isFalse();
        assertThat(KieBasePackages.matches(kb, "")).isFalse();
    }

    @Test
    void resourcesRootPrefixIsStrippedBeforeWildcardMatching() {
        KieBaseDecl kb = kbase("kb", "com.example.*");

        assertThat(KieBasePackages.matches(kb, "src.main.resources.com.example.foo")).isTrue();
        assertThat(KieBasePackages.matches(kb, "BOOT-INF.classes.com.example.foo")).isTrue();
    }

    @Test
    void wildcardAlsoMatchesRelativeToTheKieBaseName() {
        // A package may be addressed relative to the kbase's own name.
        KieBaseDecl kb = kbase("myKbase", "sub.*");

        assertThat(KieBasePackages.matches(kb, "myKbase.sub.foo")).isTrue();
        assertThat(KieBasePackages.matches(kb, "other.sub.foo")).isFalse();
    }

    // ── negation, and the ordering trap that comes with it ───────────────────

    @Test
    void negatedPatternExcludesWhenListedFirst() {
        KieBaseDecl kb = kbase("kb", "!com.example.internal.*", "com.example.*");

        assertThat(KieBasePackages.matches(kb, "com.example.internal.impl")).isFalse();
        assertThat(KieBasePackages.matches(kb, "com.example.public1")).isTrue();
    }

    @Test
    void negatedPatternHasNoEffectWhenListedAfterAMatchingPositive() {
        // First match wins, sign included: the positive pattern is reached
        // first, so the later negation never runs. Pinning this because it is
        // the single most surprising part of the compiler's semantics.
        KieBaseDecl kb = kbase("kb", "com.example.*", "!com.example.internal.*");

        assertThat(KieBasePackages.matches(kb, "com.example.internal.impl")).isTrue();
    }

    // ── includes ─────────────────────────────────────────────────────────────

    @Test
    void includedKieBaseContributesItsPackages() {
        KieBaseDecl shared = kbase("shared", "com.example.shared.*");
        KieBaseDecl app = new KieBaseDecl("app", List.of("com.example.app.*"), List.of("shared"), List.of());
        Map<String, KieBaseDecl> all = Map.of("shared", shared, "app", app);

        assertThat(KieBasePackages.matchesWithIncludes(app, all, "com.example.shared.types")).isTrue();
        assertThat(KieBasePackages.matchesWithIncludes(app, all, "com.example.app.rules")).isTrue();
        assertThat(KieBasePackages.matchesWithIncludes(app, all, "com.example.other")).isFalse();
    }

    @Test
    void includeCyclesTerminate() {
        KieBaseDecl a = new KieBaseDecl("a", List.of("com.a.*"), List.of("b"), List.of());
        KieBaseDecl b = new KieBaseDecl("b", List.of("com.b.*"), List.of("a"), List.of());
        Map<String, KieBaseDecl> all = Map.of("a", a, "b", b);

        assertThat(KieBasePackages.matchesWithIncludes(a, all, "com.b.foo")).isTrue();
        assertThat(KieBasePackages.matchesWithIncludes(a, all, "com.nope.foo")).isFalse();
    }

    @Test
    void includingAnExplicitFileGroupDoesNotClaimTheWholeWorkspace() {
        // The included group lists its files and declares no packages. An empty
        // pattern list means "every package", so consulting it here would give
        // the including group everything.
        KieBaseDecl explicit = new KieBaseDecl("legacy", List.of(), List.of(), List.of(),
                false, KieBaseDecl.Origin.UNKNOWN);
        KieBaseDecl app = new KieBaseDecl("app", List.of("com.example.app.*"), List.of("legacy"), List.of());
        Map<String, KieBaseDecl> all = Map.of("legacy", explicit, "app", app);

        assertThat(KieBasePackages.matchesWithIncludes(app, all, "com.example.app.rules")).isTrue();
        assertThat(KieBasePackages.matchesWithIncludes(app, all, "com.somewhere.else")).isFalse();
    }

    @Test
    void unknownIncludeIsIgnored() {
        KieBaseDecl app = new KieBaseDecl("app", List.of("com.example.app.*"), List.of("missing"), List.of());

        assertThat(KieBasePackages.matchesWithIncludes(app, Map.of("app", app), "com.example.app.x")).isTrue();
        assertThat(KieBasePackages.matchesWithIncludes(app, Map.of("app", app), "com.other")).isFalse();
    }
}
