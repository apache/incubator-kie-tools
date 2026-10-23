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

package org.drools.lsp.server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.drools.completion.ClassIndex;
import org.drools.completion.ClassMemberIndex;
import org.drools.completion.Field;
import org.drools.completion.LhsBindingResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A fresh checkout with only {@code .java} sources (no {@code target/classes},
 * no {@code mvn}) still flips the classpath gate and surfaces member data,
 * because the server merges Java source-derived type names into the
 * published {@link ClassIndex} and installs the source index as the {@link
 * ClassMemberIndex} fallback.
 */
class JavaSourceTypingServerTest {

    @TempDir
    Path tempDir;

    @Test
    void sourceOnlyWorkspaceFlipsGateAndExposesSourceMembers() throws IOException {
        Path srcDir = tempDir.resolve("src/main/java/com/example");
        Files.createDirectories(srcDir);
        Files.writeString(srcDir.resolve("Foo.java"), """
                package com.example;

                public class Foo {
                    private String code;

                    public String getCode() {
                        return code;
                    }
                }
                """);

        DroolsLspServer server = TestHelperMethods.getDroolsLspServerForDocument("");
        server.initializeJavaSourceTypingForTest(tempDir);

        ClassIndex classIndex = server.getTextDocumentService().getClassIndexForTest();
        assertThat(classIndex.size()).isGreaterThan(0);
        assertThat(classIndex.getMatching("Foo")).contains("com.example.Foo");

        ClassMemberIndex memberIndex = server.getTextDocumentService().getClassMemberIndexForTest();
        List<Field> members = memberIndex.membersOf("com.example.Foo");
        assertThat(members).anyMatch(f -> f.name.equals("code"));
    }

    /**
     * When the last indexed source disappears, the refreshed — now empty — index
     * still has to reach its consumers. The rebuild's early return is taken in
     * exactly this state (no build output, no jars, nothing left to index), and
     * skipping the publish there leaves definition, type hierarchy and member
     * lookup answering for a type that no longer exists.
     */
    @Test
    void removingTheLastSourceClearsWhatTheConsumersSee() throws IOException {
        Path srcDir = tempDir.resolve("src/main/java/com/example");
        Files.createDirectories(srcDir);
        Path foo = srcDir.resolve("Foo.java");
        Files.writeString(foo, """
                package com.example;

                public class Foo {
                    public String getCode() {
                        return "c";
                    }
                }
                """);

        DroolsLspServer server = TestHelperMethods.getDroolsLspServerForDocument("");
        server.initializeJavaSourceTypingForTest(tempDir);
        assertThat(server.getTextDocumentService().getClassMemberIndexForTest()
                .membersOf("com.example.Foo")).isNotEmpty();

        Files.delete(foo);
        server.rebuildClassIndex();

        assertThat(server.getTextDocumentService().getClassMemberIndexForTest()
                .membersOf("com.example.Foo"))
                .as("a deleted type must stop answering")
                .isEmpty();
        assertThat(server.getTextDocumentService().getClassIndexForTest().size())
                .as("and stop being offered")
                .isZero();
    }

    /**
     * The classpath-members seam the document service installs is JVM-wide
     * static state, and shutdown already releases this server's other global
     * state — the member index's loader, and both parse caches. The seam has to
     * go with them, or a stopped server keeps answering through a closure that
     * pins it.
     */
    @Test
    void shutdownReleasesTheClasspathMembersSeam() throws IOException {
        Path srcDir = tempDir.resolve("src/main/java/com/example");
        Files.createDirectories(srcDir);
        Files.writeString(srcDir.resolve("Foo.java"), """
                package com.example;

                public class Foo {
                    public String getCode() {
                        return "c";
                    }
                }
                """);

        DroolsLspServer server = TestHelperMethods.getDroolsLspServerForDocument("");
        server.initializeJavaSourceTypingForTest(tempDir);

        String drl = "rule R\n  when\n    Foo( $c : code )\n  then\nend\n";
        int caret = drl.indexOf("$c");
        assertThat(LhsBindingResolver.resolveAt(drl, caret, Map.of()))
                .as("the seam answers while the server is up")
                .containsEntry("c", "String");

        server.shutdown().join();

        assertThat(LhsBindingResolver.resolveAt(drl, caret, Map.of()))
                .as("and not after it has stopped")
                .doesNotContainKey("c");
    }

    @Test
    void packageFiltersSettingExcludesNonMatchingPackage() throws IOException {
        Path srcDir = tempDir.resolve("src/main/java/com/example");
        Files.createDirectories(srcDir);
        Files.writeString(srcDir.resolve("Foo.java"), """
                package com.example;

                public class Foo {
                    private String code;

                    public String getCode() {
                        return code;
                    }
                }
                """);

        System.setProperty("drools.lsp.java.packageFilters", "org.other*");
        try {
            DroolsLspServer server = TestHelperMethods.getDroolsLspServerForDocument("");
            server.initializeJavaSourceTypingForTest(tempDir);

            ClassIndex classIndex = server.getTextDocumentService().getClassIndexForTest();
            assertThat(classIndex.getMatching("Foo")).doesNotContain("com.example.Foo");

            ClassMemberIndex memberIndex = server.getTextDocumentService().getClassMemberIndexForTest();
            assertThat(memberIndex.membersOf("com.example.Foo")).isEmpty();
        } finally {
            System.clearProperty("drools.lsp.java.packageFilters");
        }
    }
}
