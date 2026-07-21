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

import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DRLDocCommentParserTest {

    @Test
    void emptyOrNullReturnsEmpty() {
        assertThat(DRLDocCommentParser.parseDocs(null)).isEmpty();
        assertThat(DRLDocCommentParser.parseDocs("")).isEmpty();
    }

    @Test
    void docOnDeclaredTypeIsCaptured() {
        String drl =
            "/**\n" +
            " * A reference to a clinical question's location\n" +
            " * within a lesion assessment.\n" +
            " */\n" +
            "declare RelevantLesionState\n" +
            "    ref : QuestionRef\n" +
            "end\n";

        Map<String, String> docs = DRLDocCommentParser.parseDocs(drl);

        assertThat(docs).containsOnlyKeys("RelevantLesionState");
        assertThat(docs.get("RelevantLesionState"))
            .isEqualTo("A reference to a clinical question's location\n"
                + "within a lesion assessment.");
    }

    @Test
    void docOnFunctionIsCaptured() {
        String drl =
            "/**\n" +
            " * Returns the sum of two ints.\n" +
            " */\n" +
            "function int add(int a, int b) {\n" +
            "    return a + b;\n" +
            "}\n";

        Map<String, String> docs = DRLDocCommentParser.parseDocs(drl);

        assertThat(docs).containsEntry("add", "Returns the sum of two ints.");
    }

    @Test
    void docOnQueryIsCaptured() {
        String drl =
            "/** Finds patients over the given age. */\n" +
            "query findPatientsOver(int minAge)\n" +
            "    $p : Patient(age > minAge)\n" +
            "end\n";

        Map<String, String> docs = DRLDocCommentParser.parseDocs(drl);

        assertThat(docs).containsEntry("findPatientsOver",
            "Finds patients over the given age.");
    }

    @Test
    void docOnQuotedQueryNameIsCaptured() {
        String drl =
            "/** Finds adults. */\n" +
            "query \"Adults\"\n" +
            "    $p : Patient(age >= 18)\n" +
            "end\n";

        Map<String, String> docs = DRLDocCommentParser.parseDocs(drl);

        assertThat(docs).containsEntry("Adults", "Finds adults.");
    }

    @Test
    void docOnGlobalIsCaptured() {
        String drl =
            "/** Audit log handle shared across rules. */\n" +
            "global org.example.AuditLog auditLog\n";

        Map<String, String> docs = DRLDocCommentParser.parseDocs(drl);

        assertThat(docs).containsEntry("auditLog",
            "Audit log handle shared across rules.");
    }

    @Test
    void docOnDeclareEnumIsCaptured() {
        // `declare enum X` uses the `enum` keyword between `declare` and
        // the name. The captured key must be X, not "enum".
        String drl =
            "/** Lookup table of lesion categories. */\n"
            + "declare enum LesionCategories\n"
            + "  Target,\n"
            + "  NonTarget,\n"
            + "  New;\n"
            + "end\n";

        Map<String, String> docs = DRLDocCommentParser.parseDocs(drl);

        assertThat(docs).containsOnlyKeys("LesionCategories");
        assertThat(docs.get("LesionCategories"))
            .isEqualTo("Lookup table of lesion categories.");
    }

    @Test
    void docOnDeclareWithExtendsIsCaptured() {
        String drl =
            "/** A child fact type. */\n" +
            "declare InputLocationFact extends LocationFact\n" +
            "end\n";

        Map<String, String> docs = DRLDocCommentParser.parseDocs(drl);

        assertThat(docs).containsEntry("InputLocationFact", "A child fact type.");
    }

    @Test
    void docNotImmediatelyFollowedByDeclIsIgnored() {
        // A free-standing /** ... */ comment without a following declaration
        // shouldn't pollute the map.
        String drl =
            "/** This is just floating text. */\n"
            + "\n"
            + "// Some other code.\n"
            + "rule \"r\" when then end\n";

        assertThat(DRLDocCommentParser.parseDocs(drl)).isEmpty();
    }

    @Test
    void multipleDocsOnDifferentDeclsAreAllCaptured() {
        String drl =
            "/** Doc for A. */\n"
            + "declare A\n  x : int\nend\n"
            + "\n"
            + "/** Doc for B. */\n"
            + "declare B\n  y : int\nend\n";

        Map<String, String> docs = DRLDocCommentParser.parseDocs(drl);

        assertThat(docs)
            .containsEntry("A", "Doc for A.")
            .containsEntry("B", "Doc for B.");
    }

    @Test
    void decorativeBannerBeforeImportsDoesNotPollutLaterDocs() {
        // Real-world layout: a `/**** Imports ****/` banner, followed by
        // imports, then a `/**** Inference facts ****/` banner, then the
        // actual `/** real doc */` immediately above the declare. Only the
        // real doc should attach to the type -- the banners must not be
        // smuggled in by lazy regex expansion.
        String drl =
            "package com.example;\n"
            + "\n"
            + "/**************************\n"
            + "Imports from base\n"
            + "**************************/\n"
            + "import com.example.Foo;\n"
            + "import com.example.Bar;\n"
            + "\n"
            + "/**************************\n"
            + "Inference facts\n"
            + "**************************/\n"
            + "/** Real doc for LesionCategory. */\n"
            + "declare LesionCategory\n"
            + "  x : int\n"
            + "end\n";

        Map<String, String> docs = DRLDocCommentParser.parseDocs(drl);

        assertThat(docs).containsOnlyKeys("LesionCategory");
        assertThat(docs.get("LesionCategory"))
            .isEqualTo("Real doc for LesionCategory.");
    }

    @Test
    void bannerCommentWithThreeOrMoreStarsIsNotADoc() {
        // Even when a `/**** banner ****/` sits immediately above a
        // declaration, it must not be treated as documentation: three or
        // more leading stars marks it as a regular block comment by
        // Javadoc convention.
        String drl =
            "/**************************\n"
            + "Banner above LesionCategory\n"
            + "**************************/\n"
            + "declare LesionCategory\n"
            + "  x : int\n"
            + "end\n";

        assertThat(DRLDocCommentParser.parseDocs(drl)).isEmpty();
    }

    @Test
    void docConvenienceLookup() {
        String drl = "/** Hello. */\ndeclare Foo\nend\n";
        assertThat(DRLDocCommentParser.docFor(drl, "Foo")).isEqualTo("Hello.");
        assertThat(DRLDocCommentParser.docFor(drl, "Bar")).isNull();
        assertThat(DRLDocCommentParser.docFor(null, "Foo")).isNull();
        assertThat(DRLDocCommentParser.docFor(drl, null)).isNull();
    }
}
